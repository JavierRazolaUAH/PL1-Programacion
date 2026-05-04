package Clases;

import java.util.ArrayList;
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

    // Ahora recibe el tiempo exacto que le falta al niño (el cálculo se hace en la clase Nino)
    public void recolectarSangre(long tiempoRestante) throws InterruptedException {
        Thread.sleep(tiempoRestante);
        zonas.esperarSiPausado();
    }

    // Nuevo método para registrar la sangre de forma segura una vez que el niño termina
    public void registrarExtraccionGlobal(int cantidad) {
        sangreRecolectada.addAndGet(cantidad);
    }

    public Nino seleccionarVictima() {
        try {
            if (ninosEnZona.isEmpty()) {
                return null;
            }

            // Filtramos a los niños para NO atacar a los que ya están peleando o son inmunes
            List<Nino> objetivosValidos = new ArrayList<>();
            for (Nino n : ninosEnZona) {
                if (!n.isBajoAtaque() && !n.isInmune()) {
                    objetivosValidos.add(n);
                }
            }

            if (objetivosValidos.isEmpty()) {
                return null; // Todos los niños en la zona están ocupados peleando o son invisibles
            }

            int indice = random.nextInt(objetivosValidos.size());
            return objetivosValidos.get(indice);
            
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