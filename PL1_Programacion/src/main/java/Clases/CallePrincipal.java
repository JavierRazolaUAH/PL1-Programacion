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

public class CallePrincipal {
    // Cola para saber quién está en la calle en este momento
    private final BlockingQueue<Nino> ninosEnCalle;
    private final Random rand;

    public CallePrincipal() {
        this.ninosEnCalle = new LinkedBlockingQueue<>();
        this.rand = new Random();
    }

    // --- MÉTODOS DE LA SIMULACIÓN ---

    // 1. Método para cuando el niño acaba de ser creado en el Main
    public void inicio(Nino nino) throws InterruptedException {
        ninosEnCalle.put(nino);
        // Logs.getInstance().log(nino.getNombre() + " ha iniciado su actividad en la CALLE PRINCIPAL.");
        
        // No hay tiempo de espera obligatorio al inicio según el enunciado, 
        // así que el niño sale inmediatamente hacia el Sótano Byers.
        ninosEnCalle.remove(nino);
    }

    // 2. Método para el final del ciclo (Deambular)
    public void deambular(Nino nino) throws InterruptedException {
        ninosEnCalle.put(nino);
        // Logs.getInstance().log(nino.getNombre() + " está deambulando por la CALLE PRINCIPAL.");
        
        // Tiempo deambulando: Aleatorio entre 3 y 5 segundos (3000 - 5000 ms)
        int tiempoDeambular = rand.nextInt(2000) + 3000; 
        Thread.sleep(tiempoDeambular);
        
        ninosEnCalle.remove(nino);
        // Logs.getInstance().log(nino.getNombre() + " deja de deambular y vuelve al SÓTANO BYERS.");
    }

    // --- MÉTODOS GETTER (Para tu Interfaz y RMI) ---

    public int getNumeroNinos() {
        return ninosEnCalle.size();
    }

    public List<Nino> getNinosEnCalle() {
        return new ArrayList<>(ninosEnCalle);
    }
}
