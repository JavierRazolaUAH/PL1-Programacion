package Clases;

import Interfaz.Interfaz;
import Interfaz.VentanaMonitor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String args[]) {
        System.out.println(">>> INICIANDO SIMULACIÓN: LA BATALLA DE HAWKINS...");

        AgrupacionZonas zonas = new AgrupacionZonas();
        
        ServidorControl servidor = new ServidorControl(zonas);
        servidor.start();
        
        java.awt.EventQueue.invokeLater(() -> {
            Interfaz ventana = new Interfaz(zonas);
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
            VentanaMonitor monitor = new VentanaMonitor(zonas); 
            monitor.setTitle("Monitor de Estadísticas (Socket)");
            monitor.setVisible(true);
        });
        
        GeneradorEventos generador = new GeneradorEventos(zonas); 
        generador.start();
        
        Demogorgon d0 = new Demogorgon("D0000", zonas);
        zonas.getUpsidedown().registrarDemogorgon(d0);
        d0.start();
        
        for (int i = 1; i <= 1500; i++) {
            try {
                zonas.esperarSiPausado(); 
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }

            String idNino = String.format("N%04d", i);

            Nino n = new Nino(idNino, zonas);
            n.start();

            try {
                Thread.sleep(500 + (int)(Math.random() * 1500));
            } catch (InterruptedException e) {
                System.out.println("Generación de niños interrumpida.");
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        System.out.println(">>> TODOS LOS NIÑOS HAN ENTRADO EN HAWKINS.");
    }
}