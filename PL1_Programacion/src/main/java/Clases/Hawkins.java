/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;

/**
 *
 * @author javir
 */
public class Hawkins {
    private CallePrincipal callePrincipal;
    private SotanoByers sotano;
    private RadioWSQK radio;
    private AgrupacionZonas zonas;

    public Hawkins(AgrupacionZonas zonas) {
        // Al crear Hawkins, se crean sus zonas interiores
        this.callePrincipal = new CallePrincipal(zonas);
        this.sotano = new SotanoByers(zonas);
        this.radio = new RadioWSQK(zonas);
        this.zonas = zonas;
    }
}
