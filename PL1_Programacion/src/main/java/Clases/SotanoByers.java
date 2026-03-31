/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author javir
 */
public class SotanoByers {
    // Cola concurrente para almacenar a los niños que están actualmente dentro
    private final BlockingQueue<Nino> ninosEnSotano;
    private final Random rand;

    public SotanoByers() {
        this.ninosEnSotano = new LinkedBlockingQueue<>();
        this.rand = new Random();
    }

    // Método principal que ejecutarán los hilos Niño
    public void prepararse(Nino nino) throws InterruptedException {
        // 1. El niño entra al sótano
        ninosEnSotano.put(nino);
        
        // ¡Recuerda usar tu clase Logs aquí en lugar de un sout!
        // Logs.getInstance().log(nino.getIdNino() + " ha entrado al SÓTANO BYERS.");
        
        // 2. Tiempo de preparación: Aleatorio entre 1 y 2 segundos (1000 - 2000 ms)
        int tiempoEspera = rand.nextInt(1000) + 1000; 
        Thread.sleep(tiempoEspera);
        
        // 3. El niño termina y sale del sótano
        ninosEnSotano.remove(nino);
        
        // Logs.getInstance().log(nino.getIdNino() + " sale del SÓTANO BYERS hacia los portales.");
    }

    // --- Métodos 'Getter' útiles para tu Interfaz Gráfica y la Parte 2 (RMI) ---

    // Devuelve el número exacto de niños dentro (para los contadores)
    public int getNumeroNinos() {
        return ninosEnSotano.size();
    }

    // Devuelve una lista con los niños dentro (para mostrarlos en el JTextArea)
    public List<Nino> getNiñosEnSotano() {
        return new ArrayList<>(ninosEnSotano);
    }
}
