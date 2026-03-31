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

 // --- MÉTODOS DE ENTRADA Y SALIDA SEPARADOS ---
    public void entrar(Nino nino) throws InterruptedException {
        ninosEnSotano.put(nino);
        // Logs.getInstance().log(nino.getIdNino() + " ha ENTRADO al Sótano Byers.");
    }

    public void salir(Nino nino) {
        ninosEnSotano.remove(nino);
        // Logs.getInstance().log(nino.getIdNino() + " ha SALIDO del Sótano Byers.");
    }

    // --- MÉTODOS DE ACCIÓN ---
    public void prepararse(Nino nino) throws InterruptedException {
        entrar(nino); // Primero entra a la lista
        
        // Permanece un tiempo aleatorio entre 1 y 2 segundos
        int tiempoEspera = rand.nextInt(1000) + 1000; 
        Thread.sleep(tiempoEspera);
        
        salir(nino); // Luego sale de la lista
    }

    // --- Métodos 'Getter' útiles para tu Interfaz Gráfica y la Parte 2 (RMI) ---

    // Devuelve el número exacto de niños dentro (para los contadores)
    public int getNumeroNinos() {
        return ninosEnSotano.size();
    }

    // Devuelve una lista con los niños dentro (para mostrarlos en el JTextArea)
    public List<Nino> getNinosEnSotano() {
        return new ArrayList<>(ninosEnSotano);
    }
}
