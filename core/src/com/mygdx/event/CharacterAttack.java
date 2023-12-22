package com.mygdx.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class represents a character attack event in the game. It contains the x and y coordinates
 * of the attack, the attack power, the speed of the attack in the x and y directions, and the
 * rotation of the attack. It extends the GameEvent class and adds additional properties specific to
 * a character attack. It is used when a character in the game performs an attack. This event is
 * created and dispatched by the Character#attack method.
 *
 * @author Hades
 */
@Getter
@Setter
@NoArgsConstructor
public class CharacterAttack extends GameEvent {
  /** The x-coordinate of the attack. */
  float x;

  /** The y-coordinate of the attack. */
  float y;

  /** The attack power of the attack. */
  int atk;

  /** The speed of the attack in the x direction. */
  float speedX;

  /** The speed of the attack in the y direction. */
  float speedY;

  /** The rotation of the attack. */
  float rotation;

  /**
   * Constructor for CharacterAttack. Initializes the attack with the provided x and y coordinates,
   * attack power, speed, rotation, and type.
   *
   * @param x The x-coordinate of the attack.
   * @param y The y-coordinate of the attack.
   * @param atk The attack power of the attack.
   * @param speedX The speed of the attack in the x direction.
   * @param speedY The speed of the attack in the y direction.
   * @param rotation The rotation of the attack.
   * @param type The type of the event.
   */
  public CharacterAttack(
      float x, float y, int atk, float speedX, float speedY, float rotation, Type type) {
    this.x = x;
    this.y = y;
    this.atk = atk;
    this.speedX = speedX;
    this.speedY = speedY;
    this.rotation = rotation;
    this.type = type;
  }
}
