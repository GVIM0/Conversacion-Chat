/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidor;

import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class Lógica implements Runnable {
    
   
    private Socket socket;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private static ArrayList <Lógica> clientesConectados = new ArrayList<>();

    public Lógica(Socket socket) {
        this.socket = socket;
        try {
            this.salida = new ObjectOutputStream(socket.getOutputStream());
            this.entrada = new ObjectInputStream(socket.getInputStream());
            
        } catch (IOException e) {
            e.printStackTrace();
        }
         clientesConectados.add(this);
    }

    
    
    @Override
    public void run() {
        try {
            while (true) {
                // Leer un mensaje del cliente
                String mensaje = (String) entrada.readObject();
                System.out.println("Mensaje recibido del cliente: " + mensaje);

                // Reenviar el mensaje a todos los clientes
                enviarMensajeATodos(mensaje);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   private void enviarMensajeATodos(String mensaje) {
        // Encriptar el mensaje
        String mensajeEncriptado = encriptarTexto(mensaje);

        for (Lógica cliente : clientesConectados) {
        try {
            if (cliente != this) {
                cliente.salida.writeObject(mensajeEncriptado);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }

    private String encriptarTexto(String texto) {
        String binarioFinal = "";
        
        if (!texto.isEmpty()) {
            for (int i = 0; i < texto.length(); i++) {
                char letra = texto.charAt(i);
                int codASCII = (int) letra;
                int func = ((codASCII - 32) % (int) Math.pow(2, 15)) * ((codASCII - 32) % (int) Math.pow(2, 15));
                String binario = String.format("%15s", Integer.toBinaryString(func)).replace(' ', '0');
                binarioFinal += binario;
            }
            return binarioFinal;
        } else {
            return "Entrada no válida. Ingrese una cadena de texto.";
        }
    }
    
}
