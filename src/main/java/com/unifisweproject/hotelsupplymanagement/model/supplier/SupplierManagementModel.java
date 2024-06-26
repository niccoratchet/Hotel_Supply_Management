package com.unifisweproject.hotelsupplymanagement.model.supplier;

import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import com.unifisweproject.hotelsupplymanagement.model.DataManagementModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SupplierManagementModel implements DataManagementModel {


    private int nextSupplierCode;               // Tiene traccia del codice dell'ultimo Articolo nel DB
    private final ArrayList<Supplier> supplierList = new ArrayList<>();

    private static final SupplierManagementModel instance = new SupplierManagementModel(); // Applicazione Singleton per la gestione del supplier

    private SupplierManagementModel() {                                                                   // Il costruttore inizializza il contenuto della variabile nextItemCode

        String getCodeQuery = "SELECT seq FROM sqlite_sequence WHERE name = 'Fornitore'";

        try {
            Statement statement = HotelSupplyManagementMain.conn.createStatement();
            ResultSet resultSet = statement.executeQuery(getCodeQuery);
            nextSupplierCode = resultSet.getInt(1);
        }
        catch(SQLException e) {
            System.err.println("Errore durante l'estrapolazione dell'ultimo codice fornitore");
        }

    }

    public static SupplierManagementModel getInstance() {
        return instance;
    }

    @Override
    public void loadFromDB() throws SQLException {

        ResultSet resultSet = getRows(true, null);
        while (resultSet.next()) {
            Supplier newSupplier = new Supplier(resultSet.getInt(1), resultSet.getString(2),
                    resultSet.getString(3), resultSet.getString(4), resultSet.getString(5),
                    resultSet.getString(7), resultSet.getString(6));
            supplierList.add(newSupplier);
        }

    }

    @Override
    public void add(Object newSupplier) {

        Supplier toBeAdded = (Supplier) newSupplier;
        toBeAdded.setCodice_fornitore(nextSupplierCode + 1);
        String addQuery = "INSERT INTO Fornitore (Data_Inserimento, P_IVA, Ragione_Sociale, Indirizzo, CAP, Civico) \n" +       // creazione della query di inserimento
                "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = HotelSupplyManagementMain.conn.prepareStatement(addQuery);
            preparedStatement.setString(1, toBeAdded.getData_inserimento());
            preparedStatement.setString(2, toBeAdded.getP_IVA());
            preparedStatement.setString(3, toBeAdded.getRagione_sociale());
            preparedStatement.setString(4, toBeAdded.getIndirizzo());
            preparedStatement.setString(5, toBeAdded.getCAP());
            preparedStatement.setString(6, toBeAdded.getCivico());
            preparedStatement.executeUpdate();                                                          // una volta creata, si invia il comando al DBMS
            nextSupplierCode++;
            supplierList.add(toBeAdded);
        }
        catch (SQLException e) {
            System.out.println("Errore durante l'aggiunta del nuovo Supplier: "+ e.getMessage() +" \n Query utilizzata: " + addQuery);
        }

    }

    @Override
    public void modify(Object value) {

        Supplier modified = (Supplier) value;
        String modifyQuery = "UPDATE Fornitore SET " + getDataTypeForQuery("Ragione_Sociale", modified.getRagione_sociale(), false) + ", "
                + getDataTypeForQuery("P_IVA", modified.getP_IVA(), false) + ", " + getDataTypeForQuery("Indirizzo", modified.getIndirizzo(), false) + ", "
                + getDataTypeForQuery("Civico", modified.getCivico(), false) + ", " + getDataTypeForQuery("CAP", modified.getCAP(), false)+ ", "
                + getDataTypeForQuery("Data_Inserimento", modified.getData_inserimento(), false) +
                " WHERE Codice_Fornitore = " + modified.getCodice_fornitore();

        try {
            PreparedStatement statement = HotelSupplyManagementMain.conn.prepareStatement(modifyQuery);
            statement.setString(1, modified.getRagione_sociale());
            statement.setString(2, modified.getIndirizzo());
            executeQuery(false, statement);
        }

        catch (SQLException e) {
            System.err.println("Errore di formattazione nella generazione della query di modifica: " + e.getMessage());
        }

    }

    public ArrayList<Object> getSearchResults(ResultSet resultSet) {              // dato un oggetto ResultSet (insieme delle righe del risultato di una query) rende un ArrayList di Supplier che corrispondono alle righe indicate

        ArrayList<Object> results = new ArrayList<>();                // conterrà gli Item che corrispondono ai valori trovati dopo la query
        try {
            while (resultSet.next()) {
                for (Supplier nextSupplier : supplierList) {
                    if (nextSupplier.getCodice_fornitore() == resultSet.getInt(1)) {
                        results.add(nextSupplier);
                    }
                }
            }
        }
        catch (SQLException e) {
            System.err.println("Errore durante la creazione del risultato di ricerca: " + e.getMessage());
            return null;
        }
        return results;

    }

    @Override
    public ArrayList<Object> search(Object toBeSearched) {

        Supplier supplier = (Supplier) toBeSearched;
        int numberOfParameters = getNumberOfParameters(supplier), numQuestionMarks = 0;
        StringBuilder searchQuery = new StringBuilder("SELECT * FROM Fornitore WHERE ");
        boolean isRagioneSocialePresent = false, isIndirizzoPresent = false;
        int i = 0;
        while (i < 6 && numberOfParameters > 0) {
            switch (i) {
                case 0 -> {
                    if(supplier.getRagione_sociale() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Ragione_Sociale", supplier.getRagione_sociale(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                        isRagioneSocialePresent = true;
                        numQuestionMarks++;
                    }
                }
                case 1 -> {
                    if(supplier.getP_IVA() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("P_IVA", supplier.getP_IVA(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }
                case 2 -> {
                    if(supplier.getIndirizzo() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Indirizzo", supplier.getIndirizzo(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                        isIndirizzoPresent = true;
                        numQuestionMarks++;
                    }
                }
                case 3 -> {
                    if(supplier.getCivico() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Civico", supplier.getCivico(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }

                case 4 -> {
                    if(supplier.getCAP() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("CAP", supplier.getCAP(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }
                case 5 -> {
                    if(supplier.getCodice_fornitore() != -1) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Codice_Fornitore", supplier.getCodice_fornitore(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }
            }
            i++;
        }
        try {
            PreparedStatement statement = HotelSupplyManagementMain.conn.prepareStatement(searchQuery.toString());
            i = 0;
            int parameterIndex = 1;
            while (i < 2 && numQuestionMarks > 0) {
                switch (i) {
                    case 0 -> {
                        if (isRagioneSocialePresent) {
                            String ragioneSocialeValue = "%" + supplier.getRagione_sociale() + "%";
                            statement.setString(parameterIndex, ragioneSocialeValue);
                            numQuestionMarks--;
                            parameterIndex++;
                        }
                    }
                    case 1 -> {
                        if (isIndirizzoPresent) {
                            String addressValue = "%" + supplier.getIndirizzo() + "%";
                            statement.setString(parameterIndex, addressValue);
                            numQuestionMarks--;
                            parameterIndex++;
                        }
                    }
                }
                i++;
            }
            return getSearchResults(getRows(false, statement));
        }
        catch (SQLException e) {
            System.err.println("Query di ricerca non correttamente formattata: " + e.getMessage());
            return null;
        }

    }

    public int getNumberOfParameters(Supplier forCounting) {

        int i = 0, count = 0;
        while (i < 6) {

            switch (i) {
                case 0 -> {
                    if (forCounting.getRagione_sociale() != null)
                        count++;
                }
                case 1 -> {
                    if (forCounting.getP_IVA() != null)
                        count++;
                }
                case 2 -> {
                    if (forCounting.getIndirizzo() != null)
                        count++;
                }
                case 3 -> {
                    if (forCounting.getCivico() != null)
                        count++;
                }
                case 4 -> {
                    if(forCounting.getCAP() != null)
                        count++;
                }
                case 5 -> {
                    if(forCounting.getCodice_fornitore() != -1)
                        count++;
                }
            }
            i++;
        }

        return count;

    }

    @Override
    public void delete(Object toBeDeleted) {

        try {
            PreparedStatement statement = HotelSupplyManagementMain.conn.prepareStatement("DELETE FROM Fornitore WHERE Codice_Fornitore = " + ((Supplier) toBeDeleted).getCodice_fornitore());
            executeQuery(false, statement);
            supplierList.remove(toBeDeleted);
        }
        catch (SQLException e) {
            System.err.println("Errore durante l'eliminazione della riga Item: " + e.getMessage());
        }

    }

    @Override
    public String getDataTypeForQuery(String dataType, Object value, boolean isSelect) {

        if (isSelect) {

            return switch (dataType) {
                case "Codice_Fornitore" -> "Codice_Fornitore = " + value;
                case "Data_Inserimento" -> "Data_Inserimento = '" + value + "'";
                case "P_IVA" -> "P_IVA = '" + value + "'";
                case "Ragione_Sociale" -> "Ragione_Sociale LIKE ?";
                case "Indirizzo" -> "Indirizzo LIKE ?";
                case "CAP" -> "CAP = '" + value + "'";
                case "Civico" -> "Civico = '" + value + "'";
                default -> " ";
            };
        }
        else {

            return switch (dataType) {
                case "Codice_Fornitore" -> "Codice_Fornitore = " + value;
                case "Data_Inserimento" -> "Data_Inserimento = '" + value + "'";
                case "P_IVA" -> "P_IVA = '" + value + "'";
                case "Ragione_Sociale" -> "Ragione_Sociale = ?";
                case "Indirizzo" -> "Indirizzo = ?";
                case "CAP" -> "CAP = '" + value + "'";
                case "Civico" -> "Civico = '" + value + "'";
                default -> " ";
            };
        }

    }

    @Override
    public void executeQuery(boolean isOutput, PreparedStatement statement) {

        try {
            if (! isOutput)
                statement.executeUpdate();
        }

        catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }

    public ResultSet getRows(boolean areAllRowsRequested, PreparedStatement statement) {

        String toBeExecutedQuery;
        try {
            if (areAllRowsRequested) {
                toBeExecutedQuery = "SELECT * FROM Fornitore ORDER BY Data_Inserimento DESC";
                PreparedStatement allRowsQuery = HotelSupplyManagementMain.conn.prepareStatement(toBeExecutedQuery);
                return allRowsQuery.executeQuery();
            }
            return statement.executeQuery();
        }
        catch (SQLException e) {
            System.err.println("Errore durante il reperimento delle righe dalla tabella Fornitore");
            return null;
        }

    }

    public ArrayList<Supplier> getSupplierList() {
        return supplierList;
    }

}
