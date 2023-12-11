package com.mygdx.character;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.bullet.Bullet;
import com.mygdx.config.config;
import com.mygdx.game.MyGdxGame;
import com.mygdx.matrix.Map;
import com.mygdx.testRunner.TestRunner;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.*;

class CharacterTest {
  static TestRunner testRunner;
  Character character;
  Character enemy;

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
    character =
        new Character(
            0,
            0,
            100,
            10,
            new Texture(config.HERO_PATH + " (1).png"),
            new Texture(config.BULLET_PATH));
    enemy =
        new Character(
            0,
            0,
            100,
            10,
            new Texture(config.HERO_PATH + " (1).png"),
            new Texture(config.BULLET_PATH));
  }

  @Test
  void attack() {
    List<Bullet> bullets = new ArrayList<>();
    character.attack(enemy, bullets);
    assertFalse(bullets.isEmpty());
  }

  @Test
  void testAttack() {
    List<Bullet> bullets = new ArrayList<>();
    character.attack(0, 0, bullets);
    assertFalse(bullets.isEmpty());
  }

  @Test
  void attackMinHp() {
    Character enemy2 =
        new Character(
            0,
            0,
            50,
            10,
            new Texture(config.HERO_PATH + " (1).png"),
            new Texture(config.BULLET_PATH));
    Character enemy3 =
        new Character(
            0,
            0,
            75,
            10,
            new Texture(config.HERO_PATH + " (1).png"),
            new Texture(config.BULLET_PATH));
    assertEquals(100, enemy.getHp());
    assertEquals(50, enemy2.getHp());
    assertEquals(75, enemy3.getHp());
    List<Bullet> bullets = new ArrayList<>();
    List<Character> characters = new ArrayList<>();
    characters.add(enemy3);
    characters.add(enemy2);
    characters.add(enemy);
    character.attackMinHp(bullets, characters);
    assertFalse(bullets.isEmpty());
  }

  @Test
  void randomMove() {
    Map map = new Map((int) config.ROWS, (int) config.COLS);
    map.set((int) character.getX(), (int) character.getY(), 1);
    character.randomMove(map, true);
    assertEquals(1, map.get((int) character.getX(), (int) character.getY()));
  }

  @Test
  void getDieTexture() {
    assertEquals(new Texture(config.DIE_PATH).toString(), character.getDieTexture().toString());
  }

  @Test
  void changeDieTexture() {
    character.changeDieTexture();
    assertEquals(new Texture(config.DIE_PATH).toString(), character.getDieTexture().toString());
  }
}
