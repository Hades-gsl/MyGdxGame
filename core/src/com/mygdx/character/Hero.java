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
 * This class represents a hero character in the game. It extends the Character class and implements
 * the Runnable interface for multithreading. It contains properties for the game map, the list of
 * bullets and enemies, and a flag for AI control. It also contains methods for setting the game
 * map, bullets and enemies, updating the hero's state, rendering the hero's border, and running the
 * hero's AI. The hero can attack by clicking, move by keyboard, or be controlled by AI. The hero's
 * AI attacks the enemy with the minimum health points and moves randomly. The hero's border is
 * rendered with a color specified in the game configuration.
 *
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

  /**
   * Constructor for the Hero class. It initializes the hero's position, health points, attack
   * points, and textures.
   *
   * @param x The x-coordinate of the hero.
   * @param y The y-coordinate of the hero.
   * @param hp The health points of the hero.
   * @param atk The attack points of the hero.
   * @param heroTexture The texture of the hero.
   * @param bulletTexture The texture of the bullet.
   */
  public Hero(int x, int y, int hp, int atk, Texture heroTexture, Texture bulletTexture) {
    super(x, y, hp, atk, heroTexture, bulletTexture);

    Gdx.app.log("Hero", "x: " + x + ", y: " + y);
  }

  /**
   * This method sets the game map, bullets and enemies.
   *
   * @param map The game map.
   * @param bullets The list of bullets.
   * @param enemies The list of enemies.
   */
  public void set(Map map, List<Bullet> bullets, List<Enemy> enemies) {
    this.map = map;
    this.bullets = bullets;
    this.enemies = enemies;
  }

  /**
   * This method updates the hero's state by attacking a position.
   *
   * @param x The x-coordinate of the position.
   * @param y The y-coordinate of the position.
   */
  public void update(float x, float y) {
    attack(x, y, bullets);
  }

  /**
   * This method updates the hero's position by moving the hero.
   *
   * @param dx The change in the x-coordinate.
   * @param dy The change in the y-coordinate.
   */
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

  /**
   * This method updates the hero's state by attacking the enemy with the minimum health points and
   * moving randomly.
   */
  public void update() {
    attackMinHp(bullets, enemies);
    synchronized (map) {
      randomMove(map, false);
    }
  }

  /**
   * This method renders the hero's border.
   *
   * @param batch The SpriteBatch to use for drawing.
   */
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

  /**
   * This method runs the hero's AI. If the hero is not controlled by AI or is dead, it does
   * nothing. Otherwise, it updates the hero's state.
   */
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
