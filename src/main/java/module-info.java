module com.unifysweproject.hotelsupplymanagement {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;

    opens com.unifysweproject.hotelsupplymanagement to javafx.fxml;
    exports com.unifysweproject.hotelsupplymanagement;
}