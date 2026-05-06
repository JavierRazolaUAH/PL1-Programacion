package Clases;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logs {
    // --- Atributos de Configuración y Singleton ---
    private static Logs loggerGlobal;
    private static final String ARCHIVO = "hawkins.txt";
    private final DateTimeFormatter formateadorTiempo = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // --- Constructor Privado ---
    private Logs() {}

    /**
     * Implementación del patrón Singleton con sincronización para hilos.
     */
    public static synchronized Logs getInstance() {
        if (loggerGlobal == null) {
            loggerGlobal = new Logs();
        }
        return loggerGlobal;
    }

    // --- Gestión de Persistencia ---
    /**
     * Registra un evento en el archivo de texto con marca de tiempo.
     */
    public synchronized void log(String evento) {
        String timestamp = LocalDateTime.now().format(formateadorTiempo);
        String linea = timestamp + " - " + evento;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO, true))) {
            writer.write(linea);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error al escribir en el log: " + e.getMessage());
        }
    }
}