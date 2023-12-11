package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.config.Config;

/**
 * @author Hades
 */
public class MainMenu extends BaseScreen {

  public MainMenu(final MyGdxGame game) {
    super(game);

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

    TextButton settingButton = new TextButton("Setting", skin);
    settingButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            dispose();
            game.setScreen(new SettingScreen(game));
          }
        });

    TextButton loadButton = new TextButton("Load", skin);
    loadButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            dispose();
            game.setScreen(new ReadRecordScreen(game));
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

    table.add(playButton).size(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT).pad(Config.BUTTON_PAD);
    table.row();
    table.add(settingButton).size(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT).pad(Config.BUTTON_PAD);
    table.row();
    table.add(loadButton).size(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT).pad(Config.BUTTON_PAD);
    table.row();
    table.add(exitButton).size(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT).pad(Config.BUTTON_PAD);
  }

  @Override
  public void show() {
    Gdx.app.log("MainMenu", "start");
  }

  @Override
  public void dispose() {
    super.dispose();

    Gdx.app.log("MainMenu", "end");
  }
}
