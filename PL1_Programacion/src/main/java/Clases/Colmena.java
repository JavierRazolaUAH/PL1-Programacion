package Clases;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Alex338
 * Esta clase actúa como Monitor para los niños capturados.
 * Según el Tema 5, usamos BlockingQueue para gestionar la cola de prisioneros.
 */
public class Colmena {
    // Cola donde se guardan los niños capturados (Tema 5)
    private final BlockingQueue<Nino> prisioneros = new LinkedBlockingQueue<>();
    
    // Contador acumulado para saber cuándo crear un nuevo Demogorgon
    private final AtomicInteger contadorCapturasTotal = new AtomicInteger(0);
    
    // Referencia al mundo para poder añadir nuevos Demogorgons
    private UpsideDown mundo;

    public Colmena() {
        // Se inicializa vacía
    }
    
    // Método para conectar la colmena con el mundo tras la creación
    public void setMundo(UpsideDown mundo) {
        this.mundo = mundo;
    }

    /**
     * Método que llama el Demogorgon al depositar un niño.
     */
    public void depositarNino(Nino nino) {
        // Añadimos al niño a la cola de prisioneros
        prisioneros.add(nino);
        
        // Incrementamos el contador y comprobamos si llegamos a 8
        int total = contadorCapturasTotal.incrementAndGet();
        
        if (total % 8 == 0) {
            crearNuevoDemogorgon();
        }
    }

    /**
     * Lógica para generar un nuevo hilo Demogorgon (Tema 6: Ciclo de vida)
     */
    private void crearNuevoDemogorgon() {
        String id = "D" + (1000 + (int)(Math.random() * 9000));
        Demogorgon nuevo = new Demogorgon(id, mundo);
        nuevo.start(); // El nuevo hilo empieza a patrullar inmediatamente
        // Log.escribir("¡Ha nacido un nuevo Demogorgon en la Colmena: " + id + "!");
    }

    /**
     * Método que llamará Eleven en su evento para liberar niños.
     * @param cantidad Número de niños a liberar según la sangre recolectada.
     */
    public void liberarNinos(int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            Nino liberado = prisioneros.poll(); // Saca al niño de la cola
            if (liberado != null) {
                liberado.setCapturado(false);
                // Aquí el hilo del niño despertará o continuará su ciclo
            }
        }
    }

    // Getters para la interfaz
    public int getNumPrisioneros() {
        return prisioneros.size();
    }
    
    public int getCapturasHistoricas() {
        return contadorCapturasTotal.get();
    }
}