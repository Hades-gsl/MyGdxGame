package com.mygdx.event;

import com.mygdx.map.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Hades
 *     <p>invoked by {@link com.mygdx.character.Character#randomMove(Map, boolean)}, {@link
 *     com.mygdx.character.Hero#update(int, int)}
 */
@Getter
@Setter
@NoArgsConstructor
public class CharacterMove extends GameEvent {
  int directionX;
  int directionY;
  String id;

  public CharacterMove(int directionX, int directionY, String id, Type type) {
    assert directionX >= -1 && directionX <= 1 && directionY >= -1 && directionY <= 1;

    this.directionX = directionX;
    this.directionY = directionY;
    this.id = id;
    this.type = type;
  }
}
