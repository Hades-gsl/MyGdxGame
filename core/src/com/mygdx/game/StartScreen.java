package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.config.Config;
import com.mygdx.nio.GameClient;
import com.mygdx.nio.GameServer;

/**
 * This class represents the start screen of the game. It extends the BaseScreen class and provides
 * options for single player, multiplayer, and server modes. It uses LibGDX's scene2d.ui package for
 * the user interface. It uses TextButton objects for the mode selection buttons. Each button has a
 * ChangeListener that handles the button click event. When a button is clicked, the screen is
 * disposed and the game is switched to the selected mode. The single player mode switches to the
 * main menu screen. The multiplayer mode switches to the game client screen. The server mode
 * switches to the game server screen.
 *
 * @author Hades
 */
public class StartScreen extends BaseScreen {
  /**
   * Constructor for StartScreen. Initializes the start screen with the provided game instance.
   * Calls the initButtons method to initialize the mode selection buttons.
   *
   * @param game The game instance.
   */
  public StartScreen(MyGdxGame game) {
    super(game);
    initButtons();
  }

  /**
   * Initializes the mode selection buttons. Each button is a TextButton object with a
   * ChangeListener. The ChangeListener handles the button click event by disposing the screen and
   * switching the game to the selected mode.
   */
  private void initButtons() {
    TextButton singlePlayerButton = new TextButton("Single Player", skin);
    singlePlayerButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            // Handle single player mode selection
            dispose();
            game.setScreen(new MainMenu(game));
          }
        });

    TextButton multiPlayerButton = new TextButton("Multiplayer", skin);
    multiPlayerButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            // Handle multi player mode selection
            dispose();
            game.setScreen(new GameClient(game, Config.SERVER_ADDRESS, Config.SERVER_PORT));
          }
        });

    TextButton serverButton = new TextButton("Server", skin);
    serverButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            // Handle server mode selection
            dispose();
            try {
              game.setScreen(new GameServer(game, Config.SERVER_PORT));
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        });

    table
        .add(singlePlayerButton)
        .size(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT)
        .pad(Config.BUTTON_PAD);
    table.row();
    table
        .add(multiPlayerButton)
        .size(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT)
        .pad(Config.BUTTON_PAD);
    table.row();
    table.add(serverButton).size(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT).pad(Config.BUTTON_PAD);
  }

  /**
   * This method is called when the start screen is shown. It logs the start of the start screen.
   */
  @Override
  public void show() {
    Gdx.app.log("StartScreen", "start");
  }

  /**
   * Disposes of all resources used by the start screen. This method is called when the start screen
   * is no longer needed. It also logs the end of the start screen.
   */
  @Override
  public void dispose() {
    super.dispose();

    Gdx.app.log("StartScreen", "end");
  }
}
