package ejercicio5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

//
// Nota: si esta clase extendiera la clase Thread, y el procesamiento lo hiciera el método "run()",
// ¡Podríamos realizar un procesado concurrente! 
//
public class ServicioCitasMedicas {
	// Referencia a un Socket para enviar/recibir las peticiones/respuestas
	private Socket socketServicio;
	// Stream de lectura (por aquí se recibe lo que envía el cliente)
	private InputStream inputStream;
        // Objeto para lectura del stream
        private BufferedReader inReader;
	// Stream de escritura (por aquí se envía los datos al cliente)
	private OutputStream outputStream;
	// Objeto para la escritura del stream 
        private PrintWriter outPrinter;
        // Información que se enviada / recibida
        private String bufferEnvio = null, 
                        bufferRecepcion = null;
        
	// Constructor que tiene como parámetro una referencia al socket abierto en por otra clase
	public ServicioCitasMedicas(Socket socketServicio) {
		this.socketServicio=socketServicio;
                try {
                        // Obtiene los flujos de escritura/lectura
			inputStream=socketServicio.getInputStream();
			outputStream=socketServicio.getOutputStream();
                        //Crear objeto PrintWriter y ReadBuffered
                        outPrinter = new PrintWriter(outputStream,true);
                        inReader = new BufferedReader (new InputStreamReader(inputStream));
		} catch (IOException e) {
			System.err.println("Error al obtener los flujos de entrada/salida.");
		}
	}
	// Determina si un cliente pertenece a la compañia
	private String procesaCompania(){
            // Bucle casi infinito, quizas no estaría mal limitar el número de intentos
            do{     // En primer lugar escribe por el stream al cliente, 
                    // solicitandole la pertenencia
                    bufferEnvio = "¿Pertenece usted a la compañia M&D?, responda SI/NO";
                    this.outPrinter.println(bufferEnvio);
                    // Lee la respuesta es sensible a mayusculas y minusculas
                    try{
                            bufferRecepcion = this.inReader.readLine();
                    } catch (IOException e) {
                            System.err.println("Error no se pudo obtener respuesta");
                    }
            }while( !bufferRecepcion.equals("SI") ||
                    !bufferRecepcion.equals("NO") );
            
            if( bufferRecepcion.equals("SI") ){
                    return "CONT";
            }else // bufferRecepcion = "NO"
            {
                    bufferEnvio = "Este servicio es de pago quiere continuar?, responda SI/NO";
                    this.outPrinter.println(bufferEnvio);
                    try{
                            bufferRecepcion = this.inReader.readLine();
                    } catch (IOException e) {
                            System.err.println("Error no se pudo obtener respuesta");
                    }
                    if( bufferRecepcion.equals("SI") )
                        return "CONT";
                    else 
                        return "DISCONNECT";
            }
        }
        
        private String autentificacion(){
            int posibleDNI; // para que sea válido ha de tener los mismos dígitos que un DNI convencional
            // Bucle casi infinito, quizas no estaría mal limitar el número de intentos
            do{     // En primer lugar escribe por el stream al cliente, 
                    // solicitandole la pertenencia
                    bufferEnvio = "Proporcione su DNI, sin letra: ";
                    this.outPrinter.println(bufferEnvio);
                    // Lee la respuesta es sensible a mayusculas y minusculas
                    try{
                            bufferRecepcion = this.inReader.readLine();
                    } catch (IOException e) {
                            System.err.println("Error no se pudo obtener respuesta");
                    }
                    posibleDNI = Integer.parseInt(bufferRecepcion);
            }while( (00000000 > posibleDNI || posibleDNI > 99999999) ||
                    bufferRecepcion.length()!=8 ); // acepta todos los DNI's
            return "MENU";
        }
        
        private String menu(){
            String seleccion;
            bufferEnvio = "Bienvenido al menú: \n" + 
                        "Elija una opcion de las siguientes \n" +
                            "\t1: Enfermedad aguda\n"+
                            "\t2: Enfermedad crónica\n"+
                            "\t3: Actividades preventivas\n"+
                            "\t*: Salir";
            outPrinter.println(bufferEnvio);
            try{
                    bufferRecepcion = this.inReader.readLine();
            } catch (IOException e) {
                    System.err.println("Error no se pudo obtener respuesta");
            }
            switch (Integer.parseInt(bufferRecepcion)) {
                case 1:
                    seleccion = "EA"; // enfermedad aguda
                    break;
                case 2:
                    seleccion = "EC"; // enfermedad crónica
                    break;
                case 3:
                    seleccion = "AP"; // actividades preventivas
                    break;
                default:
                    seleccion = "DISCONNECT";
            }
            return seleccion;
        }
	// Aquí es donde se realiza el procesamiento realmente:
	void procesa(){
            
	}

}
