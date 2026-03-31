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

    public RadioWSQK() {
        this.ninosEnRadio = new LinkedBlockingQueue<>();
        this.rand = new Random();
    }

    // Método que ejecutarán los niños al volver de los portales
    public void descansar(Nino nino) throws InterruptedException {
        // 1. El niño entra a la radio
        ninosEnRadio.put(nino);
        
        // Logs.getInstance().log(nino.getIdNino() + " ha entrado a descansar a la RADIO WSQK.");
        
        // 2. Tiempo de descanso: Aleatorio entre 2 y 4 segundos (2000 - 4000 ms)
        int tiempoDescanso = rand.nextInt(2000) + 2000; 
        Thread.sleep(tiempoDescanso);
        
        // 3. El niño termina su descanso y sale
        ninosEnRadio.remove(nino);
        
        // Logs.getInstance().log(nino.getIdNino() + " ha terminado de descansar y sale de la RADIO WSQK.");
    }

    // --- MÉTODOS GETTER (Para la Interfaz) ---

    public int getNumeroNinos() {
        return ninosEnRadio.size();
    }

    public List<Nino> getNinosEnRadio() {
        return new ArrayList<>(ninosEnRadio);
    }
}
