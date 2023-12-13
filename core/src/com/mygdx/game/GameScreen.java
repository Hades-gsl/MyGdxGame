package com.mygdx.game;

import static org.mockito.Mockito.mock;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
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
 * @author Hades
 */
public class GameScreen extends BaseScreen {

  private final ShapeRenderer shapeRenderer;
  private List<Enemy> enemies;
  private List<Hero> heroes;
  private List<Bullet> bullets;
  private List<Texture> heroTextures;
  private List<Texture> enemyTextures;
  private Texture bulletTexture;
  private Map map;
  private final Hero currentHero;
  private Music bgm;
  private ScheduledThreadPoolExecutor executor;
  private BulletUpdater bulletUpdater;
  private long lastTime = TimeUtils.millis();

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

  private void initButton() {
    Vector3 position = new Vector3(Config.MAP_WIDTH, Config.MAP_HEIGHT, 0);
    camera.project(position);

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

    TextButton exitButton = new TextButton("Exit", skin);
    exitButton.setSize(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT);
    exitButton.setPosition(position.x, position.y - Config.BUTTON_HEIGHT, Align.topLeft);
    exitButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            dispose();
            Gdx.app.exit();
          }
        });

    stage.addActor(saveButton);
    stage.addActor(exitButton);
  }

  private void initGame(List<Hero> heroes, List<Enemy> enemies, List<Bullet> bullets, Map map) {
    initTexture();
    if (heroes == null || enemies == null || bullets == null || map == null) {
      initMap();
      this.heroes = new CopyOnWriteArrayList<>();
      this.enemies = new CopyOnWriteArrayList<>();
      this.bullets = new CopyOnWriteArrayList<>();
      initHero();
      initEnemy();
    } else {
      this.map = map;
      Config.changeConfig(map.getMatrix().length, map.getMatrix()[0].length);
      camera.setToOrtho(false, Config.CAMERA_WIDTH, Config.CAMERA_HEIGHT);

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

  private void loadTexture() {
    for (int i = 0; i < heroes.size(); i++) {
      heroes.get(i).setBulletTexture(bulletTexture);
      heroes.get(i).setCharaterTexture(heroTextures.get(i));
      heroes.get(i).setSprite(new Sprite(heroTextures.get(i)));
      heroes.get(i).setDieTexture(new Texture(Gdx.files.internal(Config.DIE_PATH)));
    }
    for (int i = 0; i < enemies.size(); i++) {
      enemies.get(i).setBulletTexture(bulletTexture);
      enemies.get(i).setCharaterTexture(enemyTextures.get(i));
      enemies.get(i).setSprite(new Sprite(enemyTextures.get(i)));
      enemies.get(i).setDieTexture(new Texture(Gdx.files.internal(Config.DIE_PATH)));
    }
    for (Bullet bullet : bullets) {
      bullet.setSprite(new Sprite(bulletTexture));
      bullet.getSprite().setRotation(bullet.getRotation());
      bullet.setSound(Gdx.audio.newSound(Gdx.files.internal(Config.SHOOT_PATH)));
    }
  }

  private void initTexture() {
    heroTextures = new CopyOnWriteArrayList<>();
    enemyTextures = new CopyOnWriteArrayList<>();
    bulletTexture = new Texture(Gdx.files.internal(Config.BULLET_PATH));
    for (int i = 0; i < Config.INIT_HERO_COUNT; i++) {
      heroTextures.add(
          new Texture(Gdx.files.internal(Config.HERO_PATH + " (" + (i + 1) + ").png")));
    }
    for (int i = 0; i < Config.INIT_ENEMY_COUNT; i++) {
      enemyTextures.add(
          new Texture(Gdx.files.internal(Config.ENEMY_PATH + " (" + (i + 1) + ").png")));
    }
  }

  private void initMap() {
    map = new Map((int) Config.ROWS, (int) Config.COLS);
  }

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

  private void initBulletUpdater() {
    bulletUpdater = new BulletUpdater(bullets, heroes, enemies);
  }

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

  @Override
  public void show() {
    Gdx.app.log("GameScreen", "start");
  }

  @Override
  public void render(float delta) {
    ScreenUtils.clear(Config.BACKGROUND_COLOR);

    camera.update();

    shapeRenderer.setProjectionMatrix(camera.combined);
    map.render(shapeRenderer);
    shapeRenderer.end();

    game.batch.setProjectionMatrix(camera.combined);
    game.batch.begin();
    drawEntity();
    game.batch.end();

    stage.act(Gdx.graphics.getDeltaTime());
    stage.draw();

    checkGameOver();
  }

  private void checkGameOver() {
    if (isEmpty(heroes)) {
      dispose();
      game.setScreen(new ResultsScreen(game, "Enemy"));
    } else if (isEmpty(enemies)) {
      dispose();
      game.setScreen(new ResultsScreen(game, "Hero"));
    }
  }

  private boolean isEmpty(List<? extends Character> characters) {
    return characters.stream().allMatch(Entity::isDead);
  }

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

    Gdx.app.log("GameScreen", "end");
  }

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
