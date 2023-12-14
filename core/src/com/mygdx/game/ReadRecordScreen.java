package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.bullet.Bullet;
import com.mygdx.character.Enemy;
import com.mygdx.character.Hero;
import com.mygdx.config.Config;
import com.mygdx.matrix.Map;

import java.io.*;

/**
 * This class represents the screen for reading game records. It extends the BaseScreen class. It
 * contains a list of heroes, enemies, and bullets, and a map. It also contains an enumeration for
 * the game action (load game or view replay). It has a label and a path for the game action. It has
 * methods for initializing the fields, reading the record, and showing and disposing the screen.
 *
 * @author Hades
 */
public class ReadRecordScreen extends BaseScreen {
  java.util.List<Hero> heroList;
  java.util.List<Enemy> enemyList;
  java.util.List<Bullet> bulletList;
  Map map;

  /** This enumeration represents the game action. It can be either load game or view replay. */
  public enum GameAction {
    LOAD_GAME,
    VIEW_REPLAY
  }

  private final String labelText;
  private final String path;

  /**
   * Constructor for the ReadRecordScreen class. It initializes the game, the label and the path
   * based on the game action. It also initializes the fields.
   *
   * @param game The game instance.
   * @param action The game action.
   */
  public ReadRecordScreen(MyGdxGame game, GameAction action) {
    super(game);

    if (action == GameAction.LOAD_GAME) {
      labelText = "Load Game";
      path = Config.RECORD_PATH;
    } else {
      labelText = "View Replay";
      path = Config.REPLAY_PATH;
    }

    initField();
  }

  /**
   * This method initializes the fields. It creates the label and the buttons, adds them to the
   * table, and sets their listeners.
   */
  private void initField() {
    Label titleLabel = new Label(labelText, skin);
    table.add(titleLabel).colspan(2).center().pad(Config.BUTTON_PAD);
    table.row();

    final String[] filaPath = new String[1];

    TextButton okButton = new TextButton("ok", skin);
    okButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            dispose();
            if ("Load Game".equals(labelText)) {
              game.setScreen(new GameScreen(game, false, heroList, enemyList, bulletList, map));
            } else {
              game.setScreen(new ReplayScreen(game, filaPath[0]));
            }
          }
        });

    String[] files = new File(path).list();
    assert files != null;
    for (int i = 0; i < Config.MAX_RECORD; i++) {
      TextButton recordButton = new TextButton("", skin);
      if (i < files.length) {
        recordButton.setText(files[i]);
        recordButton.addListener(
            new ChangeListener() {
              @Override
              public void changed(ChangeEvent event, Actor actor) {
                String record = recordButton.getText().toString();
                filaPath[0] = path + record;
                try {
                  readRecord(path + record);
                  new Dialog("Success", skin)
                      .text("Read record successfully")
                      .button(okButton)
                      .show(stage);
                } catch (Exception e) {
                  new Dialog("Error", skin)
                      .text("Read record failed, error: " + e.getMessage())
                      .button("OK")
                      .show(stage);
                }
              }
            });
      }
      table.add(recordButton).colspan(2).center().pad(Config.BUTTON_PAD);
      table.row();
    }

    TextButton backButton = new TextButton("Back", skin);
    backButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            dispose();
            game.setScreen(new MainMenu(game));
          }
        });

    table.add(backButton).colspan(2).center().pad(Config.BUTTON_PAD);
  }

  /**
   * This method reads the record from the file. It reads the list of heroes, enemies, and bullets,
   * and the map from the file. It also changes the configuration based on the map size.
   *
   * @param filename The name of the file from which the record will be read.
   * @throws IOException If an I/O error occurs.
   * @throws ClassNotFoundException If the class of a serialized object cannot be found.
   */
  private void readRecord(String filename) throws IOException, ClassNotFoundException {
    FileInputStream fileIn = new FileInputStream(filename);
    ObjectInputStream in = new ObjectInputStream(fileIn);
    heroList = (java.util.List<Hero>) in.readObject();
    enemyList = (java.util.List<Enemy>) in.readObject();
    bulletList = (java.util.List<Bullet>) in.readObject();
    map = (Map) in.readObject();
    in.close();
    fileIn.close();

    Config.changeConfig(map.getMatrix().length, map.getMatrix()[0].length);
  }

  /**
   * This method is called when the screen becomes the current screen. It logs the start of the load
   * game screen.
   */
  @Override
  public void show() {
    Gdx.app.log("LoadGameScreen", "start");
  }

  /**
   * This method is called when the screen is no longer the current screen. It disposes the screen
   * and logs the end of the load game screen.
   */
  @Override
  public void dispose() {
    super.dispose();

    Gdx.app.log("LoadGameScreen", "end");
  }
}
