module com.unifysweproject.hotelsupplymanagement {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;

    opens com.unifisweproject.hotelsupplymanagement to javafx.fxml;
    exports com.unifisweproject.hotelsupplymanagement;
}