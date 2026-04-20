/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;

/**
 *
 * @author javir
 */

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
        this.callePrincipal = new CallePrincipal();
        this.sotanoByers = new SotanoByers();
        this.radioWSQK = new RadioWSQK();
        
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
    
    public boolean isApagonLaboratorio() { return apagonLaboratorio; }
    public void setApagonLaboratorio(boolean apagonLaboratorio) { this.apagonLaboratorio = apagonLaboratorio; }

    public boolean isTormentaUpsideDown() { return tormentaUpsideDown; }
    public void setTormentaUpsideDown(boolean tormentaUpsideDown) { this.tormentaUpsideDown = tormentaUpsideDown; }

    public boolean isIntervencionEleven() { return intervencionEleven; }
    public void setIntervencionEleven(boolean intervencionEleven) { this.intervencionEleven = intervencionEleven; }

    public boolean isRedMental() { return redMental; }
    public void setRedMental(boolean redMental) { this.redMental = redMental; }
}
