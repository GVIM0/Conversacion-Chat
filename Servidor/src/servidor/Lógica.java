/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidor;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Lógica implements Runnable {
    
    /*
    Declaramos las variables
    */
    
    private Socket socket;//Iniciamos un socket
    private ObjectOutputStream salida; //Iniciamos un ObjetcStream para la salida de datos
    private ObjectInputStream entrada; //Iniciamos un ObjetcStream para la entrada de datos
    private static ArrayList<Lógica> clientesConectados = new ArrayList<>(); //Creamos un Arraylist para registrar 
    //la cantidad de clientes en línea
    
    private static int clientesEnLinea = 0;   // Contador de clientes en línea
    
    private SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");  // Formato de hora

    //Este método se encarga de iniciar cada hilo cada vez que se detecta una petición
    public Lógica(Socket socket) {
        this.socket = socket;
        try {
            this.salida = new ObjectOutputStream(socket.getOutputStream());
            this.entrada = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientesConectados.add(this);//Agreamos el socket del cliente al ArraysList
        
        // Incrementar el contador de clientes en línea
        clientesEnLinea++;
        /*
        Imprime mensajes describiendo la entrada de un nuevo cliente junto con la hora
        así como un actualización de la cantidad de clientes en línea.
        */
        System.out.println("Nuevo cliente en línea a las " + formatoHora.format(new Date()));  // Imprime la hora actual
        System.out.println("Clientes en línea: " + clientesEnLinea);
        System.out.println(" ");
    }

    
    //Ejecuta que se hara cada vez que se tiene una solicitud
    @Override
    public void run() {
        try {
            while (true) {
                // Leer un mensaje del cliente
                String mensaje = (String) entrada.readObject();
                /*
                Si detecta un exit, cierra la conexión con dicho cliente al llamar al método  desconectarCliente();
                */
                if (mensaje.equalsIgnoreCase("exit")) {
                    desconectarCliente();
                    break;
                }
                /*
                  Crea una matriz al dividir el mensaje en dos que llego del cliente
                  La primera parte es el nombre
                  La segunda parte es el mensaje
                */
                String[] Nom_Men = mensaje.split(":");
                System.out.println("Mensaje recibido del cliente: " + Nom_Men[0] + " a las " + formatoHora.format(new Date()));  // Imprime la hora actual
                System.out.println("Mensaje: " + Nom_Men[1]); //Imprime el mensaje del cliente
                System.out.println(" ");

              
                enviarMensajeATodos(mensaje);  // Reenviar el mensaje a todos los clientes con el método enviarMensajeATodos(String);
            }
        } catch (Exception e) {
            desconectarCliente();//Si el servidor se apaga, corta la conexión con todos los clientes
        }
    }

    private void desconectarCliente() {
        try {
            clientesConectados.remove(this); // Remueve el cliente del ArrayList
            socket.close();// Cierra la conexión
            System.out.println("Cliente desconectado a las " + formatoHora.format(new Date()));  // Imprime la hora actual
            
            clientesEnLinea--;   // Decrementar el contador de clientes en línea
            System.out.println("Clientes en línea: " + clientesEnLinea);//Imprime la nueva cantidad de clientes en línea
            System.out.println(" ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void enviarMensajeATodos(String mensaje) {
        String mensajeEncriptado = encriptarTexto(mensaje);      // Encriptar el mensaje con el método encriptarTexto();

        /*
        El siguiente for se encarga de recorre el ArrayList creado 
        Y enviar a cada uno el mensaje si es diferente al cliente
        que envio el mensaje, lo cual se compruba con el if
        */
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

    
    //Encripta el mensaje con el Protocolo anteriormente creado
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
