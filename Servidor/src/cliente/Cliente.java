package cliente;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Cliente {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingresa tu nombre: ");
        String nombre = scanner.nextLine();

        int Puerto = 0;

        try {
            System.out.print("Ingresa el puerto: ");
            Puerto = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Error: Ingresa un número válido para el puerto.");
            return;
        }

        System.out.print("Ingresa la dirección IP del servidor: ");
        String ServidorIP = scanner.nextLine();

        Socket SocketCliente = null;

        try {
            SocketCliente = new Socket(ServidorIP, Puerto);
            System.out.println(" ");
            System.out.println("Conexión establecida. Escribe tu mensaje o 'exit' para salir.");
            System.out.println(" ");

            ObjectOutputStream salida = new ObjectOutputStream(SocketCliente.getOutputStream());
            ObjectInputStream entrada = new ObjectInputStream(SocketCliente.getInputStream());

            Thread receptorThread = new Thread(() -> {
                try {
                    while (true) {
                        String Mensaje_encriptado = (String) entrada.readObject();
                        String Mensaje = Desencriptar_Texto(Mensaje_encriptado);
                        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
                        System.out.println("[" + formatoHora.format(new Date()) + "] " + Mensaje);
                    }
                } catch (Exception e) {
                    System.out.println("Conexión Terminada");
                }
            });
            receptorThread.start();

            System.out.println(" ");
            while (true) {
                System.out.print("Tú:");
                String mensaje = scanner.nextLine();
                if (mensaje.equalsIgnoreCase("exit")) {
                    SocketCliente.close();
                    break;
                }
                // Agrega la hora al mensaje
                SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
                String mensajeConHora = "[" + formatoHora.format(new Date()) + "] " + mensaje;
                salida.writeObject(nombre + ": " + mensajeConHora);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    private static List<String> dividirBinario(String Mensaje_encriptado) {
        List<String> segmentos = new ArrayList<>();
        for (int i = 0; i < Mensaje_encriptado.length(); i += 15) {
            segmentos.add(Mensaje_encriptado.substring(i, Math.min(i + 15, Mensaje_encriptado.length())));
        }
        return segmentos;
    }
}
