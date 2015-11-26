package ejercicio2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class YodafyClienteTCP {

	public static void main(String[] args) {
		//En lugar de enviar arrays de bytes de un tamaño concreto
                //se pueden enviar y recibir objetos de la clase string
                String buferEnvio = null;
		String buferRecepcion = null;
		
		// Nombre del host donde se ejecuta el servidor:
		String host="localhost";
		// Puerto en el que espera el servidor:
		int port=8989;
		
		// Socket para la conexión TCP
		Socket socketServicio=null;
		
                //Establecer la conexión con el servidor
		try {
                    socketServicio = new Socket(host,port);
                    //flujos de lectura y escritura del socket
                    InputStream inputStream = socketServicio.getInputStream();
                    OutputStream outputStream = socketServicio.getOutputStream();
			
                    // Ya no hay que pasarla a cadena de bytes
                    buferEnvio="Al monte del volcan debes ir sin demora";
			
                    //Enviamos el array por PrintWriter en vez de por
                    //outputstream
                    PrintWriter outPrinter = new PrintWriter(outputStream,true);
                    
                    //este tipo de objeto puede enviar cadenas de texto con los
                    //métodos print o println
                    outPrinter.println(buferEnvio);
                    //"flush()" para obligar a TCP a que no espere para hacer el envío:
                    outPrinter.flush();
			
                    // Leemos desde BufferedReader en lugar de desde inputStream
                    BufferedReader inReader = new BufferedReader(new InputStreamReader(inputStream));
                    //Se pueden recibir líneas de caracteres completas mediante readLine();
                    buferRecepcion = inReader.readLine();
			
                    // Como es un string, no hace falta el bucle que lee cada
                    //byte
                    System.out.println("Recibido: " + buferRecepcion + "\n");
			
                    // Una vez terminado el servicio, cerramos el socket (automáticamente se cierran
                    // el inpuStream  y el outputStream)
                    socketServicio.close();
			
		// Excepciones:
		} catch (UnknownHostException e) {
                    System.err.println("Error: Nombre de host no encontrado.");
		} catch (IOException e) {
                    System.err.println("Error de entrada/salida al abrir el socket.");
		}
	}//end main
}//end class
