package com.unifisweproject.hotelsupplymanagement;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class MainMenuVBox extends VBox {

    private final HBox welcomeTextBox = new HBox();
    private final Text welcomeText = new Text("Bentornato nel programma gestionale dell'azienda Breig SPA! \n " +
            "Clicca uno dei bottoni per accedere ad una delle sezioni.");
    private final VBox buttonsContainer = new VBox();
    private final HBox firstButtons = new HBox();
    private final HBox secondButtons = new HBox();

    private final Button manageCustomersButton = new Button("Gestione clienti");
    private final Button manageOrdersButton = new Button("Gestione ordini");
    private final Button manageSuppliersButton = new Button("Gestione fornitori");
    private final Button manageItemsButton = new Button("Gestione articoli");

    public MainMenuVBox() {

        super();
        setAlignment(Pos.CENTER);
        setSpacing(50);
        createMainWindow();

    }

    public void createMainWindow() {

        welcomeTextBox.setAlignment(Pos.CENTER);
        welcomeTextBox.setSpacing(50);
        welcomeTextBox.getChildren().add(welcomeText);

        setButtonsContainer();

        this.getChildren().add(welcomeText);
        this.getChildren().add(buttonsContainer);

    }

    public void setButtonsContainer() {

        buttonsContainer.setAlignment(Pos.CENTER);
        buttonsContainer.setSpacing(50);

        firstButtons.setAlignment(Pos.CENTER);
        firstButtons.setSpacing(50);

        secondButtons.setAlignment(Pos.CENTER);
        secondButtons.setSpacing(50);

        setButtonsProperties();

        firstButtons.getChildren().add(manageCustomersButton);
        firstButtons.getChildren().add(manageOrdersButton);

        secondButtons.getChildren().add(manageSuppliersButton);
        secondButtons.getChildren().add(manageItemsButton);

        buttonsContainer.getChildren().add(firstButtons);
        buttonsContainer.getChildren().add(secondButtons);

    }

    public void setButtonsProperties() {

        manageCustomersButton.setOnAction((actionEvent -> System.out.println("Gestione clienti!")));

        manageCustomersButton.setPrefSize(200, 100);
        manageOrdersButton.setPrefSize(200, 100);
        manageSuppliersButton.setPrefSize(200, 100);
        manageItemsButton.setPrefSize(200, 100);

    }

}
