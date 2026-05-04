package Clases;

public class Nino extends Thread {

    // --- Atributos de Identificación y Estado ---
    private String idNino;
    private boolean vivo = true;
    private boolean capturado = false;
    private boolean bajoAtaque = false; 
    private boolean inmune = false; 
    private int sangreRecolectada = 0;
    private final AgrupacionZonas zonas; 

    // --- Constructor ---
    public Nino(String idNino, AgrupacionZonas zonas) {
        this.idNino = idNino;
        this.zonas = zonas;
    }

    // --- Ciclo de Vida (FSM - Máquina de Estados) ---
    @Override
    public void run() {
        try {
            // Inicialización en el sistema
            zonas.esperarSiPausado();
            zonas.getCallePrincipal().inicio(this);

            while (vivo) {
                // 1. ESTADO: Sótano Byers (Preparación)
                zonas.esperarSiPausado(); 
                zonas.getSotanoByers().entrarZona(this);
                Thread.sleep(1000 + (long)(Math.random() * 1001)); 
                zonas.esperarSiPausado(); 
                zonas.getSotanoByers().salirZona(this);

                // 2. ESTADO: Selección y cruce de Portal
                int rutaElegida = (int) (Math.random() * 4);
                zonas.esperarSiPausado(); 
                zonas.getPortal(rutaElegida).cruzarAlUpsideDown(this);

                // 3. ESTADO: Upside Down (Recolección de Sangre)
                gestionarFaseUpsideDown(rutaElegida);

                // 4. ESTADO: Regreso a Hawkins (Portal de vuelta)
                zonas.esperarSiPausado();
                zonas.getPortal(rutaElegida).cruzarAHawkins(this);

                // 5. ESTADO: Radio WSQK (Entrega de recursos)
                gestionarFaseRadio();

                // 6. ESTADO: Calle Principal (Deambular)
                zonas.getCallePrincipal().deambular(this);
                // El bucle reinicia y el hilo vuelve al Sótano Byers
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // --- Lógica Interna de Fases ---

    /**
     * Gestiona la estancia en el Upside Down y la posibilidad de ser capturado.
     */
    private void gestionarFaseUpsideDown(int ruta) throws InterruptedException {
        ZonaInsegura zonaActual = zonas.getUpsidedown().getZonas().get(ruta);
        zonaActual.entrarNino(this); 

        boolean sobreviveYEscapa = true;
        long tiempoRestante = 3000 + (long)(Math.random() * 2001);
        
        // Efecto Tormenta: se tarda el doble en recolectar
        if (zonas.isTormentaUpsideDown()) tiempoRestante *= 2; 

        while (tiempoRestante > 0 && sobreviveYEscapa) {
            long inicioExtraccion = System.currentTimeMillis();
            try {
                zonaActual.recolectarSangre(tiempoRestante);
                
                // Éxito en la recolección
                tiempoRestante = 0; 
                this.sangreRecolectada = zonas.isTormentaUpsideDown() ? 2 : 1;
                zonaActual.registrarExtraccionGlobal(this.sangreRecolectada);
                Logs.getInstance().log(idNino + " ha recolectado sangre con éxito.");

            } catch (InterruptedException e) {
                // Interrupción por Ataque de Demogorgon
                long tiempoPasado = System.currentTimeMillis() - inicioExtraccion;
                tiempoRestante -= tiempoPasado;
                if (tiempoRestante < 0) tiempoRestante = 0;

                this.sangreRecolectada = 0; 
                
                synchronized (this) {
                    // Esperar a que el ataque termine (éxito o fallo)
                    while (bajoAtaque) { this.wait(); } 
                    zonas.esperarSiPausado(); 
                    
                    if (capturado) {
                        sobreviveYEscapa = false;
                        // Bloqueo en la Colmena hasta rescate de Eleven
                        while (capturado) { this.wait(); } 
                        zonas.esperarSiPausado(); 
                        Logs.getInstance().log(idNino + " inicia retirada tras ser rescatado.");
                    } else {
                        this.inmune = true; // El niño resistió el ataque
                        Logs.getInstance().log(idNino + " RESISTE con inmunidad.");
                    }
                }
            }
        }
        this.inmune = false;
        zonaActual.salirNino(this);
    }

    private void gestionarFaseRadio() throws InterruptedException {
        zonas.esperarSiPausado(); 
        if (this.sangreRecolectada > 0) {
            zonas.getRadioWSQK().depositarSangre(this);
        }
        
        zonas.getRadioWSQK().entrarZona(this);
        Thread.sleep(2000 + (long)(Math.random() * 2001));
        zonas.esperarSiPausado(); 
        zonas.getRadioWSQK().salirZona(this);
    }

    // --- Getters y Setters ---
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
}