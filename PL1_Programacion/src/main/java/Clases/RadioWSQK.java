/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;

/**
 *
 * @author javir
 */

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class RadioWSQK {
    // Cola concurrente para almacenar a los niños que están descansando en la radio
    private final BlockingQueue<Nino> ninosEnRadio;
    private final Random rand;
    private int sangreTotalAlmacenada = 0;

    public RadioWSQK() {
        this.ninosEnRadio = new LinkedBlockingQueue<>();
        this.rand = new Random();
    }

// --- MÉTODOS DE ENTRADA Y SALIDA ---
    public void entrar(Nino nino) throws InterruptedException {
        ninosEnRadio.put(nino);
    }

    public void salir(Nino nino) {
        ninosEnRadio.remove(nino);
    }

    // --- MÉTODOS DE ACCIÓN ---
    
    // Método sincronizado para depositar la sangre de forma segura
    public synchronized void depositarSangre(Nino nino) {
        // Asumiendo que tu clase Nino tiene un getSangreRecolectada() que devuelve 1 o 0
        int sangreTraida = nino.getSangreRecolectada();
        if (sangreTraida > 0) {
            this.sangreTotalAlmacenada += sangreTraida;
            nino.setSangreRecolectada(0); // El niño se queda a 0 tras depositarla
            // Logs.getInstance().log(nino.getIdNino() + " ha depositado 1 ud de sangre. Total: " + sangreTotalAlmacenada);
        }
    }

    public void descansar(Nino nino) throws InterruptedException {
        entrar(nino);
        
        int tiempoDescanso = rand.nextInt(2000) + 2000; 
        Thread.sleep(tiempoDescanso);
        
        salir(nino);
    }

    // --- MÉTODOS GETTER (Para la Interfaz) ---

    public int getNumeroNinos() {
        return ninosEnRadio.size();
    }

    public List<Nino> getNinosEnRadio() {
        return new ArrayList<>(ninosEnRadio);
    }
    public synchronized int getSangreTotalAlmacenada() { 
        return sangreTotalAlmacenada; 
    }
}
