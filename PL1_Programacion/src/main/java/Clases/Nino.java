package Clases;

public class Nino extends Thread {
    private String idNino;
    private boolean vivo = true;
    private boolean capturado = false;
    private boolean bajoAtaque = false; 
    private boolean inmune = false; // <-- ESCUDO DE INMUNIDAD (I-Frames)
    private int sangreRecolectada = 0;
    private final AgrupacionZonas zonas; 

    public Nino(String idNino, AgrupacionZonas zonas) {
        this.idNino = idNino;
        this.zonas = zonas;
    }

    public String getIdNino() { return idNino; }
    public boolean isVivo() { return vivo; }
    public void setVivo(boolean vivo) { this.vivo = vivo; }
    public boolean isCapturado() { return capturado; }
    public void setCapturado(boolean capturado) { this.capturado = capturado; }
    public boolean isBajoAtaque() { return bajoAtaque; }
    public void setBajoAtaque(boolean bajoAtaque) { this.bajoAtaque = bajoAtaque; }
    public int getSangreRecolectada() { return sangreRecolectada; }
    public void setSangreRecolectada(int sangreRecolectada) { this.sangreRecolectada = sangreRecolectada; }
    public boolean isInmune() { return inmune; }
    

    @Override
    public void run() {
        try {
            zonas.esperarSiPausado();
            zonas.getCallePrincipal().inicio(this);

            while (vivo) {
                // --- SÓTANO BYERS (1 a 2 segundos) ---
                zonas.esperarSiPausado(); 
                zonas.getSotanoByers().entrarZona(this);
                
                Thread.sleep(1000 + (long)(Math.random() * 1001)); 

                zonas.esperarSiPausado(); 
                zonas.getSotanoByers().salirZona(this);

                // --- SELECCIÓN DE PORTAL ---
                int rutaElegida = (int) (Math.random() * 4);
                zonas.esperarSiPausado(); 
                zonas.getPortal(rutaElegida).cruzarAlUpsideDown(this);

                // --- UPSIDE DOWN: RECOLECCIÓN (3 a 5 segundos) ---
                ZonaInsegura zonaActual = zonas.getUpsidedown().getZonas().get(rutaElegida);
                zonaActual.entrarNino(this); 

                boolean sobreviveYEscapa = true;
                
                // Calculamos el tiempo base que requiere el niño
                long tiempoRestante = 3000 + (long)(Math.random() * 2001);
                if (zonas.isTormentaUpsideDown()) {
                    tiempoRestante *= 2; 
                }

                while (tiempoRestante > 0 && sobreviveYEscapa) {
                    long inicioExtraccion = System.currentTimeMillis();
                    
                    try {
                        // Le pasamos el tiempo exacto que debe dormir a la zona
                        zonaActual.recolectarSangre(tiempoRestante);
                        
                        // Si no hay interrupción, termina con éxito
                        tiempoRestante = 0; 
                        this.sangreRecolectada = zonas.isTormentaUpsideDown() ? 2 : 1;
                        zonaActual.registrarExtraccionGlobal(this.sangreRecolectada); // Suma al total de la zona
                        Logs.getInstance().log(idNino + " ha recolectado " + this.sangreRecolectada + " ud(s) con éxito.");

                    } catch (InterruptedException e) {
                        // ¡ATACADO! Calculamos cuánto tiempo le dio tiempo a avanzar
                        long tiempoPasado = System.currentTimeMillis() - inicioExtraccion;
                        tiempoRestante -= tiempoPasado;
                        if (tiempoRestante < 0) tiempoRestante = 0;

                        this.sangreRecolectada = 0; // Pierde la sangre momentáneamente
                        
                        synchronized (this) {
                            while (bajoAtaque) { this.wait(); } // Forcejeo con el monstruo
                            zonas.esperarSiPausado(); 
                            
                            if (capturado) {
                                // PIERDE Y ES CAPTURADO
                                sobreviveYEscapa = false;
                                while (capturado) { this.wait(); } // Espera el rescate en la colmena
                                zonas.esperarSiPausado(); 
                            } else {
                                // ¡RESISTE Y REANUDA SU MARCHA!
                                this.inmune = true; // Activa I-Frames
                                Logs.getInstance().log(idNino + " RESISTE. Reanuda su marcha con INMUNIDAD. Quedan: " + tiempoRestante + "ms.");
                            }
                        }
                    }
                }

                // Al salir de la zona, pierde el escudo de inmunidad
                this.inmune = false;
                zonaActual.salirNino(this);

                // --- PORTAL VUELTA O RESCATE ---
                if (!sobreviveYEscapa) {
                    // Si llega aquí, es porque fue rescatado por Eleven
                    zonas.getCallePrincipal().deambular(this);
                    Thread.sleep(3000 + (long)(Math.random() * 2001)); // Deambula 3 a 5 seg
                    continue; 
                }

                zonas.esperarSiPausado();
                zonas.getPortal(rutaElegida).cruzarAHawkins(this);

                // --- RADIO WSQK (2 a 4 segundos) ---
                zonas.esperarSiPausado(); 
                zonas.getRadioWSQK().depositarSangre(this);
                zonas.getRadioWSQK().entrarZona(this);

                Thread.sleep(2000 + (long)(Math.random() * 2001));

                zonas.esperarSiPausado(); 
                zonas.getRadioWSQK().salirZona(this);

                // --- CALLE PRINCIPAL (3 a 5 segundos) ---
                zonas.getCallePrincipal().deambular(this);
                Thread.sleep(3000 + (long)(Math.random() * 2001));
                
                zonas.esperarSiPausado();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}