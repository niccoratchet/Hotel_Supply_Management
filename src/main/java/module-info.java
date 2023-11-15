module com.unifysweproject.hotelsupplymanagement {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.slf4j;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.xerial.sqlitejdbc;
    requires java.sql;

    opens com.unifisweproject.hotelsupplymanagement to javafx.fxml;
    exports com.unifisweproject.hotelsupplymanagement;
}