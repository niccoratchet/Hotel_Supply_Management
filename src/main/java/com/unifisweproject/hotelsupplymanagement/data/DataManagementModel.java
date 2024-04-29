package com.unifisweproject.hotelsupplymanagement.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public interface DataManagementModel {

    void add(Object newObject);                              // Aggiunta di un'istanza di un determinato tipo di oggetto nel DB
    void modify(Object value);                           // Aggiorna i dati di un'istanza all'interno del DB
    ArrayList<Object> search(Object toBeSearched);
    void delete(Object toBeDeleted);         // Ricerca l'oggetto da eliminare nel DB e ne elimina l'istanza
    String getDataTypeForQuery(String dataType, Object value, boolean isSelect);       // Contribuisce alla creazione di query SQL
    void executeQuery(boolean isOutput, PreparedStatement statement);           // Data la query SQL e se questa è un interrogazione con uscita o meno, contatta il DBMS e prende il risultato eventualmente stampandolo
    void loadFromDB() throws SQLException;                      // Carica i dati dal DB nella struttura dati del management

}
