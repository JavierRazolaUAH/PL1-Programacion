/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;

/**
 *
 * @author javir
 */

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AgrupacionZonas {
// --- ZONAS SEGURAS DE HAWKINS ---
    private final CallePrincipal callePrincipal;
    private final SotanoByers sotanoByers;
    private final RadioWSQK radioWSQK;
    
    // --- ZONA INSEGURA ---
    private final UpsideDown upsidedown;
    
    // --- PORTALES (La conexión entre ambos mundos) ---
    private final Portal[] portales;
    
    // --- CONTROL DE PAUSA / REANUDAR ---
    private volatile boolean pausado = false;
    private final Lock lock = new ReentrantLock();
    private final Condition pausadoCondition = lock.newCondition();
    
    //Eventos globales
    private boolean apagonLaboratorio = false;
    private boolean tormentaUpsideDown = false;
    private boolean intervencionEleven = false;
    private boolean redMental = false;
    

    // Constructor
    public AgrupacionZonas() {
        // Instanciamos las zonas seguras al crear la agrupación
        this.callePrincipal = new CallePrincipal(this);
        this.sotanoByers = new SotanoByers(this);
        this.radioWSQK = new RadioWSQK(this);
        
        // Instanciamos el Upside Down y conectamos la Colmena
        this.upsidedown = new UpsideDown(this);
        this.upsidedown.getColmena().setZonas(this);
        
        // --- INICIALIZAMOS LOS PORTALES ---
        this.portales = new Portal[4];
        
        
        
        // Creamos cada portal pasándole su Nombre y la Capacidad del Grupo
        this.portales[0] = new Portal("Bosque", 2,this);           // Necesita 2 niños
        this.portales[1] = new Portal("Laboratorio", 3,this);      // Necesita 3 niños
        this.portales[2] = new Portal("Centro Comercial", 4,this); // Necesita 4 niños
        this.portales[3] = new Portal("Alcantarillado", 2,this);   // Necesita 2 niños
    }
    
    
    
    // --- MÉTODOS GETTER DE ZONAS ---
    
    public CallePrincipal getCallePrincipal() { 
        return callePrincipal; 
    }
    
    public SotanoByers getSotanoByers() { 
        return sotanoByers; 
    }
    
    public RadioWSQK getRadioWSQK() { 
        return radioWSQK; 
    }

    public UpsideDown getUpsidedown() {
        return upsidedown;
    }
    
    // --- MÉTODOS GETTER DE PORTALES ---

    // Obtener un portal específico por su índice (0 a 3)
    public Portal getPortal(int index) {
        return portales[index];
    }
    
    // Obtener todos los portales (muy útil para tu Interfaz Gráfica)
    public Portal[] getTodosLosPortales() {
        return portales;
    }
    
    // --- MÉTODOS DE PAUSA Y SINCRONIZACIÓN ---
    
    public boolean isPausado() {
        return pausado;
    }

    // Método que llamará el botón "PAUSAR" de tu interfaz
    public void pausar() {
        lock.lock();
        try {
            pausado = true;
        } finally {
            lock.unlock();
        }
    }

    // Método que llamará el botón "REANUDAR" de tu interfaz
    public void reanudar() {
        lock.lock();
        try {
            pausado = false;
            pausadoCondition.signalAll(); // Despierta a todos los hilos congelados
        } finally {
            lock.unlock();
        }
    }

    // Método que deben llamar los niños y demogorgons en cada paso de su ciclo
    public void esperarSiPausado() throws InterruptedException {
        lock.lock();
        try {
            while (pausado) {
                pausadoCondition.await(); // El hilo se queda bloqueado aquí si el juego está pausado
            }
        } finally {
            lock.unlock();
        }
    }
    // 1. Número total de niños en Hawkins (Suma de las 3 zonas seguras)
    public int getNinosTotalesHawkins() {
        return callePrincipal.getNumeroNinos() + 
               sotanoByers.getNumeroNinos() + 
               radioWSQK.getNumeroNinos();
    }
    
    // 2. Estado del evento activo (Para el servidor)
    private final AtomicInteger tiempoRestanteEvento = new AtomicInteger(0);

    public int getTiempoRestanteEvento() {
        return tiempoRestanteEvento.get();
    }

    public void setTiempoRestanteEvento(int tiempo) {
        this.tiempoRestanteEvento.set(tiempo);
    }
    
    public String getEventoActivo() {
        if (apagonLaboratorio) return "Apagón en el Laboratorio";
        if (tormentaUpsideDown) return "Tormenta en el Upside Down";
        if (intervencionEleven) return "Intervención de Eleven";
        if (redMental) return "Red Mental Activa";
        return "Sin evento activo";
    }
        public void notificarFinEvento() {
        // Recorremos el array de portales (que tiene 4 posiciones: 0 a 3)
        for (int i = 0; i < 4; i++) {
            Portal p = getPortal(i); // Usamos tu método existente
            if (p != null) {
                p.despertarHilos();
            }
        }
    }
     /**
    * Devuelve un String con los 3 contadores de un portal específico.
    * Formato: "esperandoIda,enElInterior,esperandoVuelta"
    */
   public String getEstadoPortalString(int index) {
       Portal p = portales[index];
       if (p == null) return "0,0,0";

       // Obtenemos los tamaños de las listas que definiste
       int esperandoIda = p.getNinosEsperandoAlUpsideDown().size();
       int enElInterior = p.getCruzando().size();
       int esperandoVuelta = p.getNinosEsperandoAHawkins().size();

       // Lo unimos todo en una cadena fácil de trocear luego
       return esperandoIda + "," + enElInterior + "," + esperandoVuelta;
    }
    public String getEntidadesZonaString(int indice) {
    try {
        ZonaInsegura zona = upsidedown.getZonas().get(indice);
        int ninos = zona.getNinosEnZona().size();
        int demos = zona.getDemogorgonsEnZona().size();
        
        // Ejemplo de retorno: "3/1" (3 niños y 1 demogorgon)
        return ninos + "/" + demos; 
    } catch (Exception e) {
        return "0/0"; 
    }
    }
    public String getTop3Demogorgons() {
    try {
        // Accedemos a la lista que está en UpsideDown
        List<Demogorgon> lista = upsidedown.getListaDemogorgons();

        if (lista == null || lista.isEmpty()) {
            return "No hay datos";
        }

        // Usamos Streams para ordenar y obtener el Top 3
        return lista.stream()
                // Ordenar por capturas (Descendente)
                .sorted((d1, d2) -> Integer.compare(d2.getCapturasRealizadas(), d1.getCapturasRealizadas()))
                .limit(3)
                .map(d -> d.getIdDemogorgon() + ": " + d.getCapturasRealizadas())
                .reduce((a, b) -> a + " | " + b)
                .orElse("Sin capturas");
                
    } catch (Exception e) {
        return "Error al calcular Top";
    }
}
    public String getEstadoGlobalParaMonitor() {
       return getNinosTotalesHawkins() + ";" +           // partes[0]
              getEventoActivo() + ";" +                  // partes[1]
              getTiempoRestanteEvento() + ";" + // partes[2]
              getUpsidedown().getColmena().getNumPrisioneros() + ";" + // partes[3]
              getEstadoPortalString(0) + ";" +           // partes[4] (Bosque)
              getEstadoPortalString(1) + ";" +                 // partes[5] (Lab)
              getEstadoPortalString(2) + ";" +                    // partes[6] (Centro)
              getEstadoPortalString(3) + ";" +                  // partes[7] (Alcantarilla)
              getEntidadesZonaString(0) + ";" +                 // partes[8]
              getEntidadesZonaString(1) + ";" +                 // partes[9]
              getEntidadesZonaString(2) + ";" +                 // partes[10]
              getEntidadesZonaString(3) + ";" +                 // partes[11]
              getTop3Demogorgons();                             // partes[12
   }

    public boolean isApagonLaboratorio() { return apagonLaboratorio; }
    public void setApagonLaboratorio(boolean apagonLaboratorio) { this.apagonLaboratorio = apagonLaboratorio; }

    public boolean isTormentaUpsideDown() { return tormentaUpsideDown; }
    public void setTormentaUpsideDown(boolean tormentaUpsideDown) { this.tormentaUpsideDown = tormentaUpsideDown; }

    public boolean isIntervencionEleven() { return intervencionEleven; }
    public void setIntervencionEleven(boolean intervencionEleven) { this.intervencionEleven = intervencionEleven; }

    public boolean isRedMental() { return redMental; }
    public void setRedMental(boolean redMental) { this.redMental = redMental; }
}
