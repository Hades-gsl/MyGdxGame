package com.mygdx.bullet;

import com.mygdx.character.Enemy;
import com.mygdx.character.Hero;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Hades
 */
public class BulletUpdater implements Runnable {
  private final List<Bullet> bullets;
  private final List<Hero> heroes;
  private final List<Enemy> enemies;

  public BulletUpdater(List<Bullet> bullets, List<Hero> heroes, List<Enemy> enemies) {
    this.bullets = bullets;
    this.heroes = heroes;
    this.enemies = enemies;
  }

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
