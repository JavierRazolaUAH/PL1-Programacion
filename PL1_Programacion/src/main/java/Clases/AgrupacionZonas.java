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
    private final UpsideDown upsidedown;
    
    // --- CONTROL DE PAUSA / REANUDAR (Sacado de tu práctica anterior) ---
    private volatile boolean pausado = false;
    private final Lock lock = new ReentrantLock();
    private final Condition pausadoCondition = lock.newCondition();

    // Constructor
    public AgrupacionZonas() {
        // Instanciamos las tres zonas seguras al crear la agrupación
        this.callePrincipal = new CallePrincipal();
        this.sotanoByers = new SotanoByers();
        this.radioWSQK = new RadioWSQK();
        this.upsidedown = new UpsideDown();
    }

    // --- MÉTODOS GETTER ---
    
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
}
