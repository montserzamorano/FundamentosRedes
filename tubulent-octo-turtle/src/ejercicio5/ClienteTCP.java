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
        // Referencia a un Socket para enviar/recibir las peticiones/respuestas
	static private Socket socketServicio;
	// Stream de lectura (por aquí se recibe lo que envía el cliente)
	static private InputStream inputStream;
        // Objeto para lectura del stream
        static private BufferedReader inReader;
	// Stream de escritura (por aquí se envía los datos al cliente)
	static private OutputStream outputStream;
	// Objeto para la escritura del stream 
        static private PrintWriter outPrinter;
        // Información que se enviada / recibida
        static private String bufferEnvio = null;
        static private String bufferRecepcion = null;
        // Booleano para saber cuando salimos en caso de error
        static private boolean fin = false;
        static private boolean salir = false;
        // Localhost
        static private String host="localhost";
        //Puerto
	static private int port=8989;
        //Para leer desde pantalla
	static Scanner scan = new Scanner(System.in);
        //codigo
        static String codigo;

        // Envía un tipo determinado de mensaje especificado en los argumentos
        // y retorna la respuesta del cliente
        static private String enviarMensajeCodArg(String cod, String cuerpo, String arg){
            // Si el mensaje es uno de los que requiere argumentos
            bufferEnvio = cod + cuerpo;    
            outPrinter.println(bufferEnvio);
            outPrinter.flush();       
            try{
                    bufferRecepcion = inReader.readLine();
            } catch (IOException e) {
                    System.err.println("Error no se pudo obtener respuesta");
                    bufferRecepcion = null;
            }
            return bufferRecepcion;
        }
        
        static private String enviarMensajeCod(String cod, String cuerpo){
            bufferEnvio = cod + cuerpo;
            outPrinter.println(bufferEnvio);
            outPrinter.flush();       
            try{
                    bufferRecepcion = inReader.readLine();
            } catch (IOException e) {
                    System.err.println("Error no se pudo obtener respuesta");
                    bufferRecepcion = null;
            }
            return bufferRecepcion;
        }
        
        //Lee el mensaje y sale si es de desconexión. Lee por pantalla
        
        static private void LeerEscribir(String mensaje) throws IOException{
            bufferRecepcion = inReader.readLine();
            if( bufferRecepcion.startsWith("107")){ //si DISCONNECT
                outPrinter.println("007"); //OKBYE
                outPrinter.flush();
                fin = true;
            }
            else{
                System.out.println(mensaje);
                bufferEnvio = scan.nextLine(); //leer por pantalla
            }
        }
        
        //Para respuestas si o no
        
        static private void SiNo(){
            if(bufferEnvio == "SI")
               codigo = "001";
            else if(bufferEnvio == "NO"){
                codigo = "002";
            }
            else{
                codigo = "008";
            }
            enviarMensajeCod(codigo,bufferEnvio);
        }
        
        static private void DNI() throws IOException{
            while(bufferEnvio.length() != 8){
               bufferEnvio = scan.nextLine(); 
            }
            codigo = "003";
            enviarMensajeCod(codigo,bufferEnvio);
            
        }
        
        static private void select(){
            if(bufferEnvio == "EXIT"){
                codigo = "008";
            }
            else{
                codigo = "004";
            }
            enviarMensajeCod(codigo,bufferEnvio);
        }
        
        static private void fecha(){
            codigo = "005";
            enviarMensajeCodArg(codigo,"FECHA",bufferEnvio);
        }
        
        static private void masFechas(){
            codigo = "006";
            enviarMensajeCod(codigo,bufferEnvio);
        }

	public static void main(String[] args) {
                //Establecer la conexión con el servidor
		try {
                    socketServicio = new Socket(host,port);
                    //flujos de lectura y escritura del socket
                    InputStream inputStream = socketServicio.getInputStream();
                    OutputStream outputStream = socketServicio.getOutputStream();
                    
                    PrintWriter outPrinter = new PrintWriter(outputStream,true);
                    BufferedReader inReader = new BufferedReader(new InputStreamReader(inputStream));
                    bufferRecepcion = inReader.readLine(); //leo mensaje HELLO
			
                    do{
                        //Compañia
                        LeerEscribir("SI/NO:");
                        SiNo();
                        //si no lo es redirige a servicio pago
                        if(codigo=="002"){
                            LeerEscribir("SI/NO:");
                            SiNo();
                        }
                        //dni
                        LeerEscribir("Introduzca DNI:");
                        DNI();
                        //menu
                        while(!salir){
                            LeerEscribir("Seleccione una opción");
                            select();
                            //cita
                            LeerEscribir("Seleccione una opción");
                            while(codigo == "006"){ //si mas fechas
                                masFechas();
                                LeerEscribir("Seleccione una opción:");
                            }
                            fecha();
                        }
                        
                 } while(!fin);
                    socketServicio.close(); //Corte de comunicación
			
		// Excepciones:
		} catch (UnknownHostException e) {
                    System.err.println("Error: Nombre de host no encontrado.");
		} catch (IOException e) {
                    System.err.println("Error de entrada/salida al abrir el socket.");
		}
	}//end main
}//end class
