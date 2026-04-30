package Clases;

public class Hawkins {
    private CallePrincipal callePrincipal;
    private SotanoByers sotano;
    private RadioWSQK radio;
    private AgrupacionZonas zonas;

    public Hawkins(AgrupacionZonas zonas) {
        this.callePrincipal = new CallePrincipal(zonas);
        this.sotano = new SotanoByers(zonas);
        this.radio = new RadioWSQK(zonas);
        this.zonas = zonas;
    }

    public CallePrincipal getCallePrincipal() {
        return callePrincipal;
    }

    public SotanoByers getSotano() {
        return sotano;
    }

    public RadioWSQK getRadio() {
        return radio;
    }
}