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
    // Referencia a la Colmena (la crearemos después)
    private Colmena colmena; 
    private final Random random = new Random();

    public UpsideDown() {
        // Inicializamos las 4 zonas que pide la práctica
        zonasInseguras.add(new ZonaInsegura("BOSQUE"));
        zonasInseguras.add(new ZonaInsegura("LABORATORIO"));
        zonasInseguras.add(new ZonaInsegura("CENTRO COMERCIAL"));
        zonasInseguras.add(new ZonaInsegura("ALCANTARILLADO"));
        
        // La colmena se inicializa aquí también
        this.colmena = new Colmena();
    }

    /**
     * Método para que los Demogorgons elijan una zona al azar para cazar.
     */
    public ZonaInsegura obtenerZonaAleatoria() {
        return zonasInseguras.get(random.nextInt(zonasInseguras.size()));
    }

    /**
     * Método para obtener una zona específica por nombre (útil para el Main o la GUI)
     */
    public ZonaInsegura getZona(String nombre) {
        for (ZonaInsegura z : zonasInseguras) {
            if (z.getNombre().equalsIgnoreCase(nombre)) {
                return z;
            }
        }
        return null;
    }

    // Getters necesarios
    public List<ZonaInsegura> getZonas() {
        return zonasInseguras;
    }

    public Colmena getColmena() {
        return colmena;
    }
}