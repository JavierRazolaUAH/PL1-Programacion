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
        DataInputStream entrada = new DataInputStream(socket.getInputStream());
        DataOutputStream salida = new DataOutputStream(socket.getOutputStream());

        String peticion = entrada.readUTF();

        if (peticion.equals("GET_DATA")) {
            // Construimos la super-cadena con TODOS los datos necesarios
            String respuesta = mundo.getEstadoGlobalParaMonitor();

            salida.writeUTF(respuesta);
        }
        socket.close();
    } catch (Exception e) {
        e.printStackTrace(); 
    }
}}
