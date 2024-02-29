package com.unifisweproject.hotelsupplymanagement;

public interface Data_Management {

    public void add(Object newObject);                              // Aggiunta di un'istanza di un determinato tipo di oggetto nel DB
    public void modify(Object value);                           // Aggiorna i dati di un'istanza all'interno del DB
    public void modifyParameter(int code, String dataType, Object value);
    public Object search(String dataType, Object value);                         // Predispone la ricerca nel Database di una determinata istanza di un dato
    public void printAll();                         // Interroga il DB e genera la finestra di riepilogo di tutti i dati di un certo tipo (ad es. lista di tutti i clienti)
    public void print(int code);           // Interroga il DB e genera una finestra per visualizzare le informazioni su un oggetto
    public void delete(int code);         // Ricerca l'oggetto da eliminare nel DB e ne elimina l'istanza
    public String getDataTypeForQuery(String dataType, Object value);       // Contribuisce alla creazione di query SQL
    public void executeQuery(String query, boolean isOutput);           // Data la query SQL e se questa è un interrogazione con uscita o meno, contatta il DBMS e prende il risultato eventualmente stampandolo

}
