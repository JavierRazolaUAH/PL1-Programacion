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
 public synchronized void depositarSangre(Nino nino) {
        int sangreTraida = nino.getSangreRecolectada();
        if (sangreTraida > 0) {
            this.sangreTotalAlmacenada += sangreTraida;
            nino.setSangreRecolectada(0);
            Logs.getInstance().log(nino.getIdNino() + " ha depositado 1 unidad de sangre en la RADIO. Total sangre: " + sangreTotalAlmacenada);
        }
    }

    // Unificamos el entrar y el descansar aquí
    public void entrarZona(Nino nino) throws InterruptedException {
        // 1. Entra a la lista
        ninosEnRadio.put(nino);
        Logs.getInstance().log(nino.getIdNino() + " ha ENTRADO a descansar a la Radio WSQK.");
        
        // 2. Tiempo de descanso: Aleatorio entre 2 y 4 segundos
        int tiempoDescanso = rand.nextInt(2000) + 2000; 
        Thread.sleep(tiempoDescanso);
        
        // 3. Llama al método salir
        salirZona(nino);
    }

    public void salirZona(Nino nino) {
        if (ninosEnRadio.remove(nino)) {
            Logs.getInstance().log(nino.getIdNino() + " ha terminado de descansar y SALE de la Radio WSQK.");
        }
    }

public void descansar(Nino nino) throws InterruptedException {
        ninosEnRadio.put(nino);
        Logs.getInstance().log(nino.getIdNino() + " ha ENTRADO a descansar a la Radio WSQK.");
        
        // Tiempo de descanso: Aleatorio entre 2 y 4 segundos
        int tiempoDescanso = rand.nextInt(2000) + 2000; 
        Thread.sleep(tiempoDescanso);
        
        salirZona(nino);
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
