package ejercicio5;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

//
// YodafyServidorIterativo
// (CC) jjramos, 2012
//
public class Servidor {

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
                        socketServicio = socketServidor.accept();
                        
			ServicioCitasMedicas procesador=new ServicioCitasMedicas(socketServicio);
			procesador.procesa();
				
			} while (true);
			
		} catch (IOException e) {
			System.err.println("Error al escuchar en el puerto "+port);
		}

	}

}
