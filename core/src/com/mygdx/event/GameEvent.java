package com.mygdx.event;

import lombok.Getter;

/**
 * This class represents a game event. It is a base class for other specific game event classes. It
 * contains an enumeration of the different types of game events. The type of the event is stored in
 * the 'type' property. This class is part of the event system of the game, which is used to handle
 * game logic in a decoupled manner. Game events are created and dispatched by various methods in
 * the game's classes. They are then handled by the game's event handlers.
 *
 * @author Hades
 */
@Getter
public class GameEvent {
  /**
   * Enumeration of the different types of game events. HERO_MOVE: Represents a move event of a hero
   * character. ENEMY_MOVE: Represents a move event of an enemy character. CHARACTER_ATTACK:
   * Represents an attack event of a character. HERO_ATTACK: Represents an attack event of a hero
   * character.
   */
  public enum Type {
    HERO_MOVE,
    ENEMY_MOVE,
    CHARACTER_ATTACK,
    HERO_ATTACK,
  }

  /** The type of the game event. */
  protected Type type;
}
