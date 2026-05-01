package Clases;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class UpsideDown {
    private final List<ZonaInsegura> zonasInseguras = new ArrayList<>();
    private Colmena colmena; 
    private final Random random = new Random();
    private List<Demogorgon> listaDemogorgons = new CopyOnWriteArrayList<>();

    public UpsideDown(AgrupacionZonas zonas) {
        zonasInseguras.add(new ZonaInsegura("BOSQUE", zonas));
        zonasInseguras.add(new ZonaInsegura("LABORATORIO", zonas));
        zonasInseguras.add(new ZonaInsegura("CENTRO COMERCIAL", zonas));
        zonasInseguras.add(new ZonaInsegura("ALCANTARILLADO", zonas));
        this.colmena = new Colmena();
        this.colmena.setZonas(zonas);
    }

    public ZonaInsegura obtenerZonaAleatoria() {
        return zonasInseguras.get(random.nextInt(zonasInseguras.size()));
    }

    public ZonaInsegura getZona(String nombre) {
        for (ZonaInsegura z : zonasInseguras) {
            if (z.getNombre().equalsIgnoreCase(nombre)) return z;
        }
        return null;
    }
    
    public void registrarDemogorgon(Demogorgon d) {
        this.listaDemogorgons.add(d);
    }

    public String obtenerDatosSocket() {
        StringBuilder sb = new StringBuilder();
        for (ZonaInsegura z : zonasInseguras) {
            sb.append(z.getNombre()).append(": N=").append(z.getNinosEnZona().size())
              .append(", D=").append(z.getDemogorgonsEnZona().size()).append(" | ");
        }
        sb.append(";RANKING:");
        List<Demogorgon> copiaDemos = new ArrayList<>(listaDemogorgons);
        copiaDemos.sort((d1, d2) -> Integer.compare(d2.getCapturasRealizadas(), d1.getCapturasRealizadas()));
        int limite = Math.min(3, copiaDemos.size());
        if (limite == 0) {
            sb.append("Sin datos");
        } else {
            for (int i = 0; i < limite; i++) {
                Demogorgon d = copiaDemos.get(i);
                sb.append(d.getIdDemogorgon()).append("(").append(d.getCapturasRealizadas()).append(")");
                if (i < limite - 1) sb.append(" - ");
            }
        }
        return sb.toString();
    }

    public List<ZonaInsegura> getZonas() { return zonasInseguras; }
    public Colmena getColmena() { return colmena; }
    public List<Demogorgon> getListaDemogorgons() { return listaDemogorgons; }

    // --- EL MÉTODO CORREGIDO PARA LA RED MENTAL ---
    public ZonaInsegura obtenerZonaMasPoblada() {
        ZonaInsegura zonaMasPoblada = null;
        int maxNinos = 0; // Solo nos interesan zonas con > 0 niños

        for (ZonaInsegura zona : zonasInseguras) {
            int numNinos = zona.getNinosEnZona().size();
            if (numNinos > maxNinos) {
                maxNinos = numNinos;
                zonaMasPoblada = zona;
            }
        }

        // Si maxNinos sigue siendo 0, es que todas las zonas están vacías.
        // En este caso, devolvemos una al azar para que no vayan todos al Bosque.
        if (zonaMasPoblada == null) {
            return obtenerZonaAleatoria();
        }

        return zonaMasPoblada;
    }
}