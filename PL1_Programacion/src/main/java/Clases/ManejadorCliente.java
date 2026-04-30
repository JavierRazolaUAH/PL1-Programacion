package Clases;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ManejadorCliente extends Thread {
    private Socket socket;
    private AgrupacionZonas mundo;

    public ManejadorCliente(Socket s, AgrupacionZonas m) {
        this.socket = s;
        this.mundo = m;
    }

    @Override
    public void run() {
        try {
            DataInputStream entrada = new DataInputStream(socket.getInputStream());
            DataOutputStream salida = new DataOutputStream(socket.getOutputStream());

            String peticion = entrada.readUTF();

            if (peticion.equals("GET_DATA")) {
                String respuesta = mundo.getEstadoGlobalParaMonitor();
                salida.writeUTF(respuesta);
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}