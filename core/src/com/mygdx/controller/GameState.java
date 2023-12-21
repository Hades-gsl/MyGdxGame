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

  public GameState(CopyOnWriteArrayList<Hero> heroes, CopyOnWriteArrayList<Enemy> enemies, CopyOnWriteArrayList<Bullet> bullets, Map map) {
    this.heroes = heroes;
    this.enemies = enemies;
    this.bullets = bullets;
    this.map = map;
  }

  public void addObserver(GameObserver observer) {
    observers.add(observer);
  }

  public void removeObserver(GameObserver observer) {
    observers.remove(observer);
  }

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
