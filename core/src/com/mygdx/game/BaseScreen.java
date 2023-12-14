package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.config.Config;

/**
 * This class represents a base screen for the game. It implements the Screen interface. It contains
 * common properties and methods for screens.
 *
 * @author Hades
 */
public class BaseScreen implements Screen {

  protected final MyGdxGame game;
  protected final OrthographicCamera camera;
  protected final Stage stage;
  protected Table table;
  protected final Skin skin;
  protected InputMultiplexer multiplexer;

  /**
   * Constructor for the BaseScreen class. It initializes the game, camera, stage, table, skin and
   * multiplexer.
   *
   * @param game The game instance.
   */
  public BaseScreen(MyGdxGame game) {
    this.game = game;

    camera = new OrthographicCamera();
    camera.setToOrtho(false, Config.CAMERA_WIDTH, Config.CAMERA_HEIGHT);

    skin = new Skin(Gdx.files.internal("uiskin/uiskin.json"));

    table = new Table(skin);
    table.setFillParent(true);

    stage = new Stage();
    multiplexer = new InputMultiplexer();
    multiplexer.addProcessor(stage);
    Gdx.input.setInputProcessor(multiplexer);

    stage.addActor(table);
  }

  @Override
  public void show() {}

  /**
   * This method is called every frame to render the screen. It clears the screen, updates the
   * camera, sets the projection matrix for the batch, acts the stage and draws the stage.
   *
   * @param delta The time in seconds since the last frame.
   */
  @Override
  public void render(float delta) {
    ScreenUtils.clear(Config.BACKGROUND_COLOR);

    camera.update();
    game.batch.setProjectionMatrix(camera.combined);

    stage.act(Gdx.graphics.getDeltaTime());
    stage.draw();
  }

  /**
   * This method is called when the screen is resized. It updates the viewport of the stage.
   *
   * @param width The new width.
   * @param height The new height.
   */
  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  @Override
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void hide() {}

  /** This method is called when the screen is disposed. It disposes the stage and the skin. */
  @Override
  public void dispose() {
    stage.dispose();
    skin.dispose();
  }
}
