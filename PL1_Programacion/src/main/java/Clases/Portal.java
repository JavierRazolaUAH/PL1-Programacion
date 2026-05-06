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

    // --- Herramientas de Sincronización ---
    private final Lock cerrojoPaso = new ReentrantLock();
    private final Condition condicionTurno = cerrojoPaso.newCondition();

    // --- Estado del Portal ---
    private boolean tunelEnUso = false;
    private Nino ninoCruzando = null;

    // --- Estructuras de Datos ---
    private final Queue<Nino> ninosEsperandoCruzar = new LinkedList<>();
    private final List<Nino> escuadronSalida = new ArrayList<>();
    private final List<Nino> ninosRetornando = new ArrayList<>();

    public Portal(String nombre, int capacidadGrupo, AgrupacionZonas zonas) {
        this.nombre = nombre;
        this.capacidadGrupo = capacidadGrupo;
        this.zonas = zonas;
    }

    public void cruzarAlUpsideDown(Nino nino) throws InterruptedException {
        gestionarPausaFueraDeLock(); 

        cerrojoPaso.lock();
        try {
            // Añadir a la cola si no está en ella ni en el escuadrón
            if (!ninosEsperandoCruzar.contains(nino) && !escuadronSalida.contains(nino)) {
                ninosEsperandoCruzar.add(nino);
            }

            while (true) {
                // Formación del grupo
                if (escuadronSalida.isEmpty() && ninosEsperandoCruzar.size() >= capacidadGrupo) {
                    for (int i = 0; i < capacidadGrupo; i++) {
                        escuadronSalida.add(ninosEsperandoCruzar.poll());
                    }
                    condicionTurno.signalAll();
                }

                // Condiciones de avance
                boolean esMiTurno = !escuadronSalida.isEmpty() && escuadronSalida.get(0).equals(nino);
                boolean prioridadVuelta = !ninosRetornando.isEmpty();

                if (esMiTurno && !tunelEnUso && !prioridadVuelta && !zonas.isApagonLaboratorio() && !zonas.isPausado()) {
                    break; 
                }

                try {
                    condicionTurno.await();
                    gestionarPausa(); 
                } catch (InterruptedException e) {
                    // Limpieza en caso de ser atacado mientras esperaba
                    ninosEsperandoCruzar.remove(nino);
                    if (escuadronSalida.remove(nino)) {
                        ninosEsperandoCruzar.addAll(escuadronSalida);
                        escuadronSalida.clear();
                    }
                    condicionTurno.signalAll();
                    throw e;
                }
            }

            // Iniciar el cruce
            tunelEnUso = true;
            ninoCruzando = nino;
            escuadronSalida.remove(nino);
            Logs.getInstance().log(">>> [PORTAL " + nombre + "] " + nino.getIdNino() + " ha ENTRADO al túnel (Hawkins -> Upside Down)");

        } finally {
            cerrojoPaso.unlock();
        }

        // Simulación física del tránsito
        try {
            Thread.sleep(1000);
            gestionarPausaFueraDeLock(); 
        } catch (InterruptedException e) {
            Logs.getInstance().log("[ALERTA] " + nino.getIdNino() + " ha sido interrumpido mientras cruzaba " + nombre);
            throw e;
        } finally {
            liberarPortalManual();
            Logs.getInstance().log("<<< [PORTAL " + nombre + "] " + nino.getIdNino() + " ha SALIDO del túnel hacia el Upside Down.");
        }
    }

    public void cruzarAHawkins(Nino nino) throws InterruptedException {
        gestionarPausaFueraDeLock();

        cerrojoPaso.lock();
        try {
            if (!ninosRetornando.contains(nino)) {
                ninosRetornando.add(nino);
            }

            while (true) {
                boolean esMiTurno = !ninosRetornando.isEmpty() && ninosRetornando.get(0).equals(nino);
                
                // Los que retornan NO comprueban si hay gente esperando para ir al Upside Down (Prioridad Absoluta)
                if (esMiTurno && !tunelEnUso && !zonas.isApagonLaboratorio() && !zonas.isPausado()) {
                    break;
                }
                
                try {
                    condicionTurno.await();
                    gestionarPausa();
                } catch (InterruptedException e) {
                    ninosRetornando.remove(nino);
                    condicionTurno.signalAll();
                    throw e;
                }
            }

            tunelEnUso = true;
            ninoCruzando = nino;
            ninosRetornando.remove(nino);
            Logs.getInstance().log(">>> [PORTAL " + nombre + "] " + nino.getIdNino() + " ha ENTRADO al túnel (Upside Down -> Hawkins)");
            
        } finally {
            cerrojoPaso.unlock();
        }

        try {
            Thread.sleep(1000);
            gestionarPausaFueraDeLock();
        } catch (InterruptedException e) {
            Logs.getInstance().log("[ALERTA] " + nino.getIdNino() + " ha sido interrumpido mientras volvía por " + nombre);
            throw e;
        } finally {
            liberarPortalManual();
            Logs.getInstance().log("<<< [PORTAL " + nombre + "] " + nino.getIdNino() + " TERMINA de cruzar " + nombre + " y está a salvo.");
        }
    }

    private void gestionarPausaFueraDeLock() throws InterruptedException {
        zonas.esperarSiPausado();
    }

    private void liberarPortalManual() {
        cerrojoPaso.lock();
        try {
            tunelEnUso = false;
            ninoCruzando = null;
            condicionTurno.signalAll();
        } finally {
            cerrojoPaso.unlock();
        }
    }

    public void despertarHilos() {
        cerrojoPaso.lock();
        try {
            condicionTurno.signalAll();
        } finally {
            cerrojoPaso.unlock();
        }
    }

    private void gestionarPausa() throws InterruptedException {
        if (zonas.isPausado()) {
            cerrojoPaso.unlock();
            try {
                zonas.esperarSiPausado();
            } finally {
                cerrojoPaso.lock();
            }
        }
    }

    public String getNombre() { return nombre; }
    
    public List<Nino> getCruzando() {
        cerrojoPaso.lock();
        try {
            List<Nino> l = new ArrayList<>();
            if (ninoCruzando != null) l.add(ninoCruzando);
            return l;
        } finally { cerrojoPaso.unlock(); }
    }
    
    public List<Nino> getNinosEsperandoAlUpsideDown() {
        cerrojoPaso.lock();
        try {
            List<Nino> l = new ArrayList<>(escuadronSalida);
            l.addAll(ninosEsperandoCruzar);
            return l;
        } finally { cerrojoPaso.unlock(); }
    }
    
    public List<Nino> getNinosEsperandoAHawkins() {
        cerrojoPaso.lock();
        try { return new ArrayList<>(ninosRetornando); } finally { cerrojoPaso.unlock(); }
    }
}