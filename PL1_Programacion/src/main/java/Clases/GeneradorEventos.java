/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;

import java.util.Random;

/**
 *
 * @author javir
 */
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
                
                // 1. Espera aleatoria entre 30 y 60 segundos
                int tiempoHastaEvento = 30000 + random.nextInt(30001);
                Thread.sleep(tiempoHastaEvento);
                
                zonas.esperarSiPausado();

                // 2. Elegir un evento al azar (0 a 3)
                int tipoEvento = random.nextInt(4);
                
                // 3. Duración del evento entre 5 y 10 segundos
                int duracionEvento = 5000 + random.nextInt(5001);

                switch (tipoEvento) {
                    case 0:
                        zonas.setApagonLaboratorio(true);
                        Logs.getInstance().log("¡EVENTO! APAGÓN DEL LABORATORIO: Portales bloqueados por " + (duracionEvento/1000) + "s.");
                        break;
                    case 1:
                        zonas.setTormentaUpsideDown(true);
                        Logs.getInstance().log("¡EVENTO! TORMENTA UPSIDE DOWN: Sangre x2 y Demogorgons rápidos por " + (duracionEvento/1000) + "s.");
                        break;
                    case 2:
                        zonas.setIntervencionEleven(true);
                        Logs.getInstance().log("¡EVENTO! INTERVENCIÓN DE ELEVEN: Demogorgons paralizados por " + (duracionEvento/1000) + "s.");
                        // Liberamos tantos niños como sangre haya
                        int sangreDisponible = zonas.getRadioWSQK().getSangreTotalAlmacenada();
                        zonas.getUpsidedown().getColmena().liberarNinos(sangreDisponible);
                        break;
                    case 3:
                        zonas.setRedMental(true);
                        Logs.getInstance().log("¡EVENTO! LA RED MENTAL: Los Demogorgons van a la zona más poblada por " + (duracionEvento/1000) + "s.");
                        break;
                }

                // 4. Esperamos a que pase el evento
                Thread.sleep(duracionEvento);

                // 5. Apagamos todos los eventos
                zonas.setApagonLaboratorio(false);
                zonas.setTormentaUpsideDown(false);
                zonas.setIntervencionEleven(false);
                zonas.setRedMental(false);
                Logs.getInstance().log("FIN DEL EVENTO GLOBAL. La normalidad vuelve a Hawkins.");
            }
        } catch (InterruptedException e) {
            System.out.println("Generador de Eventos interrumpido.");
            Thread.currentThread().interrupt();
        }
    }
}
