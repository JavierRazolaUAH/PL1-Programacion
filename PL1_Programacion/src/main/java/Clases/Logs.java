/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;

/**
 *
 * @author javir
 */
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logs {
    // Instancia única de la clase Logs
    private static Logs instancia;
    
    // Nombre del archivo de log actualizado a la nueva práctica
    private static final String ARCHIVO = "hawkins.txt"; 
    
    // Formato de fecha para los logs
    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Constructor privado para evitar instanciación externa
    private Logs() {}

    // Método para obtener la instancia única de la clase Logs
    public static synchronized Logs getInstance() {
        // Si la instancia es nula, se crea una nueva
        if (instancia == null) {
            instancia = new Logs();
        }
        // Se retorna la instancia única
        return instancia;
    }

    // Método para registrar un evento en el log
    public synchronized void log(String evento) {
        // Se obtiene el timestamp actual y se formatea
        String timestamp = LocalDateTime.now().format(formatoFecha);
        // Se crea la línea de log con el timestamp y el evento
        String linea = timestamp + " - " + evento;

        // Se intenta escribir la línea en el archivo de log
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO, true))) {
            writer.write(linea);
            writer.newLine();
        } catch (IOException e) {
            // Si ocurre un error, se imprime en la consola
            System.err.println("Error al escribir en el log: " + e.getMessage());
        }
    }
}
