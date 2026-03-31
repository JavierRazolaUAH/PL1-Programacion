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
        Nino n1 = new Nino("N0001", zonas);
        Nino n2 = new Nino("N0002", zonas);
        Nino n3 = new Nino("N0003", zonas);
        
        // 4. Lanzamos los hilos para que empiecen a moverse
        n1.start();
        n2.start();
        n3.start();
    }
}