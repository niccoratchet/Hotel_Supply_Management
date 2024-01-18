package com.unifisweproject.hotelsupplymanagement;

public interface Data_Management {

    public void add(Object newObject);                              // Aggiunta di un'istanza di un determinato tipo di oggetto nel DB
    public void modify(int code, String dataType, Object value);                           // Aggiorna i dati di un'istanza all'interno del DB
    public Object search(String dataType, Object value);                         // Predispone la ricerca nel Database di una determinata istanza di un dato
    public void printAll();                         // Interroga il DB e genera la finestra di riepilogo di tutti i dati di un certo tipo (ad es. lista di tutti i clienti)
    public void print(int code);           // Interroga il DB e genera una finestra per visualizzare le informazioni su un oggetto
    public void delete(int code);         // Ricerca l'oggetto da eliminare nel DB e ne elimina l'istanza
    public String getDataTypeForQuery(String dataType, Object value);
    public void executeQuery(String query, boolean isOutput);

}
