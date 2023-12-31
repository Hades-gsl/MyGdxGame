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

class HeroTest {
  static TestRunner testRunner;
  Hero hero;
  Map map;
  CopyOnWriteArrayList<Bullet> bullets;
  CopyOnWriteArrayList<Enemy> enemies;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    testRunner = new TestRunner(new MyGdxGame());
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
    testRunner.exit();
  }

  @BeforeEach
  void setUp() {
    hero =
        new Hero(
            0,
            0,
            100,
            10,
            new Texture(Config.HERO_PATH + " (1).png"),
            new Texture(Config.BULLET_PATH));
    bullets = new CopyOnWriteArrayList<>();
    CopyOnWriteArrayList<Hero> heroes = new CopyOnWriteArrayList<>();
    enemies = new CopyOnWriteArrayList<>();
    map = new Map(10, 10);
    hero.setGameState(new GameState(heroes, enemies, bullets, map));
    map.set(0, 0, 1);
  }

  @Test
  void set() {
    assertEquals(map, hero.getGameState().getMap());
    assertEquals(bullets, hero.getGameState().getBullets());
    assertEquals(enemies, hero.getGameState().getEnemies());
  }

  @Test
  void update() {
    hero.update(1f, 1f);
    assertFalse(bullets.isEmpty());
  }

  @Test
  void testUpdate() {
    hero.update((int) Config.CELL_SIZE, (int) Config.CELL_SIZE);
    assertEquals(1, map.get((int) Config.CELL_SIZE, (int) Config.CELL_SIZE));
    assertEquals(0, map.get(0, 0));
  }

  @Test
  void testUpdate1() {
    hero.update();
    assertTrue(bullets.isEmpty());
  }

  @Test
  void setAI() {
    hero.setAI(false);
    assertFalse(hero.isAI());
  }

  @Test
  void isAI() {
    assertTrue(hero.isAI());
    hero.setAI(false);
    assertFalse(hero.isAI());
  }
}
