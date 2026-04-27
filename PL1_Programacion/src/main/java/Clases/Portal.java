package Clases;

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
    private final AgrupacionZonas zonas;

    private final Lock lock = new ReentrantLock();
    private final Condition condicionPortal = lock.newCondition();

    private boolean ocupado = false;
    private Nino cruzando = null;

    private final Queue<Nino> colaIda = new LinkedList<>();
    private final List<Nino> grupoActual = new ArrayList<>();
    private final List<Nino> colaVuelta = new ArrayList<>();

    public Portal(String nombre, int capacidadGrupo, AgrupacionZonas zonas) {
        this.nombre = nombre;
        this.capacidadGrupo = capacidadGrupo;
        this.zonas = zonas;
    }

public void cruzarAlUpsideDown(Nino nino) throws InterruptedException {
    gestionarPausaFueraDeLock(); 

    lock.lock();
    try {
        if (!colaIda.contains(nino) && !grupoActual.contains(nino)) {
            colaIda.add(nino);
        }

        while (true) {
            if (grupoActual.isEmpty() && colaIda.size() >= capacidadGrupo) {
                for (int i = 0; i < capacidadGrupo; i++) {
                    grupoActual.add(colaIda.poll());
                }
                condicionPortal.signalAll();
            }

            boolean esMiTurno = !grupoActual.isEmpty() && grupoActual.get(0).equals(nino);
            boolean prioridadVuelta = !colaVuelta.isEmpty();

            if (esMiTurno && !ocupado && !prioridadVuelta && !zonas.isApagonLaboratorio() && !zonas.isPausado()) {
                break; 
            }

            try {
                condicionPortal.await();
                gestionarPausa(); 
            } catch (InterruptedException e) {
                colaIda.remove(nino);
                if (grupoActual.remove(nino)) {
                    colaIda.addAll(grupoActual);
                    grupoActual.clear();
                }
                condicionPortal.signalAll();
                throw e;
            }
        }

        // --- ENTRADA AL TÚNEL ---
        ocupado = true;
        cruzando = nino;
        grupoActual.remove(nino);
        Logs.getInstance().log(">>> [PORTAL " + nombre + "] " + nino.getIdNino() + " ha ENTRADO al túnel (Hawkins -> Upside Down)");

    } finally {
        lock.unlock();
    }

    try {
            Thread.sleep(1000);
            
            // --- CAMBIO AQUÍ: Primero pausamos si es necesario ---
            gestionarPausaFueraDeLock(); 
            
        } catch (InterruptedException e) {
            Logs.getInstance().log("[ALERTA] " + nino.getIdNino() + " ha sido interrumpido mientras cruzaba " + nombre);
            throw e;
        } finally {
            // --- CAMBIO AQUÍ: Solo liberamos cuando el juego NO esté pausado ---
            liberarPortalManual();
            Logs.getInstance().log("<<< [PORTAL " + nombre + "] " + nino.getIdNino() + " ha SALIDO del túnel hacia el Upside Down.");
        }
}

public void cruzarAHawkins(Nino nino) throws InterruptedException {
    gestionarPausaFueraDeLock();

    lock.lock();
    try {
        if (!colaVuelta.contains(nino)) colaVuelta.add(nino);

        while (true) {
            boolean esMiTurno = !colaVuelta.isEmpty() && colaVuelta.get(0).equals(nino);
            if (esMiTurno && !ocupado && !zonas.isApagonLaboratorio() && !zonas.isPausado()) {
                break;
            }
            
            try {
                condicionPortal.await();
                gestionarPausa();
            } catch (InterruptedException e) {
                colaVuelta.remove(nino);
                condicionPortal.signalAll();
                throw e;
            }
        }

        // --- ENTRADA AL TÚNEL ---
        ocupado = true;
        cruzando = nino;
        colaVuelta.remove(nino);
        Logs.getInstance().log(">>> [PORTAL " + nombre + "] " + nino.getIdNino() + " ha ENTRADO al túnel (Upside Down -> Hawkins)");
        
    } finally {
        lock.unlock();
    }

    try {
            Thread.sleep(1000);
            
            // --- CAMBIO AQUÍ: Pausa antes de salir ---
            gestionarPausaFueraDeLock();
            
        } catch (InterruptedException e) {
            Logs.getInstance().log("[ALERTA] " + nino.getIdNino() + " ha sido interrumpido mientras volvía por " + nombre);
            throw e;
        } finally {
            // --- CAMBIO AQUÍ: Liberar y log después de la pausa ---
            liberarPortalManual();
            Logs.getInstance().log("<<< [PORTAL " + nombre + "] " + nino.getIdNino() + " TERMINA de cruzar " + nombre + " y está a salvo.");
        }
}

    // Método auxiliar para cuando no tenemos el lock del portal cogido
    private void gestionarPausaFueraDeLock() throws InterruptedException {
        zonas.esperarSiPausado();
    }
    // MÉTODO MAESTRO DE LIBERACIÓN
    private void liberarPortalManual() {
        lock.lock();
        try {
            ocupado = false;
            cruzando = null;
            condicionPortal.signalAll(); // Despertar al siguiente
        } finally {
            lock.unlock();
        }
    }

    public void despertarHilos() {
        lock.lock();
        try {
            condicionPortal.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private void gestionarPausa() throws InterruptedException {
        if (zonas.isPausado()) {
            lock.unlock();
            try {
                zonas.esperarSiPausado();
            } finally {
                lock.lock();
            }
        }
    }

    // --- Getters ---
    public String getNombre() { return nombre; }
    
    public List<Nino> getCruzando() {
        lock.lock();
        try {
            List<Nino> l = new ArrayList<>();
            if (cruzando != null) l.add(cruzando);
            return l;
        } finally { lock.unlock(); }
    }
    
    public List<Nino> getNinosEsperandoAlUpsideDown() {
        lock.lock();
        try {
            List<Nino> l = new ArrayList<>(grupoActual);
            l.addAll(colaIda);
            return l;
        } finally { lock.unlock(); }
    }
    
    public List<Nino> getNinosEsperandoAHawkins() {
        lock.lock();
        try { return new ArrayList<>(colaVuelta); } finally { lock.unlock(); }
    }
}