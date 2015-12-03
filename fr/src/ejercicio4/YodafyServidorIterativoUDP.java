package ejercicio4;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

public class YodafyServidorIterativoUDP {

    public static void main(String[] args){
	
	// Puerto de escucha
	int port=8989;
        // Socket UDP
        DatagramSocket socketServidor = null;
		
	do{
            try{
                // Abrimos el socket en modo pasivo, escuchando el en puerto indicado por "port"
                socketServidor = new DatagramSocket(port); //inicializamos    

            } catch (IOException e) {
		System.err.println("Error: no se pudo atender en el puerto  "+port);
            }
		  
		  
            ProcesadorYodafy procesador = new ProcesadorYodafy(socketServidor);
            try{
                procesador.procesa();
            } catch(IOException e){
                System.err.println("Error al procesar la frase");
            }
            
            // Cerramos el socket del servidor
            socketServidor.close();
			
        } while (true);
	
    }

}
