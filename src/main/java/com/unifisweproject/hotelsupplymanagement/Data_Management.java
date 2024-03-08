package com.unifisweproject.hotelsupplymanagement;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public interface Data_Management {

    public void add(Object newObject);                              // Aggiunta di un'istanza di un determinato tipo di oggetto nel DB
    public void modify(Object value);                           // Aggiorna i dati di un'istanza all'interno del DB
    public ArrayList<Object> search(Object toBeSearched);
    public void delete(int code);         // Ricerca l'oggetto da eliminare nel DB e ne elimina l'istanza
    public String getDataTypeForQuery(String dataType, Object value, boolean isSelect);       // Contribuisce alla creazione di query SQL
    public void executeQuery(boolean isOutput, PreparedStatement statement);           // Data la query SQL e se questa è un interrogazione con uscita o meno, contatta il DBMS e prende il risultato eventualmente stampandolo

}
