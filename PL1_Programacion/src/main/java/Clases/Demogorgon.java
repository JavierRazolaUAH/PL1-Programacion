package Clases;

import java.util.Random;

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
        ZonaInsegura zonaActual = null;

        try {
            while (!Thread.currentThread().isInterrupted()) {
                zonas.esperarSiPausado();

                while (zonas.isIntervencionEleven()) {
                    Thread.sleep(500);
                }

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

                if (zonaActual != zonaNueva) {
                    if (zonaActual != null) {
                        zonaActual.salirDemogorgon(this);
                    }
                    zonaActual = zonaNueva;
                    zonaActual.entrarDemogorgon(this);
                }

                zonas.esperarSiPausado();

                Nino objetivo = zonaActual.seleccionarVictima();

                if (objetivo != null) {
                    synchronized (objetivo) {
                        if (!zonaActual.getNinosEnZona().contains(objetivo)) {
                            continue;
                        }
                        objetivo.setBajoAtaque(true);
                        objetivo.interrupt();
                    }

                    Logs.getInstance().log(idDemogorgon + " está ATACANDO a " + objetivo.getIdNino() + " en " + zonaActual.getNombre());

                    int tiempoAtaque = 500 + random.nextInt(1001);
                    if (zonas.isTormentaUpsideDown()) {
                        tiempoAtaque /= 2;
                    }
                    
                    Thread.sleep(tiempoAtaque);
                    zonas.esperarSiPausado();

                    if (random.nextInt(3) == 0) {
                        zonaActual.salirDemogorgon(this);
                        realizarCaptura(objetivo, zonaActual);
                        zonaActual = null;
                    } else {
                        Logs.getInstance().log(objetivo.getIdNino() + " ha RESISTIDO el ataque de " + idDemogorgon + " y huye!");

                        if (!zonas.isApagonLaboratorio()) {
                            zonaActual.salirDemogorgon(this);
                            zonaActual = null;
                        }
                    }

                    synchronized (objetivo) {
                        objetivo.setBajoAtaque(false);
                        objetivo.notifyAll();
                    }

                } else {
                    int tiempoEspera = 4000 + random.nextInt(1001);

                    if (zonas.isTormentaUpsideDown()) {
                        tiempoEspera /= 2;
                    }
                    
                    Thread.sleep(tiempoEspera);
                    zonas.esperarSiPausado();

                    if (!zonas.isApagonLaboratorio()) {
                        zonaActual.salirDemogorgon(this);
                        zonaActual = null;
                    }
                }
            }
        } catch (InterruptedException e) {
            System.out.println(idDemogorgon + " interrumpido. Terminando hilo.");
            Thread.currentThread().interrupt();
        }
    }

    private void realizarCaptura(Nino victima, ZonaInsegura zona) throws InterruptedException {
        zona.salirNino(victima);
        victima.setCapturado(true);

        Thread.sleep(500 + random.nextInt(501));
        zonas.esperarSiPausado();
        zonas.getUpsidedown().getColmena().depositarNino(victima);
        this.capturasRealizadas++;
        
        Logs.getInstance().log(idDemogorgon + " ha encerrado a " + victima.getIdNino() + " en la Colmena.");
    }

    public String getIdDemogorgon() {
        return idDemogorgon;
    }

    public int getCapturasRealizadas() {
        return capturasRealizadas;
    }
}