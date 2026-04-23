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
            String respuesta = mundo.getNinosTotalesHawkins() + ";DATOS_PRUEBA";
            salida.writeUTF(respuesta);
            System.out.println(">>> [SERVIDOR] Respuesta enviada: " + respuesta); // CHIVATO 3
        }
        socket.close();
    } catch (Exception e) {
        e.printStackTrace(); 
    }
}}
