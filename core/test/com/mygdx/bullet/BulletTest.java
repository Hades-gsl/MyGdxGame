package com.mygdx.bullet;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.character.Hero;
import com.mygdx.config.Config;
import com.mygdx.game.MyGdxGame;
import com.mygdx.testRunner.TestRunner;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class BulletTest {
  Bullet bullet;
  static List<Hero> heroList;
  static Texture texture;
  static TestRunner testRunner;

  @BeforeEach
  public void setUp() {
    bullet = new Bullet(0, 0, 1, 1, 1, 0, texture);
  }

  @BeforeAll
  public static void setUpAll() {
    testRunner = new TestRunner(new MyGdxGame());

    texture = new Texture(Gdx.files.internal(Config.BULLET_PATH));

    heroList = new ArrayList<>();
    heroList.add(new Hero(0, 0, 1, 1, texture, texture));
  }

  @AfterAll
  public static void tearDownAll() {
    texture.dispose();

    testRunner.exit();
  }

  @Test
  public void update() {
    bullet.update(heroList);
    assertEquals(bullet.getHp(), 0);
    assertTrue(heroList.get(0).getHp() < 100);
  }

  @ParameterizedTest
  @ArgumentsSource(FloatArgumentsProvider.class)
  public void isDead(float x, float y) {
    bullet.move(x, y);
    assertTrue(bullet.isDead());
  }

  static class FloatArgumentsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
      return Stream.of(
          Arguments.of(-32f, 1f),
          Arguments.of(1f, -32f),
          Arguments.of(1f, Config.MAP_HEIGHT),
          Arguments.of(Config.MAP_WIDTH, 1f));
    }
  }
}
