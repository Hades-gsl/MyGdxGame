package com.mygdx.game;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.Game;
import com.mygdx.testRunner.TestRunner;
import org.junit.jupiter.api.*;

class GameScreenTest {
  static TestRunner testRunner;
  static Game game;
  GameScreen gameScreen;

  @BeforeAll
  static void setUpAll() {
    game = new MyGdxGame();
    testRunner = new TestRunner(game);
  }

  @AfterAll
  static void tearDownAll() {
    testRunner.exit();
  }

  @BeforeEach
  void setUp() {
    gameScreen = new GameScreen((MyGdxGame) game, true, null, null, null);
  }

  @Test
  void show() {
    assertNotNull(gameScreen);
    gameScreen.show();
  }
}
