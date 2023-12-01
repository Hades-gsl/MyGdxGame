package com.mygdx.character;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.bullet.Bullet;
import com.mygdx.constants.Constants;
import com.mygdx.game.MyGdxGame;
import com.mygdx.matrix.Map;
import com.mygdx.testRunner.TestRunner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
            new Texture(Constants.ENEMY_PATH + " (1).png"),
            new Texture(Constants.BULLET_PATH));
  }

  @Test
  void set() {
    List<Bullet> bullets = new ArrayList<>();
    List<Hero> heroes = new ArrayList<>();
    Map map = new Map(10, 10);
    enemy.set(map, bullets, heroes);
    assertEquals(map, enemy.getMap());
    assertEquals(bullets, enemy.getBullets());
    assertEquals(heroes, enemy.getHeroes());
  }

  @Test
  void update() {
    List<Bullet> bullets = new ArrayList<>();
    List<Hero> heroes = new ArrayList<>();
    Map map = new Map(10, 10);
    enemy.set(map, bullets, heroes);
    enemy.update();
    assertTrue(bullets.isEmpty());
  }
}
