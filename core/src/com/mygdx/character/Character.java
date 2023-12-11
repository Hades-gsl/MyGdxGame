package com.mygdx.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.bullet.Bullet;
import com.mygdx.config.Config;
import com.mygdx.entity.Entity;
import com.mygdx.matrix.Map;

import java.util.List;

/**
 * @author Hades
 */
public class Character extends Entity {
  private final Texture bulletTexture;
  private final int[] dirs = {0, 0, 1, 0, -1, 0};
  private final Texture charaterTexture;
  private final Texture dieTexture;

  public Character(int x, int y, int hp, int atk, Texture charactorTexture, Texture bulletTexture) {
    super(x, y, hp, atk, charactorTexture);

    this.charaterTexture = charactorTexture;
    this.bulletTexture = bulletTexture;
    this.dieTexture = new Texture(Gdx.files.internal(Config.DIE_PATH));
  }

  public void attack(Character enemy, List<Bullet> bullets) {
    attack(enemy.getX() + Config.CELL_SIZE / 2, enemy.getY() + Config.CELL_SIZE / 2, bullets);
  }

  public void attack(float x, float y, List<Bullet> bullets) {
    float rotation = MathUtils.atan2(y - getY(), x - getX()) * MathUtils.radiansToDegrees;
    float speedX = MathUtils.cosDeg(rotation) * Config.BULLET_SPEED;
    float speedY = MathUtils.sinDeg(rotation) * Config.BULLET_SPEED;
    bullets.add(
        new Bullet(
            getX() + Config.CELL_SIZE / 2,
            getY() + Config.CELL_SIZE / 2,
            getAtk(),
            speedX,
            speedY,
            rotation,
            bulletTexture));
  }

  public void attackMinHp(List<Bullet> bullets, List<? extends Character> characters) {
    int minHp = 999999;
    Character character = null;
    for (Character c : characters) {
      if (c.getHp() < minHp && !c.isDead()) {
        minHp = c.getHp();
        character = c;
      }
    }
    if (character != null) {
      attack(character, bullets);
    }
  }

  public void randomMove(Map map, boolean isGreaterHalf) {
    int dir = MathUtils.random(4);

    boolean condition =
        isGreaterHalf
            ? getX() / Config.CELL_SIZE + dirs[dir] >= Config.ROWS / 2
            : getX() / Config.CELL_SIZE + dirs[dir] < Config.ROWS / 2;

    if (map.get(
                (int) (getX() + dirs[dir] * Config.CELL_SIZE),
                (int) (getY() + dirs[dir + 1] * Config.CELL_SIZE))
            == 0
        && condition) {
      map.set((int) getX(), (int) getY(), 0);
      map.set(
          (int) (getX() + dirs[dir] * Config.CELL_SIZE),
          (int) (getY() + dirs[dir + 1] * Config.CELL_SIZE),
          1);
      move(getX() + dirs[dir] * Config.CELL_SIZE, getY() + dirs[dir + 1] * Config.CELL_SIZE);
    }
  }

  public void changeDieTexture() {
    if (getSprite().getTexture() == charaterTexture) {
      getSprite().setTexture(dieTexture);
    }
  }

  public Texture getDieTexture() {
    return dieTexture;
  }
}
