/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;

/**
 *
 * @author javir
 */


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Portal {
private final String nombre;
    private final int capacidadGrupo; 
    
    // --- HERRAMIENTAS DE SINCRONIZACIÓN ---
    private final Lock lock = new ReentrantLock();
    private final Condition esperandoParaCruzar = lock.newCondition();
    private final Condition esperandoParaVolver = lock.newCondition();
    
    // --- ESTADO DEL PORTAL ---
    private boolean ocupado = false;
    private int esperandoVolver = 0; 
    private Nino cruzando = null; // El que está en el túnel en ese milisegundo
    
    // --- LISTAS DE ESPERA ---
    private final Queue<Nino> colaHaciaUpsideDown = new LinkedList<>();
    private final List<Nino> grupoActual = new ArrayList<>();
    private final List<Nino> esperandoVolverLista = new ArrayList<>();

    public Portal(String nombre, int capacidadGrupo) {
        this.nombre = nombre;
        this.capacidadGrupo = capacidadGrupo;
    }

    // =================================================================
    // 1. SALIR AL UPSIDE DOWN (Se forma el grupo, pero cruzan de 1 en 1)
    // =================================================================
    public void cruzarAlUpsideDown(Nino nino) throws InterruptedException {
        lock.lock();
        try {
            colaHaciaUpsideDown.add(nino);
            Logs.getInstance().log(nino.getIdNino() + " espera en " + nombre + " (Esperando: " + colaHaciaUpsideDown.size() + "/" + capacidadGrupo + ")");
            
            while (true) {
                // 1. Si soy el PRIMERO de la fila del grupo VIP, y el túnel está libre... ¡Me toca cruzar!
                if (!grupoActual.isEmpty() && grupoActual.get(0).equals(nino) && !ocupado && esperandoVolver == 0) {
                    break; 
                }
                
                // 2. Si no hay grupo formado, hay gente suficiente, y el túnel está libre... ¡Formamos grupo!
                if (grupoActual.isEmpty() && colaHaciaUpsideDown.size() >= capacidadGrupo && esperandoVolver == 0 && !ocupado) {
                    for (int i = 0; i < capacidadGrupo; i++) {
                        grupoActual.add(colaHaciaUpsideDown.poll());
                    }
                    esperandoParaCruzar.signalAll(); // Avisamos a todos
                    continue; // Reiniciamos el bucle para que el primero del nuevo grupo pase
                }
                
                // 3. Si no es mi turno, a dormir
                esperandoParaCruzar.await(); 
            }

            // --- COMIENZA A CRUZAR ---
            ocupado = true;
            grupoActual.remove(nino); // Desaparece del recuadro izquierdo 
            cruzando = nino;          // Aparece en el recuadro central
            Logs.getInstance().log(nino.getIdNino() + " EMPIEZA a cruzar " + nombre + " hacia el Upside Down.");
            
        } finally {
            lock.unlock();
        }

        // Simula el tiempo de cruce (1 segundo)
        Thread.sleep(1000); 

        lock.lock();
        try {
            cruzando = null; // Desaparece del túnel central
            ocupado = false;
            
            // Avisamos a los siguientes
            if (esperandoVolver > 0) {
                esperandoParaVolver.signalAll(); // Tienen prioridad los que vuelven
            } else {
                esperandoParaCruzar.signalAll(); // Que pase el siguiente del grupo de ida
            }
            Logs.getInstance().log(nino.getIdNino() + " TERMINA de cruzar " + nombre + " y entra al Upside Down.");
        } finally {
            lock.unlock();
        }
    }

    // =================================================================
    // 2. REGRESAR A HAWKINS (Cruce individual en orden, PRIORIDAD ABSOLUTA)
    // =================================================================
    public void cruzarAHawkins(Nino nino) throws InterruptedException {
        lock.lock();
        try {
            esperandoVolver++; 
            esperandoVolverLista.add(nino); // Aparece en el recuadro derecho
            
            while (true) {
                // Si soy el primero de la cola para volver y el túnel está libre, cruzo
                if (!esperandoVolverLista.isEmpty() && esperandoVolverLista.get(0).equals(nino) && !ocupado) {
                    break;
                }
                esperandoParaVolver.await();
            }
            
            ocupado = true;
            esperandoVolverLista.remove(nino); // Desaparece del derecho
            cruzando = nino;                   // Aparece en el central
            Logs.getInstance().log(nino.getIdNino() + " EMPIEZA a cruzar " + nombre + " volviendo a Hawkins.");
        } finally {
            lock.unlock();
        }

        Thread.sleep(1000);

        lock.lock();
        try {
            cruzando = null;
            ocupado = false;
            esperandoVolver--;
            
            if (esperandoVolver > 0) {
                esperandoParaVolver.signalAll(); // Pasa el siguiente que vuelve
            } else {
                esperandoParaCruzar.signalAll(); // Si no quedan regresos, volvemos a dar paso a la ida
            }
            Logs.getInstance().log(nino.getIdNino() + " TERMINA de cruzar " + nombre + " y está a salvo.");
        } finally {
            lock.unlock();
        }
    }

    // --- GETTERS PARA LA INTERFAZ ---
    public String getNombre() { return nombre; }
    
    // Este método devuelve una lista con 1 solo niño (o vacía) para que tu interfaz actual funcione sin cambiarla
    public List<Nino> getCruzando() { 
        lock.lock();
        try { 
            List<Nino> listaEnMedio = new ArrayList<>();
            if (cruzando != null) listaEnMedio.add(cruzando);
            return listaEnMedio; 
        } finally { lock.unlock(); }
    }
    
    public List<Nino> getNinosEsperandoAlUpsideDown() {
        lock.lock();
        try { 
            List<Nino> todosEnEspera = new ArrayList<>(grupoActual);
            todosEnEspera.addAll(colaHaciaUpsideDown);
            return todosEnEspera; 
        } finally { lock.unlock(); }
    }
    
    public List<Nino> getNinosEsperandoAHawkins() {
        lock.lock();
        try { return new ArrayList<>(esperandoVolverLista); } finally { lock.unlock(); }
    }
}


