package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.config.Config;

/**
 * @author Hades
 */
public class MainMenu implements Screen {

  final MyGdxGame game;
  private final OrthographicCamera camera;
  private final Stage stage;
  private Table table;
  private final Skin skin;

  public MainMenu(final MyGdxGame game) {
    this.game = game;

    camera = new OrthographicCamera();
    camera.setToOrtho(false, Config.CAMERA_WIDTH, Config.CAMERA_HEIGHT);

    table = new Table();
    table.setFillParent(true);

    stage = new Stage();
    Gdx.input.setInputProcessor(stage);
    stage.addActor(table);

    skin = new Skin(Gdx.files.internal("uiskin/uiskin.json"));

    initButton();
  }

  private void initButton() {
    TextButton playButton = new TextButton("Play", skin);
    playButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            dispose();
            game.setScreen(new GameScreen(game, false));
          }
        });
    table.add(playButton).size(150, 50);

    table.row().padTop(30);

    TextButton exitButton = new TextButton("Exit", skin);
    exitButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            dispose();
            Gdx.app.exit();
          }
        });
    table.add(exitButton).size(150, 50);
  }

  @Override
  public void show() {
    Gdx.app.log("MainMenu", "start");
  }

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

    Gdx.app.log("MainMenu", "end");
  }
}
