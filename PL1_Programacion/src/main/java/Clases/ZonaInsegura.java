/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Alex338
 */
public class ZonaInsegura {
    private String nombre;
    private final List<Nino> ninosEnZona = new CopyOnWriteArrayList<>();
    private final List<Demogorgon> demogorgonsEnZona = new CopyOnWriteArrayList<>();
    private final Random random = new Random();
    private final AtomicInteger sangreRecolectada = new AtomicInteger(0);
    
    // --- ¡NUEVO! ---
    // Necesitamos conocer el entorno para saber si hay tormenta
    private final AgrupacionZonas zonas; 
    
    // Constructor Iniciar para la ZonaInsegura
    public ZonaInsegura(String nombre, AgrupacionZonas zonas) {
        this.nombre = nombre;
        this.zonas = zonas;
    }

    // --- GESTIÓN DE NIÑOS ---
    public void entrarNino(Nino n) throws InterruptedException{
        ninosEnZona.add(n);
        zonas.esperarSiPausado();
    }

    public void salirNino(Nino n) {
        ninosEnZona.remove(n);
    }

    // --- GESTIÓN DE DEMOGORGONS ---
    public void entrarDemogorgon(Demogorgon d) {
        demogorgonsEnZona.add(d);
    }

    public void salirDemogorgon(Demogorgon d) {
        demogorgonsEnZona.remove(d);
    }

    public void recolectarSangre(Nino n) throws InterruptedException {
        long tiempo = 3000 + random.nextInt(2001);

        if (zonas != null && zonas.isTormentaUpsideDown()) {
            tiempo *= 2; 
        }

        Thread.sleep(tiempo); 
        
        // Al despertar del sleep, comprobamos si han pausado el juego 
        // ANTES de sumar la sangre y terminar la acción.
        zonas.esperarSiPausado(); 
        
        sangreRecolectada.incrementAndGet(); 
    }

    public Nino seleccionarVictima() {
        // En lugar de leer el tamaño, intentamos cogerlo directamente de forma segura
        try {
            if (ninosEnZona.isEmpty()) {
                return null;
            }
            int indice = random.nextInt(ninosEnZona.size());
            return ninosEnZona.get(indice);
        } catch (Exception e) {
            // Si el niño justo se ha escapado en ese milisegundo, devolvemos null
            return null; 
        }
    }
        
    public String getNombre() {
        return nombre;
    }

    public List<Nino> getNinosEnZona() {
        return ninosEnZona;
    }

    public List<Demogorgon> getDemogorgonsEnZona() {
        return demogorgonsEnZona;
    }
}