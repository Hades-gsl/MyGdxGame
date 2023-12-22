package com.mygdx.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mygdx.bullet.Bullet;
import com.mygdx.character.Enemy;
import com.mygdx.character.Hero;
import com.mygdx.event.CharacterAttack;
import com.mygdx.event.CharacterMove;
import com.mygdx.event.GameEvent;
import com.mygdx.event.HeroAttack;
import com.mygdx.map.Map;
import com.mygdx.observer.GameObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class represents the state of the game at any given moment. It contains lists of heroes,
 * enemies, and bullets, as well as the game map. It also maintains a list of observers that are
 * notified of game events. It uses the observer pattern to notify observers of game events.
 *
 * @author Hades
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"observers"})
public class GameState {
  private CopyOnWriteArrayList<Hero> heroes;
  private CopyOnWriteArrayList<Enemy> enemies;
  private CopyOnWriteArrayList<Bullet> bullets;
  private Map map;
  private List<GameObserver> observers = new ArrayList<>();

  /**
   * Constructor for GameState. Initializes the game state with provided heroes, enemies, bullets,
   * and map.
   *
   * @param heroes List of heroes
   * @param enemies List of enemies
   * @param bullets List of bullets
   * @param map Game map
   */
  public GameState(
      CopyOnWriteArrayList<Hero> heroes,
      CopyOnWriteArrayList<Enemy> enemies,
      CopyOnWriteArrayList<Bullet> bullets,
      Map map) {
    this.heroes = heroes;
    this.enemies = enemies;
    this.bullets = bullets;
    this.map = map;
  }

  /**
   * Adds an observer to the list of observers.
   *
   * @param observer The observer to add
   */
  public void addObserver(GameObserver observer) {
    observers.add(observer);
  }

  /**
   * Removes an observer from the list of observers.
   *
   * @param observer The observer to remove
   */
  public void removeObserver(GameObserver observer) {
    observers.remove(observer);
  }

  /**
   * Notifies all observers of a game event. The type of event determines which method is called on
   * the observer.
   *
   * @param event The game event to notify observers of
   */
  public void notifyObservers(GameEvent event) {
    for (GameObserver observer : observers) {
      if (event instanceof CharacterMove) {
        observer.handleEvent((CharacterMove) event);
      } else if (event instanceof CharacterAttack) {
        observer.handleEvent((CharacterAttack) event);
      } else if (event instanceof HeroAttack) {
        observer.handleEvent((HeroAttack) event);
      } else {
        throw new UnsupportedOperationException();
      }
    }
  }
}
