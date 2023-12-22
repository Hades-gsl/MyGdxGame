package com.mygdx.game;

import static org.mockito.Mockito.mock;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.bullet.Bullet;
import com.mygdx.character.Enemy;
import com.mygdx.character.Hero;
import com.mygdx.config.Config;
import com.mygdx.controller.GameController;
import com.mygdx.map.Map;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The GameScreen class represents the main screen of the game where the gameplay takes place. It
 * extends the BaseScreen class and implements the game logic. It manages the game entities (heroes,
 * enemies, bullets), the game map, and the game state. It also handles user input and game
 * rendering. It provides functionality for saving the game state and starting a replay of the game.
 * It uses a ShapeRenderer to render the game entities and a Music object to play background music.
 * It also uses a GameController to control the game logic. The game state is saved to a file and
 * can be loaded later. The game replay is also saved to a file and can be viewed later. The game
 * replay is recorded by serializing the game state to a file at each frame. The game replay is
 * played back by deserializing the game state from the file and rendering it.
 *
 * @author Hades
 */
public class GameScreen extends BaseScreen {
  private final ShapeRenderer shapeRenderer;
  private final Hero currentHero;
  private Music bgm;
  private boolean isRecording = false;
  private String recordPath;
  private FileOutputStream fileOut;
  private ObjectOutputStream out;
  private final GameController gameController;

  /**
   * The constructor initializes the game screen. It sets up the game entities, the game map, and
   * the game state. It also sets up the user interface and starts the game.
   *
   * @param game the game instance
   * @param isHeadless whether the game is running in headless mode
   * @param heroes the list of heroes
   * @param enemies the list of enemies
   * @param bullets the list of bullets
   * @param map the game map
   */
  public GameScreen(
      MyGdxGame game,
      boolean isHeadless,
      CopyOnWriteArrayList<Hero> heroes,
      CopyOnWriteArrayList<Enemy> enemies,
      CopyOnWriteArrayList<Bullet> bullets,
      Map map) {
    super(game);

    if (isHeadless) {
      shapeRenderer = mock(ShapeRenderer.class);
    } else {
      shapeRenderer = new ShapeRenderer();
    }

    gameController = new GameController(heroes, enemies, bullets, map);

    bgm = Gdx.audio.newMusic(Gdx.files.internal(Config.BGM_PATH));
    bgm.setLooping(true);
    bgm.play();

    initButton();

    currentHero = gameController.getGameState().getHeroes().get(0);
    currentHero.setAI(false);
    multiplexer.addProcessor(new InputHandler(currentHero, camera));

    gameController.start();
  }

  /** Initializes the game buttons (save, replay, back) and adds them to the stage. */
  private void initButton() {
    Vector3 position = new Vector3(Config.MAP_WIDTH, Config.MAP_HEIGHT, 0);
    camera.project(position);

    initSaveButton(position);

    initReplayButton(position);

    initBackButton(position);
  }

  /**
   * Initializes the back button and adds it to the stage.
   *
   * @param position the position where the button will be placed
   */
  private void initBackButton(Vector3 position) {
    TextButton backButton = new TextButton("Back", skin);
    backButton.setSize(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT);
    backButton.setPosition(position.x, position.y - Config.BUTTON_HEIGHT * 2, Align.topLeft);
    backButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            dispose();
            game.setScreen(new MainMenu(game));
          }
        });

    stage.addActor(backButton);
  }

  /**
   * Initializes the replay button and adds it to the stage.
   *
   * @param position the position where the button will be placed
   */
  private void initReplayButton(Vector3 position) {
    TextButton replayButton = new TextButton("Start Replay", skin);
    replayButton.setSize(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT);
    replayButton.setPosition(position.x, position.y - Config.BUTTON_HEIGHT, Align.topLeft);
    replayButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            TextButton sourceButton = (TextButton) actor;
            try {
              if ("Start Replay".contentEquals(sourceButton.getText())) {
                recordPath = Config.REPLAY_PATH + UUID.randomUUID() + ".ser";
                fileOut = new FileOutputStream(recordPath);
                out = new ObjectOutputStream(fileOut);
                isRecording = true;
                sourceButton.setText("Stop Replay");
              } else {
                isRecording = false;
                out.close();
                fileOut.close();
                sourceButton.setText("Start Replay");
                new Dialog("Success", skin)
                    .text("Replay saved successfully to file: " + recordPath.substring(7))
                    .button("OK")
                    .show(stage);
              }
            } catch (IOException e) {
              new Dialog("Error", skin)
                  .text("An error occurred while " + sourceButton.getText() + ": " + e.getMessage())
                  .button("OK")
                  .show(stage);
            }
          }
        });
    stage.addActor(replayButton);
  }

  /**
   * Initializes the save button and adds it to the stage.
   *
   * @param position the position where the button will be placed
   */
  private void initSaveButton(Vector3 position) {
    TextButton saveButton = new TextButton("Save Progress", skin);
    saveButton.setSize(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT);
    saveButton.setPosition(position.x, position.y, Align.topLeft);
    saveButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            gameController.stop();
            saveGame();
          }
        });
    stage.addActor(saveButton);
  }

  /** This method saves the game. It serializes the game entities and the game map to a file. */
  private void saveGame() {
    TextButton okButton = new TextButton("OK", skin);
    okButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            gameController.start();
          }
        });

    try {
      String filename = Config.RECORD_PATH + UUID.randomUUID() + ".ser";
      FileOutputStream fileOut = new FileOutputStream(filename);
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(gameController.getGameState().getHeroes());
      out.writeObject(gameController.getGameState().getEnemies());
      out.writeObject(gameController.getGameState().getBullets());
      out.writeObject(gameController.getGameState().getMap());
      out.close();
      fileOut.close();
      new Dialog("Success", skin)
          .text("Game saved successfully to file: " + filename)
          .button(okButton)
          .show(stage);
    } catch (IOException i) {
      new Dialog("Error", skin)
          .text("An error occurred while saving the game: " + i.getMessage())
          .button("OK")
          .show(stage);
    }
  }

  /**
   * This method records the game. It serializes the game entities and the game map to a file at
   * each frame.
   */
  private void record() {
    if (isRecording) {
      try {
        out.writeObject(gameController.getGameState().getHeroes());
        out.writeObject(gameController.getGameState().getEnemies());
        out.writeObject(gameController.getGameState().getBullets());
        out.writeObject(gameController.getGameState().getMap());
        out.flush();
        out.reset();
      } catch (IOException e) {
        new Dialog("Error", skin)
            .text("An error occurred while recording the game: " + e.getMessage())
            .button("OK")
            .show(stage);
      }
    }
  }

  /**
   * This method is called when the screen becomes the current screen for rendering. It logs the
   * start of the game screen.
   */
  @Override
  public void show() {
    Gdx.app.log("GameScreen", "start");
  }

  /**
   * This method is called every frame to render the game state. It first records the game state if
   * recording is enabled. Then it calls the parent's render method, renders the map, the heroes,
   * the enemies and the bullets, and updates and draws the stage. Finally, it checks if the game is
   * over.
   *
   * @param delta The time in seconds since the last frame.
   */
  @Override
  public void render(float delta) {
    record();

    super.render(delta);

    shapeRenderer.setProjectionMatrix(camera.combined);
    gameController.getGameState().getMap().render(shapeRenderer);
    shapeRenderer.end();

    game.batch.begin();
    drawEntity();
    game.batch.end();

    stage.act(delta);
    stage.draw();

    checkGameOver();
  }

  /**
   * This method checks if the game is over. The game is over if all heroes or all enemies are dead.
   */
  private void checkGameOver() {
    if (gameController.isHeroEmpty()) {
      dispose();
      game.setScreen(new ResultsScreen(game, "Enemy"));
    } else if (gameController.isEnemyEmpty()) {
      dispose();
      game.setScreen(new ResultsScreen(game, "Hero"));
    }
  }

  /** Draws the game entities (heroes, enemies, bullets). */
  private void drawEntity() {
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
  }

  /**
   * This method is called when the screen is no longer the current screen. It stops the game
   * controller, disposes the shape renderer and the background music, and closes the file output
   * stream and the object output stream if they are open. It logs the end of the game screen.
   */
  @Override
  public void dispose() {
    super.dispose();

    gameController.stop();

    shapeRenderer.dispose();
    bgm.dispose();

    try {
      if (out != null) {
        out.close();
      }
      if (fileOut != null) {
        fileOut.close();
      }
    } catch (IOException e) {
      Gdx.app.log("GameScreen", "error when close file: " + e.getMessage());
      Gdx.app.exit();
    }

    Gdx.app.log("GameScreen", "end");
  }

  /**
   * This class handles user input. It implements the InputProcessor interface and overrides its
   * methods to handle key presses and touch events.
   */
  public static class InputHandler implements InputProcessor {
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

      currentHero.update((int) (dx * Config.CELL_SIZE), (int) (dy * Config.CELL_SIZE));

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
      currentHero.update(v3.x, v3.y);

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
