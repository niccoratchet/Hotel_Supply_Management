package com.unifisweproject.hotelsupplymanagement;

public interface Data_Management {

    public void add();                              // Aggiunta di un'istanza di un determinato tipo di oggetto nel DB
    public void modify();                           // Aggiorna i dati di un'istanza all'interno del DB
    public Object search();                         // Predispone la ricerca nel Database di una determinata istanza di un dato
    public void printAll();                         // Interroga il DB e genera la finestra di riepilogo di tutti i dati di un certo tipo (ad es. lista di tutti i clienti)
    public void print(Object tobeViewed);           // Interroga il DB e genera una finestra per visualizzare le informazioni su un oggetto
    public void delete(Object toBeDeleted);         // Ricerca l'oggetto da eliminare nel DB e ne elimina l'istanza

}
