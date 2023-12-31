package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.config.Config;

/**
 * This class represents the screen for changing the map size. It extends the BaseScreen class. It
 * contains text fields for the number of rows and columns and buttons for applying the changes and
 * going back.
 *
 * @author Hades
 */
public class ChangeMapScreen extends BaseScreen {
  private TextField rowsField;
  private TextField colsField;

  /**
   * Constructor for the ChangeMapScreen class. It initializes the game and the text fields.
   *
   * @param game The game instance.
   */
  public ChangeMapScreen(MyGdxGame game) {
    super(game);

    initFields();
  }

  /**
   * This method initializes the text fields and buttons. It adds them to the table and sets their
   * listeners.
   */
  private void initFields() {
    Label titleLabel = new Label("Modify Map Size", skin);
    table.add(titleLabel).colspan(2).center().pad(Config.BUTTON_PAD);
    table.row();

    rowsField = new TextField("", skin);
    colsField = new TextField("", skin);

    TextButton applyButton = new TextButton("Apply", skin);
    applyButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            try {
              int rows = Integer.parseInt(rowsField.getText());
              int cols = Integer.parseInt(colsField.getText());
              if (isValidInput(rows) && isValidInput(cols)) {
                Config.changeConfig(rows, cols);
                new Dialog("Success", skin)
                    .text("Settings applied successfully")
                    .button("OK")
                    .show(stage);
              } else {
                new Dialog("Error", skin)
                    .text("Invalid input. Rows and cols should be even numbers between 6 and 12")
                    .button("OK")
                    .show(stage);
              }
            } catch (NumberFormatException e) {
              new Dialog("Error", skin)
                  .text("Invalid input. Please enter a number")
                  .button("OK")
                  .show(stage);
            }
          }
        });

    TextButton backButton = new TextButton("Back", skin);
    backButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            dispose();
            game.setScreen(new MainMenu(game));
          }
        });

    table.add("Rows: ").left();
    table.add(rowsField).width(Config.BUTTON_WIDTH).pad(Config.BUTTON_PAD);
    table.row();
    table.add("Cols: ").left();
    table.add(colsField).width(Config.BUTTON_WIDTH).pad(Config.BUTTON_PAD);
    table.row();
    table.add(applyButton).colspan(2).center().pad(Config.BUTTON_PAD);
    table.row();
    table.add(backButton).colspan(2).center().pad(Config.BUTTON_PAD);
  }

  /**
   * This method checks if the input value is valid. A valid value is an even number between 6 and
   * 12.
   *
   * @param value The input value.
   * @return true if the value is valid, false otherwise.
   */
  private boolean isValidInput(int value) {
    return value >= 6 && value <= 12 && value % 2 == 0;
  }

  /**
   * This method is called when the screen becomes the current screen. It logs the start of the
   * settings screen.
   */
  @Override
  public void show() {
    Gdx.app.log("SettingScreen", "start");
  }

  /**
   * This method is called when the screen is no longer the current screen. It disposes the screen
   * and logs the end of the settings screen.
   */
  @Override
  public void dispose() {
    super.dispose();

    Gdx.app.log("SettingScreen", "end");
  }
}
