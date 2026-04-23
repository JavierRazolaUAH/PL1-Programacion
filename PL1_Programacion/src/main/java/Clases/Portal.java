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
        // REGISTRO INICIAL
        lock.lock();
        try {
            if (!colaIda.contains(nino) && !grupoActual.contains(nino)) {
                colaIda.add(nino);
            }
        } finally {
            lock.unlock();
        }

        try {
            lock.lock();
            try {
                while (true) {
                    // Formar grupo
                    if (grupoActual.isEmpty() && colaIda.size() >= capacidadGrupo) {
                        for (int i = 0; i < capacidadGrupo; i++) {
                            grupoActual.add(colaIda.poll());
                        }
                        condicionPortal.signalAll();
                    }

                    boolean esMiTurno = !grupoActual.isEmpty() && grupoActual.get(0).equals(nino);
                    boolean prioridadVuelta = !colaVuelta.isEmpty();

                    // Si es mi turno pero algo me para, logeamos la causa
                    if (esMiTurno) {
                        if (prioridadVuelta) Logs.getInstance().log("[ESPERA] " + nino.getIdNino() + " en " + nombre + ": Prioridad niños volviendo.");
                        else if (ocupado) Logs.getInstance().log("[ESPERA] " + nino.getIdNino() + " en " + nombre + ": Túnel ocupado por " + (cruzando != null ? cruzando.getIdNino() : "Nadie (ERROR DE ESTADO)"));
                        else if (zonas.isApagonLaboratorio()) Logs.getInstance().log("[ESPERA] " + nino.getIdNino() + " en " + nombre + ": Apagón activo.");
                    }

                    if (esMiTurno && !ocupado && !prioridadVuelta && !zonas.isApagonLaboratorio() && !zonas.isPausado()) {
                        break;
                    }
                    condicionPortal.await();
                    gestionarPausa();
                }

                // RESERVAR EL TÚNEL
                ocupado = true;
                cruzando = nino;
                grupoActual.remove(nino);
                Logs.getInstance().log(nino.getIdNino() + " EMPIEZA a cruzar " + nombre + " hacia el Upside Down.");
            } finally {
                lock.unlock();
            }

            // SIMULACIÓN (Esto puede ser interrumpido por el Demogorgon)
            Thread.sleep(1000);

        } catch (InterruptedException e) {
            Logs.getInstance().log("[ALERTA] " + nino.getIdNino() + " ha sido interrumpido mientras cruzaba " + nombre);
            throw e;
        } finally {
            // ESTA ES LA CLAVE: Liberar siempre al salir, sea por éxito o por error/interrupción
            liberarPortalManual();
            Logs.getInstance().log(nino.getIdNino() + " HA SALIDO del túnel de " + nombre + ".");
        }
    }

    public void cruzarAHawkins(Nino nino) throws InterruptedException {
        lock.lock();
        try {
            if (!colaVuelta.contains(nino)) colaVuelta.add(nino);
        } finally {
            lock.unlock();
        }

        try {
            lock.lock();
            try {
                while (true) {
                    boolean esMiTurno = !colaVuelta.isEmpty() && colaVuelta.get(0).equals(nino);
                    if (esMiTurno && !ocupado && !zonas.isApagonLaboratorio() && !zonas.isPausado()) {
                        break;
                    }
                    condicionPortal.await();
                    gestionarPausa();
                }

                ocupado = true;
                cruzando = nino;
                colaVuelta.remove(nino);
                Logs.getInstance().log(nino.getIdNino() + " EMPIEZA a cruzar " + nombre + " volviendo a Hawkins.");
            } finally {
                lock.unlock();
            }

            Thread.sleep(1000);

        } catch (InterruptedException e) {
            throw e;
        } finally {
            liberarPortalManual();
            Logs.getInstance().log(nino.getIdNino() + " TERMINA de cruzar " + nombre + " y está a salvo.");
        }
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