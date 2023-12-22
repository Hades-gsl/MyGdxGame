package com.mygdx.bullet;

import com.mygdx.controller.GameState;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class is responsible for updating the state of bullets in the game. It implements the
 * Runnable interface for multithreading. It contains properties for the current game state, which
 * includes the list of bullets, heroes, and enemies. It also contains a method for running the
 * bullet updater. The bullet updater updates the state of each bullet and removes the dead bullets.
 * The state of a bullet is updated by moving the bullet and checking for collisions with
 * characters. A bullet is dead if it is outside the game map or if it has hit a character. The
 * bullet updater is run in a separate thread to avoid blocking the main game thread.
 *
 * @author Hades
 */
@Setter
public class BulletUpdater implements Runnable {
  // The current game state, which includes the list of bullets, heroes, and enemies.
  GameState gameState;

  /**
   * Constructor for the BulletUpdater class. It initializes the current game state.
   *
   * @param gameState The current game state.
   */
  public BulletUpdater(GameState gameState) {
    this.gameState = gameState;
  }

  /**
   * This method runs the bullet updater. It updates the state of each bullet and removes the dead
   * bullets. The state of a bullet is updated by moving the bullet and checking for collisions with
   * characters. A bullet is dead if it is outside the game map or if it has hit a character. The
   * method also checks the rotation of the bullet to determine whether it should collide with
   * heroes or enemies.
   */
  @Override
  public void run() {
    // A list to store the bullets that are dead.
    List<Bullet> dead = new CopyOnWriteArrayList<>();
    gameState
        .getBullets()
        .forEach(
            bullet -> {
              // If the bullet's rotation is greater than 90 or less than -90, it collides with
              // heroes.
              // Otherwise, it collides with enemies.
              if (bullet.getSprite().getRotation() > 90 || bullet.getSprite().getRotation() < -90) {
                bullet.update(gameState.getHeroes());
              } else {
                bullet.update(gameState.getEnemies());
              }
              // If the bullet is dead, add it to the dead list.
              if (bullet.isDead()) {
                dead.add(bullet);
              }
            });
    // Remove all the dead bullets from the game state.
    gameState.getBullets().removeAll(dead);
  }
}
