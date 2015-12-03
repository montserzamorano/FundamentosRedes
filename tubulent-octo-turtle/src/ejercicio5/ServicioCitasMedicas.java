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
        private String bufferEnvio = null, bufferRecepcion = null;
        
        ////////////////////////////////////////////////////////////////////////
        // ATRIBUTOS DE CLASE
    	////////////////////////////////////////////////////////////////////////
    
        // Conjunto de fechas ( tipo de dato GregorianCalendar )
        private static Set<Calendar> agendaCalendario = new TreeSet<>();
        // Fecha del día de hoy
        final private static Calendar hoy = Calendar.getInstance();
        
        // Lee lo que haya sido escrito en el stream de entrada
        private String lecturaStream(){
                String leido = null;
                try{
                        leido = this.inReader.readLine();
                } catch (IOException e) {
                        System.err.println("Error no se pudo obtener respuesta");
                }
                return leido;
        }
        // Realiza la desconexión
        private void fin(){
                try{
                        socketServicio.close();
                } catch (IOException e){
                        System.err.println("Error no se pudo cerrar el socket");
                } 
        }
        
	// Constructor que tiene como parámetro una referencia al socket abierto
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
                    outPrinter.println(bufferEnvio);
                    // Lee la respuesta es sensible a mayusculas y minusculas
                    bufferRecepcion = lecturaStream();
            }while( !bufferRecepcion.equals("SI") ||
                    !bufferRecepcion.equals("NO") );
            
            if( bufferRecepcion.equals("SI") ){
                    return "CONT";
            }else // bufferRecepcion = "NO"
            {
                    bufferEnvio = "Este servicio es de pago quiere continuar?, responda SI/NO";
                    outPrinter.println(bufferEnvio);
                    bufferRecepcion = lecturaStream();
                    if( bufferRecepcion.equals("SI") )
                        return "CONT";
                    else 
                        return "DISCONNECT";
            }
        }
        
        private void autentificacion(){
            int posibleDNI; // ha de tener los mismos dígitos que un DNI convencional
            // Bucle casi infinito, quizas no estaría mal limitar el número de intentos
            do{     // En primer lugar escribe por el stream al cliente, 
                    // solicitandole la pertenencia
                    bufferEnvio = "Proporcione su DNI, sin letra: ";
                    outPrinter.println(bufferEnvio);
                    // Lee la respuesta es sensible a mayusculas y minusculas
                    bufferRecepcion = lecturaStream();
                    posibleDNI = Integer.parseInt(bufferRecepcion);
            }while(00000000 > posibleDNI && posibleDNI > 99999999); // acepta todos los DNI's
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
            bufferRecepcion = lecturaStream();
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
        // Proporciona una cantidadde  fechas que no esten ocupadas en la 
        // agendaCalendario a partir de la fecha que se pasa como parámetro
        private ArrayList<Calendar> MasFechas(Calendar inicial, int cantidad){
                ArrayList<Calendar> fechasCandidatas = new ArrayList<>();
                do{
                        // Añadimos un día a la fecha inicial
                        inicial.add(Calendar.DATE, 1);
                        // Si dicha fecha no está en la agendaCalendario
                        if( !agendaCalendario.contains(inicial) ){
                            // es una fecha candidata
                            fechasCandidatas.add( 
                                    new GregorianCalendar( inicial.get(Calendar.YEAR),
                                                        inicial.get(Calendar.MONTH),
                                                        inicial.get(Calendar.DATE) )
                            );
                        }
                    
                }while( fechasCandidatas.size() != cantidad ); // mientras no haya 10 fechas candidatas
                
                return fechasCandidatas;
        }
        
        // Ofrece una lista de fechas disponibles para que el usuario elija su
        // cita, devuelve la fecha seleccionada por el cliente
        private Calendar seleccionCita(){
            int cant = 10;
            boolean continua = true;
            Calendar fechaSeleccionada = null;
            Calendar fechaActual = Calendar.getInstance();
            do{
                    ArrayList<Calendar> fechasPosibles = MasFechas(fechaActual, cant);
                    int numeroFecha = 0;
                    String listaFechas = "Fechas disponibles: \n";
                    for(Calendar j : fechasPosibles){
                        // Formato de la fecha ->  \t Fecha nº: dia - mes - año \n
                            listaFechas += "\t"+ "Fecha nº: " + numeroFecha +
                                j.get(Calendar.DATE) + " - " +
                                j.get(Calendar.MONTH) + " - " +
                                j.get(Calendar.YEAR) + "\n";
                            numeroFecha++;
                    }
                    bufferEnvio = "Elija una fecha \n"+
                            "\t *: Mostrar más fechas \n" + listaFechas;
                    outPrinter.println(bufferEnvio);
                    bufferRecepcion = lecturaStream();
                    int seleccion = Integer.parseInt(bufferRecepcion);
                    // Si ha seleccionado una fecha de las proporcionadas
                    if( 0 <= seleccion && seleccion < cant){
                        fechaSeleccionada = fechasPosibles.get(seleccion);
                        continua = false;
                    } else // quiere ver más fechas
                    { 
                        // adelantamos la fecha actual 10 días
                        fechaActual.add(Calendar.DATE, cant);
                    }
                       
            }while(continua);
            
            return fechaSeleccionada;
        }
        
        void menu(){
            String mensaje;
            Calendar fechaCita;
            boolean continua = true;
            do{
                    mensaje = this.menuSeleccion();
                    if ( mensaje.equals("DISCONNECT") ){
                    // ESTADO: FIN
                            fin();
                            continua = false;
                    }else // mensaje == "EA" | "EC" | "AP"
                    {
                    // ESTADO: CITA SELECCION
                            fechaCita = seleccionCita();
                            agendaCalendario.add(fechaCita);
                    }
            }while(continua);
        }
        
	// Aquí es donde se realiza el procesamiento realmente:
        // Cada método-estado incorpora la gestión de errores, es decir
        // si lo introducido es erróneo, es el método quien se encarga de
        // volver a pedirlo hasta que la entrada sea correcta,
        // además los posibles bucles del diagrama están agrupados como se indica
	void procesa(){
            // ESTADO: COMPAÑIA
            String mensaje = this.procesaCompania(); // agrupa el caso de error y
                                                     // el de continua
            if( mensaje.equals("DISCONNECT") ){ // el cliente solicita desconexión
            // ESTADO: FIN
                fin();
            }else{ // mensaje == "CONT"
            // ESTADO: AUTENTIFICACION
                    this.autentificacion(); // agrupa el posible bucle de introducción
                                            // incorrecta del DNI
            // ESTADO: MENU
                    this.menu();            // agrupa el paso por CITA SELECCION
            }
        }
}
