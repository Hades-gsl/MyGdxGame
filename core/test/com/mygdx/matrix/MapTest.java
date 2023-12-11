package com.mygdx.matrix;

import static org.junit.jupiter.api.Assertions.*;

import com.mygdx.config.Config;
import com.mygdx.game.MyGdxGame;
import com.mygdx.testRunner.TestRunner;
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

class MapTest {
  static TestRunner testRunner;
  Map map;

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
    map = new Map(10, 10);
  }

  @ParameterizedTest
  @ArgumentsSource(MapTestProvider.class)
  void get(int x, int y, int expected) {
    assertEquals(
        expected, map.get((int) (x * Config.CELL_SIZE), (int) (y * Config.CELL_SIZE)));
  }

  static class MapTestProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
      return Stream.of(
          Arguments.of(0, 0, 0),
          Arguments.of(9, 9, 0),
          Arguments.of(-1, -1, 2),
          Arguments.of(10, 10, 2));
    }
  }

  @Test
  void set() {
    map.set(0, 0, 1);
    assertEquals(1, map.get(0, 0));
  }
}
