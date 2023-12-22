package com.mygdx.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class represents a hero attack event in the game. It contains the id of the hero that
 * attacked and the x and y coordinates of the attack. It extends the GameEvent class and adds
 * additional properties specific to a hero attack. This event is created and dispatched by the
 * Hero#attack method. The id property represents the id of the hero that attacked. The x and y
 * properties represent the coordinates of the attack. The type property is inherited from the
 * GameEvent class and represents the type of the event. In this case, the type is always
 * HERO_ATTACK.
 *
 * @author Hades
 */
@Getter
@Setter
@NoArgsConstructor
public class HeroAttack extends GameEvent {
  /** The id of the hero that attacked. */
  String id;

  /** The x-coordinate of the attack. */
  float x;

  /** The y-coordinate of the attack. */
  float y;

  /**
   * Constructor for HeroAttack. Initializes the attack with the provided id, x and y coordinates,
   * and type.
   *
   * @param id The id of the hero that attacked.
   * @param x The x-coordinate of the attack.
   * @param y The y-coordinate of the attack.
   * @param type The type of the event. Always HERO_ATTACK for this class.
   */
  public HeroAttack(String id, float x, float y, Type type) {
    this.id = id;
    this.x = x;
    this.y = y;
    this.type = type;
  }
}
