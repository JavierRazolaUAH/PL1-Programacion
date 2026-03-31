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
            // 1. Desplazamiento aleatorio a una de las 4 áreas inseguras 
            ZonaInsegura zonaActual = zonas.getUpsidedown().obtenerZonaAleatoria();
            
            // Registrar entrada para la interfaz [cite: 95]
            zonaActual.entrarDemogorgon(this);
            
            // 2. Comprobar presencia de niños [cite: 42]
            Nino objetivo = zonaActual.seleccionarVictima();

            if (objetivo != null) {
                // FASE DE ATAQUE: Duración entre 0,5 y 1,5 segundos [cite: 42]
                Thread.sleep(500 + random.nextInt(1001));

                // Probabilidad de éxito 1/3 (Resistencia del niño es 2/3) 
                if (random.nextInt(3) == 0) {
                    // ÉXITO: El niño es capturado [cite: 43]
                    zonaActual.salirDemogorgon(this); // Sale de la zona para el traslado
                    realizarCaptura(objetivo, zonaActual);
                } else {
                    // FRACASO: El niño permanece en el área [cite: 44]
                    // Tras el ataque fallido, se desplaza a otra área [cite: 45]
                    zonaActual.salirDemogorgon(this);
                }
            } else {
                // ZONA VACÍA: Permanece entre 4 y 5 segundos [cite: 46]
                Thread.sleep(4000 + random.nextInt(1001));
                
                // Tras la espera, se desplaza a otra área [cite: 46]
                zonaActual.salirDemogorgon(this);
            }
            
            // El demogorgon vuelve a iterar seleccionando una nueva área 
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}

private void realizarCaptura(Nino victima, ZonaInsegura zona) throws InterruptedException {
    // 1. Traslado a la COLMENA [cite: 43]
    zona.salirNino(victima);
    victima.setCapturado(true); 

    // 2. Tiempo para depositar al niño: entre 0,5 y 1 segundos [cite: 43]
    Thread.sleep(500 + random.nextInt(501));

    // 3. Depositar en la COLMENA e incrementar contador de capturas [cite: 43, 64]
    zonas.getUpsidedown().getColmena().depositarNino(victima);
    this.capturasRealizadas++;
}

    public String getIdDemogorgon() {
        return idDemogorgon;
    }

    public int getCapturasRealizadas() {
        return capturasRealizadas;
    }
}