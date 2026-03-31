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
    String nombre;
    private final List<Nino> ninosEnZona = new CopyOnWriteArrayList<>();
    private final List<Demogorgon> demogorgonsEnZona = new CopyOnWriteArrayList<>();
    private final Random random = new Random();
    private final AtomicInteger sangreRecolectada = new AtomicInteger(0);
    
   
    //Constructor Iniciar para la ZonaInsegura
    public ZonaInsegura(String nombre) {
        this.nombre = nombre;
    }
    // --- GESTIÓN DE NIÑOS ---

    public void entrarNino(Nino n) {
        ninosEnZona.add(n);
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

        Thread.sleep(tiempo); // El hilo se detiene simulando el trabajo
        
        sangreRecolectada.incrementAndGet(); // Suma 1 de forma segura
    }
    public Nino seleccionarVictima() {
        if (ninosEnZona.isEmpty()) {
            return null;
        }
        int indice = random.nextInt(ninosEnZona.size());
        return ninosEnZona.get(indice);
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
