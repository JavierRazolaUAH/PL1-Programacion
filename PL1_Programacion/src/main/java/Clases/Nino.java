package Clases;

public class Nino extends Thread {
    private String idNino;
    private boolean vivo = true;
    private boolean capturado = false;
    private boolean bajoAtaque = false; 
    private int sangreRecolectada = 0;
    private final AgrupacionZonas zonas; 

    public Nino(String idNino, AgrupacionZonas zonas) {
        this.idNino = idNino;
        this.zonas = zonas;
    }

    // --- GETTERS Y SETTERS ---
    public String getIdNino() { return idNino; }
    public boolean isVivo() { return vivo; }
    public void setVivo(boolean vivo) { this.vivo = vivo; }
    public boolean isCapturado() { return capturado; }
    public void setCapturado(boolean capturado) { this.capturado = capturado; }
    public boolean isBajoAtaque() { return bajoAtaque; }
    public void setBajoAtaque(boolean bajoAtaque) { this.bajoAtaque = bajoAtaque; }
    public int getSangreRecolectada() { return sangreRecolectada; }
    public void setSangreRecolectada(int sangreRecolectada) { this.sangreRecolectada = sangreRecolectada; }

    @Override
    public void run() {
        try {
            zonas.esperarSiPausado();
            zonas.getCallePrincipal().inicio(this);

            while (vivo) {
                // --- FASE 1: SÓTANO ---
                zonas.esperarSiPausado(); 
                zonas.getSotanoByers().entrarZona(this);

                Thread.sleep(500); 

                zonas.esperarSiPausado(); 
                zonas.getSotanoByers().salirZona(this);

                // --- FASE 2: ELECCIÓN DE RUTA ---
                int rutaElegida = (int) (Math.random() * 4);
                zonas.esperarSiPausado(); 

                // --- FASE 3: PORTALES (HACIA UPSIDE DOWN) ---
                zonas.getPortal(rutaElegida).cruzarAlUpsideDown(this);

                // --- FASE 4: UPSIDE DOWN ---
                // Importante: La pausa ahora ocurre DENTRO de entrarNino para que 
                // el monitor vea que ya está en la lista antes de congelarse.
                ZonaInsegura zonaActual = zonas.getUpsidedown().getZonas().get(rutaElegida);
                zonaActual.entrarNino(this); 

                try {
                    zonaActual.recolectarSangre(this);
                    
                    // Pausa tras despertar del recolectar (antes de asignar la sangre)
                    zonas.esperarSiPausado(); 
                    
                    if (zonas.isTormentaUpsideDown()) {
                        this.sangreRecolectada = 2;
                        Logs.getInstance().log(idNino + " ha recolectado 2 uds (¡TORMENTA!).");
                    } else {
                        this.sangreRecolectada = 1;
                        Logs.getInstance().log(idNino + " ha recolectado 1 ud.");
                    }
                    
                } catch (InterruptedException e) {
                    this.sangreRecolectada = 0;
                    synchronized (this) {
                        while (bajoAtaque) { this.wait(); }
                        zonas.esperarSiPausado(); 
                        if (capturado) {
                            while (capturado) { this.wait(); }
                            zonas.esperarSiPausado(); 
                        }
                    }
                } finally {
                    zonaActual.salirNino(this);
                }

                // --- FASE 5: VUELTA ---
                zonas.esperarSiPausado();
                zonas.getPortal(rutaElegida).cruzarAHawkins(this);

                // --- FASE 6: RADIO (ENTREGA DE SANGRE) ---
                // CLAVE: Pausamos ANTES de depositar para que el RMI no cuente sangre de más
                zonas.esperarSiPausado(); 
                
                zonas.getRadioWSQK().depositarSangre(this);
                zonas.getRadioWSQK().entrarZona(this);

                Thread.sleep(500);

                zonas.esperarSiPausado(); 
                zonas.getRadioWSQK().salirZona(this);

                // --- FASE 7: CALLE ---
                zonas.getCallePrincipal().deambular(this);
                zonas.esperarSiPausado();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}