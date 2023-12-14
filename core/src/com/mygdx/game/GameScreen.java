package com.mygdx.game;

import static org.mockito.Mockito.mock;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.bullet.Bullet;
import com.mygdx.bullet.BulletUpdater;
import com.mygdx.character.Character;
import com.mygdx.character.Enemy;
import com.mygdx.character.Hero;
import com.mygdx.config.Config;
import com.mygdx.entity.Entity;
import com.mygdx.matrix.Map;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class represents the game screen where the game is played. It extends the AbstractGameScreen
 * class and implements the game logic. It manages the game entities (heroes, enemies, bullets), the
 * game map, and the game state. It also handles user input and game rendering.
 *
 * @author Hades
 */
public class GameScreen extends AbstractGameScreen {
  private final ShapeRenderer shapeRenderer;
  private final Hero currentHero;
  private Music bgm;
  private ScheduledThreadPoolExecutor executor;
  private BulletUpdater bulletUpdater;
  private long lastTime = TimeUtils.millis();
  private boolean isRecording = false;
  private String recordPath;
  private FileOutputStream fileOut;
  private ObjectOutputStream out;

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
      List<Hero> heroes,
      List<Enemy> enemies,
      List<Bullet> bullets,
      Map map) {
    super(game);

    if (isHeadless) {
      shapeRenderer = mock(ShapeRenderer.class);
    } else {
      shapeRenderer = new ShapeRenderer();
    }

    initGame(heroes, enemies, bullets, map);

    initButton();

    currentHero = this.heroes.get(0);
    currentHero.setAI(false);
    multiplexer.addProcessor(new InputHandler());

    start();
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
            stop();
            saveGame();
          }
        });
    stage.addActor(saveButton);
  }

  /**
   * This method initializes the game. It sets up the game entities, the game map, and the game
   * state.
   *
   * @param heroes the list of heroes
   * @param enemies the list of enemies
   * @param bullets the list of bullets
   * @param map the game map
   */
  private void initGame(List<Hero> heroes, List<Enemy> enemies, List<Bullet> bullets, Map map) {
    if (heroes == null || enemies == null || bullets == null || map == null) {
      initMap();
      this.heroes = new CopyOnWriteArrayList<>();
      this.enemies = new CopyOnWriteArrayList<>();
      this.bullets = new CopyOnWriteArrayList<>();
      initHero();
      initEnemy();
    } else {
      this.map = map;
      this.heroes = heroes;
      this.enemies = enemies;
      this.bullets = bullets;

      loadTexture();
    }

    initBulletUpdater();

    bgm = Gdx.audio.newMusic(Gdx.files.internal(Config.BGM_PATH));
    bgm.setLooping(true);
    bgm.play();
  }

  /** Initializes the game map. */
  private void initMap() {
    map = new Map((int) Config.ROWS, (int) Config.COLS);
  }

  /** Initializes the heroes and adds them to the heroes list. */
  private void initHero() {
    for (int i = 0; i < Config.INIT_HERO_COUNT; i++) {
      int x = (int) (MathUtils.random(Config.ROWS / 2));
      int y = (int) (MathUtils.random(Config.COLS));

      while (map.get((int) (x * Config.CELL_SIZE), (int) (y * Config.CELL_SIZE)) != 0) {
        x = (int) (MathUtils.random(Config.ROWS / 2));
        y = (int) (MathUtils.random(Config.COLS));
      }

      map.set((int) (x * Config.CELL_SIZE), (int) (y * Config.CELL_SIZE), 1);
      Hero hero =
          new Hero(
              (int) (x * Config.CELL_SIZE),
              (int) (y * Config.CELL_SIZE),
              Config.HERO_HP,
              Config.HERO_ATK,
              heroTextures.get(i),
              bulletTexture);
      hero.set(map, bullets, enemies);
      heroes.add(hero);
    }
  }

  /** Initializes the enemies and adds them to the enemies list. */
  private void initEnemy() {
    for (int i = 0; i < Config.INIT_ENEMY_COUNT; i++) {
      int x = (int) (MathUtils.random(Config.ROWS / 2, Config.ROWS));
      int y = (int) (MathUtils.random(Config.COLS));

      while (map.get((int) (x * Config.CELL_SIZE), (int) (y * Config.CELL_SIZE)) != 0) {
        x = (int) (MathUtils.random(Config.ROWS / 2, Config.ROWS));
        y = (int) (MathUtils.random(Config.COLS));
      }

      map.set((int) (x * Config.CELL_SIZE), (int) (y * Config.CELL_SIZE), 1);
      Enemy enemy =
          new Enemy(
              (int) (x * Config.CELL_SIZE),
              (int) (y * Config.CELL_SIZE),
              Config.ENEMY_HP,
              Config.ENEMY_ATK,
              enemyTextures.get(i),
              bulletTexture);
      enemy.set(map, bullets, heroes);
      enemies.add(enemy);
    }
  }

  /** Initializes the bullet updater. */
  private void initBulletUpdater() {
    bulletUpdater = new BulletUpdater(bullets, heroes, enemies);
  }

  /** This method starts the game. It schedules the game entities to update at fixed intervals. */
  private void start() {
    executor =
        new ScheduledThreadPoolExecutor(Config.INIT_ENEMY_COUNT + Config.INIT_HERO_COUNT + 1);

    heroes.forEach(
        hero ->
            executor.scheduleWithFixedDelay(hero, 0, Config.INTERVAL_MILLI, TimeUnit.MILLISECONDS));

    enemies.forEach(
        enemy ->
            executor.scheduleWithFixedDelay(
                enemy, 0, Config.INTERVAL_MILLI, TimeUnit.MILLISECONDS));

    executor.scheduleWithFixedDelay(
        bulletUpdater, 0, Config.INTERVAL_MILLI / 40, TimeUnit.MILLISECONDS);
  }

  /** This method stops the game. It stops the game entities from updating. */
  private void stop() {
    executor.shutdown(); // Disable new tasks from being submitted
    try {
      // Wait a while for existing tasks to terminate
      if (!executor.awaitTermination(Config.INTERVAL_MILLI / 10, TimeUnit.MILLISECONDS)) {
        executor.shutdownNow(); // Cancel currently executing tasks
        // Wait a while for tasks to respond to being cancelled
        if (!executor.awaitTermination(Config.INTERVAL_MILLI / 10, TimeUnit.MILLISECONDS)) {
          Gdx.app.log("GameScreen", "Pool did not terminate");
        }
      }
    } catch (InterruptedException ie) {
      // (Re-)Cancel if current thread also interrupted
      executor.shutdownNow();
      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }
  }

  /** This method saves the game. It serializes the game entities and the game map to a file. */
  private void saveGame() {
    TextButton okButton = new TextButton("OK", skin);
    okButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            start();
          }
        });

    try {
      String filename = Config.RECORD_PATH + UUID.randomUUID() + ".ser";
      FileOutputStream fileOut = new FileOutputStream(filename);
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(heroes);
      out.writeObject(enemies);
      out.writeObject(bullets);
      out.writeObject(map);
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
        out.writeObject(heroes);
        out.writeObject(enemies);
        out.writeObject(bullets);
        out.writeObject(map);
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

  @Override
  public void show() {
    Gdx.app.log("GameScreen", "start");
  }

  @Override
  public void render(float delta) {
    record();

    super.render(delta);

    shapeRenderer.setProjectionMatrix(camera.combined);
    map.render(shapeRenderer);
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
    if (isEmpty(heroes)) {
      dispose();
      game.setScreen(new ResultsScreen(game, "Enemy"));
    } else if (isEmpty(enemies)) {
      dispose();
      game.setScreen(new ResultsScreen(game, "Hero"));
    }
  }

  /**
   * This method checks if all characters in a list are dead.
   *
   * @param characters the list of characters
   * @return true if all characters are dead, false otherwise
   */
  private boolean isEmpty(List<? extends Character> characters) {
    return characters.stream().allMatch(Entity::isDead);
  }

  /** Draws the game entities (heroes, enemies, bullets). */
  private void drawEntity() {
    heroes.forEach(
        hero -> {
          if (hero == currentHero) {
            if (hero.isDead()) {
              hero.changeDieTexture();
            }
            hero.renderBorder(game.batch);
          }
          hero.render(game.batch);
        });

    enemies.forEach(enemy -> enemy.render(game.batch));

    bullets.forEach(bullet -> bullet.render(game.batch));
  }

  @Override
  public void dispose() {
    super.dispose();

    stop();

    shapeRenderer.dispose();
    bgm.dispose();

    heroes.clear();
    enemies.clear();
    bullets.clear();

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
  class InputHandler implements InputProcessor {

    @Override
    public boolean keyDown(int keycode) {
      if (currentHero.isDead()) {
        return false;
      }

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
      if (currentHero.isDead() || TimeUtils.millis() - lastTime < Config.INTERVAL_MILLI) {
        return false;
      }

      lastTime = TimeUtils.millis();

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
