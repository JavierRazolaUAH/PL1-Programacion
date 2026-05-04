package Clases;

import sockets.ServidorControl;
import Interfaz.Interfaz;
import Interfaz.VentanaMonitor;
import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String args[]) {
 
System.out.println(">>> [SERVIDOR] INICIANDO SIMULACIÓN Y NÚCLEO DE RED...");

        // 1. Creamos el mundo (objeto compartido por los hilos del servidor)
        AgrupacionZonas zonas = new AgrupacionZonas();

        // 2. Lanzamos el servidor de Sockets para que el monitor pueda conectarse
        ServidorControl servidorSockets = new ServidorControl(zonas);
        servidorSockets.start();

        // 3. Lanzamos la Interfaz Principal (el mapa donde se ve todo)
        EventQueue.invokeLater(() -> {
            Interfaz ventanaPrincipal = new Interfaz(zonas);
            ventanaPrincipal.setLocationRelativeTo(null); // Centrar en pantalla
            ventanaPrincipal.setVisible(true);
        });

        // 4. Lanzamos la lógica de la simulación
        new GeneradorEventos(zonas).start();
        
        // Lanzamos al Demogorgon Alpha
        Demogorgon d0 = new Demogorgon("D0000", zonas);
        zonas.getUpsidedown().registrarDemogorgon(d0);
        d0.start();

        // 5. Bucle de generación de niños
        for (int i = 1; i <= 1500; i++) {
            try {
                zonas.esperarSiPausado();
                String idNino = String.format("N%04d", i);
                new Nino(idNino, zonas).start();
                
                // Tiempo de espera entre niños
                Thread.sleep(500 + (int)(Math.random() * 1500));
            } catch (InterruptedException e) {
                break;
            }
        }
}}