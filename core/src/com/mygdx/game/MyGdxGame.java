package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * This class represents the main game application. It extends the Game class from the libGDX
 * framework. It contains a SpriteBatch that is used to draw 2D images. It sets the screen to the
 * main menu screen when the application is created. It also disposes the SpriteBatch when the
 * application is disposed.
 *
 * @author Hades
 */
public class MyGdxGame extends Game {
  public SpriteBatch batch;

  /**
   * This method is called when the application is created. It initializes the SpriteBatch and sets
   * the screen to the start screen.
   */
  @Override
  public void create() {
    batch = new SpriteBatch();
    this.setScreen(new StartScreen(this));
  }

  /**
   * This method is called every frame to render the application. It calls the parent's render
   * method.
   */
  @Override
  public void render() {
    super.render();
  }

  /** This method is called when the application is disposed. It disposes the SpriteBatch. */
  @Override
  public void dispose() {
    batch.dispose();
  }
}
