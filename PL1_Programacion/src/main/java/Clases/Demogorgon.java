package Clases;

import java.util.Random;

public class Demogorgon extends Thread {
    
    // --- Atributos de Identificación y Estado ---
    private final String idDemogorgon;
    private final AgrupacionZonas zonas;
    private final Random random = new Random();
    private int capturasRealizadas = 0;

    // --- Constructor ---
    public Demogorgon(String id, AgrupacionZonas zonas) {
        this.idDemogorgon = id;
        this.zonas = zonas;
    }

    // --- Ciclo de Vida del Hilo ---
    @Override
    public void run() {
        ZonaInsegura zonaActual = null; 

        try {
            while (!Thread.currentThread().isInterrupted()) {
                zonas.esperarSiPausado(); 

                // --- Fase de Desplazamiento ---
                zonaActual = gestionarMovimiento(zonaActual);
                
                zonas.esperarSiPausado(); 

                // --- Manejo de Eventos: Parálisis por Eleven ---
                while (zonas.isIntervencionEleven()) {
                    Thread.sleep(500); 
                }

                // --- Lógica de Combate y Captura ---
                Nino objetivo = zonaActual.seleccionarVictima();

                if (objetivo != null) {
                    gestionarAtaque(objetivo, zonaActual);
                    // Si hubo captura, zonaActual se resetea en el flujo de realizarCaptura
                    if (objetivo.isCapturado()) {
                        zonaActual = null;
                    }
                } else {
                    gestionarEsperaEnZonaVacia();
                }
            }
        } catch (InterruptedException e) {
            System.out.println(idDemogorgon + " interrumpido. Terminando hilo.");
            Thread.currentThread().interrupt();
        }
    }

    // --- Lógica de Movimiento ---
    private ZonaInsegura gestionarMovimiento(ZonaInsegura zonaActual) {
        ZonaInsegura zonaNueva;

        // Selección de destino basada en eventos globales
        if (zonaActual == null) {
            zonaNueva = zonas.getUpsidedown().obtenerZonaAleatoria();
        } else if (zonas.isApagonLaboratorio()) {
            zonaNueva = zonaActual; // Se queda en la zona por el apagón
        } else if (zonas.isRedMental()) {
            zonaNueva = zonas.getUpsidedown().obtenerZonaMasPoblada();
        } else {
            zonaNueva = zonas.getUpsidedown().obtenerZonaAleatoria();
        }
        
        // Ejecución del cambio de zona
        if (zonaActual != zonaNueva) {
            if (zonaActual != null) {
                zonaActual.salirDemogorgon(this);
            }
            zonaNueva.entrarDemogorgon(this);
        }
        return zonaNueva;
    }

    // --- Lógica de Ataque ---
    private void gestionarAtaque(Nino objetivo, ZonaInsegura zonaActual) throws InterruptedException {
        synchronized (objetivo) {
            if (!zonaActual.getNinosEnZona().contains(objetivo)) {
                return; // El niño abandonó la zona antes del ataque
            }
            objetivo.setBajoAtaque(true); 
            objetivo.interrupt(); 
        }
        
        Logs.getInstance().log(idDemogorgon + " está ATACANDO a " + objetivo.getIdNino() + " en " + zonaActual.getNombre());

        // Cálculo de tiempo de ataque afectado por Tormenta
        int tiempoAtaque = 500 + random.nextInt(1001);
        if (zonas.isTormentaUpsideDown()) {
            tiempoAtaque /= 2;
        }
        Thread.sleep(tiempoAtaque);

        // Resolución de la probabilidad de captura (33%)
        if (random.nextInt(3) == 0) {
            zonaActual.salirDemogorgon(this); 
            realizarCaptura(objetivo, zonaActual);
        } else {
            Logs.getInstance().log(idDemogorgon + " ha FALLADO su ataque contra " + objetivo.getIdNino() + ".");
        }
        
        synchronized (objetivo) {
            objetivo.setBajoAtaque(false);
            objetivo.notifyAll(); 
        }
    }

    private void realizarCaptura(Nino victima, ZonaInsegura zona) throws InterruptedException {
        zona.salirNino(victima);
        victima.setCapturado(true); 

        Thread.sleep(500 + random.nextInt(501));

        zonas.getUpsidedown().getColmena().depositarNino(victima);
        this.capturasRealizadas++;
        Logs.getInstance().log(idDemogorgon + " ha encerrado a " + victima.getIdNino() + " en la Colmena.");
    }

    private void gestionarEsperaEnZonaVacia() throws InterruptedException {
        int tiempoEspera = 4000 + random.nextInt(1001);
        if (zonas.isTormentaUpsideDown()) {
            tiempoEspera /= 2; 
        }
        Thread.sleep(tiempoEspera);
    }

    // --- Getters ---
    public String getIdDemogorgon() { return idDemogorgon; }
    public int getCapturasRealizadas() { return capturasRealizadas; }
}