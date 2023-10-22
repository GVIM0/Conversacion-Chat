/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

package servidor;

import java.io.*;
import java.net.*;

public class Servidor {

        public static void main(String[] args) {    
         /*
            Se declaran las variables
        */    
        int puerto = 9991; //Declaramos el puerto
        Socket socketCliente = null; //Se declara el socket del servidor

        try {
            ServerSocket serverSocket = new ServerSocket(puerto);
            System.out.println("Servidor de chat en línea, en el puerto " + puerto);

            while (true) {
                
                //Hace que el servidor se bloque y entre en modo espera, hasta la llegada de un petición 
                socketCliente = serverSocket.accept();
                
                //Se crea un Thread con el socket del Cliente que envío la petición
                //Y  ademas se crea teniendo en cuenta la Clase logica
                Thread clienteThread = new Thread(new Lógica(socketCliente));
               //Se inicia el Hilo
                clienteThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


