package com.mygdx.bullet;

import com.mygdx.character.Enemy;
import com.mygdx.character.Hero;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class is responsible for updating the state of bullets in the game. It implements the
 * Runnable interface for multithreading. It contains properties for the list of bullets, heroes,
 * and enemies. It also contains a method for running the bullet updater. The bullet updater updates
 * the state of each bullet and removes the dead bullets. The state of a bullet is updated by moving
 * the bullet and checking for collisions with characters. A bullet is dead if it is outside the
 * game map or if it has hit a character. The bullet updater is run in a separate thread to avoid
 * blocking the main game thread.
 *
 * @author Hades
 */
public class BulletUpdater implements Runnable {
  private final List<Bullet> bullets;
  private final List<Hero> heroes;
  private final List<Enemy> enemies;

  /**
   * Constructor for the BulletUpdater class. It initializes the list of bullets, heroes, and
   * enemies.
   *
   * @param bullets The list of bullets.
   * @param heroes The list of heroes.
   * @param enemies The list of enemies.
   */
  public BulletUpdater(List<Bullet> bullets, List<Hero> heroes, List<Enemy> enemies) {
    this.bullets = bullets;
    this.heroes = heroes;
    this.enemies = enemies;
  }

  /**
   * This method runs the bullet updater. It updates the state of each bullet and removes the dead
   * bullets. The state of a bullet is updated by moving the bullet and checking for collisions with
   * characters. A bullet is dead if it is outside the game map or if it has hit a character.
   */
  @Override
  public void run() {
    List<Bullet> dead = new CopyOnWriteArrayList<>();
    bullets.forEach(
        bullet -> {
          if (bullet.getSprite().getRotation() > 90 || bullet.getSprite().getRotation() < -90) {
            bullet.update(heroes);
          } else {
            bullet.update(enemies);
          }
          if (bullet.isDead()) {
            dead.add(bullet);
          }
        });
    bullets.removeAll(dead);
  }
}
