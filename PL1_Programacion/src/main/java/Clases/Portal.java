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
    private final AgrupacionZonas zonas; // <--- ¡NUEVO! Necesitamos acceder a las zonas para leer los eventos
    
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

    // ¡OJO AQUÍ! Hemos actualizado el constructor para recibir AgrupacionZonas
    public Portal(String nombre, int capacidadGrupo, AgrupacionZonas zonas) {
        this.nombre = nombre;
        this.capacidadGrupo = capacidadGrupo;
        this.zonas = zonas;
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
                // 1. Si soy el PRIMERO de la fila, el túnel está libre... ¡Y NO HAY APAGÓN!
                if (!grupoActual.isEmpty() && grupoActual.get(0).equals(nino) && !ocupado && esperandoVolver == 0 && !zonas.isApagonLaboratorio()) {
                    break; 
                }
                
                // 2. Si no hay grupo formado, hay gente suficiente, el túnel está libre... ¡Y NO HAY APAGÓN!
                if (grupoActual.isEmpty() && colaHaciaUpsideDown.size() >= capacidadGrupo && esperandoVolver == 0 && !ocupado && !zonas.isApagonLaboratorio()) {
                    for (int i = 0; i < capacidadGrupo; i++) {
                        grupoActual.add(colaHaciaUpsideDown.poll());
                    }
                    esperandoParaCruzar.signalAll(); // Avisamos a todos
                    continue; // Reiniciamos el bucle para que el primero del nuevo grupo pase
                }
                
                // 3. Si no es mi turno (o hay apagón), a dormir.
                // --- ¡BLINDAJE ANTI-DEADLOCK! ---
                try {
                    esperandoParaCruzar.await(); 
                    zonas.esperarSiPausado();
                } catch (InterruptedException e) {
                    // Si el niño muere mientras espera la ida, nos aseguramos de borrarlo de las listas
                    colaHaciaUpsideDown.remove(nino);
                    grupoActual.remove(nino);
                    // Avisamos a los demás por si hemos roto un grupo a medio formar
                    esperandoParaCruzar.signalAll();
                    throw e; // Propagamos la interrupción para que el hilo Nino sepa que ha muerto
                }
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
        zonas.esperarSiPausado();
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
                // Si soy el primero de la cola para volver, el túnel está libre... ¡Y NO HAY APAGÓN!
                if (!esperandoVolverLista.isEmpty() && esperandoVolverLista.get(0).equals(nino) && !ocupado && !zonas.isApagonLaboratorio()) {
                    break;
                }
                
                // --- ¡BLINDAJE ANTI-DEADLOCK! ---
                try {
                    esperandoParaVolver.await();
                    zonas.esperarSiPausado();
                } catch (InterruptedException e) {
                    // Si el niño muere mientras espera volver (el Sniper Demogorgon), LIMPIAMOS LA PUERTA
                    esperandoVolverLista.remove(nino);
                    esperandoVolver--; // Reducimos la prioridad para no bloquear el portal
                    // Avisamos a los siguientes (ya sean otros regresos o la ida si no quedan regresos)
                    if (esperandoVolver > 0) esperandoParaVolver.signalAll();
                    else esperandoParaCruzar.signalAll();
                    throw e; // Propagamos la interrupción
                }
            }
            
            ocupado = true;
            esperandoVolverLista.remove(nino); // Desaparece del derecho
            cruzando = nino;                   // Aparece en el central
            Logs.getInstance().log(nino.getIdNino() + " EMPIEZA a cruzar " + nombre + " volviendo a Hawkins.");
        } finally {
            lock.unlock();
        }

        Thread.sleep(1000);
        zonas.esperarSiPausado();
        
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


