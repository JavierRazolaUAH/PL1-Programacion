package Clases;

import java.util.Random;

/**
 * @author Alex338
 */
public class Demogorgon extends Thread {
    private final String idDemogorgon;
    private final AgrupacionZonas zonas;
    private final Random random = new Random();
    private int capturasRealizadas = 0;

    public Demogorgon(String id, AgrupacionZonas zonas) {
        this.idDemogorgon = id;
        this.zonas = zonas;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // 1. ELIGE UNA ZONA ALEATORIA PARA CAZAR
                ZonaInsegura zonaActual = zonas.getUpsidedown().obtenerZonaAleatoria();
                
                // 2. INTENTA BUSCAR UNA VÍCTIMA
                Nino objetivo = zonaActual.seleccionarVictima();

                if (objetivo != null) {
                    // Si hay niños, tarda entre 0.5 y 1.5 segundos en atacar (Enunciado)
                    Thread.sleep(500 + random.nextInt(1001));

                    // Probabilidad de éxito 1/3
                    if (random.nextInt(3) == 0) {
                        realizarCaptura(objetivo, zonaActual);
                    } else {
                        // Log.escribir("Demogorgon " + idDemogorgon + " falló ataque en " + zonaActual.getNombre());
                    }
                } else {
                    // Si la zona está vacía, espera de 4 a 5 segundos antes de cambiar de zona (Enunciado)
                    Thread.sleep(4000 + random.nextInt(1001));
                }
            }
        } catch (InterruptedException e) {
            // Permitimos que el hilo termine si es interrumpido (Tema 6)
            Thread.currentThread().interrupt();
        }
    }

    private void realizarCaptura(Nino victima, ZonaInsegura zona) throws InterruptedException {
        // El niño deja de estar en la zona y pasa a estar capturado
        zona.salir(victima);
        victima.setCapturado(true); // Necesitarás este método en la clase Nino
        
        capturasRealizadas++;

        // El Demogorgon tarda entre 0.5 y 1 segundo en llevarlo a la Colmena (Enunciado)
        Thread.sleep(500 + random.nextInt(501));

        // Lo deposita en la Colmena
        zonas.getUpsidedown().getColmena().depositarNino(victima);
        
        // Log.escribir("Demogorgon " + idDemogorgon + " capturó a " + victima.getNombre() + " y lo llevó a la Colmena");
    }

    public String getIdDemogorgon() {
        return idDemogorgon;
    }

    public int getCapturasRealizadas() {
        return capturasRealizadas;
    }
}