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

                // --- FASE DE DESPLAZAMIENTO ---
                ZonaInsegura zonaNueva;

                if (zonaActual == null) {
                    // Si acaba de nacer o viene de la Colmena
                    zonaNueva = zonas.getUpsidedown().obtenerZonaAleatoria();
                } else if (zonas.isApagonLaboratorio()) {
                    zonaNueva = zonaActual;
                } else if (zonas.isRedMental()) {
                    zonaNueva = zonas.getUpsidedown().obtenerZonaMasPoblada();
                } else {
                    zonaNueva = zonas.getUpsidedown().obtenerZonaAleatoria();
                }
                
                // Realizamos el cambio de zona limpiamente
                if (zonaActual != zonaNueva) {
                    if (zonaActual != null) {
                        zonaActual.salirDemogorgon(this);
                    }
                    zonaActual = zonaNueva;
                    zonaActual.entrarDemogorgon(this);
                }
                
                zonas.esperarSiPausado(); 

                // --- EVENTO: INTERVENCIÓN DE ELEVEN ---
                // ¡AQUÍ ESTÁ LA MAGIA! Lo paralizamos CUANDO YA ESTÁ DENTRO de una zona, 
                // así la interfaz gráfica lo sigue dibujando sin problema.
                while (zonas.isIntervencionEleven()) {
                    Thread.sleep(500); 
                }

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

                    if (random.nextInt(3) == 0) {
                        // ÉXITO: El niño es capturado 
                        zonaActual.salirDemogorgon(this); 
                        realizarCaptura(objetivo, zonaActual);
                        zonaActual = null; // Va a la colmena físicamente, así que reseteamos su zona
                    } else {
                        // FRACASO: El niño resiste 
                        Logs.getInstance().log(idDemogorgon + " ha FALLADO su ataque contra " + objetivo.getIdNino() + ".");
                        // Eliminamos el zonaActual = null de aquí para que la transición sea limpia
                    }
                    
                    synchronized (objetivo) {
                        objetivo.setBajoAtaque(false);
                        objetivo.notifyAll(); 
                    }
                    
                } else {
                    // ZONA VACÍA
                    int tiempoEspera = 4000 + random.nextInt(1001);
                    if (zonas.isTormentaUpsideDown()) {
                        tiempoEspera /= 2; 
                    }
                    Thread.sleep(tiempoEspera);
                    
                    // Eliminamos el zonaActual = null de aquí también.
                    // En la siguiente vuelta del bucle se moverá con total normalidad.
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

        zonas.getUpsidedown().getColmena().depositarNino(victima);
        this.capturasRealizadas++;
        Logs.getInstance().log(idDemogorgon + " ha encerrado a " + victima.getIdNino() + " en la Colmena.");
    }

    public String getIdDemogorgon() { return idDemogorgon; }
    public int getCapturasRealizadas() { return capturasRealizadas; }
}