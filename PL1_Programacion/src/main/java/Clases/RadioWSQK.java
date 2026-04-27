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
import java.util.concurrent.atomic.AtomicInteger;

public class RadioWSQK {
    // Lista de niños descansando
    private final BlockingQueue<Nino> ninosEnRadio;
    private final AtomicInteger sangreTotalAlmacenada = new AtomicInteger(0);
    private final AgrupacionZonas zonas;
    private final Random rand = new Random();

    public RadioWSQK(AgrupacionZonas zonas) {
        this.ninosEnRadio = new LinkedBlockingQueue<>();
        this.zonas = zonas;
    }

    // --- GESTIÓN DE SANGRE ---

    public void depositarSangre(Nino nino) {
        int sangreTraida = nino.getSangreRecolectada();
        if (sangreTraida > 0) {
            // Sumamos de forma atómica
            int totalActual = sangreTotalAlmacenada.addAndGet(sangreTraida);
            nino.setSangreRecolectada(0);
            Logs.getInstance().log(nino.getIdNino() + " ha depositado " + sangreTraida + 
                " unidad(es) de sangre. Total en RADIO: " + totalActual);
        }
    }

    /**
     * MÉTODO CLAVE: Eleven consume sangre para rescatar niños.
     * Esto evita que la sangre crezca infinitamente y permite que el ciclo siga.
     */
    public void consumirSangre(int cantidad) {
        int actual = sangreTotalAlmacenada.get();
        // Restamos asegurándonos de no bajar de cero
        int nuevaCantidad = Math.max(0, actual - cantidad);
        sangreTotalAlmacenada.set(nuevaCantidad);
    }

    // --- MÉTODOS DE ENTRADA Y SALIDA ---

    public void entrarZona(Nino nino) throws InterruptedException {
        // Solo añadimos a la lista. El tiempo de descanso lo controla el Nino.run()
        if (!ninosEnRadio.contains(nino)) {
            ninosEnRadio.put(nino);
            Logs.getInstance().log(nino.getIdNino() + " ha ENTRADO a la Radio WSQK para descansar.");
        }
    }

    public void salirZona(Nino nino) {
        if (ninosEnRadio.remove(nino)) {
            Logs.getInstance().log(nino.getIdNino() + " ha terminado de descansar y SALE de la Radio WSQK.");
        }
    }

    // --- GETTERS ---

    public int getNumeroNinos() {
        return ninosEnRadio.size();
    }

    public List<Nino> getNinosEnRadio() {
        return new ArrayList<>(ninosEnRadio);
    }

    public int getSangreTotalAlmacenada() {
        return sangreTotalAlmacenada.get();
    }
}
