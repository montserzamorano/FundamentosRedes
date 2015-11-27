package ejercicio1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class YodafyClienteUDP {

	public static void main(String[] args) {
		
		byte []buferEnvio;
		byte []buferRecepcion=new byte[256];
		String direccion;
		DatagramPacket paqueteEnvio, paqueteRecibo;
		InetAddress direccion;
		DatagramSocket socket; 
		
		// Abrir un datagram socket
		socket =  new DatagramSocket();
		// Nombre del host donde se ejecuta el servidor:
		String host="localhost";
		// Puerto en el que espera el servidor:
		int port=8989;
    // Obtenemos la dirección IP del servidor
    direccion = InetAddress.getByName(host);
    
    // Si queremos enviar una cadena de caracteres por un OutputStream, hay que pasarla primero
    // a un array de bytes:
    buferEnvio="Al monte del volcán debes ir sin demora".getBytes();
    // Rellenamos el paquete con la información que queremos
    paqueteEnvio = new DatagramPacket(buferEnvio, buferEnvio.length, direccion, port);
		
		// Enviamos el array
		socket.send(paqueteEnvio);
		
		// Tratamos la recepción de los datos que nos envía el servidor
		paqueteRecibo = new DatagramPacket(buferRecepcion, buferRecepcion.length);
		socket.receive(paqueteRecibo);
		
		paqueteRecibo.getData();
		paqueteRecibo.getAddress();
		paqueteRecibo.getPort();
		
			              
    // Mostremos la cadena de caracteres recibidos:
    System.out.println("Recibido: ");
    for(int i=0;i<bytesLeidos;i++){
      System.out.print((char)buferRecepcion[i]);
    }
			
    // Una vez terminado el servicio, cerramos el socket 
    socket.close();
			

	}//end main
}//end class
