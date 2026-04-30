package Clases;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SotanoByers {
    private final BlockingQueue<Nino> ninosEnSotano;
    private final Random rand;
    private AgrupacionZonas zonas;

    public SotanoByers(AgrupacionZonas zonas) {
        this.ninosEnSotano = new LinkedBlockingQueue<>();
        this.rand = new Random();
        this.zonas = zonas;
    }

    public void entrarZona(Nino nino) throws InterruptedException {
        ninosEnSotano.put(nino);
        Logs.getInstance().log(nino.getIdNino() + " ha ENTRADO al Sótano Byers y empieza a prepararse.");
        
        int tiempoEspera = rand.nextInt(1000) + 1000; 
        Thread.sleep(tiempoEspera);
        zonas.esperarSiPausado();
        
        Logs.getInstance().log(nino.getIdNino() + " ha terminado de prepararse en el Sótano Byers.");
    }

    public void salirZona(Nino nino) {
        if (ninosEnSotano.remove(nino)) {
            Logs.getInstance().log(nino.getIdNino() + " ha SALIDO del Sótano Byers hacia los Portales.");
        }
    }

    public int getNumeroNinos() {
        return ninosEnSotano.size();
    }

    public List<Nino> getNinosEnSotano() {
        return new ArrayList<>(ninosEnSotano);
    }
}