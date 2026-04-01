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
    private final int capacidadGrupo; // 2, 3 o 4 dependiendo del portal
    
    // --- HERRAMIENTAS DE SINCRONIZACIÓN ---
    private final Lock lock = new ReentrantLock();
    private final Condition esperandoParaCruzarAlUpsideDown = lock.newCondition();
    private final Condition esperandoParaVolverAHawkins = lock.newCondition();
    
    // --- ESTADO DEL PORTAL ---
    private boolean ocupado = false;
    private int esperandoVolver = 0; // Contador para darles prioridad
    private Nino cruzando = null;    // Para pintar en la interfaz quién está dentro
    
    // --- LISTAS DE ESPERA ---
    private final Queue<Nino> colaHaciaUpsideDown = new LinkedList<>();
    private final List<Nino> grupoActual = new ArrayList<>();
    private final List<Nino> esperandoVolverLista = new ArrayList<>();

    // Constructor: ahora recibe cuántos niños hacen falta para formar grupo
    public Portal(String nombre, int capacidadGrupo) {
        this.nombre = nombre;
        this.capacidadGrupo = capacidadGrupo;
    }

    // =================================================================
    // 1. SALIR AL UPSIDE DOWN (Requiere formar grupo y tiene baja prioridad)
    // =================================================================
    public void cruzarAlUpsideDown(Nino nino) throws InterruptedException {
        lock.lock();
        try {
            colaHaciaUpsideDown.add(nino);
            Logs.getInstance().log(nino.getIdNino() + " espera en " + nombre + " para ir al Upside Down. (Esperando: " + colaHaciaUpsideDown.size() + "/" + capacidadGrupo + ")");
            
            // Avisamos por si ya se puede formar un grupo
            esperandoParaCruzarAlUpsideDown.signalAll();

            // Bucle de espera: Mientras no esté en el grupo, o el portal esté ocupado, o haya gente queriendo volver (PRIORIDAD)
            while (!grupoActual.contains(nino) || ocupado || esperandoVolver > 0) {
                
                // Si no hay grupo activo, hay suficientes niños para formarlo, no hay nadie queriendo volver, y está libre: FORMAMOS GRUPO
                if (grupoActual.isEmpty() && colaHaciaUpsideDown.size() >= capacidadGrupo && esperandoVolver == 0 && !ocupado) {
                    for (int i = 0; i < capacidadGrupo; i++) {
                        grupoActual.add(colaHaciaUpsideDown.poll());
                    }
                    Logs.getInstance().log("¡Grupo formado en " + nombre + " para cruzar al Upside Down!");
                    esperandoParaCruzarAlUpsideDown.signalAll(); // Despierta a los afortunados
                }
                
                esperandoParaCruzarAlUpsideDown.await(); // Se queda dormido hasta que le toque
            }

            // --- COMIENZA A CRUZAR ---
            ocupado = true;
            Logs.getInstance().log(nino.getIdNino() + " EMPIEZA a cruzar " + nombre + " hacia el Upside Down.");
        } finally {
            lock.unlock();
        }

        // Fuera del lock simulamos el tiempo de cruce (1 segundo)
        cruzando = nino;
        Thread.sleep(1000); 
        cruzando = null;

        lock.lock();
        try {
            ocupado = false;
            grupoActual.remove(nino); // Ya ha cruzado, sale del grupo
            
            // Si el grupo ha terminado de cruzar y hay gente para formar otro (y nadie quiere volver), formamos otro grupo
            if (grupoActual.isEmpty() && colaHaciaUpsideDown.size() >= capacidadGrupo && esperandoVolver == 0) {
                for (int i = 0; i < capacidadGrupo; i++) {
                    grupoActual.add(colaHaciaUpsideDown.poll());
                }
            }
            
            // --- GESTIÓN DE PRIORIDADES AL TERMINAR ---
            if (esperandoVolver > 0) {
                esperandoParaVolverAHawkins.signal(); // Damos paso a los que vuelven
            } else {
                esperandoParaCruzarAlUpsideDown.signalAll(); // Si no, damos paso al resto del grupo o a grupos nuevos
            }
            Logs.getInstance().log(nino.getIdNino() + " TERMINA de cruzar " + nombre + " y entra al Upside Down.");
        } finally {
            lock.unlock();
        }
    }


    // =================================================================
    // 2. REGRESAR A HAWKINS (Cruce individual, PRIORIDAD ABSOLUTA)
    // =================================================================
    public void cruzarAHawkins(Nino nino) throws InterruptedException {
        lock.lock();
        try {
            esperandoVolver++; // Aumentamos el contador de prioridad
            esperandoVolverLista.add(nino);
            
            // Si está ocupado, tiene que esperar sí o sí
            while (ocupado) {
                Logs.getInstance().log(nino.getIdNino() + " espera en " + nombre + " para VOLVER a Hawkins. (¡Tiene prioridad!)");
                esperandoParaVolverAHawkins.await();
            }
            
            ocupado = true;
            Logs.getInstance().log(nino.getIdNino() + " EMPIEZA a cruzar " + nombre + " volviendo a Hawkins.");
        } finally {
            lock.unlock();
        }

        // Simula el cruce individual
        cruzando = nino;
        Thread.sleep(1000);
        cruzando = null;

        lock.lock();
        try {
            ocupado = false;
            esperandoVolver--;
            esperandoVolverLista.remove(nino);
            
            // Al terminar de cruzar, si hay más niños queriendo volver, pasan ellos
            if (esperandoVolver > 0) {
                esperandoParaVolverAHawkins.signal();
            } else {
                // Si ya no queda nadie para volver, dejamos que crucen los grupos hacia el Upside Down
                esperandoParaCruzarAlUpsideDown.signalAll();
            }
            Logs.getInstance().log(nino.getIdNino() + " TERMINA de cruzar " + nombre + " y está a salvo en Hawkins.");
        } finally {
            lock.unlock();
        }
    }

    // --- GETTERS (Igual que los tenías en Tunel.java para tu Interfaz) ---
    public String getNombre() { return nombre; }
    public Nino getCruzando() { return cruzando; }
    
    public List<Nino> getNinosEsperandoAlUpsideDown() {
        lock.lock();
        try { return new ArrayList<>(colaHaciaUpsideDown); } finally { lock.unlock(); }
    }
    
    public List<Nino> getNinosEsperandoAHawkins() {
        lock.lock();
        try { return new ArrayList<>(esperandoVolverLista); } finally { lock.unlock(); }
    }
}

