
package cliente;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Cliente{
    
    public static void main(String[] args) {
        
        //Se declara un Scanner para que el Usuario introduzca su mensaje y Usuario
        Scanner scanner = new Scanner(System.in);
        
        /*
        Se declaran las variables
        */

        System.out.print("Ingresa tu nombre: ");
        String nombre = scanner.nextLine();
        
        System.out.print("Ingresa el puerto: ");
        int Puerto = Integer.parseInt(scanner.nextLine());

        System.out.print("Ingresa la dirección IP del servidor: ");
        String ServidorIP = scanner.nextLine();
        
        Socket SocketCliente = null;


        try {

            
             SocketCliente = new Socket(ServidorIP, Puerto);//Se establece la conexión con el servidor
           
            //Se declaran los Stream para la salida y recibo de datos
            ObjectOutputStream salida = new ObjectOutputStream(SocketCliente.getOutputStream());
            ObjectInputStream entrada = new ObjectInputStream(SocketCliente.getInputStream());

            //Se define un Thread para recibir mensajes del servidor
            //Este Thread tienen la estructura 
            /*
            Thread receptorThread = new Thread(() -> { ... });
            */
            //Para recibir mensajes del servidor de manera asincrona a los mensajes que envía
            
            Thread receptorThread = new Thread(() -> {
                try {
                    while (true) {
                        // Leer un mensaje del servidor
                        String Mensaje_encriptado = (String) entrada.readObject();
                        //Desencripta el Mensaje 
                        String Mensaje = Desencriptar_Texto(Mensaje_encriptado);
                        //Imprimir el Mensaje Desencriptado
                        System.out.println(Mensaje);
                        
                    }
                } catch (Exception e) {
                    System.out.println("Conexión Terminada");
                }
            });receptorThread.start(); //Establece un Loop hasta que la conexión se termine
         
            System.out.println("Conexión establecida. Escribe tu mensaje o 'exit' para salir.");
            
            //Se envía el mensaje al servidor
            while (true) {
                // Enviar un mensaje al servidor
               String mensaje = scanner.nextLine();
                if (mensaje.equalsIgnoreCase("exit")) {
                    break; // Termina la conexión
                }
                String mensajeConNombre = nombre + ": " + mensaje;
                salida.writeObject(mensajeConNombre);
            }
            
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Método para descencriptar, lo mismo que python
    private static String Desencriptar_Texto(String Mensaje_encriptado) {
        
        String cadenaFinal = "";
        
         if (!Mensaje_encriptado.isEmpty()) {
                List<String> segmentos = dividirBinario(Mensaje_encriptado);

                for (String segmento : segmentos) {
                    int fx = Integer.parseInt(segmento, 2);
                    int codASCII = (int) (Math.sqrt(fx) + 32);
                    char caracter = (char) codASCII;
                    cadenaFinal += caracter;
                }
               
            } else {
                System.out.println("Entrada no válida, por favor ingrese un binario");
            }
       return cadenaFinal;
    }

    //Método para divir el binario en segementos de 15
    private static List<String> dividirBinario(String Mensaje_encriptado) {
          List <String> segmentos = new ArrayList<>();
        for (int i = 0; i < Mensaje_encriptado.length(); i += 15) {
            segmentos.add(Mensaje_encriptado.substring(i, Math.min(i + 15, Mensaje_encriptado.length())));
        }
        return segmentos;
    }
}

