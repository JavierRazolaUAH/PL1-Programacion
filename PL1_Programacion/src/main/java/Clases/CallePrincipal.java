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
    private AgrupacionZonas zonas;

    public CallePrincipal(AgrupacionZonas zonas) {
        this.ninosEnCalle = new LinkedBlockingQueue<>();
        this.rand = new Random();
        this.zonas= zonas;
    }

    // --- MÉTODOS DE LA SIMULACIÓN ---

  // --- MÉTODOS DE ENTRADA Y SALIDA ---
public void inicio(Nino nino) throws InterruptedException {
        ninosEnCalle.put(nino);
        Logs.getInstance().log(nino.getIdNino() + " ha INICIADO su actividad en Hawkins (Calle Principal).");
        Thread.sleep(1000);
        zonas.esperarSiPausado();
        salirZona(nino); // Sale automáticamente sin esperar
    }

    public void deambular(Nino nino) throws InterruptedException {
        ninosEnCalle.put(nino);
        Logs.getInstance().log(nino.getIdNino() + " está DEAMBULANDO por la Calle Principal.");
        
        // Tiempo deambulando: Aleatorio entre 3 y 5 segundos
        int tiempoDeambular = rand.nextInt(2000) + 3000; 
        Thread.sleep(tiempoDeambular);
        zonas.esperarSiPausado();
        
        salirZona(nino);
    }

    public void salirZona(Nino nino) {
        if (ninosEnCalle.remove(nino)) {
            Logs.getInstance().log(nino.getIdNino() + " ha SALIDO de la Calle Principal.");
        }
    }

    // --- MÉTODOS GETTER (Para tu Interfaz y RMI) ---

    public int getNumeroNinos() {
        return ninosEnCalle.size();
    }

    public List<Nino> getNinosEnCalle() {
        return new ArrayList<>(ninosEnCalle);
    }
}
