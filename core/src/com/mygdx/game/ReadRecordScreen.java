package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.bullet.Bullet;
import com.mygdx.character.Enemy;
import com.mygdx.character.Hero;
import com.mygdx.config.Config;
import java.io.*;

/**
 * @author Hades
 */
public class ReadRecordScreen extends BaseScreen {
  java.util.List<Hero> heroList;
  java.util.List<Enemy> enemyList;
  java.util.List<Bullet> bulletList;

  public ReadRecordScreen(MyGdxGame game) {
    super(game);

    initField();
  }

  private void initField() {
    Label titleLabel = new Label("Read Record", skin);
    table.add(titleLabel).colspan(2).center().pad(Config.BUTTON_PAD);
    table.row();

    TextButton okButton = new TextButton("ok", skin);
    okButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            dispose();
            game.setScreen(new GameScreen(game, false, heroList, enemyList, bulletList));
          }
        });

    String[] files = new File(Config.RECORD_PATH).list();
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
                try {
                  readRecord(Config.RECORD_PATH + record);
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

  private void readRecord(String filename) throws IOException, ClassNotFoundException {
    FileInputStream fileIn = new FileInputStream(filename);
    ObjectInputStream in = new ObjectInputStream(fileIn);
    heroList = (java.util.List<Hero>) in.readObject();
    enemyList = (java.util.List<Enemy>) in.readObject();
    bulletList = (java.util.List<Bullet>) in.readObject();
    in.close();
    fileIn.close();
  }

  @Override
  public void show() {
    Gdx.app.log("LoadGameScreen", "start");
  }

  @Override
  public void dispose() {
    super.dispose();

    Gdx.app.log("LoadGameScreen", "end");
  }
}
