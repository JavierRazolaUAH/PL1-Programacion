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

    // --- EL MOTOR DEL HILO ---
    @Override
    public void run() {
        try {
            // 1. FASE DE NACIMIENTO
            // Entra a la calle al nacer.
            zonas.getCallePrincipal().inicio(this);

            // 2. CICLO ITERATIVO
            while (vivo) {
                zonas.esperarSiPausado();
                
                // Entra al sótano a prepararse (1 a 2 segundos)
                zonas.getSotanoByers().prepararse(this);
                
                zonas.esperarSiPausado();
                
                // --- AQUÍ IRÁN LOS PORTALES MÁS ADELANTE ---
                // (Por ahora nos saltamos el Upside Down para probar la interfaz)
                
                // Entra a la radio a descansar (2 a 4 segundos)
                zonas.getRadioWSQK().descansar(this);
                
                zonas.esperarSiPausado();
                
                // Vuelve a la calle a deambular (3 a 5 segundos)
                zonas.getCallePrincipal().deambular(this);
                
                // Al terminar, el bucle while vuelve a mandarlo al Sótano
            }

        } catch (InterruptedException e) {
            System.out.println(idNino + " ha sido interrumpido.");
        }
    }
    
}
