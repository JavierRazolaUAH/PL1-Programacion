package Clases;

public class Nino extends Thread {
    private String idNino;
    private boolean vivo = true;
    private boolean capturado = false;
    private boolean bajoAtaque = false; 
    private boolean inmune = false; 
    private int sangreRecolectada = 0;
    private final AgrupacionZonas zonas; 

    public Nino(String idNino, AgrupacionZonas zonas) {
        this.idNino = idNino;
        this.zonas = zonas;
    }

    // Getters y Setters (Se mantienen igual)
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
                // --- SÓTANO BYERS ---
                zonas.esperarSiPausado(); 
                zonas.getSotanoByers().entrarZona(this);
                Thread.sleep(1000 + (long)(Math.random() * 1001)); 
                zonas.esperarSiPausado(); 
                zonas.getSotanoByers().salirZona(this);

                // --- SELECCIÓN DE PORTAL ---
                int rutaElegida = (int) (Math.random() * 4);
                zonas.esperarSiPausado(); 
                zonas.getPortal(rutaElegida).cruzarAlUpsideDown(this);

                // --- UPSIDE DOWN: RECOLECCIÓN ---
                ZonaInsegura zonaActual = zonas.getUpsidedown().getZonas().get(rutaElegida);
                zonaActual.entrarNino(this); 

                boolean sobreviveYEscapa = true;
                long tiempoRestante = 3000 + (long)(Math.random() * 2001);
                if (zonas.isTormentaUpsideDown()) {
                    tiempoRestante *= 2; 
                }

                while (tiempoRestante > 0 && sobreviveYEscapa) {
                    long inicioExtraccion = System.currentTimeMillis();
                    try {
                        zonaActual.recolectarSangre(tiempoRestante);
                        
                        // Si termina sin interrupción
                        tiempoRestante = 0; 
                        this.sangreRecolectada = zonas.isTormentaUpsideDown() ? 2 : 1;
                        zonaActual.registrarExtraccionGlobal(this.sangreRecolectada);
                        Logs.getInstance().log(idNino + " ha recolectado sangre con éxito.");

                    } catch (InterruptedException e) {
                        long tiempoPasado = System.currentTimeMillis() - inicioExtraccion;
                        tiempoRestante -= tiempoPasado;
                        if (tiempoRestante < 0) tiempoRestante = 0;

                        this.sangreRecolectada = 0; 
                        
                        synchronized (this) {
                            while (bajoAtaque) { this.wait(); } 
                            zonas.esperarSiPausado(); 
                            
                            if (capturado) {
                                sobreviveYEscapa = false;
                                // Espera en la colmena hasta que Eleven intervenga
                                while (capturado) { this.wait(); } 
                                zonas.esperarSiPausado(); 
                            } else {
                                this.inmune = true; 
                                Logs.getInstance().log(idNino + " RESISTE con inmunidad.");
                            }
                        }
                    }
                }

                this.inmune = false;
                zonaActual.salirNino(this);

                // --- PORTAL DE VUELTA (CORREGIDO) ---
                // No usamos 'continue'. Todos los niños deben cruzar el portal físicamente.
                if (!sobreviveYEscapa) {
                    Logs.getInstance().log(idNino + " inicia retirada tras ser rescatado.");
                }

                zonas.esperarSiPausado();
                // Esta llamada asegura que el niño aparezca en la lista "Cruzando" del portal
                zonas.getPortal(rutaElegida).cruzarAHawkins(this);

                // --- RADIO WSQK ---
                // Solo depositan si traen sangre (los rescatados suelen traer 0)
                zonas.esperarSiPausado(); 
                if (this.sangreRecolectada > 0) {
                    zonas.getRadioWSQK().depositarSangre(this);
                }
                
                zonas.getRadioWSQK().entrarZona(this);
                Thread.sleep(2000 + (long)(Math.random() * 2001));
                zonas.esperarSiPausado(); 
                zonas.getRadioWSQK().salirZona(this);

                // --- CALLE PRINCIPAL ---
                zonas.getCallePrincipal().deambular(this);
                Thread.sleep(3000 + (long)(Math.random() * 2001));
                
                zonas.esperarSiPausado();
                // Reinicia el ciclo: vuelve al Sótano Byers
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}