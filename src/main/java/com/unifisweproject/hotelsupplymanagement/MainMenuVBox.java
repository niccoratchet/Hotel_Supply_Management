package com.unifisweproject.hotelsupplymanagement;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


/*
    Questa classe estende VBox. È il container principale che contiene gli altri sotto container che compongono la finestra del menu principale.
 */

public class MainMenuVBox extends VBox {

    private final HBox welcomeTextBox = new HBox();
    private final Text welcomeText = new Text("Bentornato nel programma gestionale dell'azienda Breig SPA! \n " +
            "Clicca uno dei bottoni per accedere ad una delle sezioni.");
    private final VBox buttonsContainer = new VBox();           // contiene due file di bottoni
    private final HBox firstButtons = new HBox();               // prima fila contenente due bottoni
    private final HBox secondButtons = new HBox();              // seconda fila

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

    public void createMainWindow() {            // creazione della finestra principale

        welcomeTextBox.setAlignment(Pos.CENTER);
        welcomeTextBox.setSpacing(50);
        welcomeTextBox.getChildren().add(welcomeText);

        setButtonsContainer();

        this.getChildren().addAll(welcomeText, buttonsContainer);

    }

    public void setButtonsContainer() {              // creazione del contenitore verticale contenenti due contenitori orizzontali di bottoni che portano alla varie sezioni del programma

        buttonsContainer.setAlignment(Pos.CENTER);
        buttonsContainer.setSpacing(50);

        firstButtons.setAlignment(Pos.CENTER);
        firstButtons.setSpacing(50);

        secondButtons.setAlignment(Pos.CENTER);
        secondButtons.setSpacing(50);

        setButtonsProperties();

        firstButtons.getChildren().addAll(manageCustomersButton, manageOrdersButton);
        secondButtons.getChildren().addAll(manageSuppliersButton, manageItemsButton);
        buttonsContainer.getChildren().addAll(firstButtons, secondButtons);


    }

    public void setButtonsProperties() {            // associa un evento al click del tasto sulla gestione dei clienti e definisce le dimensioni dei bottoni

        manageCustomersButton.setOnAction((actionEvent -> System.out.println("Gestione clienti!")));

        manageCustomersButton.setPrefSize(200, 100);
        manageOrdersButton.setPrefSize(200, 100);
        manageSuppliersButton.setPrefSize(200, 100);
        manageItemsButton.setPrefSize(200, 100);

    }

}
