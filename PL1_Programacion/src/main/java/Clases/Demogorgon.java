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
        // Sacamos zonaActual fuera del bucle para que el Demogorgon "recuerde" dónde está
        ZonaInsegura zonaActual = null; 

        try {
            while (!Thread.currentThread().isInterrupted()) {
                zonas.esperarSiPausado(); 

                // --- EVENTO: INTERVENCIÓN DE ELEVEN ---
                // Si Eleven está actuando, los Demogorgons se quedan totalmente paralizados
                while (zonas.isIntervencionEleven()) {
                    Thread.sleep(500); 
                }

                // --- FASE DE DESPLAZAMIENTO (Afectada por Apagón y Red Mental) ---
                ZonaInsegura zonaNueva;

                if (zonaActual == null) {
                    // Si acaba de nacer (o viene de la Colmena), aparece en un sitio aleatorio
                    zonaNueva = zonas.getUpsidedown().obtenerZonaAleatoria();
                } else if (zonas.isApagonLaboratorio()) {
                    // EVENTO: APAGÓN -> No puede cambiar de zona
                    zonaNueva = zonaActual;
                } else if (zonas.isRedMental()) {
                    // EVENTO: LA RED MENTAL -> Va a la zona con más niños
                    zonaNueva = zonas.getUpsidedown().obtenerZonaMasPoblada();
                } else {
                    // Normalidad -> Se desplaza a una zona aleatoria
                    zonaNueva = zonas.getUpsidedown().obtenerZonaAleatoria();
                }
                
                // Si ha cambiado de zona, registramos la salida y la entrada en la Interfaz
                if (zonaActual != zonaNueva) {
                    if (zonaActual != null) {
                        zonaActual.salirDemogorgon(this);
                    }
                    zonaActual = zonaNueva;
                    zonaActual.entrarDemogorgon(this);
                }
                
                zonas.esperarSiPausado(); 

                // 2. Comprobar presencia de niños 
                Nino objetivo = zonaActual.seleccionarVictima();

                if (objetivo != null) {
                    // Blindaje Anti-Race Condition
                    synchronized (objetivo) {
                        if (!zonaActual.getNinosEnZona().contains(objetivo)) {
                            continue; // Se escapó
                        }
                        objetivo.setBajoAtaque(true); 
                        objetivo.interrupt(); 
                    }
                    
                    Logs.getInstance().log(idDemogorgon + " está ATACANDO a " + objetivo.getIdNino() + " en " + zonaActual.getNombre());

                    // --- EVENTO: TORMENTA DEL UPSIDE DOWN ---
                    int tiempoAtaque = 500 + random.nextInt(1001);
                    if (zonas.isTormentaUpsideDown()) {
                        tiempoAtaque /= 2; // ¡Doble de rápido!
                    }
                    Thread.sleep(tiempoAtaque);
                    zonas.esperarSiPausado();
                    // Probabilidad de éxito 1/3
                    if (random.nextInt(3) == 0) {
                        // ÉXITO: El niño es capturado 
                        zonaActual.salirDemogorgon(this); 
                        realizarCaptura(objetivo, zonaActual);
                        zonaActual = null; // Como fue a la colmena, reseteamos su zona para el próximo bucle
                    } else {
                        // FRACASO: El niño resiste 
                        Logs.getInstance().log(objetivo.getIdNino() + " ha RESISTIDO el ataque de " + idDemogorgon + " y huye!");
                        
                        // Solo abandona la zona si NO hay apagón
                        if (!zonas.isApagonLaboratorio()) {
                            zonaActual.salirDemogorgon(this);
                            zonaActual = null;
                        }
                    }
                    
                    // El ataque termina. Soltamos al niño
                    synchronized (objetivo) {
                        objetivo.setBajoAtaque(false);
                        objetivo.notifyAll(); 
                    }
                    
                } else {
                    // ZONA VACÍA
                    int tiempoEspera = 4000 + random.nextInt(1001);
                    
                    // --- EVENTO: TORMENTA DEL UPSIDE DOWN ---
                    if (zonas.isTormentaUpsideDown()) {
                        tiempoEspera /= 2; // Menos tiempo entre ataques/esperas
                    }
                    Thread.sleep(tiempoEspera);
                    zonas.esperarSiPausado();
                    // Tras la espera, se desplaza a otra área (salvo que haya apagón)
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

    public String getIdDemogorgon() { return idDemogorgon; }
    public int getCapturasRealizadas() { return capturasRealizadas; }
}