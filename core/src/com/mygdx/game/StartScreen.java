package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.config.Config;
import com.mygdx.nio.GameClient;
import com.mygdx.nio.GameServer;

/**
 * @author Hades
 */
public class StartScreen extends BaseScreen {

  public StartScreen(MyGdxGame game) {
    super(game);
    initButtons();
  }

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

  @Override
  public void show() {
    Gdx.app.log("StartScreen", "start");
  }

  @Override
  public void dispose() {
    super.dispose();

    Gdx.app.log("StartScreen", "end");
  }
}
