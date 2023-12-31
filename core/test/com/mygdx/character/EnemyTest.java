package com.mygdx.character;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.bullet.Bullet;
import com.mygdx.config.Config;
import com.mygdx.controller.GameState;
import com.mygdx.game.MyGdxGame;
import com.mygdx.map.Map;
import com.mygdx.testRunner.TestRunner;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EnemyTest {
  static TestRunner testRunner;
  Enemy enemy;

  @BeforeAll
  static void setUpAll() {
    testRunner = new TestRunner(new MyGdxGame());
  }

  @AfterAll
  static void tearDownAll() {
    testRunner.exit();
  }

  @BeforeEach
  void setUp() {
    enemy =
        new Enemy(
            0,
            0,
            100,
            10,
            new Texture(Config.ENEMY_PATH + " (1).png"),
            new Texture(Config.BULLET_PATH));
  }

  @Test
  void set() {
    CopyOnWriteArrayList<Bullet> bullets = new CopyOnWriteArrayList<>();
    CopyOnWriteArrayList<Hero> heroes = new CopyOnWriteArrayList<>();
    CopyOnWriteArrayList<Enemy> enemies = new CopyOnWriteArrayList<>();
    Map map = new Map(10, 10);
    enemy.setGameState(new GameState(heroes, enemies, bullets, map));
    assertEquals(map, enemy.getGameState().getMap());
    assertEquals(bullets, enemy.getGameState().getBullets());
    assertEquals(heroes, enemy.getGameState().getHeroes());
    assertEquals(enemies, enemy.getGameState().getEnemies());
  }

  @Test
  void update() {
    CopyOnWriteArrayList<Bullet> bullets = new CopyOnWriteArrayList<>();
    CopyOnWriteArrayList<Hero> heroes = new CopyOnWriteArrayList<>();
    CopyOnWriteArrayList<Enemy> enemies = new CopyOnWriteArrayList<>();
    Map map = new Map(10, 10);
    enemy.setGameState(new GameState(heroes, enemies, bullets, map));
    enemy.update();
    assertTrue(bullets.isEmpty());
  }
}
