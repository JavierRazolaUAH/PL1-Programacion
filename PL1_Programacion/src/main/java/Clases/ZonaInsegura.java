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
    private final Random random = new Random();
    private final AtomicInteger sangreRecolectada = new AtomicInteger(0);
    
   
    //Constructor Iniciar para la ZonaInsegura
    public ZonaInsegura(String nombre) {
        this.nombre = nombre;
    }
    
    public void entrar(Nino n) {
    ninosEnZona.add(n);
}

    public void salir(Nino n) {
    ninosEnZona.remove(n);
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
}
