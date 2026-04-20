package Clases;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Alex338
 */
public class Colmena {
    // Cola donde se guardan los niños capturados (Tema 5)
    private final BlockingQueue<Nino> prisioneros = new LinkedBlockingQueue<>();
    
    // Contador acumulado para saber cuándo crear un nuevo Demogorgon
    private final AtomicInteger contadorCapturasTotal = new AtomicInteger(0);
    private final AtomicInteger contadorDemogorgons = new AtomicInteger(1); // Empezamos en 1 porque el 0 es el Alpha
    
    // Referencia al mundo para poder añadir nuevos Demogorgons
    private AgrupacionZonas zonas;

    public Colmena() {
    }
    
    // Método para conectar la colmena con el mundo tras la creación
    public void setZonas(AgrupacionZonas zonas) {
        this.zonas = zonas;
    }

    /**
     * Método que llama el Demogorgon al depositar un niño.
     */
    public void depositarNino(Nino nino) {
        prisioneros.add(nino);
        int total = contadorCapturasTotal.incrementAndGet();
        
        // Usamos el 'total' histórico en lugar del size() de la cola
        if (zonas != null && total % 8 == 0) {
            Logs.getInstance().log("¡Los Demogorgons han capturado 8 niños! Nace un nuevo Demogorgon.");
            crearNuevoDemogorgon();
        }
    }

    /**
     * Lógica para generar un nuevo hilo Demogorgon (Tema 6: Ciclo de vida)
     */
    private void crearNuevoDemogorgon() {
        // Incrementamos y obtenemos el siguiente número
        int siguienteId = contadorDemogorgons.getAndIncrement();
        
        // Formateamos para que siempre tenga 4 cifras: D0001, D0002... 
        String idFormateado = String.format("D%04d", siguienteId);
        
        Demogorgon nuevo = new Demogorgon(idFormateado, this.zonas);
        nuevo.start();
    }

    /**
     * Método que llamará Eleven en su evento para liberar niños.
     * @param cantidad Número de niños a liberar según la sangre recolectada.
     */
    public void liberarNinos(int cantidad) {
        int liberados = 0;
        
        // Sacamos de la lista hasta llegar a la cantidad, o hasta que se vacíe la colmena
        while (liberados < cantidad && !prisioneros.isEmpty()) {
            Nino rescatado = prisioneros.poll(); // Saca al niño de la cola
            
            if (rescatado != null) {
                // ¡AQUÍ ESTÁ LA MAGIA! Nos sincronizamos con el hilo del niño para despertarlo
                synchronized (rescatado) {
                    rescatado.setCapturado(false); // Le quitamos las esposas
                    rescatado.notifyAll();         // ¡Despierta del wait() y huye hacia el portal!
                }
                liberados++;
            }
        }
        
        if (liberados > 0) {
            Logs.getInstance().log("⚡ ELEVEN ha usado sus poderes y ha rescatado a " + liberados + " niños de la Colmena.");
        } else {
            Logs.getInstance().log("⚡ ELEVEN ha llegado a la Colmena, pero estaba vacía. No había nadie a quien rescatar.");
        }
    }

    // --- Getters para la interfaz ---
    public int getNumPrisioneros() {
        return prisioneros.size();
    }
    
    public int getCapturasHistoricas() {
        return contadorCapturasTotal.get();
    }
}