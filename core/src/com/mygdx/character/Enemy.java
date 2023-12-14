package com.mygdx.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.bullet.Bullet;
import com.mygdx.matrix.Map;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * This class represents an enemy character in the game. It extends the Character class and
 * implements the Runnable interface for multithreading. It contains properties for the game map,
 * the list of bullets and heroes. It also contains methods for setting the game map, bullets and
 * heroes, updating the enemy's state, and running the enemy's AI. The enemy's AI attacks the hero
 * with the minimum health points and moves randomly. The enemy can be controlled by AI.
 *
 * @author Hades
 */
@Getter
@Setter
public class Enemy extends Character implements Runnable {
  private static final long serialVersionUID = 1L;
  private Map map;
  private List<Bullet> bullets;
  private List<Hero> heroes;

  /**
   * Constructor for the Enemy class. It initializes the enemy's position, health points, attack
   * points, and textures.
   *
   * @param x The x-coordinate of the enemy.
   * @param y The y-coordinate of the enemy.
   * @param hp The health points of the enemy.
   * @param atk The attack points of the enemy.
   * @param enemyTexture The texture of the enemy.
   * @param bulletTexture The texture of the bullet.
   */
  public Enemy(int x, int y, int hp, int atk, Texture enemyTexture, Texture bulletTexture) {
    super(x, y, hp, atk, enemyTexture, bulletTexture);
    Gdx.app.log("Enemy", "x: " + x + ", y: " + y);
  }

  /**
   * This method sets the game map, bullets and heroes.
   *
   * @param map The game map.
   * @param bullets The list of bullets.
   * @param heroes The list of heroes.
   */
  public void set(Map map, List<Bullet> bullets, List<Hero> heroes) {
    this.map = map;
    this.bullets = bullets;
    this.heroes = heroes;
  }

  /**
   * This method updates the enemy's state by attacking the hero with the minimum health points and
   * moving randomly.
   */
  public void update() {
    attackMinHp(bullets, heroes);
    synchronized (map) {
      randomMove(map, true);
    }
  }

  /**
   * This method runs the enemy's AI. If the enemy is dead, it changes the enemy's texture.
   * Otherwise, it updates the enemy's state.
   */
  @Override
  public void run() {
    if (!isDead()) {
      update();
    } else {
      changeDieTexture();
    }
  }
}
