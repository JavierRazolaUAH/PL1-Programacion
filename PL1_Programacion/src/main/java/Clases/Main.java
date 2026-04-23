/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package Clases;

import Interfaz.Interfaz;
import Interfaz.VentanaMonitor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author javir
 */
public class Main {

    public static void main(String args[]) {
    System.out.println(">>> INICIANDO SIMULACIÓN: LA BATALLA DE HAWKINS...");

        // 1. Creamos el "mapa" o entorno con todas las zonas seguras y el Upside Down
        AgrupacionZonas zonas = new AgrupacionZonas();
        
        ServidorControl servidor = new ServidorControl(zonas);
        servidor.start();
        
        // 2. Arrancamos la interfaz gráfica pasándole nuestro mapa
        // Usamos invokeLater porque es la forma más segura de arrancar ventanas en Java Swing
        java.awt.EventQueue.invokeLater(() -> {
            Interfaz ventana = new Interfaz(zonas);
            ventana.setLocationRelativeTo(null); // Centra la ventana en la pantalla
            ventana.setVisible(true);
            VentanaMonitor monitor = new VentanaMonitor(); 
            monitor.setTitle("Monitor de Estadísticas (Socket)");
            monitor.setVisible(true);
        });
        
        // --- ¡PERFECTO! El motor de eventos ya está encendido ---
        GeneradorEventos generador = new GeneradorEventos(zonas); 
        generador.start();
        
        // 3. Nace el Demogorgon Alpha en el Upside Down
        Demogorgon d0 = new Demogorgon("D0000", zonas);
        zonas.getUpsidedown().registrarDemogorgon(d0); //Añadimos el primer Demogorgon a la lista de TODOS los demogorgons
        d0.start();
        
        // 4. Creamos los 100 niños de forma escalonada
        for (int i = 1; i <= 1500; i++) {
            
        try {
            // --- ¡EL FRENO DE MANO QUE FALTABA! ---
            // Obligamos al hilo Main a comprobar si le has dado al botón de PAUSAR.
            // Si está pausado, se quedará congelado aquí y no nacerán más niños.
            zonas.esperarSiPausado(); 
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

            // Generamos un ID único: N001, N002... N100
            String idNino = String.format("N%03d", i);

            // Creamos y arrancamos el hilo del niño
            Nino n = new Nino(idNino, zonas);
            n.start();

            // --- EL TRUCO ESTRELLA ---
            // Hacemos que el programa principal espere un poco antes de crear al siguiente niño.
            // Generará un niño nuevo cada 0.5 - 2 segundos aproximadamente.
            try {
                Thread.sleep(500 + (int)(Math.random() * 1500));
            } catch (InterruptedException e) {
                System.out.println("Generación de niños interrumpida.");
            }
        }
        
        System.out.println(">>> TODOS LOS NIÑOS HAN ENTRADO EN HAWKINS.");
    }
}