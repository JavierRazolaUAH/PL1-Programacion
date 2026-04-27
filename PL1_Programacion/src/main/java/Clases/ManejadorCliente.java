/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 *
 * @author Alex338
 */
public class ManejadorCliente extends Thread {
    private Socket socket;
    private AgrupacionZonas mundo;

    public ManejadorCliente(Socket s, AgrupacionZonas m) {
        this.socket = s;
        this.mundo = m;
    }

    public void run() {
    try {
        System.out.println(">>> [SERVIDOR] Cliente conectado. Esperando petición..."); // CHIVATO 1
        DataInputStream entrada = new DataInputStream(socket.getInputStream());
        DataOutputStream salida = new DataOutputStream(socket.getOutputStream());

        String peticion = entrada.readUTF();
        System.out.println(">>> [SERVIDOR] Petición recibida: " + peticion); // CHIVATO 2

        if (peticion.equals("GET_DATA")) {
            // Construimos la super-cadena con TODOS los datos necesarios
            String respuesta = mundo.getNinosTotalesHawkins() + ";" +           // partes[0]
                               mundo.getEventoActivo() + ";" +                  // partes[1]
                               mundo.getTiempoRestanteEvento() + ";" + // partes[2]
                               mundo.getUpsidedown().getColmena().getNumPrisioneros() + ";" + // partes[3]
                               mundo.getEstadoPortalString(0) + ";" +           // partes[4] (Bosque)
                               mundo.getEstadoPortalString(1) + ";" +           // partes[5] (Lab)
                               mundo.getEstadoPortalString(2) + ";" +           // partes[6] (Centro)
                               mundo.getEstadoPortalString(3) + ";" +                  // partes[7] (Alcantarilla)
                               mundo.getEntidadesZonaString(0) + ";" +          // partes[8]
                               mundo.getEntidadesZonaString(1) + ";" +          // partes[9]
                               mundo.getEntidadesZonaString(2) + ";" +          // partes[10]
                               mundo.getEntidadesZonaString(3);                 // partes[11]

            salida.writeUTF(respuesta);
            System.out.println(">>> [SERVIDOR] Datos enviados correctamente."); 
        }
        socket.close();
    } catch (Exception e) {
        e.printStackTrace(); 
    }
}}
