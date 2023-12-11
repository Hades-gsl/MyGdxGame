package com.mygdx.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.config.Config;
import com.mygdx.game.MyGdxGame;
import com.mygdx.testRunner.TestRunner;
import org.junit.jupiter.api.*;

class EntityTest {
  static TestRunner testRunner;
  Entity entity;

  static class EntityClass extends Entity {
    public EntityClass(float x, float y, int hp, int atk, Texture texture) {
      super(x, y, hp, atk, texture);
    }
  }

  @BeforeAll
  public static void setUpAll() {
    testRunner = new TestRunner(new MyGdxGame());
  }

  @BeforeEach
  void setUp() {
    entity = new EntityClass(0, 0, 100, 10, new Texture(Config.HERO_PATH + " (1).png"));
  }

  @AfterAll
  static void tearDownAll() {
    testRunner.exit();
  }

  @Test
  void getX() {
    assertEquals(entity.getX(), 0);
  }

  @Test
  void getY() {
    assertEquals(entity.getY(), 0);
  }

  @Test
  void getHp() {
    assertEquals(entity.getHp(), 100);
  }

  @Test
  void getAtk() {
    assertEquals(entity.getAtk(), 10);
  }

  @Test
  void getSprite() {
    assertNotNull(entity.getSprite());
    assertEquals(entity.getSprite().getX(), 0);
    assertEquals(entity.getSprite().getY(), 0);
  }

  @Test
  void setX() {
    entity.setX(1);
    assertEquals(entity.getX(), 1);
  }

  @Test
  void setY() {
    entity.setY(1);
    assertEquals(entity.getY(), 1);
  }

  @Test
  void move() {
    entity.move(1, 1);
    assertEquals(entity.getX(), 1);
    assertEquals(entity.getY(), 1);
  }

  @Test
  void setHp() {
    entity.setHp(1);
    assertEquals(entity.getHp(), 1);
  }

  @Test
  void setAtk() {
    entity.setAtk(1);
    assertEquals(entity.getAtk(), 1);
  }

  @Test
  void isDead() {
    assertFalse(entity.isDead());
    entity.setHp(0);
    assertTrue(entity.isDead());
  }

  @Test
  void getBound() {
    assertNotNull(entity.getBound());
    assertEquals(entity.getBound().x, 0);
    assertEquals(entity.getBound().y, 0);
  }
}
