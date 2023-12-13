package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.mygdx.config.Config;

/**
 * @author Hades
 */
public class ResultsScreen extends BaseScreen {
  private final String winner;

  public ResultsScreen(final MyGdxGame game, String winner) {
    super(game);

    this.winner = winner;

    draw();

    Music music = Gdx.audio.newMusic(Gdx.files.internal(Config.WIN_PATH));
    music.play();
  }

  private void draw() {
    Label label = new Label(winner + " wins!", skin);
    label.setColor(Color.BLUE);
    label.setAlignment(Align.center);
    label.setFontScale(4);

    TextButton exitButton = new TextButton("Exit", skin);
    exitButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            dispose();
            Gdx.app.exit();
          }
        });

    TextButton backButton = new TextButton("Back", skin);
    backButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            dispose();
            game.setScreen(new MainMenu(game));
          }
        });

    table.add(label).pad(Config.BUTTON_PAD);
    table.row();
    table.add(exitButton).size(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT).pad(Config.BUTTON_PAD);
    table.row();
    table.add(backButton).size(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT).pad(Config.BUTTON_PAD);
  }

  @Override
  public void show() {
    Gdx.app.log("ResultsScreen", "start, winner: " + winner);
  }

  @Override
  public void dispose() {
    super.dispose();

    Gdx.app.log("ResultsScreen", "end");
  }
}
