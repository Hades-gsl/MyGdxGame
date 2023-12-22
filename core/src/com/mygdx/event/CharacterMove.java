package com.mygdx.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class represents a character move event in the game. It contains the x and y directions of
 * the move and the id of the character that moved. It extends the GameEvent class and adds
 * additional properties specific to a character move. This event is created and dispatched by the
 * Character#randomMove and Hero#update methods. The directionX and directionY properties represent
 * the direction of the move in the x and y axes respectively. They can take values between -1 and
 * 1, inclusive. The id property represents the id of the character that moved.
 *
 * @author Hades
 */
@Getter
@Setter
@NoArgsConstructor
public class CharacterMove extends GameEvent {
  /** The direction of the move in the x axis. */
  int directionX;

  /** The direction of the move in the y axis. */
  int directionY;

  /** The id of the character that moved. */
  String id;

  /**
   * Constructor for CharacterMove. Initializes the move with the provided x and y directions, id,
   * and type. The x and y directions must be between -1 and 1, inclusive.
   *
   * @param directionX The direction of the move in the x axis.
   * @param directionY The direction of the move in the y axis.
   * @param id The id of the character that moved.
   * @param type The type of the event.
   */
  public CharacterMove(int directionX, int directionY, String id, Type type) {
    assert directionX >= -1 && directionX <= 1 && directionY >= -1 && directionY <= 1;

    this.directionX = directionX;
    this.directionY = directionY;
    this.id = id;
    this.type = type;
  }
}
