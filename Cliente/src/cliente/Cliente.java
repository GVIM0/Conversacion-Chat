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
        // Inicializa un objeto Scanner para la entrada del usuario
        Scanner scanner = new Scanner(System.in);

        // Solicita y obtiene un nombre válido del usuario
        String nombre = obtenerNombreValido(scanner);

        // Si el nombre no es válido (null), muestra un mensaje de error y sale del programa
        if (nombre == null) {
            System.out.println("Nombre no válido. Saliendo del programa.");
            return;
        }

        // Inicializa la variable 'Puerto' para el número de puerto
        int Puerto = 0;

        try {
            // Solicita y obtiene un número de puerto del usuario como entrada
            System.out.print("Ingresa el puerto: ");
            Puerto = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            // Si el usuario ingresa un valor no numérico, muestra un mensaje de error y sale del programa
            System.out.println("Error: Ingresa un número válido para el puerto, entre 9000-9999.");
            return;
        }

        // Solicita y obtiene la dirección IP del servidor como entrada del usuario
        System.out.print("Ingresa la dirección IP del servidor: ");
        String ServidorIP = scanner.nextLine();

        Socket SocketCliente = null;

        try {
            // Crea un socket para conectarse al servidor usando la dirección IP y el puerto especificados
            SocketCliente = new Socket(ServidorIP, Puerto);

            // Muestra un mensaje de conexión exitosa
            System.out.println(" ");
            System.out.println("Conexión establecida. Escribe tu mensaje o 'exit' para salir.");
            System.out.println(" ");

            // Crea flujos de entrada y salida para comunicarse con el servidor
            ObjectOutputStream salida = new ObjectOutputStream(SocketCliente.getOutputStream());
            ObjectInputStream entrada = new ObjectInputStream(SocketCliente.getInputStream());

            // Inicia un hilo para recibir y mostrar mensajes del servidor
            Thread receptorThread = new Thread(() -> {
                try {
                    while (true) {
                        // Lee y desencripta un mensaje del servidor
                        String Mensaje_encriptado = (String) entrada.readObject();
                        String Mensaje = Desencriptar_Texto(Mensaje_encriptado);

                        // Obtiene la hora actual y muestra el mensaje con la hora
                        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
                        System.out.println("[" + formatoHora.format(new Date()) + "] " + Mensaje);
                    }
                } catch (Exception e) {
                    // Muestra un mensaje cuando la conexión termina
                    System.out.println("Conexión Terminada");
                }
            });
            receptorThread.start();

            System.out.println(" ");

            while (true) {
                // Lee un mensaje del usuario
                String mensaje = scanner.nextLine().trim(); // Elimina espacios en blanco al principio y al final

                // Si el usuario ingresa 'exit', cierra la conexión y sale del programa
                if (mensaje.equalsIgnoreCase("exit")) {
                    SocketCliente.close();
                    break;
                }

                // Valida que el mensaje no esté vacío ni sea solo espacios en blanco
                if (!mensaje.isEmpty()) {
                    // Agrega la hora actual al mensaje y lo envía al servidor
                    salida.writeObject(nombre + ": " + mensaje);
                }
            }

        } catch (Exception e) {
            // Muestra un mensaje de error si ocurre un problema con la dirección IP o el puerto
            System.out.println("Dirección IP o Puerto incorrectos");
        }
    }

    // Método para obtener un nombre válido del usuario
    private static String obtenerNombreValido(Scanner scanner) {
        while (true) {
            System.out.print("Ingresa tu nombre: ");
            String nombre = scanner.nextLine().trim(); // Elimina espacios en blanco al principio y al final
            if (!nombre.isEmpty()) {
                return nombre;
            } else {
                System.out.println("Nombre no válido. Por favor, ingresa un nombre válido.");
            }
        }
    }

    // Método para desencriptar un mensaje en formato binario
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

    // Método para dividir el mensaje en binario en segmentos de 15 caracteres
    private static List<String> dividirBinario(String Mensaje_encriptado) {
        List<String> segmentos = new ArrayList<>();
        for (int i = 0; i < Mensaje_encriptado.length(); i += 15) {
            segmentos.add(Mensaje_encriptado.substring(i, Math.min(i + 15, Mensaje_encriptado.length())));
        }
        return segmentos;
    }
}