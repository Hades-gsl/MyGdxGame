package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.config.Config;

/**
 * @author Hades
 */
public class ResultsScreen implements Screen {
  private final MyGdxGame game;
  private final String winner;
  private final Stage stage;
  private final Table table;
  private final OrthographicCamera camera;

  public ResultsScreen(final MyGdxGame game, String winner) {
    this.game = game;
    this.winner = winner;

    camera = new OrthographicCamera();
    camera.setToOrtho(false, Config.CAMERA_WIDTH, Config.CAMERA_HEIGHT);

    table = new Table();
    table.setFillParent(true);

    stage = new Stage();
    Gdx.input.setInputProcessor(stage);
    stage.addActor(table);

    draw();

    Music music = Gdx.audio.newMusic(Gdx.files.internal(Config.WIN_PATH));
    music.play();
  }

  private void draw() {
    Skin skin = new Skin(Gdx.files.internal("uiskin/uiskin.json"));

    Label label = new Label(winner + " wins!", skin);
    label.setColor(Color.BLUE);
    label.setAlignment(Align.center);
    label.setFontScale(4);
    table.add(label);

    table.row().padTop(40);

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
    Gdx.app.log("ResultsScreen", "start, winner: " + winner);
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
  public void resize(int width, int height) {}

  @Override
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void hide() {}

  @Override
  public void dispose() {
    stage.dispose();

    Gdx.app.log("ResultsScreen", "end");
  }
}
