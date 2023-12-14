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
 * This class represents the results screen of the game. It extends the BaseScreen class. It
 * displays the winner of the game and provides options to exit the game or go back to the main
 * menu. It also plays a music when the screen is shown.
 *
 * @author Hades
 */
public class ResultsScreen extends BaseScreen {
  private final String winner;

  /**
   * Constructor for the ResultsScreen class. It initializes the game and the winner. It also draws
   * the screen and plays a music.
   *
   * @param game The game instance.
   * @param winner The winner of the game.
   */
  public ResultsScreen(final MyGdxGame game, String winner) {
    super(game);

    this.winner = winner;

    draw();

    Music music = Gdx.audio.newMusic(Gdx.files.internal(Config.WIN_PATH));
    music.play();
  }

  /**
   * This method draws the screen. It creates a label for the winner and buttons for exiting the
   * game and going back to the main menu. It adds them to the table.
   */
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

  /**
   * This method is called when the screen becomes the current screen. It logs the start of the
   * results screen and the winner.
   */
  @Override
  public void show() {
    Gdx.app.log("ResultsScreen", "start, winner: " + winner);
  }

  /**
   * This method is called when the screen is no longer the current screen. It disposes the screen
   * and logs the end of the results screen.
   */
  @Override
  public void dispose() {
    super.dispose();

    Gdx.app.log("ResultsScreen", "end");
  }
}
