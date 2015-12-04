package ejercicio5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class ClienteTCP {

	public static void main(String[] args) {
		
                String envio = null;
                String respuesta = null;
                boolean fin = false;
		
		// Nombre del host donde se ejecuta el servidor:
		String host="localhost";
		// Puerto en el que espera el servidor:
		int port=8989;
		
		// Socket para la conexión TCP
		Socket socketServicio=null;
		
                Scanner scan = new Scanner(System.in);
                
                
                //Establecer la conexión con el servidor
		try {
                    socketServicio = new Socket(host,port);
                    //flujos de lectura y escritura del socket
                    InputStream inputStream = socketServicio.getInputStream();
                    OutputStream outputStream = socketServicio.getOutputStream();
                    
                    PrintWriter outPrinter = new PrintWriter(outputStream,true);
                    BufferedReader inReader = new BufferedReader(new InputStreamReader(inputStream));
			
                    do{
                        //Leo del buffer
                        respuesta = inReader.readLine();
                        
                        // Comparo
                        if (respuesta.equals("FIN"))
                            fin = true;
                       
                        
                        //Envio al servidor
                        envio = scan.nextLine();
                        outPrinter.println(envio);
                        
                 } while(!fin);
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
