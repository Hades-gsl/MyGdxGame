package com.mygdx.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.bullet.Bullet;
import com.mygdx.config.Config;
import com.mygdx.controller.GameState;
import com.mygdx.controller.TextureManager;
import com.mygdx.entity.Entity;
import com.mygdx.event.CharacterAttack;
import com.mygdx.event.CharacterMove;
import com.mygdx.event.GameEvent;
import com.mygdx.map.Map;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@NoArgsConstructor
public class Character extends Entity {
  private transient Texture bulletTexture; // The texture of the bullet that the character fires.
  private final int[] dirs = {
    0, 0, 1, 0, -1, 0
  }; // The possible directions that the character can move in.
  private transient Texture charaterTexture; // The texture of the character.
  private transient Texture dieTexture; // The texture of the character when it dies.
  private String id; // The id of the character.
  protected GameState gameState; // The current game state.

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
    this.dieTexture = TextureManager.getInstance().getDieTexture();
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
   * This method makes the character attack a specific position. It calculates the rotation angle
   * between the character's current position and the target position, and then calculates the
   * bullet's speed in the x and y directions based on this rotation angle. A new bullet is created
   * with these properties and added to the list of bullets. The bullet's position is set to the
   * character's current position, and its attack points are the same as the character's attack
   * points. After the bullet is created, a CharacterAttack event is generated and sent to all
   * observers of the game state.
   *
   * @param x The x-coordinate of the target position.
   * @param y The y-coordinate of the target position.
   * @param bullets The list of bullets in the game.
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

    gameState.notifyObservers(
        new CharacterAttack(
            getX() + Config.CELL_SIZE / 2,
            getY() + Config.CELL_SIZE / 2,
            getAtk(),
            speedX,
            speedY,
            rotation,
            GameEvent.Type.CHARACTER_ATTACK));
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
   * This method makes the character move randomly on the game map. The character can move up, down,
   * left, or right. The character's movement is restricted to the game map and it cannot move
   * outside the game map. The method first generates a random direction for the character's
   * movement. Then it checks if the new position is within the game map and is not occupied. If
   * these conditions are met, the character is moved to the new position. After the character is
   * moved, a CharacterMove event is generated and sent to all observers of the game state.
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

      gameState.notifyObservers(
          new CharacterMove(
              dirs[dir],
              dirs[dir + 1],
              id,
              isGreaterHalf ? GameEvent.Type.ENEMY_MOVE : GameEvent.Type.HERO_MOVE));
    }
  }

  /**
   * This method changes the texture of the character when it dies. If the current texture is the
   * character's original texture, it sets the texture to the die texture.
   */
  public void changeDieTexture() {
    getSprite().setTexture(dieTexture);
  }
}
