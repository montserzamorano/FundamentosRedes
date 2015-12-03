package ejercicio5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

//
// Nota: si esta clase extendiera la clase Thread, y el procesamiento lo hiciera el método "run()",
// ¡Podríamos realizar un procesado concurrente! 
//
public class ServicioCitasMedicas {
	////////////////////////////////////////////////////////////////////////
        // ATRIBUTOS DE INSTANCIA
    	////////////////////////////////////////////////////////////////////////
    
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
        
        ////////////////////////////////////////////////////////////////////////
        // ATRIBUTOS DE CLASE
    	////////////////////////////////////////////////////////////////////////
    
        // Conjunto de fechas ( tipo de dato GregorianCalendar )
        private static Set<Calendar> agendaCalendario = new TreeSet<>();
        // Fecha del día de hoy
        final private static Calendar hoy = Calendar.getInstance();
        
	// Constructor que tiene como parámetro una referencia al socket abierto en por otra clase
	public ServicioCitasMedicas(Socket socketServicio) {
            agendaCalendario.add(hoy);    
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
        // Muestra el menu en el cual el usuario decide su tipo de cita
        private String menuSeleccion(){
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
            // clasifica la respuesta del usuario
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
                default: // si respondiera cualquier cosa
                    seleccion = "DISCONNECT"; 
            }
            return seleccion;
        }
        // Proporciona 10 fechas que no esten ocupadas en la agendaCalendario
        // a partir de la fecha que se pasa como parámetro
        private ArrayList<Calendar> MasFechas(Calendar inicial){
                ArrayList<Calendar> fechasCandidatas = new ArrayList<>();
                do{
                        // Añadimos un día a la fecha inicial
                        inicial.add(Calendar.DATE, 1);
                        // Si dicha fecha no está en el agendaCalendario
                        if( !agendaCalendario.contains(inicial) ){
                            // es una fecha candidata
                            fechasCandidatas.add( 
                                    new GregorianCalendar( inicial.get(Calendar.YEAR),
                                                        inicial.get(Calendar.MONTH),
                                                        inicial.get(Calendar.DATE) )
                            );
                        }
                    
                }while( fechasCandidatas.size() != 10 ); // mientras no haya 10 fechas candidatas
                
                return fechasCandidatas;
        }
        // Ofrece una lista de fechas disponibles para que el usuario elija su cita
        //private String seleccionCita(){
                
        //}
	// Aquí es donde se realiza el procesamiento realmente:
	void procesa(){
            
	}

}
