package com.mygdx.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.bullet.Bullet;
import com.mygdx.constants.Constants;
import com.mygdx.matrix.Map;
import java.util.List;

/**
 * @author Hades
 */
public class Hero extends Character implements Runnable {
  private Map map;
  private List<Bullet> bullets;
  private List<Enemy> enemies;
  private boolean isAI = true;

  public Hero(int x, int y, int hp, int atk, Texture heroTexture, Texture bulletTexture) {
    super(x, y, hp, atk, heroTexture, bulletTexture);

    Gdx.app.log("Hero", "x: " + x + ", y: " + y);
  }

  public void set(Map map, List<Bullet> bullets, List<Enemy> enemies) {
    this.map = map;
    this.bullets = bullets;
    this.enemies = enemies;
  }

  // attack by click
  public void update(float x, float y) {
    attack(x, y, bullets);
  }

  // update position by keyboard
  public void update(int dx, int dy) {
    synchronized (map) {
      if (map.get((int) (getX() + dx), (int) (getY() + dy)) == 0
          && (getX() + dx) / Constants.CELL_SIZE < Constants.ROWS / 2) {
        map.set((int) getX(), (int) getY(), 0);
        map.set((int) (getX() + dx), (int) (getY() + dy), 1);
        move(getX() + dx, getY() + dy);
      }
    }
  }

  // AI
  public void update() {
    attackMinHp(bullets, enemies);
    synchronized (map) {
      randomMove(map, false);
    }
  }

  public void setAI(boolean isAI){
    this.isAI = isAI;
  }

  public void renderBorder(SpriteBatch batch) {
    batch.setColor(Constants.BACKGROUND_COLOR);
    batch.draw(
        getSprite().getTexture(),
        getSprite().getX() - Constants.BORDER_WIDTH,
        getSprite().getY() - Constants.BORDER_WIDTH,
        getSprite().getWidth() + 2 * Constants.BORDER_WIDTH,
        getSprite().getHeight() + 2 * Constants.BORDER_WIDTH);
    batch.setColor(Color.WHITE);
  }

  @Override
  public void run() {
    if (!isAI) {
        return;
    }

    if (!isDead()) {
      update();
    }else{
      changeDieTexture();
    }
  }
}
