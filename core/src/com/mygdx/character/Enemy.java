package com.mygdx.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.bullet.Bullet;
import com.mygdx.matrix.Map;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Hades
 */
@Getter
@Setter
public class Enemy extends Character implements Runnable {
  private Map map;
  private List<Bullet> bullets;
  private List<Hero> heroes;

  public Enemy(int x, int y, int hp, int atk, Texture enemyTexture, Texture bulletTexture) {
    super(x, y, hp, atk, enemyTexture, bulletTexture);

    Gdx.app.log("Enemy", "x: " + x + ", y: " + y);
  }

  public void set(Map map, List<Bullet> bullets, List<Hero> heroes) {
    this.map = map;
    this.bullets = bullets;
    this.heroes = heroes;
  }

  // AI
  public void update() {
    attackMinHp(bullets, heroes);
    synchronized (map) {
      randomMove(map, true);
    }
  }

  @Override
  public void run() {
    if (!isDead()) {
      update();
    } else {
      changeDieTexture();
    }
  }
}
