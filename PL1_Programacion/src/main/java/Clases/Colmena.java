package Clases;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Colmena {

    // --- Atributos de Estado y Contadores ---
    private final BlockingQueue<Nino> prisioneros = new LinkedBlockingQueue<>();
    private final AtomicInteger contadorCapturasTotal = new AtomicInteger(0);
    private final AtomicInteger contadorDemogorgons = new AtomicInteger(1);
    private AgrupacionZonas zonas;

    // --- Constructor y Configuración ---
    public Colmena() {
    }

    public void setZonas(AgrupacionZonas zonas) {
        this.zonas = zonas;
    }

    // --- Lógica de Captura y Generación ---

    /**
     * Registra un niño capturado y evalúa la creación de nuevos enemigos.
     */
    public void depositarNino(Nino nino) {
        prisioneros.add(nino);
        contadorCapturasTotal.incrementAndGet();

        // Lógica de spawn: cada 8 capturas se genera una nueva entidad Demogorgon
        if (zonas != null && prisioneros.size() % 8 == 0) {
            Logs.getInstance().log("¡Los Demogorgons han capturado 8 niños! Nace un nuevo Demogorgon.");
            crearNuevoDemogorgon();
        }
    }

    /**
     * Instancia y arranca un nuevo hilo de Demogorgon con ID único.
     */
    private void crearNuevoDemogorgon() {
        int siguienteId = contadorDemogorgons.getAndIncrement();
        String idFormateado = String.format("D%04d", siguienteId);

        Demogorgon nuevo = new Demogorgon(idFormateado, this.zonas);
        this.zonas.getUpsidedown().registrarDemogorgon(nuevo);
        nuevo.start();
    }

    // --- Lógica de Rescate ---

    /**
     * Libera una cantidad específica de hilos Nino bloqueados en la colmena.
     */
    public int liberarNinos(int cantidad) {
        int liberados = 0;

        while (liberados < cantidad && !prisioneros.isEmpty()) {
            Nino rescatado = prisioneros.poll();

            if (rescatado != null) {
                // Sincronización sobre el objeto Nino para notificar la liberación
                synchronized (rescatado) {
                    rescatado.setCapturado(false);
                    rescatado.notifyAll();
                }
                liberados++;
            }
        }

        this.registrarLogRescate(liberados);
        return liberados;
    }

    // --- Métodos de Consulta y Utilidad ---

    private void registrarLogRescate(int liberados) {
        if (liberados > 0) {
            Logs.getInstance().log("⚡ ELEVEN ha usado sus poderes y ha rescatado a " + liberados + " niños de la Colmena.");
        } else {
            Logs.getInstance().log("⚡ ELEVEN ha llegado a la Colmena, pero estaba vacía.");
        }
    }

    public int getNumPrisioneros() {
        return prisioneros.size();
    }

    public int getCapturasHistoricas() {
        return contadorCapturasTotal.get();
    }
}