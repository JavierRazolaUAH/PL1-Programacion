package Clases;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ZonaInsegura {
    private String nombre;
    private final List<Nino> ninosEnZona = new CopyOnWriteArrayList<>();
    private final List<Demogorgon> demogorgonsEnZona = new CopyOnWriteArrayList<>();
    private final Random random = new Random();
    private final AtomicInteger sangreRecolectada = new AtomicInteger(0);
    private final AgrupacionZonas zonas;

    public ZonaInsegura(String nombre, AgrupacionZonas zonas) {
        this.nombre = nombre;
        this.zonas = zonas;
    }

    public void entrarNino(Nino n) throws InterruptedException {
        ninosEnZona.add(n);
        zonas.esperarSiPausado();
    }

    public void salirNino(Nino n) {
        ninosEnZona.remove(n);
    }

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
        zonas.esperarSiPausado();
        sangreRecolectada.incrementAndGet();
    }

    public Nino seleccionarVictima() {
        try {
            if (ninosEnZona.isEmpty()) {
                return null;
            }
            int indice = random.nextInt(ninosEnZona.size());
            return ninosEnZona.get(indice);
        } catch (Exception e) {
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