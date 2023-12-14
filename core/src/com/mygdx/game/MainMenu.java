package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.mygdx.config.Config;

/**
 * This class represents the main menu screen of the game. It extends the BaseScreen class. It
 * contains buttons for starting a new game, changing the map, loading a game, viewing a replay and
 * exiting the game.
 *
 * @author Hades
 */
public class MainMenu extends BaseScreen {
  /**
   * Constructor for the MainMenu class. It initializes the game and the buttons.
   *
   * @param game The game instance.
   */
  public MainMenu(final MyGdxGame game) {
    super(game);

    initButton();
  }

  /** This method initializes the buttons. It adds them to the table and sets their listeners. */
  private void initButton() {
    Label label = new Label("Main Menu", skin);
    label.setColor(Color.BLUE);
    label.setAlignment(Align.center);
    label.setFontScale(3);

    TextButton playButton = new TextButton("Play", skin);
    playButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            dispose();
            game.setScreen(new GameScreen(game, false, null, null, null, null));
          }
        });

    TextButton mapButton = new TextButton("Change Map", skin);
    mapButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            dispose();
            game.setScreen(new ChangeMapScreen(game));
          }
        });

    TextButton loadButton = new TextButton("Load", skin);
    loadButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            dispose();
            game.setScreen(new ReadRecordScreen(game, ReadRecordScreen.GameAction.LOAD_GAME));
          }
        });

    TextButton replayButton = new TextButton("Replay", skin);
    replayButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            dispose();
            game.setScreen(new ReadRecordScreen(game, ReadRecordScreen.GameAction.VIEW_REPLAY));
          }
        });

    TextButton exitButton = new TextButton("Exit", skin);
    exitButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            dispose();
            Gdx.app.exit();
          }
        });

    table.add(label).pad(Config.BUTTON_PAD);
    table.row();
    table.add(playButton).size(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT).pad(Config.BUTTON_PAD);
    table.row();
    table.add(mapButton).size(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT).pad(Config.BUTTON_PAD);
    table.row();
    table.add(loadButton).size(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT).pad(Config.BUTTON_PAD);
    table.row();
    table.add(replayButton).size(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT).pad(Config.BUTTON_PAD);
    table.row();
    table.add(exitButton).size(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT).pad(Config.BUTTON_PAD);
  }

  /**
   * This method is called when the screen becomes the current screen. It logs the start of the main
   * menu screen.
   */
  @Override
  public void show() {
    Gdx.app.log("MainMenu", "start");
  }

  /**
   * This method is called when the screen is no longer the current screen. It disposes the screen
   * and logs the end of the main menu screen.
   */
  @Override
  public void dispose() {
    super.dispose();

    Gdx.app.log("MainMenu", "end");
  }
}
