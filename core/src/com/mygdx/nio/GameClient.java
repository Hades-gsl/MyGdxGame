package com.mygdx.nio;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mygdx.character.Hero;
import com.mygdx.config.Config;
import com.mygdx.controller.GameController;
import com.mygdx.controller.GameState;
import com.mygdx.event.CharacterAttack;
import com.mygdx.event.CharacterMove;
import com.mygdx.event.GameEvent;
import com.mygdx.event.HeroAttack;
import com.mygdx.game.BaseScreen;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.ResultsScreen;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * This class represents the game client in a multiplayer game. It is responsible for handling user
 * input, communicating with the game server, and rendering the game state. It extends the
 * BaseScreen class and implements the Runnable interface. It uses non-blocking I/O with Java's NIO
 * package to communicate with the server. It uses Jackson for JSON serialization and
 * deserialization. It uses LibGDX for rendering and user input handling.
 *
 * @author Hades
 */
public class GameClient extends BaseScreen implements Runnable {

  private SocketChannel socketChannel;
  private final ShapeRenderer shapeRenderer;
  private final GameController gameController;
  private Hero currentHero;

  /**
   * Constructor for GameClient. Initializes the game client with the provided game instance, server
   * address, and port. Establishes a connection to the game server and initializes the game. Starts
   * a new thread for receiving messages from the server.
   *
   * @param game The game instance.
   * @param serverAddress The address of the game server.
   * @param port The port of the game server.
   */
  public GameClient(MyGdxGame game, String serverAddress, int port) {
    super(game);

    Config.changeHeroCount(Config.MULTI_HERO_COUNT);

    shapeRenderer = new ShapeRenderer();
    gameController = new GameController();

    establishConnection(serverAddress, port);

    try {
      initGame();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    multiplexer.addProcessor(new InputHandler(currentHero, camera));

    new Thread(this).start();
  }

  /**
   * Establishes a connection to the game server. If the connection fails, it throws a
   * RuntimeException.
   *
   * @param serverAddress The address of the game server.
   * @param port The port of the game server.
   */
  private void establishConnection(String serverAddress, int port) {
    try {
      socketChannel = SocketChannel.open();
      socketChannel.connect(new InetSocketAddress(serverAddress, port));
      //      socketChannel.configureBlocking(false);

      // Print connection information
      System.out.println("Attempting to connect to server at " + serverAddress + ":" + port);
      if (socketChannel.isConnected()) {
        System.out.println("Connection established successfully.");
      } else {
        System.out.println("Failed to establish connection.");
        throw new RuntimeException("Failed to establish connection.");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Initializes the game by receiving the initial game state from the server. The game state is
   * received as a JSON string and deserialized into a GameState object. The textures for the game
   * entities are loaded.
   *
   * @throws IOException If an I/O error occurs.
   */
  private void initGame() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    String s = receiveMessage();
    String[] info = s.split("\\|");
    assert info.length == 2;

    gameController.setGameState(objectMapper.readValue(info[0], GameState.class));
    currentHero = gameController.getGameState().getHeroes().get(Integer.parseInt(info[1].trim()));

    gameController.loadTexture();
  }

  /**
   * Sends a message to the game server. The message is sent as a JSON string.
   *
   * @param message The message to send.
   * @throws IOException If an I/O error occurs.
   */
  public void sendMessage(String message) throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(Config.BUFFER_SIZE);
    buffer.clear();
    buffer.put(message.getBytes());
    buffer.flip();
    while (buffer.hasRemaining()) {
      socketChannel.write(buffer);
    }

    System.out.println("Sent to server: " + message);
  }

  /**
   * Receives a message from the game server. The message is received as a JSON string.
   *
   * @return The received message.
   * @throws IOException If an I/O error occurs.
   */
  public String receiveMessage() throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(Config.BUFFER_SIZE);
    StringBuilder receivedMessage = new StringBuilder();

    socketChannel.read(buffer);
    receivedMessage.append(new String(buffer.array(), StandardCharsets.UTF_8));

    System.out.println("Received from server: " + receivedMessage);

    return receivedMessage.toString();
  }

  /**
   * Handles a message received from the game server. The message is a JSON string that represents a
   * game event. The event is deserialized and handled according to its type.
   *
   * @param msg The message to handle.
   * @throws JsonProcessingException If an error occurs during JSON processing.
   */
  private void handleMassage(String msg) throws JsonProcessingException {
    String[] msgs = msg.split("\\|");
    ObjectMapper objectMapper = new ObjectMapper();
    for (String s : msgs) {
      if (s.trim().isEmpty()) {
        continue;
      }

      JsonNode rootNode = objectMapper.readTree(s);
      String eventType = rootNode.path("type").asText();
      if (Objects.equals(eventType, GameEvent.Type.HERO_MOVE.name())) {
        gameController.handleClientEvent(objectMapper.treeToValue(rootNode, CharacterMove.class));
      } else if (Objects.equals(eventType, GameEvent.Type.CHARACTER_ATTACK.name())) {
        gameController.handleClientEvent(objectMapper.treeToValue(rootNode, CharacterAttack.class));
      } else if (Objects.equals(eventType, GameEvent.Type.ENEMY_MOVE.name())) {
        gameController.handleClientEvent(objectMapper.treeToValue(rootNode, CharacterMove.class));
      } else if (Objects.equals(eventType, GameEvent.Type.HERO_ATTACK.name())) {
        gameController.handleClientEvent(objectMapper.treeToValue(rootNode, HeroAttack.class));
      } else {
        throw new RuntimeException("Unknown event type: " + eventType);
      }
    }
  }

  /**
   * Renders the game state. This method is called once per frame. It renders the map, heroes,
   * enemies, and bullets. If all heroes or enemies are dead, it switches to the results screen.
   *
   * @param delta The time in seconds since the last frame.
   */
  @Override
  public void render(float delta) {
    super.render(delta);

    if (gameController.isHeroEmpty()) {
      game.setScreen(new ResultsScreen(game, "Enemy"));
    } else if (gameController.isEnemyEmpty()) {
      game.setScreen(new ResultsScreen(game, "Hero"));
    }

    shapeRenderer.setProjectionMatrix(camera.combined);
    gameController.getGameState().getMap().render(shapeRenderer);
    shapeRenderer.end();

    game.batch.begin();
    gameController
        .getGameState()
        .getHeroes()
        .forEach(
            hero -> {
              if (hero == currentHero) {
                hero.renderBorder(game.batch);
              }
              hero.render(game.batch);
            });
    gameController.getGameState().getEnemies().forEach(enemy -> enemy.render(game.batch));
    gameController.getGameState().getBullets().forEach(bullet -> bullet.render(game.batch));
    game.batch.end();
  }

  /**
   * Disposes of all resources used by the game client. This method is called when the game client
   * is no longer needed.
   */
  @Override
  public void dispose() {
    super.dispose();

    gameController.stop();
    shapeRenderer.dispose();
  }

  /**
   * The main loop for receiving messages from the game server. This method is run in a separate
   * thread. It continuously receives messages from the server and handles them.
   */
  @Override
  public void run() {
    try {
      while (true) {
        String s = receiveMessage();
        if ("start".equals(s.trim())) {
          System.out.println("Game start!");
          Thread.sleep(1000);
          gameController.startBullet();
        } else {
          handleMassage(s);
        }
      }
    } catch (InterruptedException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * This class handles user input. It implements the InputProcessor interface from LibGDX. It
   * handles key presses for moving the hero and mouse clicks for attacking.
   */
  class InputHandler implements InputProcessor {
    private long lastTimeMove = TimeUtils.millis();
    private long lastTimeAttack = TimeUtils.millis();
    private final Hero currentHero;
    private final OrthographicCamera camera;

    public InputHandler(Hero currentHero, OrthographicCamera camera) {
      this.currentHero = currentHero;
      this.camera = camera;
    }

    @Override
    public boolean keyDown(int keycode) {
      if (currentHero.isDead() || TimeUtils.millis() - lastTimeMove < Config.INTERVAL_MILLI / 2) {
        return false;
      }

      lastTimeMove = TimeUtils.millis();

      int dx = 0, dy = 0;
      switch (keycode) {
        case Input.Keys.UP:
        case Input.Keys.W:
          dy = 1;
          break;
        case Input.Keys.DOWN:
        case Input.Keys.S:
          dy = -1;
          break;
        case Input.Keys.LEFT:
        case Input.Keys.A:
          dx = -1;
          break;
        case Input.Keys.RIGHT:
        case Input.Keys.D:
          dx = 1;
          break;
      }

      try {
        sendMessage(
            new ObjectMapper()
                .writeValueAsString(
                    new CharacterMove(dx, dy, currentHero.getId(), GameEvent.Type.HERO_MOVE)));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      return false;
    }

    @Override
    public boolean keyUp(int keycode) {
      return false;
    }

    @Override
    public boolean keyTyped(char character) {
      return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
      if (currentHero.isDead() || TimeUtils.millis() - lastTimeAttack < Config.INTERVAL_MILLI) {
        return false;
      }

      lastTimeAttack = TimeUtils.millis();

      Vector3 v3 = new Vector3(screenX, screenY, 0);
      camera.unproject(v3);
      try {
        sendMessage(
            new ObjectMapper()
                .writeValueAsString(
                    new HeroAttack(currentHero.getId(), v3.x, v3.y, GameEvent.Type.HERO_ATTACK)));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
      return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
      return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
      return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
      return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
      return false;
    }
  }
}
