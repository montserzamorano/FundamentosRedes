package ejercicio1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class YodafyServidorIterativoUDP {

	public static void main(String[] args) {
	
		// Puerto de escucha
		int port=8989;
		// array de bytes auxiliar para recibir o enviar datos.
		byte []buffer=new byte[256];
		// Número de bytes leídos
		int bytesLeidos=0;
    // Socket UDP
    DatagramSocket socketServidor;
		
		do{
		  try{
      // Abrimos el socket en modo pasivo, escuchando el en puerto indicado por "port"
        socketServidor = new DatagramSocket(port); //inicializamos    
      // no abrimos socket de servicio porque no es orientado a conexión

		  } catch (IOException e) {
		  	System.err.println("Error al escuchar en el puerto "+port);
		  }
		  
		  
		  ProcesadorYodafy procesador = new ProcesadorYodafy(socketServidor);
			procesador.procesa();
			
		} while (true);
	
	}

}
