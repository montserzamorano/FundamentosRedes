package ejercicio4;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;


//
// Nota: si esta clase extendiera la clase Thread, y el procesamiento lo hiciera el método "run()",
// ¡Podríamos realizar un procesado concurrente! 
//
public class ProcesadorYodafy {
    // Referencia a un socket para enviar/recibir las peticiones/respuestas
    private DatagramSocket socketServicio = null;
    //flujos de entrada/salida ya no son necesarion
    // Para que la respuesta sea siempre diferente, usamos un generador de números aleatorios.
    private Random random;
    InetAddress direccion = null;
    DatagramPacket paquete = null;
    int port;
	
    // Constructor que tiene como parámetro una referencia al socket abierto en por otra clase
    public ProcesadorYodafy(DatagramSocket socketServicio) {
        this.socketServicio=socketServicio;
        random=new Random();
    }
	
	
    // Aquí es donde se realiza el procesamiento realmente:
    void procesa() throws UnknownHostException, IOException{
        // Como máximo leeremos un bloque de 1024 bytes. Esto se puede modificar.
	byte [] datosRecibidos=new byte[2048];
        //array de bytes que recibe la respuesta
        byte [] datosEnviar = new byte[2048];
        int bytesRecibidos = 0;
	try{
            //ahora nos basamos en el envío de paquetes...
            paquete = new DatagramPacket(datosRecibidos, datosRecibidos.length);
            socketServicio.receive(paquete);
            datosRecibidos=paquete.getData(); //datos
            direccion = paquete.getAddress(); //buscar direccion IP
            port = paquete.getPort(); //puerto
            bytesRecibidos=datosRecibidos.length;
			
            // Yoda hace su magia:
            // Creamos un String a partir de un array de bytes de tamaño "bytesRecibidos":
            String peticion=new String(datosRecibidos,0,bytesRecibidos);
            // Yoda reinterpreta el mensaje:
            String respuesta=yodaDo(peticion);
            // Convertimos el String de respuesta en una array de bytes:
            datosEnviar=respuesta.getBytes();
			
            // Enviamos la traducción de Yoda:
            paquete = new DatagramPacket(datosEnviar, datosEnviar.length, direccion,port);
            socketServicio.send(paquete);
            ////////////////////////////////////////////////////////
        } catch (IOException e) {
		System.err.println("Error al obtener los flujos de entrada/salida.");
            }
	}

	// Yoda interpreta una frase y la devuelve en su "dialecto":
	private String yodaDo(String peticion) {
            // Desordenamos las palabras:
            String[] s = peticion.split(" ");
            String resultado="";
		
            for(int i=0;i<s.length;i++){
		int j=random.nextInt(s.length);
		int k=random.nextInt(s.length);
		String tmp=s[j];
			
		s[j]=s[k];
		s[k]=tmp;
            }
		
            resultado=s[0];
            for(int i=1;i<s.length;i++){
		resultado+=" "+s[i];
            }
		
            return resultado;
	}
}
