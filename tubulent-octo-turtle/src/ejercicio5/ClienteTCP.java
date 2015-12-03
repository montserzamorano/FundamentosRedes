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
		
		byte []buferEnvio = new byte[256];
		byte []buferRecepcion=new byte[256];
		int bytesLeidos=0;
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
                    do{
                        //Leo del buffer
                        String envio = new String(buferRecepcion);
                        
                        // Comparo
                        if (envio.equals("FIN"))
                            fin = true;
                       
                        
                        //Envio al servidor
                        String respuesta = scan.nextLine();
                        buferEnvio = respuesta.getBytes();
                    
                        // Leemos la respuesta del servidor. Para ello le pasamos un array de bytes, que intentará
                        // rellenar. El método "read(...)" devolverá el número de bytes leídos.
                        outputStream.write(buferEnvio,0,buferEnvio.length) ;
			
                    // Aunque le indiquemos a TCP que queremos enviar varios arrays de bytes, sólo
                    // los enviará efectivamente cuando considere que tiene suficientes datos que enviar...
                    // Podemos usar "flush()" para obligar a TCP a que no espere para hacer el envío:
                    outputStream.flush();
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
