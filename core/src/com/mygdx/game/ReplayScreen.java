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
import java.awt.*;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

/**
 * @author Hades
 */
public class ReplayScreen extends AbstractGameScreen {
  private FileInputStream fileIn;
  private ObjectInputStream in;
  ShapeRenderer shapeRenderer;
  private boolean isEnd = false;

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

  @Override
  public void show() {
    Gdx.app.log("ReplayScreen", "start");
  }

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

    Gdx.app.log("ReplayScreen", "end");
  }
}
