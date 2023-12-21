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

  private void handleWrite(SelectionKey key) throws IOException {
    String clientId = (String) key.attachment();

    StringBuilder s = new StringBuilder();
    while (!buffers.get(clientId).isEmpty()) {
      s.append(buffers.get(clientId).remove(0)).append("|");
    }

    sendMessage(clientId, s.toString());
  }

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

      System.out.println("Sent to client " + clientId + ": " + message);
    }
  }

  @Override
  public void handleEvent(GameEvent event) {
    throw new UnsupportedOperationException();
  }

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
