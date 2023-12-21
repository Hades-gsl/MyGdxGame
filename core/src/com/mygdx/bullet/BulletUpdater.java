package com.mygdx.bullet;

import com.mygdx.controller.GameState;
import lombok.Setter;

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
@Setter
public class BulletUpdater implements Runnable {
  GameState gameState;

  /**
   * Constructor for the BulletUpdater class. It initializes the list of bullets, heroes, and
   * enemies.
   */
  public BulletUpdater(GameState gameState) {
    this.gameState = gameState;
  }

  /**
   * This method runs the bullet updater. It updates the state of each bullet and removes the dead
   * bullets. The state of a bullet is updated by moving the bullet and checking for collisions with
   * characters. A bullet is dead if it is outside the game map or if it has hit a character.
   */
  @Override
  public void run() {
    List<Bullet> dead = new CopyOnWriteArrayList<>();
    gameState
        .getBullets()
        .forEach(
            bullet -> {
              if (bullet.getSprite().getRotation() > 90 || bullet.getSprite().getRotation() < -90) {
                bullet.update(gameState.getHeroes());
              } else {
                bullet.update(gameState.getEnemies());
              }
              if (bullet.isDead()) {
                dead.add(bullet);
              }
            });
    gameState.getBullets().removeAll(dead);
  }
}
