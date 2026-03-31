/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package Clases;

import Interfaz.Interfaz;

/**
 *
 * @author javir
 */
public class Main {

    public static void main(String args[]) {
// 1. Creamos el "mapa" o entorno con todas las zonas seguras
        AgrupacionZonas zonas = new AgrupacionZonas();
        
        // 2. Arrancamos la interfaz gráfica pasándole nuestro mapa
        // Usamos invokeLater porque es la forma más segura de arrancar ventanas en Java
        java.awt.EventQueue.invokeLater(() -> {
            Interfaz ventana = new Interfaz(zonas);
            ventana.setLocationRelativeTo(null); // Centra la ventana en la pantalla
            ventana.setVisible(true);
        });
        
        System.out.println(">>> INICIANDO SIMULACIÓN DE PRUEBA...");
        
        // 3. Creamos 3 niños de prueba
        for (int i = 1; i <= 100; i++) {
            // Generamos un ID único: N001, N002... N100
            String idNino = String.format("N%03d", i);

            // Creamos el hilo del niño pasándole la referencia de las zonas
            Nino n = new Nino(idNino, zonas);

            // Arrancamos el hilo del niño
            n.start();
        }
        
        Demogorgon d0 = new Demogorgon("D0000", zonas);
        // 4. Lanzamos los hilos para que empiecen a moverse
  
        d0.start();
    }
}