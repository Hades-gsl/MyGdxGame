package com.mygdx.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.bullet.Bullet;
import com.mygdx.bullet.BulletUpdater;
import com.mygdx.character.Enemy;
import com.mygdx.character.Hero;
import com.mygdx.config.Config;
import com.mygdx.entity.Entity;
import com.mygdx.event.CharacterAttack;
import com.mygdx.event.CharacterMove;
import com.mygdx.event.GameEvent;
import com.mygdx.event.HeroAttack;
import com.mygdx.map.Map;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Hades
 */
@Getter
@Setter
@NoArgsConstructor
public class GameController {
  private BulletUpdater bulletUpdater;
  private ScheduledThreadPoolExecutor executor;
  private GameState gameState;

  public GameController(
      CopyOnWriteArrayList<Hero> heroes,
      CopyOnWriteArrayList<Enemy> enemies,
      CopyOnWriteArrayList<Bullet> bullets,
      Map map) {
    if (heroes == null || enemies == null || bullets == null || map == null) {
      initGame();
    } else {
      initGame(heroes, enemies, bullets, map);
    }
  }

  private void initGame(
      CopyOnWriteArrayList<Hero> heroes,
      CopyOnWriteArrayList<Enemy> enemies,
      CopyOnWriteArrayList<Bullet> bullets,
      Map map) {
    gameState = new GameState(heroes, enemies, bullets, map);

    loadTexture();

    initBulletUpdaterAndExecutor();
  }

  private void initGame() {
    gameState =
        new GameState(
            new CopyOnWriteArrayList<>(),
            new CopyOnWriteArrayList<>(),
            new CopyOnWriteArrayList<>(),
            initMap());

    initCharacter(Config.INIT_HERO_COUNT, 0, (int) (Config.ROWS / 2), 1);
    initCharacter(Config.INIT_ENEMY_COUNT, (int) (Config.ROWS / 2), (int) Config.ROWS, 2);

    initBulletUpdaterAndExecutor();
  }

  private Map initMap() {
    return new Map((int) Config.ROWS, (int) Config.COLS);
  }

  private void initCharacter(int count, int l, int r, int type) {
    for (int i = 0; i < count; i++) {
      int x = MathUtils.random(l, r - 1);
      int y = (int) (MathUtils.random(Config.COLS));

      while (gameState.getMap().get((int) (x * Config.CELL_SIZE), (int) (y * Config.CELL_SIZE))
          != 0) {
        x = MathUtils.random(l, r - 1);
        y = (int) (MathUtils.random(Config.COLS));
      }

      gameState.getMap().set((int) (x * Config.CELL_SIZE), (int) (y * Config.CELL_SIZE), 1);

      if (type == 1) {
        gameState.getHeroes().add(initHero(x, y, i));
      } else if (type == 2) {
        gameState.getEnemies().add(initEnemy(x, y, i));
      }
    }
  }

  private Hero initHero(int x, int y, int i) {
    TextureManager textureManager = TextureManager.getInstance();

    Hero hero =
        new Hero(
            (int) (x * Config.CELL_SIZE),
            (int) (y * Config.CELL_SIZE),
            Config.HERO_HP,
            Config.HERO_ATK,
            textureManager.getHeroTextures().get(i),
            textureManager.getBulletTexture());
    hero.setGameState(gameState);
    hero.setId("hero" + i);

    return hero;
  }

  private Enemy initEnemy(int x, int y, int i) {
    TextureManager textureManager = TextureManager.getInstance();

    Enemy enemy =
        new Enemy(
            (int) (x * Config.CELL_SIZE),
            (int) (y * Config.CELL_SIZE),
            Config.ENEMY_HP,
            Config.ENEMY_ATK,
            textureManager.getEnemyTextures().get(i),
            textureManager.getBulletTexture());
    enemy.setGameState(gameState);
    enemy.setId("enemy" + i);

    return enemy;
  }

  private void initBulletUpdaterAndExecutor() {
    bulletUpdater = new BulletUpdater(gameState);
    executor =
        new ScheduledThreadPoolExecutor(Config.INIT_ENEMY_COUNT + Config.INIT_HERO_COUNT + 1);
  }

  public void setGameState(GameState gameState) {
    this.gameState = gameState;
    initBulletUpdaterAndExecutor();
  }

  public void start() {
    startHero();
    startEnemy();
    startBullet();
  }

  public void startHero() {
    gameState
        .getHeroes()
        .forEach(
            hero ->
                executor.scheduleWithFixedDelay(
                    hero, 0, Config.INTERVAL_MILLI, TimeUnit.MILLISECONDS));
  }

  public void startEnemy() {
    gameState
        .getEnemies()
        .forEach(
            enemy ->
                executor.scheduleWithFixedDelay(
                    enemy, 0, Config.INTERVAL_MILLI, TimeUnit.MILLISECONDS));
  }

  public void startBullet() {
    executor.scheduleWithFixedDelay(
        bulletUpdater, 0, Config.INTERVAL_MILLI / 40, TimeUnit.MILLISECONDS);
  }

  public void stop() {
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

  public void loadTexture() {
    TextureManager textureManager = TextureManager.getInstance();
    List<Texture> heroTextures = textureManager.getHeroTextures();
    List<Texture> enemyTextures = textureManager.getEnemyTextures();
    Texture bulletTexture = textureManager.getBulletTexture();
    Texture dieTexture = textureManager.getDieTexture();

    for (int i = 0; i < gameState.getHeroes().size(); i++) {
      Hero hero = gameState.getHeroes().get(i);
      hero.setBulletTexture(bulletTexture);
      hero.setCharaterTexture(heroTextures.get(i));
      hero.setSprite(new Sprite(heroTextures.get(i)));
      hero.setDieTexture(dieTexture);
      if (hero.getHp() <= 0) {
        hero.changeDieTexture();
      }
      hero.setGameState(gameState);
    }

    for (int i = 0; i < gameState.getEnemies().size(); i++) {
      Enemy enemy = gameState.getEnemies().get(i);
      enemy.setBulletTexture(bulletTexture);
      enemy.setCharaterTexture(enemyTextures.get(i));
      enemy.setSprite(new Sprite(enemyTextures.get(i)));
      enemy.setDieTexture(dieTexture);
      if (enemy.getHp() <= 0) {
        enemy.changeDieTexture();
      }
      enemy.setGameState(gameState);
    }

    for (Bullet bullet : getGameState().getBullets()) {
      bullet.setSprite(new Sprite(bulletTexture));
      bullet.getSprite().setRotation(bullet.getRotation());
      bullet.setSound(Gdx.audio.newSound(Gdx.files.internal(Config.SHOOT_PATH)));
    }
  }

  public boolean isEnemyEmpty() {
    return gameState.getEnemies().stream().allMatch(Entity::isDead);
  }

  public boolean isHeroEmpty() {
    return gameState.getHeroes().stream().allMatch(Entity::isDead);
  }

  public void handleServerEvent(CharacterMove event) {
    if (event.getType() == CharacterMove.Type.HERO_MOVE) {
      gameState
          .getHeroes()
          .forEach(
              hero -> {
                if (hero.getId().equals(event.getId())) {
                  hero.update(
                      (int) (event.getDirectionX() * Config.CELL_SIZE),
                      (int) (event.getDirectionY() * Config.CELL_SIZE));
                }
              });
    } else if (event.getType() == GameEvent.Type.ENEMY_MOVE) {
      throw new UnsupportedOperationException();
    } else {
      throw new RuntimeException("Unknown event type: " + event.getType());
    }
  }

  public void handleServerEvent(CharacterAttack event) {
    throw new UnsupportedOperationException();
  }

  public void handleServerEvent(HeroAttack event) {
    gameState
        .getHeroes()
        .forEach(
            hero -> {
              if (hero.getId().equals(event.getId())) {
                hero.update(event.getX(), event.getY());
              }
            });
  }

  public void handleClientEvent(CharacterMove event) {
    if (event.getType() == CharacterMove.Type.HERO_MOVE) {
      gameState
          .getHeroes()
          .forEach(
              hero -> {
                if (hero.getId().equals(event.getId())) {
                  hero.move(
                      hero.getX() + event.getDirectionX() * Config.CELL_SIZE,
                      hero.getY() + event.getDirectionY() * Config.CELL_SIZE);
                }
              });
    } else if (event.getType() == GameEvent.Type.ENEMY_MOVE) {
      gameState
          .getEnemies()
          .forEach(
              enemy -> {
                if (enemy.getId().equals(event.getId())) {
                  enemy.move(
                      enemy.getX() + event.getDirectionX() * Config.CELL_SIZE,
                      enemy.getY() + event.getDirectionY() * Config.CELL_SIZE);
                }
              });
    } else {
      throw new RuntimeException("Unknown event type: " + event.getType());
    }
  }

  public void handleClientEvent(CharacterAttack event) {
    gameState
        .getBullets()
        .add(
            new Bullet(
                event.getX(),
                event.getY(),
                event.getAtk(),
                event.getSpeedX(),
                event.getSpeedY(),
                event.getRotation(),
                TextureManager.getInstance().getBulletTexture()));
  }

  public void handleClientEvent(HeroAttack event) {
    throw new UnsupportedOperationException();
  }
}
