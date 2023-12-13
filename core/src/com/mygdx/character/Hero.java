package com.mygdx.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.bullet.Bullet;
import com.mygdx.config.Config;
import com.mygdx.matrix.Map;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Hades
 */
@Getter
@Setter
public class Hero extends Character implements Runnable {
  private static final long serialVersionUID = 1L;
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
          && (getX() + dx) / Config.CELL_SIZE < Config.ROWS / 2) {
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

  public void renderBorder(SpriteBatch batch) {
    batch.setColor(Config.BACKGROUND_COLOR);
    batch.draw(
        getSprite().getTexture(),
        getSprite().getX() - Config.BORDER_WIDTH,
        getSprite().getY() - Config.BORDER_WIDTH,
        getSprite().getWidth() + 2 * Config.BORDER_WIDTH,
        getSprite().getHeight() + 2 * Config.BORDER_WIDTH);
    batch.setColor(Color.WHITE);
  }

  @Override
  public void run() {
    if (!isAI) {
      return;
    }

    if (!isDead()) {
      update();
    } else {
      changeDieTexture();
    }
  }
}
