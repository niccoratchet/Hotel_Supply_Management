module com.unifysweproject.hotelsupplymanagement {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.slf4j;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.xerial.sqlitejdbc;
    
    exports com.unifisweproject.hotelsupplymanagement.company;
    opens com.unifisweproject.hotelsupplymanagement.company to javafx.fxml;
    exports com.unifisweproject.hotelsupplymanagement.contact;
    opens com.unifisweproject.hotelsupplymanagement.contact to javafx.fxml;
    exports com.unifisweproject.hotelsupplymanagement.customer;
    opens com.unifisweproject.hotelsupplymanagement.customer to javafx.fxml;
    exports com.unifisweproject.hotelsupplymanagement.item;
    opens com.unifisweproject.hotelsupplymanagement.item to javafx.fxml;
    exports com.unifisweproject.hotelsupplymanagement.order;
    opens com.unifisweproject.hotelsupplymanagement.order to javafx.fxml;
    exports com.unifisweproject.hotelsupplymanagement.supplier;
    opens com.unifisweproject.hotelsupplymanagement.supplier to javafx.fxml;
    exports com.unifisweproject.hotelsupplymanagement.supply;
    opens com.unifisweproject.hotelsupplymanagement.supply to javafx.fxml;
    exports com.unifisweproject.hotelsupplymanagement.data;
    opens com.unifisweproject.hotelsupplymanagement.data to javafx.fxml;
    exports com.unifisweproject.hotelsupplymanagement.login;
    opens com.unifisweproject.hotelsupplymanagement.login to javafx.fxml;
    exports com.unifisweproject.hotelsupplymanagement.main;
    opens com.unifisweproject.hotelsupplymanagement.main to javafx.fxml;
    exports com.unifisweproject.hotelsupplymanagement.itemsInOderAndSupply;
    opens com.unifisweproject.hotelsupplymanagement.itemsInOderAndSupply to javafx.fxml;
}