package Clases;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Portal {

    // --- Atributos de Configuración ---
    private final String nombre;
    private final int capacidadGrupo;
    private final AgrupacionZonas zonas;

    // --- Herramientas de Sincronización ---
    private final Lock lock = new ReentrantLock();
    private final Condition condicionPortal = lock.newCondition();

    // --- Estado del Túnel ---
    private boolean ocupado = false;
    private Nino cruzando = null;

    // --- Colas de Espera y Tránsito ---
    private final Queue<Nino> colaIda = new LinkedList<>();    // Cola general de entrada
    private final List<Nino> grupoActual = new ArrayList<>();   // Grupo que tiene permiso de cruce
    private final List<Nino> colaVuelta = new ArrayList<>();    // Niños que quieren volver (Prioridad)

    public Portal(String nombre, int capacidadGrupo, AgrupacionZonas zonas) {
        this.nombre = nombre;
        this.capacidadGrupo = capacidadGrupo;
        this.zonas = zonas;
    }

    // --- Lógica de Cruce: Hawkins -> Upside Down ---

    /**
     * Gestiona el acceso al Upside Down en grupos de tamaño fijo.
     */
    public void cruzarAlUpsideDown(Nino nino) throws InterruptedException {
        gestionarPausaFueraDeLock(); 

        lock.lock();
        try {
            // Registro en cola de espera
            if (!colaIda.contains(nino) && !grupoActual.contains(nino)) {
                colaIda.add(nino);
            }

            while (true) {
                // Lógica de formación de grupos
                if (grupoActual.isEmpty() && colaIda.size() >= capacidadGrupo) {
                    for (int i = 0; i < capacidadGrupo; i++) {
                        grupoActual.add(colaIda.poll());
                    }
                    condicionPortal.signalAll();
                }

                // Condiciones de acceso al túnel
                boolean esMiTurno = !grupoActual.isEmpty() && grupoActual.get(0).equals(nino);
                boolean prioridadVuelta = !colaVuelta.isEmpty(); // El retorno tiene prioridad sobre la ida

                if (esMiTurno && !ocupado && !prioridadVuelta && !zonas.isApagonLaboratorio() && !zonas.isPausado()) {
                    break; 
                }

                try {
                    condicionPortal.await();
                    gestionarPausa(); 
                } catch (InterruptedException e) {
                    gestionarInterrupcionEnEspera(nino);
                    throw e;
                }
            }

            ocupado = true;
            cruzando = nino;
            grupoActual.remove(nino);
            Logs.getInstance().log(">>> [PORTAL " + nombre + "] " + nino.getIdNino() + " ha ENTRADO al túnel (Hawkins -> Upside Down)");

        } finally {
            lock.unlock();
        }

        realizarTransitoFisico(nino, "<<< [PORTAL " + nombre + "] " + nino.getIdNino() + " ha SALIDO hacia el Upside Down.");
    }

    // --- Lógica de Cruce: Upside Down -> Hawkins ---

    /**
     * Gestiona el regreso a Hawkins con prioridad sobre los que intentan entrar.
     */
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

            ocupado = true;
            cruzando = nino;
            colaVuelta.remove(nino);
            Logs.getInstance().log(">>> [PORTAL " + nombre + "] " + nino.getIdNino() + " ha ENTRADO al túnel (Upside Down -> Hawkins)");
            
        } finally {
            lock.unlock();
        }

        realizarTransitoFisico(nino, "<<< [PORTAL " + nombre + "] " + nino.getIdNino() + " TERMINA de cruzar y está a salvo.");
    }

    // --- Métodos de Apoyo ---

    private void realizarTransitoFisico(Nino nino, String mensajeExito) throws InterruptedException {
        try {
            Thread.sleep(1000); // Tiempo que tarda en recorrer el túnel
            gestionarPausaFueraDeLock();
        } catch (InterruptedException e) {
            Logs.getInstance().log("[ALERTA] " + nino.getIdNino() + " interrumpido en " + nombre);
            throw e;
        } finally {
            liberarPortalManual();
            Logs.getInstance().log(mensajeExito);
        }
    }

    private void liberarPortalManual() {
        lock.lock();
        try {
            ocupado = false;
            cruzando = null;
            condicionPortal.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private void gestionarInterrupcionEnEspera(Nino nino) {
        colaIda.remove(nino);
        if (grupoActual.remove(nino)) {
            colaIda.addAll(grupoActual);
            grupoActual.clear();
        }
        condicionPortal.signalAll();
    }

    private void gestionarPausa() throws InterruptedException {
        if (zonas.isPausado()) {
            lock.unlock();
            try {
                zonas.wait(); // O el método de pausa que utilices
            } finally {
                lock.lock();
            }
        }
    }

    private void gestionarPausaFueraDeLock() throws InterruptedException {
        zonas.esperarSiPausado();
    }

    public void despertarHilos() {
        lock.lock();
        try {
            condicionPortal.signalAll();
        } finally {
            lock.unlock();
        }
    }

    // --- Consultas de Estado para Interfaz ---

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