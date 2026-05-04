package Clases;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logs {
    
    // --- Atributos de Configuración y Singleton ---
    private static Logs instancia;
    private static final String ARCHIVO = "hawkins.txt"; 
    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // --- Constructor Privado ---
    private Logs() {}

    /**
     * Implementación del patrón Singleton con sincronización para hilos.
     */
    public static synchronized Logs getInstance() {
        if (instancia == null) {
            instancia = new Logs();
        }
        return instancia;
    }

    // --- Gestión de Persistencia ---

    /**
     * Registra un evento en el archivo de texto con marca de tiempo.
     * Utiliza un bloque try-with-resources para asegurar el cierre del stream.
     */
    public synchronized void log(String evento) {
        String timestamp = LocalDateTime.now().format(formatoFecha);
        String linea = timestamp + " - " + evento;

        // FileWriter en modo 'append' (true) para no sobrescribir el archivo
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO, true))) {
            writer.write(linea);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error al escribir en el log: " + e.getMessage());
        }
    }
}