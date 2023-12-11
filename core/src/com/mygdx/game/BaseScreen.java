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
 * @author Hades
 */
public class BaseScreen implements Screen {

  protected final MyGdxGame game;
  protected final OrthographicCamera camera;
  protected final Stage stage;
  protected Table table;
  protected final Skin skin;
  protected InputMultiplexer multiplexer;

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

  @Override
  public void render(float delta) {
    ScreenUtils.clear(Config.BACKGROUND_COLOR);

    camera.update();
    game.batch.setProjectionMatrix(camera.combined);

    stage.act(Gdx.graphics.getDeltaTime());
    stage.draw();
  }

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

  @Override
  public void dispose() {
    stage.dispose();
    skin.dispose();
  }
}
