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

  // --- MÉTODOS DE ENTRADA Y SALIDA ---
    public void entrar(Nino nino) throws InterruptedException {
        ninosEnCalle.put(nino);
    }

    public void salir(Nino nino) {
        ninosEnCalle.remove(nino);
    }

    // --- MÉTODOS DE ACCIÓN ---
    public void inicio(Nino nino) throws InterruptedException {
        entrar(nino);
        // Como no hay tiempo de espera al inicio, sale inmediatamente
        salir(nino);
    }

    public void deambular(Nino nino) throws InterruptedException {
        entrar(nino);
        
        int tiempoDeambular = rand.nextInt(2000) + 3000; 
        Thread.sleep(tiempoDeambular);
        
        salir(nino);
    }

    // --- MÉTODOS GETTER (Para tu Interfaz y RMI) ---

    public int getNumeroNinos() {
        return ninosEnCalle.size();
    }

    public List<Nino> getNinosEnCalle() {
        return new ArrayList<>(ninosEnCalle);
    }
}
