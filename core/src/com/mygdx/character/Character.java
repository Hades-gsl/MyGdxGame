package com.mygdx.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.bullet.Bullet;
import com.mygdx.config.Config;
import com.mygdx.entity.Entity;
import com.mygdx.matrix.Map;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * This class represents a character in the game. It extends the Entity class. It contains
 * properties for the character's textures and directions. It also contains methods for attacking
 * another character, attacking a position, attacking the character with the minimum health points,
 * moving randomly, and changing the character's texture when it dies. The character can be a hero
 * or an enemy. The character's actions are controlled by AI. The character's texture changes when
 * it dies. The character's position and direction are updated at each frame. The character's attack
 * points are subtracted from the health points of the character it attacks. The character moves
 * randomly by changing its direction at each frame.
 *
 * @author Hades
 */
@Getter
@Setter
public class Character extends Entity {
  private transient Texture bulletTexture;
  private final int[] dirs = {0, 0, 1, 0, -1, 0};
  private transient Texture charaterTexture;
  private transient Texture dieTexture;

  /**
   * Constructor for the Character class. It initializes the character's position, health points,
   * attack points, and textures.
   *
   * @param x The x-coordinate of the character.
   * @param y The y-coordinate of the character.
   * @param hp The health points of the character.
   * @param atk The attack points of the character.
   * @param charactorTexture The texture of the character.
   * @param bulletTexture The texture of the bullet.
   */
  public Character(int x, int y, int hp, int atk, Texture charactorTexture, Texture bulletTexture) {
    super(x, y, hp, atk, charactorTexture);

    this.charaterTexture = charactorTexture;
    this.bulletTexture = bulletTexture;
    this.dieTexture = new Texture(Gdx.files.internal(Config.DIE_PATH));
  }

  /**
   * This method makes the character attack another character. It creates a new bullet and adds it
   * to the list of bullets. The bullet's position is the same as the character's position. The
   * bullet's direction is towards the other character.
   *
   * @param enemy The character to attack.
   * @param bullets The list of bullets.
   */
  public void attack(Character enemy, List<Bullet> bullets) {
    attack(enemy.getX() + Config.CELL_SIZE / 2, enemy.getY() + Config.CELL_SIZE / 2, bullets);
  }

  /**
   * This method makes the character attack a position. It creates a new bullet and adds it to the
   * list of bullets. The bullet's position is the same as the character's position. The bullet's
   * direction is towards the position.
   *
   * @param x The x-coordinate of the position.
   * @param y The y-coordinate of the position.
   * @param bullets The list of bullets.
   */
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

  /**
   * This method makes the character attack the character with the minimum health points. It finds
   * the character with the minimum health points from a list of characters and attacks it.
   *
   * @param bullets The list of bullets.
   * @param characters The list of characters.
   */
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

  /**
   * This method makes the character move randomly. It changes the character's direction at each
   * frame. The character can move up, down, left, or right. The character cannot move outside the
   * game map.
   *
   * @param map The game map.
   * @param isGreaterHalf A flag indicating if the character is in the greater half of the map.
   */
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

  /**
   * This method changes the texture of the character when it dies. If the current texture is the
   * character's original texture, it sets the texture to the die texture.
   */
  public void changeDieTexture() {
    if (getSprite().getTexture() == charaterTexture) {
      getSprite().setTexture(dieTexture);
    }
  }
}
