/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;

/**
 *
 * @author javir
 */
public class Nino  extends Thread{
private String idNino;
    private boolean vivo = true;
    private boolean capturado = false;
    private boolean bajoAtaque = false; 
    private int sangreRecolectada = 0;
    
    // El niño necesita conocer el mapa para moverse
    private final AgrupacionZonas zonas; 

    public Nino(String idNino, AgrupacionZonas zonas) {
        this.idNino = idNino;
        this.zonas = zonas;
    }

    // --- GETTERS Y SETTERS ---
    public String getIdNino() { return idNino; }
    public void setIdNino(String idNino) { this.idNino = idNino; }

    public boolean isVivo() { return vivo; }
    public void setVivo(boolean vivo) { this.vivo = vivo; }
    
    public boolean isCapturado() { return capturado; }
    public void setCapturado(boolean capturado) { this.capturado = capturado; }

    public boolean isBajoAtaque() { return bajoAtaque; }
    public void setBajoAtaque(boolean bajoAtaque) { this.bajoAtaque = bajoAtaque; }

    public int getSangreRecolectada() { return sangreRecolectada; }
    public void setSangreRecolectada(int sangreRecolectada) { this.sangreRecolectada = sangreRecolectada; }
    
    // ==========================================
    //        EL MOTOR DEL HILO (CICLO DE VIDA)
    // ==========================================
    @Override
    public void run() {
        try {
            // Bloqueo al nacer
            zonas.esperarSiPausado();
            zonas.getCallePrincipal().inicio(this);

            while (vivo) {
                // --- FASE 1: SÓTANO ---
                zonas.esperarSiPausado(); // Antes de entrar
                zonas.getSotanoByers().entrarZona(this);

                Thread.sleep(500); 

                // EL FILTRO QUE TE FALTA:
                // Si el juego se pausó mientras dormía el sleep de arriba,
                // el hilo debe morir aquí antes de decir que "terminó de prepararse".
                zonas.esperarSiPausado(); 
                zonas.getSotanoByers().salirZona(this);

                // --- FASE 2: ELECCIÓN DE RUTA ---
                int rutaElegida = (int) (Math.random() * 4);
                zonas.esperarSiPausado(); // Bloqueo tras elegir, antes de entrar al portal

                // --- FASE 3: PORTALES ---
                zonas.getPortal(rutaElegida).cruzarAlUpsideDown(this);

                // --- FASE 4: UPSIDE DOWN ---
                zonas.esperarSiPausado(); 
                ZonaInsegura zonaActual = zonas.getUpsidedown().getZonas().get(rutaElegida);
                zonaActual.entrarNino(this);

                try {
                    zonaActual.recolectarSangre(this);
                    zonas.esperarSiPausado(); // Bloqueo tras recolectar
                    this.sangreRecolectada = 1;
                } catch (InterruptedException e) {
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

                // --- FASE 6: RADIO ---
                zonas.esperarSiPausado();
                zonas.getRadioWSQK().depositarSangre(this);
                zonas.getRadioWSQK().entrarZona(this);

                Thread.sleep(500);

                zonas.esperarSiPausado(); // Bloqueo tras dormir en la radio
                zonas.getRadioWSQK().salirZona(this);

                // --- FASE 7: CALLE ---
                zonas.getCallePrincipal().deambular(this);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
