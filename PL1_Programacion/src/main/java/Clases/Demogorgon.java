package Clases;

import java.util.Random;

public class Demogorgon extends Thread {
    // --- Atributos ---
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
                
                zonaActual = gestionarMovimiento(zonaActual);
                zonas.esperarSiPausado();

                while (zonas.isIntervencionEleven()) {
                    Thread.sleep(500);
                }

                Nino objetivo = zonaActual.seleccionarVictima();
                
                if (objetivo != null) {
                    gestionarAtaque(objetivo, zonaActual);
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
        
        if (zonaActual == null) {
            zonaNueva = zonas.getUpsidedown().obtenerZonaAleatoria();
        } else if (zonas.isApagonLaboratorio()) {
            zonaNueva = zonaActual; 
        } else if (zonas.isRedMental()) {
            zonaNueva = zonas.getUpsidedown().obtenerZonaMasPoblada(); 
        } else {
            zonaNueva = zonas.getUpsidedown().obtenerZonaAleatoria();
        }
        // Realiza el cambio físico entre las listas de las zonas
        if (zonaActual != zonaNueva) {
            if (zonaActual != null) {
                zonaActual.salirDemogorgon(this); 
            }
            zonaNueva.entrarDemogorgon(this);
        }
        return zonaNueva;
    }

    // --- Lógica de Combate ---
    private void gestionarAtaque(Nino objetivo, ZonaInsegura zonaActual) {
        // Intenta "bloquear" al niño atómicamente para evitar ataques simultáneos
        if (objetivo.intentarAtrapar()) {
            boolean exitoCaptura = false;
            
            try {
                // Interrumpe la recolección de sangre del niño inmediatamente
                objetivo.interrupt();
                Logs.getInstance().log("El demogorgon " + idDemogorgon + " ataca al niño " + objetivo.getIdNino() + " (capturas: " + capturasRealizadas + ")");

                int tiempoAtaque = 500 + random.nextInt(1001);
                if (zonas.isTormentaUpsideDown()) {
                    tiempoAtaque /= 2; 
                }
                Thread.sleep(tiempoAtaque);

                exitoCaptura = (random.nextInt(3) == 0);
                
                if (exitoCaptura) {
                    zonaActual.salirDemogorgon(this);
                    realizarCaptura(objetivo, zonaActual);
                } else {
                    Logs.getInstance().log(idDemogorgon + " ha FALLADO su ataque contra " + objetivo.getIdNino() + ".");
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                // Notifica al niño el resultado final para que pueda reanudar su marcha o quedar cautivo
                objetivo.resolverAtaque(exitoCaptura);
            }
        }
    }

    // --- Lógica de Captura y Espera ---
    private void realizarCaptura(Nino victima, ZonaInsegura zona) throws InterruptedException {
        zona.salirNino(victima);
        Logs.getInstance().log("El niño " + victima.getIdNino() + " ha sido capturado");
        
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

    public String getIdDemogorgon() { return idDemogorgon; }
    public int getCapturasRealizadas() { return capturasRealizadas; }
}