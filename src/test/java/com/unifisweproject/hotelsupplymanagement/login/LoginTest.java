package com.unifisweproject.hotelsupplymanagement.login;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginTest {

    @Test
    void firstAccess() {

        String psw = "Prova";
        File file = new File("test.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(psw);
        }
        catch (IOException e) {
            System.out.println("Errore nella scrittura su file: " + e.getMessage());
        }
        try {
            String fileContent = Files.readString(Paths.get("test.txt"));
            assertEquals(psw, fileContent);
        }
        catch (IOException e) {
            System.out.println("Errore nella lettura dal file: " + e.getMessage());
        }

    }

}
