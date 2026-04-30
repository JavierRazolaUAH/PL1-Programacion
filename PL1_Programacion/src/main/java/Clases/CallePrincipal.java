package Clases;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class CallePrincipal {

    private final BlockingQueue<Nino> ninosEnCalle;
    private final Random rand;
    private final AgrupacionZonas zonas;

    public CallePrincipal(AgrupacionZonas zonas) {
        this.ninosEnCalle = new LinkedBlockingQueue<>();
        this.rand = new Random();
        this.zonas = zonas;
    }

    public void inicio(Nino nino) throws InterruptedException {
        ninosEnCalle.put(nino);
        Logs.getInstance().log(nino.getIdNino() + " ha INICIADO su actividad en Hawkins (Calle Principal).");
        
        Thread.sleep(1000);
        zonas.esperarSiPausado();
        
        salirZona(nino);
    }

    public void deambular(Nino nino) throws InterruptedException {
        ninosEnCalle.put(nino);
        Logs.getInstance().log(nino.getIdNino() + " está DEAMBULANDO por la Calle Principal.");
        
        int tiempoDeambular = rand.nextInt(2000) + 3000;
        Thread.sleep(tiempoDeambular);
        zonas.esperarSiPausado();
        
        salirZona(nino);
    }

    public void salirZona(Nino nino) {
        if (ninosEnCalle.remove(nino)) {
            Logs.getInstance().log(nino.getIdNino() + " ha SALIDO de la Calle Principal.");
        }
    }

    public int getNumeroNinos() {
        return ninosEnCalle.size();
    }

    public List<Nino> getNinosEnCalle() {
        return new ArrayList<>(ninosEnCalle);
    }
}