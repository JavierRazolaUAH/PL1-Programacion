package Clases;

import java.util.Random;

public class GeneradorEventos extends Thread {

    private final AgrupacionZonas zonas;
    private final Random random = new Random();

    public GeneradorEventos(AgrupacionZonas zonas) {
        this.zonas = zonas;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                zonas.esperarSiPausado();

                int tiempoHastaEvento = 3000 + random.nextInt(100001);
                int tiempoTranscurrido = 0;

                while (tiempoTranscurrido < tiempoHastaEvento) {
                    Thread.sleep(1000);
                    zonas.esperarSiPausado();
                    tiempoTranscurrido += 1000;
                }

                int tipoEvento = random.nextInt(4);
                int duracionEvento = 5000 + random.nextInt(5001);

                activarEvento(tipoEvento, duracionEvento);

                int tiempoPasado = 0;
                while (tiempoPasado < duracionEvento) {
                    int segundosRestantes = (duracionEvento - tiempoPasado) / 1000;
                    zonas.setTiempoRestanteEvento(segundosRestantes);

                    Thread.sleep(500);
                    zonas.esperarSiPausado();
                    tiempoPasado += 500;
                }

                finalizarEventos();
            }
        } catch (InterruptedException e) {
            System.out.println("Generador de Eventos interrumpido.");
            Thread.currentThread().interrupt();
        }
    }

    private void activarEvento(int tipo, int duracion) {
        switch (tipo) {
            case 0:
                zonas.setRedMental(true);
                Logs.getInstance().log("¡EVENTO! LA RED MENTAL: Los Demogorgons van a la zona más poblada por " + (duracion / 1000) + "s.");
                break;
            case 1:
                zonas.setRedMental(true);
                Logs.getInstance().log("¡EVENTO! LA RED MENTAL: Los Demogorgons van a la zona más poblada por " + (duracion / 1000) + "s.");
                break;
            case 2:
                zonas.setRedMental(true);
                Logs.getInstance().log("¡EVENTO! LA RED MENTAL: Los Demogorgons van a la zona más poblada por " + (duracion / 1000) + "s.");
                break;
            case 3:
                zonas.setRedMental(true);
                Logs.getInstance().log("¡EVENTO! LA RED MENTAL: Los Demogorgons van a la zona más poblada por " + (duracion / 1000) + "s.");
                break;
        }
    }

    private void finalizarEventos() {
        zonas.setTiempoRestanteEvento(0);
        zonas.setApagonLaboratorio(false);
        zonas.setTormentaUpsideDown(false);
        zonas.setIntervencionEleven(false);
        zonas.setRedMental(false);

        zonas.notificarFinEvento();
        Logs.getInstance().log("FIN DEL EVENTO GLOBAL. La normalidad vuelve a Hawkins.");
    }
}