package com.mygdx.nio;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mygdx.config.Config;
import com.mygdx.controller.GameController;
import com.mygdx.event.CharacterAttack;
import com.mygdx.event.CharacterMove;
import com.mygdx.event.GameEvent;
import com.mygdx.event.HeroAttack;
import com.mygdx.game.BaseScreen;
import com.mygdx.game.MyGdxGame;
import com.mygdx.observer.GameObserver;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class represents the game server in a multiplayer game. It is responsible for handling
 * client connections, communicating with clients, and updating the game state. It extends the
 * BaseScreen class and implements the GameObserver and Runnable interfaces. It uses non-blocking
 * I/O with Java's NIO package to handle client connections. It uses Jackson for JSON serialization
 * and deserialization. It uses LibGDX for rendering the game state. It maintains a map of client
 * connections, where each client is identified by a unique id. It also maintains a map of message
 * buffers for each client, which are used for sending game events to clients. It uses a Selector to
 * handle I/O events on the ServerSocketChannel and the client SocketChannels. It uses a separate
 * thread to continuously listen for incoming I/O events and handle them. It also observes the game
 * state and sends game events to clients when they occur.
 *
 * @author Hades
 */
public class GameServer extends BaseScreen implements GameObserver, Runnable {

  private final ServerSocketChannel serverSocketChannel;
  private final Selector selector;
  private final Map<String, SocketChannel> clientConnections;
  private final Map<String, CopyOnWriteArrayList<String>> buffers;
  private final GameController gameController;
  private int clientId = 0;
  private int activeConnections = 0;
  private final ShapeRenderer shapeRenderer;

  /**
   * Constructor for GameServer. Initializes the game server with the provided game instance and
   * port. Opens a ServerSocketChannel and binds it to the provided port. Configures the
   * ServerSocketChannel to be non-blocking and registers it with a Selector. Initializes the client
   * connections map, the buffers map, and the game controller. Starts a new thread to listen for
   * and handle I/O events.
   *
   * @param game The game instance.
   * @param port The port to listen on.
   * @throws IOException If an I/O error occurs.
   */
  public GameServer(MyGdxGame game, int port) throws IOException {
    super(game);
    Config.changeHeroCount(Config.MULTI_HERO_COUNT);
    shapeRenderer = new ShapeRenderer();

    serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.bind(new InetSocketAddress(port));
    serverSocketChannel.configureBlocking(false);

    selector = Selector.open();
    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

    clientConnections = new HashMap<>();
    buffers = new HashMap<>();

    gameController = new GameController(null, null, null, null);
    gameController.getGameState().addObserver(this);

    new Thread(this).start();
  }

  /**
   * The main loop for handling I/O events. This method is run in a separate thread. It continuously
   * listens for I/O events on the Selector and handles them. It handles accept events on the
   * ServerSocketChannel and read and write events on the client SocketChannels. If all heroes or
   * enemies are dead, it stops the game and returns.
   */
  @Override
  public void run() {
    System.out.println("Server listening on port " + serverSocketChannel.socket().getLocalPort());

    try {
      while (true) {
        if (gameController.isHeroEmpty() || gameController.isEnemyEmpty()) {
          System.out.println("end");
          gameController.stop();
          return;
        }

        selector.select();
        Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

        while (keyIterator.hasNext()) {
          SelectionKey key = keyIterator.next();
          keyIterator.remove();

          if (key.isAcceptable() && activeConnections < Config.MAX_CONNECTIONS) {
            String clientId = handleAccept(key);
            sendInitMessage(clientId);
          } else if (key.isReadable()) {
            handleRead(key);
          } else if (key.isWritable()) {
            handleWrite(key);
          }
        }
      }
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Sends the initial game state to a newly connected client. The game state is serialized to a
   * JSON string and sent to the client. If the maximum number of connections has been reached, it
   * sends a start message to all clients and starts the game.
   *
   * @param clientId The id of the client to send the initial game state to.
   * @throws IOException If an I/O error occurs.
   * @throws InterruptedException If the thread is interrupted.
   */
  private void sendInitMessage(String clientId) throws IOException, InterruptedException {
    ObjectMapper objectMapper = new ObjectMapper();
    String s;
    s = objectMapper.writeValueAsString(gameController.getGameState()) + "|" + (this.clientId - 1);
    sendMessage(clientId, s);

    if (activeConnections == Config.MAX_CONNECTIONS) {
      Thread.sleep(1000);
      for (Map.Entry<String, SocketChannel> entry : clientConnections.entrySet()) {
        String k = entry.getKey();
        SocketChannel v = entry.getValue();
        sendMessage(k, "start");
      }

      Thread.sleep(1000);
      gameController.startEnemy();
      gameController.startBullet();
    }
  }

  /**
   * Handles an accept event on the ServerSocketChannel. Accepts the new client connection,
   * configures the client SocketChannel to be non-blocking, and registers it with the Selector.
   * Adds the client SocketChannel to the client connections map and creates a new message buffer
   * for the client. Returns the id of the new client.
   *
   * @param key The SelectionKey for the ServerSocketChannel.
   * @return The id of the new client.
   * @throws IOException If an I/O error occurs.
   */
  private String handleAccept(SelectionKey key) throws IOException {
    activeConnections++;
    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
    SocketChannel clientChannel = serverSocketChannel.accept();
    clientChannel.configureBlocking(false);

    String clientId = "Player" + this.clientId;
    this.clientId++;
    clientConnections.put(clientId, clientChannel);
    buffers.put(clientId, new CopyOnWriteArrayList<>());
    clientChannel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, clientId);

    System.out.println("Client connected: " + clientId);

    return clientId;
  }

  /**
   * Handles a read event on a client SocketChannel. Reads the message from the client, deserializes
   * the game event from the message, and handles the game event. If the client has disconnected, it
   * closes the client SocketChannel and removes it from the client connections map.
   *
   * @param key The SelectionKey for the client SocketChannel.
   * @throws IOException If an I/O error occurs.
   */
  private void handleRead(SelectionKey key) throws IOException {
    SocketChannel clientChannel = (SocketChannel) key.channel();
    ByteBuffer buffer = ByteBuffer.allocate(Config.BUFFER_SIZE);
    int bytesRead = clientChannel.read(buffer);

    if (bytesRead == -1) {
      String clientId = (String) key.attachment();
      clientChannel.close();
      key.cancel();
      clientConnections.remove(clientId);
      System.out.println("Client disconnected: " + clientId);
      return;
    }

    buffer.flip();
    byte[] data = new byte[buffer.remaining()];
    buffer.get(data);

    String clientId = (String) key.attachment();
    System.out.println("Received from client " + clientId + ": " + new String(data));

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rootNode = objectMapper.readTree(data);
    String eventType = rootNode.path("type").asText();
    if (GameEvent.Type.HERO_MOVE.name().equals(eventType)) {
      gameController.handleServerEvent(objectMapper.treeToValue(rootNode, CharacterMove.class));
    } else if (GameEvent.Type.HERO_ATTACK.name().equals(eventType)) {
      gameController.handleServerEvent(objectMapper.treeToValue(rootNode, HeroAttack.class));
    } else {
      throw new RuntimeException("Unknown event type: " + eventType);
    }
  }

  /**
   * Handles a write event on a client SocketChannel. Sends all messages in the client's message
   * buffer to the client.
   *
   * @param key The SelectionKey for the client SocketChannel.
   * @throws IOException If an I/O error occurs.
   */
  private void handleWrite(SelectionKey key) throws IOException {
    String clientId = (String) key.attachment();

    StringBuilder s = new StringBuilder();
    while (!buffers.get(clientId).isEmpty()) {
      s.append(buffers.get(clientId).remove(0)).append("|");
    }

    sendMessage(clientId, s.toString());
  }

  /**
   * Sends a message to a client. The message is sent as a JSON string.
   *
   * @param clientId The id of the client to send the message to.
   * @param message The message to send.
   * @throws IOException If an I/O error occurs.
   */
  private void sendMessage(String clientId, String message) throws IOException {
    SocketChannel clientChannel = clientConnections.get(clientId);
    if (clientChannel != null) {
      ByteBuffer buffer = ByteBuffer.allocate(Config.BUFFER_SIZE);
      buffer.clear();
      buffer.put(message.getBytes());
      buffer.flip();
      while (buffer.hasRemaining()) {
        clientChannel.write(buffer);
      }

      if (!message.isEmpty()) {
        System.out.println("Sent to client " + clientId + ": " + message);
      }
    }
  }

  /**
   * Handles a game event. This method is not supported and will throw an
   * UnsupportedOperationException if called.
   *
   * @param event The game event to handle.
   */
  @Override
  public void handleEvent(GameEvent event) {
    throw new UnsupportedOperationException();
  }

  /**
   * Handles a character move event. Serializes the event to a JSON string and adds it to the
   * message buffers of all clients.
   *
   * @param event The character move event to handle.
   */
  @Override
  public void handleEvent(CharacterMove event) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      String s = objectMapper.writeValueAsString(event);
      buffers.forEach((k, v) -> v.add(s));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Handles a character attack event. Serializes the event to a JSON string and adds it to the
   * message buffers of all clients.
   *
   * @param event The character attack event to handle.
   */
  @Override
  public void handleEvent(CharacterAttack event) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      String s = objectMapper.writeValueAsString(event);
      buffers.forEach((k, v) -> v.add(s));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Handles a hero attack event. Serializes the event to a JSON string and adds it to the message
   * buffers of all clients.
   *
   * @param event The hero attack event to handle.
   */
  @Override
  public void handleEvent(HeroAttack event) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      String s = objectMapper.writeValueAsString(event);
      buffers.forEach((k, v) -> v.add(s));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Renders the game state. This method is called once per frame. It renders the map, heroes,
   * enemies, and bullets.
   *
   * @param delta The time in seconds since the last frame.
   */
  @Override
  public void render(float delta) {
    super.render(delta);

    shapeRenderer.setProjectionMatrix(camera.combined);
    gameController.getGameState().getMap().render(shapeRenderer);
    shapeRenderer.end();

    game.batch.begin();
    gameController.getGameState().getHeroes().forEach(hero -> hero.render(game.batch));
    gameController.getGameState().getEnemies().forEach(enemy -> enemy.render(game.batch));
    gameController.getGameState().getBullets().forEach(bullet -> bullet.render(game.batch));
    game.batch.end();
  }
}
