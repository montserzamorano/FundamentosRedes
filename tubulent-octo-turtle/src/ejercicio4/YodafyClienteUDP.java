package ejercicio4;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class YodafyClienteUDP {

    public static void main(String[] args) {
		
        byte []buferEnvio = new byte[256];
	byte []buferRecepcion=new byte[256];
	DatagramPacket paqueteEnvio = null;
        DatagramPacket paqueteRecibo = null;
	InetAddress direccion = null;
	DatagramSocket socket = null;
		
	// Abrir un datagram socket
        try{
            socket =  new DatagramSocket();
	} catch (IOException e) {
            System.err.println("Error de entrada/salida al abrir el socket.");
        }
	// Nombre del host donde se ejecuta el servidor:
	String host="localhost";
        // Puerto en el que espera el servidor:
	int port=8989;
        // Obtenemos la dirección IP del servidor
        try{
            direccion = InetAddress.getByName(host);
        } catch (UnknownHostException e){
            System.out.println("Error: Dirección."); //cambiar
        }
    
        // Si queremos enviar una cadena de caracteres por un OutputStream, hay que pasarla primero
        // a un array de bytes:
        buferEnvio="Al monte del volcán debes ir sin demora".getBytes();
		
	// Enviamos el array
        try{
            // Rellenamos el paquete con la información que queremos
            paqueteEnvio = new DatagramPacket(buferEnvio, buferEnvio.length, direccion, port);
            socket.send(paqueteEnvio);
        } catch(IOException e){
            System.err.println("Error de entrada/salida al enviar el paquete.");
        }
		
	// Tratamos la recepción de los datos que nos envía el servidor
	paqueteRecibo = new DatagramPacket(buferRecepcion, buferRecepcion.length);
	try{
            socket.receive(paqueteRecibo);
        } catch(IOException e){
            System.err.println("Error de entrada/salida al recibir el paquete.");
        }
		
			              
        // Mostremos la cadena de caracteres recibidos:
        System.out.println("Recibido: ");
        for(int i=0; i<buferRecepcion.length; i++){
            System.out.print((char)buferRecepcion[i]);
        }
			
        // Una vez terminado el servicio, cerramos el socket 
        socket.close();
			

	}//end main
}//end class
