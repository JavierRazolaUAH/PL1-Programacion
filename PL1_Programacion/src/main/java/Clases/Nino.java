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
    public String getIdNino() {
        return idNino;
    }

    public void setIdNino(String idNino) {
        this.idNino = idNino;
    }

    public boolean isVivo() {
        return vivo;
    }

    public void setVivo(boolean vivo) {
        this.vivo = vivo;
    }
    
    public boolean isCapturado() {
        return capturado;
    }

    public void setCapturado(boolean capturado) {
        this.capturado = capturado;
    }
    // --- EL MOTOR DEL HILO ---
    @Override
public void run() {
    try {
        // 1. FASE DE NACIMIENTO
        zonas.getCallePrincipal().inicio(this);

        // 2. CICLO ITERATIVO
        while (vivo) {
            zonas.esperarSiPausado();
            
            // --- NUEVA FASE: VIAJE AL UPSIDE DOWN ---
            // Elegimos una zona aleatoria para ir a recolectar
            ZonaInsegura zonaActual = zonas.getUpsidedown().obtenerZonaAleatoria();
            
            // Entramos en la lista de la zona
            zonaActual.entrarNino(this);
            
            // Recolectamos sangre (dentro de recolectarSangre ya hay un sleep de 3-5s)
            // Solo recolectamos si NO hemos sido capturados todavía
            if (!capturado) {
                zonaActual.recolectarSangre(this);
            }
            
            // IMPORTANTE: Si un Demogorgon nos captura, él mismo nos saca de la zona.
            // Pero si terminamos de recolectar y nadie nos ha pillado, salimos nosotros.
            zonaActual.salirNino(this);
            
            // --- GESTIÓN DE CAPTURA ---
            if (capturado) {
                System.out.println(idNino + " ha sido capturado. Esperando en la Colmena...");
                synchronized (this) {
                    while (capturado) {
                        this.wait(); // El hilo se duerme hasta que Eleven haga notify()
                    }
                }
                System.out.println(idNino + " ¡Ha sido liberado por Eleven!");
            }

            zonas.esperarSiPausado();
            
            // Entra a la radio a descansar (2 a 4 segundos)
            zonas.getRadioWSQK().descansar(this);
            
            zonas.esperarSiPausado();
            
            // Vuelve a la calle a deambular (3 a 5 segundos)
            zonas.getCallePrincipal().deambular(this);
        }

    } catch (InterruptedException e) {
        System.out.println(idNino + " ha sido interrumpido.");
        Thread.currentThread().interrupt();
    }
}
    public int getSangreRecolectada() {
        return sangreRecolectada;
    }

    public void setSangreRecolectada(int sangreRecolectada) {
        this.sangreRecolectada = sangreRecolectada;
    }
    
}
