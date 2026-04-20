package Clases;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Alex338
 */
public class UpsideDown {

    // Lista con las 4 zonas inseguras
    private final List<ZonaInsegura> zonasInseguras = new ArrayList<>();
    // Referencia a la Colmena
    private Colmena colmena; 
    private final Random random = new Random();

    // --- ACTUALIZACIÓN: El constructor ahora recibe 'zonas' ---
    public UpsideDown(AgrupacionZonas zonas) {
        // Pasamos la referencia de 'zonas' a cada ZonaInsegura al crearla
        zonasInseguras.add(new ZonaInsegura("BOSQUE", zonas));
        zonasInseguras.add(new ZonaInsegura("LABORATORIO", zonas));
        zonasInseguras.add(new ZonaInsegura("CENTRO COMERCIAL", zonas));
        zonasInseguras.add(new ZonaInsegura("ALCANTARILLADO", zonas));
        
        // Inicializamos la colmena y le conectamos el mundo
        this.colmena = new Colmena();
        this.colmena.setZonas(zonas);
    }

    /**
     * Método para que los Demogorgons elijan una zona al azar para cazar.
     */
    public ZonaInsegura obtenerZonaAleatoria() {
        return zonasInseguras.get(random.nextInt(zonasInseguras.size()));
    }

    /**
     * Método para obtener una zona específica por nombre
     */
    public ZonaInsegura getZona(String nombre) {
        for (ZonaInsegura z : zonasInseguras) {
            if (z.getNombre().equalsIgnoreCase(nombre)) {
                return z;
            }
        }
        return null;
    }

    // Getters
    public List<ZonaInsegura> getZonas() {
        return zonasInseguras;
    }

    public Colmena getColmena() {
        return colmena;
    }
    
    /**
     * Devuelve la zona insegura que tiene más niños en ese preciso milisegundo.
     * Útil para el evento "LA RED MENTAL".
     */
    public ZonaInsegura obtenerZonaMasPoblada() {
        ZonaInsegura zonaMasPoblada = zonasInseguras.get(0);
        int maxNinos = zonaMasPoblada.getNinosEnZona().size();

        for (ZonaInsegura zona : zonasInseguras) {
            if (zona.getNinosEnZona().size() > maxNinos) {
                maxNinos = zona.getNinosEnZona().size();
                zonaMasPoblada = zona;
            }
        }
        return zonaMasPoblada;
    }
}