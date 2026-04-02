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

    public int getSangreRecolectada() { return sangreRecolectada; }
    public void setSangreRecolectada(int sangreRecolectada) { this.sangreRecolectada = sangreRecolectada; }
    
    // ==========================================
    //       EL MOTOR DEL HILO (CICLO DE VIDA)
    // ==========================================
    @Override
    public void run() {
        try {
            // 1. FASE DE NACIMIENTO
            zonas.getCallePrincipal().inicio(this);

            // 2. CICLO ITERATIVO
            while (vivo) {
                zonas.esperarSiPausado();
                
                // --- 1. PREPARACIÓN EN SÓTANO BYERS ---
                zonas.getSotanoByers().entrarZona(this);
                
                zonas.esperarSiPausado();

                // --- ¡EL TRUCO DE LA RUTA! ---
                // Tiramos el dado UNA sola vez (0 = Bosque, 1 = Lab, 2 = Centro Comercial, 3 = Alcantarillado)
                int rutaElegida = (int) (Math.random() * 4);

                // --- 2. CRUZAR PORTAL HACIA EL UPSIDE DOWN ---
                zonas.getPortal(rutaElegida).cruzarAlUpsideDown(this);
                
                zonas.esperarSiPausado();
                
                // --- 3. UPSIDE DOWN (Recolección y Peligro) ---
                // Entra exactamente a la zona insegura que está al otro lado de su portal
                ZonaInsegura zonaActual = zonas.getUpsidedown().getZonas().get(rutaElegida);
                zonaActual.entrarNino(this);
                
                try {
                    // Intenta recolectar sangre (esto tiene el Thread.sleep que el Demogorgon puede interrumpir)
                    zonaActual.recolectarSangre(this);
                    
                    // Si llega a esta línea, es que NADIE le ha interrumpido. ¡Éxito!
                    this.sangreRecolectada = 1; // Guarda 1 unidad de sangre en la mochila
                    Logs.getInstance().log(idNino + " ha recolectado sangre con éxito en " + zonaActual.getNombre());
                    
                } catch (InterruptedException e) {
                    // ¡SI CAE AQUÍ, ES QUE UN DEMOGORGON LE HA DADO CAZA!
                    if (capturado) {
                        Logs.getInstance().log(idNino + " ha sido arrastrado a la Colmena. Esperando rescate...");
                        synchronized (this) {
                            while (capturado) {
                                this.wait(); // Se congela aquí hasta que Eleven llame a notify()
                            }
                        }
                        Logs.getInstance().log(idNino + " ¡Ha sido liberado por Eleven y huye a Hawkins!");
                    }
                } finally {
                    // Pase lo que pase (recolecte o sea capturado), al final sale de la zona insegura
                    zonaActual.salirNino(this);
                }

                zonas.esperarSiPausado();

                // --- 4. CRUZAR PORTAL DE REGRESO A HAWKINS ---
                // Vuelve a casa exactamente por el mismo portal por el que entró
                zonas.getPortal(rutaElegida).cruzarAHawkins(this);

                zonas.esperarSiPausado();
                
                // --- 5. RADIO WSQK (Depositar y Descansar) ---
                // Primero deja la sangre (el método ya se encarga de poner su mochila a 0)
                zonas.getRadioWSQK().depositarSangre(this);
                // Luego entra a descansar
                zonas.getRadioWSQK().entrarZona(this);
                
                zonas.esperarSiPausado();
                
                // --- 6. CALLE PRINCIPAL (Deambular) ---
                zonas.getCallePrincipal().deambular(this);
            }

        } catch (InterruptedException e) {
            // Este catch global se activará si interrumpes el hilo desde la ventana principal para apagar el programa
            System.out.println(idNino + " ha sido interrumpido de forma global. Terminando hilo.");
            Thread.currentThread().interrupt();
        }
    }
    
}
