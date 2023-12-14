package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.bullet.Bullet;
import com.mygdx.character.Enemy;
import com.mygdx.character.Hero;
import com.mygdx.matrix.Map;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

/**
 * This class represents the replay screen of the game. It extends the AbstractGameScreen class. It
 * is responsible for reading the game state from a file and replaying it.
 *
 * @author Hades
 */
public class ReplayScreen extends AbstractGameScreen {
  private FileInputStream fileIn;
  private ObjectInputStream in;
  private final ShapeRenderer shapeRenderer;
  private boolean isEnd = false;

  /**
   * Constructor for the ReplayScreen class. It initializes the shapeRenderer and opens the file
   * from which the game state will be read.
   *
   * @param game The game instance.
   * @param filePath The path of the file from which the game state will be read.
   */
  public ReplayScreen(MyGdxGame game, String filePath) {
    super(game);

    shapeRenderer = new ShapeRenderer();

    try {
      fileIn = new FileInputStream(filePath);
      in = new ObjectInputStream(fileIn);
    } catch (Exception e) {
      Gdx.app.log("ReplayScreen", "error when open file: " + e.getMessage());
      Gdx.app.exit();
    }
  }

  /**
   * This method reads the game state from the file. It reads the heroes, enemies, bullets and map
   * from the file and loads the textures. If it reaches the end of the file, it shows a dialog
   * indicating the end of the replay and sets isEnd to true. If an error occurs while reading the
   * file, it logs the error and exits the application.
   */
  private void readRecord() {
    try {
      heroes = (List<Hero>) in.readObject();
      enemies = (List<Enemy>) in.readObject();
      bullets = (List<Bullet>) in.readObject();
      map = (Map) in.readObject();
      loadTexture();
    } catch (EOFException e) {
      TextButton okButton = new TextButton("ok", skin);
      okButton.addListener(
          new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
              System.out.println("ok");
              dispose();
              game.setScreen(new MainMenu(game));
            }
          });
      new Dialog("End", skin).text("Game Replay end").button(okButton).show(stage);
      isEnd = true;
    } catch (Exception e) {
      Gdx.app.log("ReplayScreen", "error when read file: " + e.getMessage());
      Gdx.app.exit();
    }
  }

  /**
   * This method is called every frame to render the game state. If the replay is not ended, it
   * reads the next game state from the file. It then calls the parent's render method, renders the
   * map, the heroes, the enemies and the bullets, and updates and draws the stage.
   *
   * @param delta The time in seconds since the last frame.
   */
  @Override
  public void render(float delta) {
    if (!isEnd) {
      readRecord();
    }

    super.render(delta);

    shapeRenderer.setProjectionMatrix(camera.combined);
    map.render(shapeRenderer);
    shapeRenderer.end();

    game.batch.begin();
    heroes.forEach(hero -> hero.render(game.batch));
    enemies.forEach(enemy -> enemy.render(game.batch));
    bullets.forEach(bullet -> bullet.render(game.batch));
    game.batch.end();

    stage.act(delta);
    stage.draw();
  }

  /**
   * This method is called when the screen becomes the current screen. It logs the start of the
   * replay.
   */
  @Override
  public void show() {
    Gdx.app.log("ReplayScreen", "start");
  }

  /**
   * This method is called when the screen is no longer the current screen. It closes the file from
   * which the game state was read, disposes the shapeRenderer and logs the end of the replay.
   */
  @Override
  public void dispose() {
    super.dispose();

    try {
      fileIn.close();
      in.close();
    } catch (IOException e) {
      Gdx.app.log("ReplayScreen", "error when close file: " + e.getMessage());
      Gdx.app.exit();
    }

    shapeRenderer.dispose();

    Gdx.app.log("ReplayScreen", "end");
  }
}
