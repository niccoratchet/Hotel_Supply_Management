package com.unifisweproject.hotelsupplymanagement;

import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class FXMLWindowLoader extends FXMLLoader {

    public FXMLWindowLoader(URL location, Object controller) {
        super(location);
        setController(controller);
    }

    @Override
    public void setController(Object controller) {
        super.setController(controller);
        this.setControllerFactory(c -> controller);                 // Associa il controller alle azioni definite nel file FXML
    }

    public static Parent loadParent(URL location, Object controller) throws IOException {

        FXMLWindowLoader loader = new FXMLWindowLoader(location, controller);
        return loader.load();

    }

    public static void loadFXML(URL location, Object controller, boolean isSameWindow, ActionEvent event, String title, boolean isResizable) throws IOException {

        Parent root = loadParent(location, controller);
        if (root != null) {
            Stage stage;
            if (isSameWindow) {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            }
            else {
                stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);             // Nel caso di finestre diverse, blocca l'interazione con le altre finestre
            }
            setStage(stage, root, title, isResizable);
        }

    }

    public static void loadFXML(URL location, Object controller, String title, boolean isResizable, Stage initialStage) throws IOException {

        Parent root = loadParent(location, controller);
        if (root != null) {
            setStage(initialStage, root, title, isResizable);
        }

    }

    public static void setStage(Stage stage, Parent root, String title, boolean isResizable) {

        stage.setScene(root.getScene());            // FIXME: Deve essere aggiunta la modalità per impedire di cliccare sotto quando sono aperte più finestre
        stage.setTitle(title);
        stage.getIcons().add(HotelSupplyManagementMain.icon);
        stage.setResizable(isResizable);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }


}
