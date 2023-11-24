package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.bullet.Bullet;
import com.mygdx.bullet.BulletUpdater;
import com.mygdx.character.Character;
import com.mygdx.character.Enemy;
import com.mygdx.character.Hero;
import com.mygdx.constants.Constants;
import com.mygdx.matrix.Map;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Hades
 */
public class GameScreen implements Screen {

  final MyGdxGame game;
  private final OrthographicCamera camera;
  private final ShapeRenderer shapeRenderer;
  private List<Texture> heroTextures;
  private List<Texture> enemyTextures;
  private final List<Enemy> enemies;
  private final List<Hero> heroes;
  private final List<Bullet> bullets;
  private Texture bulletTexture;
  private Map map;
  private final Hero currentHero;
  private final Music bgm;
  private ScheduledThreadPoolExecutor executor;
  private BulletUpdater bulletUpdater;

  public GameScreen(MyGdxGame game) {
    this.game = game;

    camera = new OrthographicCamera();
    camera.setToOrtho(false, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);

    shapeRenderer = new ShapeRenderer();

    initMap();
    initTexture();
    heroes = new CopyOnWriteArrayList<>();
    enemies = new CopyOnWriteArrayList<>();
    bullets = new CopyOnWriteArrayList<>();
    initHero();
    initEnemy();
    initBullet();

    currentHero = heroes.get(0);
    currentHero.setAI(false);
    Gdx.input.setInputProcessor(new InputHandler());

    bgm = Gdx.audio.newMusic(Gdx.files.internal("sound/bgm.mp3"));
    bgm.setLooping(true);
    bgm.play();

    start();
  }

  private void initTexture() {
    heroTextures = new CopyOnWriteArrayList<>();
    enemyTextures = new CopyOnWriteArrayList<>();

    for (int i = 1; i <= Constants.CHARACTER_COUNT; i++) {
      heroTextures.add(new Texture(Gdx.files.internal("people/hero (" + i + ").png")));
      enemyTextures.add(new Texture(Gdx.files.internal("people/enemy (" + i + ").png")));
    }

    bulletTexture = new Texture(Gdx.files.internal("bullet.png"));
  }

  private void initMap() {
    map = new Map((int) Constants.ROWS, (int) Constants.COLS);
  }

  private void initHero() {
    for (int i = 0; i < Constants.INIT_HERO_COUNT; i++) {
      int x = (int) (MathUtils.random(Constants.ROWS / 2));
      int y = (int) (MathUtils.random(Constants.COLS));

      while (map.get((int) (x * Constants.CELL_SIZE), (int) (y * Constants.CELL_SIZE)) != 0) {
        x = (int) (MathUtils.random(Constants.ROWS / 2));
        y = (int) (MathUtils.random(Constants.COLS));
      }

      map.set((int) (x * Constants.CELL_SIZE), (int) (y * Constants.CELL_SIZE), 1);
      Hero hero =
          new Hero(
              (int) (x * Constants.CELL_SIZE),
              (int) (y * Constants.CELL_SIZE),
              Constants.HERO_HP,
              Constants.HERO_ATK,
              heroTextures.get(i),
              bulletTexture);
      hero.set(map, bullets, enemies);
      heroes.add(hero);
    }
  }

  private void initEnemy() {
    for (int i = 0; i < Constants.INIT_ENEMY_COUNT; i++) {
      int x = (int) (MathUtils.random(Constants.ROWS / 2, Constants.ROWS));
      int y = (int) (MathUtils.random(Constants.COLS));

      while (map.get((int) (x * Constants.CELL_SIZE), (int) (y * Constants.CELL_SIZE)) != 0) {
        x = (int) (MathUtils.random(Constants.ROWS / 2, Constants.ROWS));
        y = (int) (MathUtils.random(Constants.COLS));
      }

      map.set((int) (x * Constants.CELL_SIZE), (int) (y * Constants.CELL_SIZE), 1);
      Enemy enemy =
          new Enemy(
              (int) (x * Constants.CELL_SIZE),
              (int) (y * Constants.CELL_SIZE),
              Constants.ENEMY_HP,
              Constants.ENEMY_ATK,
              enemyTextures.get(i),
              bulletTexture);
      enemy.set(map, bullets, heroes);
      enemies.add(enemy);
    }
  }

  private void initBullet() {
    bulletUpdater = new BulletUpdater(bullets, heroes, enemies);
  }

  // start thread
  private void start() {
    executor =
        new ScheduledThreadPoolExecutor(Constants.INIT_ENEMY_COUNT + Constants.INIT_HERO_COUNT + 1);
    for (Hero hero : heroes) {
      executor.scheduleWithFixedDelay(hero, 0, Constants.INTERVAL_MILLI, TimeUnit.MILLISECONDS);
    }

    for (Enemy enemy : enemies) {
      executor.scheduleWithFixedDelay(enemy, 0, Constants.INTERVAL_MILLI, TimeUnit.MILLISECONDS);
    }

    executor.scheduleWithFixedDelay(
        bulletUpdater, 0, Constants.INTERVAL_MILLI / 40, TimeUnit.MILLISECONDS);
  }

  @Override
  public void show() {
    Gdx.app.log("GameScreen", "start");
  }

  @Override
  public void render(float delta) {
    ScreenUtils.clear(Constants.BACKGROUND_COLOR);

    camera.update();

    shapeRenderer.setProjectionMatrix(camera.combined);
    map.render(shapeRenderer);
    shapeRenderer.end();

    game.batch.setProjectionMatrix(camera.combined);
    game.batch.begin();
    drawEntity();
    game.batch.end();

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
    for (Character character : characters) {
      if (!character.isDead()) {
        return false;
      }
    }
    return true;
  }

  private void drawEntity() {
    for (Hero hero : heroes) {
      if (hero == currentHero) {
        hero.renderBorder(game.batch);
      }
      hero.render(game.batch);
    }

    for (Enemy enemy : enemies) {
      enemy.render(game.batch);
    }

    for (Bullet bullet : bullets) {
      bullet.render(game.batch);
    }
  }

  @Override
  public void resize(int width, int height) {}

  @Override
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void hide() {}

  @Override
  public void dispose() {
    try {
      if (executor.awaitTermination(Constants.INTERVAL_MILLI / 10, TimeUnit.MILLISECONDS)) {
        shapeRenderer.dispose();
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    bgm.dispose();

    heroes.clear();
    enemies.clear();
    bullets.clear();
    heroTextures.clear();
    enemyTextures.clear();
    bulletTexture.dispose();

    Gdx.app.log("GameScreen", "end");
  }

  class InputHandler implements InputProcessor {

    @Override
    public boolean keyDown(int keycode) {
      if (currentHero == null) {
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

      currentHero.update((int) (dx * Constants.CELL_SIZE), (int) (dy * Constants.CELL_SIZE));

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
      if (currentHero == null) {
        return false;
      }

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
