package ejercicio3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class YodafyServidorConcurrente {

	public static void main(String[] args) {
	
		// Puerto de escucha
		int port=8989;
                // Socket
                ServerSocket socketServidor;
		
		try {
                    // Abrimos el socket en modo pasivo, escuchando el en puerto indicado por "port"
                    socketServidor = new ServerSocket(port); //inicializamos    
                    do {
                        // Aceptamos una nueva conexión con accept()
			Socket socketServicio = null ;
                        try {
                            //El método accept() para que se quede esperando una
                            //petición de conexión. Cuando esto suceda, el ServerSocket
                            //devuelve un objeto Socket que contiene los datos de
                            //la tubería correspondiente a la conexión entre 
                            //cliente y servidor. A partir de ahora es el nuevo
                            //socket el que se utilizará para enviar y recibir
                            //bytes.
                            socketServicio = socketServidor.accept();
                        } catch (IOException e){
                            System.out.println("Error: no se pudo aceptar la conexión solicitada.");
                        }
                        
                        // Creamos un objeto de la clase ProcesadorYodafy, pasándole como 
                        // argumento el nuevo socket, para que realice el procesamiento
			// Este esquema permite que se puedan usar hebras más fácilmente.
			ProcesadorYodafy procesador=new ProcesadorYodafy(socketServicio);
			procesador.procesa();
				
			} while (true);
			
		} catch (IOException e) {
			System.err.println("Error al escuchar en el puerto "+port);
		}

	}

}
