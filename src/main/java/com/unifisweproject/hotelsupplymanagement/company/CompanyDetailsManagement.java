package com.unifisweproject.hotelsupplymanagement.company;

import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CompanyDetailsManagement {

    public static void executeAddQuery(String P_IVA, String ragioneSociale) {

        if ("".equals(P_IVA) || "".equals(ragioneSociale))
            return;
        String insertCompanyDataQuery = "INSERT INTO Dati_Azienda (P_IVA, Ragione_Sociale) VALUES (" + P_IVA + ", ?)";
        try {
            PreparedStatement preparedStatement = HotelSupplyManagementMain.conn.prepareStatement(insertCompanyDataQuery);
            preparedStatement.setString(1, ragioneSociale);
            try {
                preparedStatement.executeUpdate();
            }
            catch (SQLException e) {
                System.out.println("Elemento già presente nel DB");
            }
        }
        catch (SQLException e) {
            System.err.println("Errore durante la generazione della query in CompanyDetailsAddWindow: " + e.getMessage());
        }

    }

    public static void executeUpdateQuery(String P_IVA, String ragioneSociale) {

        String modifyQuery = "UPDATE Dati_Azienda SET P_IVA = ?, Ragione_Sociale = ? WHERE P_IVA = ? AND Ragione_Sociale = ?";
        try {
            PreparedStatement preparedStatement = HotelSupplyManagementMain.conn.prepareStatement(modifyQuery);
            preparedStatement.setString(1, P_IVA);
            preparedStatement.setString(2, ragioneSociale);
            try {
                preparedStatement.executeUpdate();
            }
            catch (SQLException e) {
                System.out.println("Errore durante la query di modifica di Dati_Azienda: " + e.getMessage());
            }
        }
        catch (SQLException e) {
            System.err.println("Errore durante la generazione della query di modifica in Dati_Azienda: " + e.getMessage());
        }

    }

}
