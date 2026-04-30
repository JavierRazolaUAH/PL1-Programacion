package Clases;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logs {
    private static Logs instancia;
    private static final String ARCHIVO = "hawkins.txt"; 
    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Logs() {}

    public static synchronized Logs getInstance() {
        if (instancia == null) {
            instancia = new Logs();
        }
        return instancia;
    }

    public synchronized void log(String evento) {
        String timestamp = LocalDateTime.now().format(formatoFecha);
        String linea = timestamp + " - " + evento;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO, true))) {
            writer.write(linea);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error al escribir en el log: " + e.getMessage());
        }
    }
}