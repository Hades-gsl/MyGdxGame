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

class HeroTest {
  static TestRunner testRunner;
  Hero hero;
  Map map;
  List<Bullet> bullets;
  List<Enemy> enemies;

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
            new Texture(Constants.HERO_PATH + " (1).png"),
            new Texture(Constants.BULLET_PATH));
    map = new Map(10, 10);
    bullets = new ArrayList<>();
    enemies = new ArrayList<>();
    hero.set(map, bullets, enemies);
    map.set(0, 0, 1);
  }

  @Test
  void set() {
    assertEquals(map, hero.getMap());
    assertEquals(bullets, hero.getBullets());
    assertEquals(enemies, hero.getEnemies());
  }

  @Test
  void update() {
    hero.update(1f, 1f);
    assertFalse(bullets.isEmpty());
  }

  @Test
  void testUpdate() {
    hero.update((int) Constants.CELL_SIZE, (int) Constants.CELL_SIZE);
    assertEquals(1, map.get((int) Constants.CELL_SIZE, (int) Constants.CELL_SIZE));
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
