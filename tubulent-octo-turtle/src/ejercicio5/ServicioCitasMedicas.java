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
    
        // Conjunto de fechas ( tipo de dato Calendar )
        private static Set<Calendar> agendaCalendario = new TreeSet<>();
        // Fecha del día de hoy
        final private static Calendar hoy = Calendar.getInstance();
        
	// Constructor que tiene como parámetro una referencia al socket abierto
	public ServicioCitasMedicas(Socket socketServicio) {
            agendaCalendario.add(hoy);    
            this.socketServicio=socketServicio;
                try {
                        // Obtiene los flujos de escritura/lectura
			inputStream=socketServicio.getInputStream();
			outputStream=socketServicio.getOutputStream();
                        // Crear objeto PrintWriter y ReadBuffered
                        outPrinter = new PrintWriter(outputStream,true);
                        inReader = new BufferedReader (new InputStreamReader(inputStream));
                        // Envía mensaje de éxito de conexión
                        bufferEnvio = 101 + "HELLO";
                        outPrinter.println(bufferEnvio);
                        outPrinter.flush();
                } catch (IOException e) {
			System.err.println("Error al obtener los flujos de entrada/salida.");
		}
                
	}
        
        
        // Realiza el proceso de desconexión
        // esta implementación no tiene un contador de desconexión
        private void fin(){
            boolean continua=true;
            // espera a que el cliente envíe la señal de desconexión
            do{
                bufferRecepcion = enviarMensaje(117, "DISCONNECT","10");
                if( bufferRecepcion.startsWith("007") ){ // bufferRecepcion == OKBYE
                    continua = false;
                    try{
                        socketServicio.close();
                    }catch( IOException e ){
                        System.err.println("Error, no se pudo cerrar el socket");
                    }
                }
            }while(continua);
            
        }
        
        // Envía un tipo determinado de mensaje especificado en los argumentos
        // y retorna la respuesta del cliente
        private String enviarMensaje(int cod, String cuerpo, String args){
            // Si el mensaje es uno de los que requiere argumentos
            if( cuerpo.equals("FECHAS") || cuerpo.equals("DISCONNECT") ){
                bufferEnvio = cod + cuerpo + args;    
            }
            else // el mensaje no requiere de mensajes
            {
                bufferEnvio = cod + cuerpo;
            }
            System.out.println("Envio: "+bufferEnvio);
            outPrinter.println(bufferEnvio);
            outPrinter.flush();       
            try{
                    bufferRecepcion = this.inReader.readLine();
            } catch (IOException e) {
                    System.err.println("Error no se pudo obtener respuesta");
                    bufferRecepcion = null;
            }
            return bufferRecepcion;
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
        
	// Aquí es donde se realiza el procesamiento realmente:
        void procesa()
        {
            //ESTADO: NO_COMP
            bufferRecepcion = enviarMensaje(102, "COMP", "");
            System.out.println(bufferRecepcion);
            if( bufferRecepcion.startsWith("002") ) // repuesta NO
            {
                //ESTADO: SERVICIO_PAGO
                bufferRecepcion = enviarMensaje(103, "PAY2CONT", "");
                if( bufferRecepcion.startsWith("002") ||
                        !bufferRecepcion.startsWith("001") ) // respuesta NO | *
                       fin();// ESTADO: FIN 
            }
            else
            { 
                if ( !bufferRecepcion.startsWith("001") ) // respuesta *
                    fin();// ESTADO: FIN
            }
            // respuesta SI
            // ESTADO: NO_AUTH
            int posibleDNI = -1;
            do // no estaría mal limitar el número de intentos
            {    
                bufferRecepcion = enviarMensaje(104,"DNI", "");
                System.out.println(bufferRecepcion);
                if( bufferRecepcion.startsWith("003") ) // respuesta XXXXXXXX
                {
                    posibleDNI = Integer.parseInt( bufferRecepcion.substring(10) );
                    // "003XXXXXXXX".length = 11, pero empezando desde 0 a contar es 10
                }    
                
            }while(00000000 > posibleDNI && posibleDNI > 99999999);
            // ESTADO: AUTH
            boolean continua = true;
            int cant = 10, tipoint;
            String tipo, listaFechas = "";
            do{
                outPrinter.println("1-EA(enfermedad aguda), 2-EC(enfermedad cronica), 3-AP(actividad preventiva)");
                bufferRecepcion = enviarMensaje(105, "MENU", "");
                System.out.println(bufferRecepcion);
                if( bufferRecepcion.startsWith("004") ) { // responde SELECT + TIPO
                    tipo = bufferRecepcion.substring(8);    
                // ESTADO: CITA
                    Calendar fechaActual = Calendar.getInstance();
                    ArrayList<Calendar> fechasPosibles = MasFechas(fechaActual, cant);
                    int numeroFecha = 0;
                    for(Calendar j : fechasPosibles){
                        // Formato de la fecha ->  \t Fecha nº: dia - mes - año \n
                            listaFechas += " nº: "+ numeroFecha + " Fecha " +
                                j.get(Calendar.DATE) + " - " +
                                j.get(Calendar.MONTH) + " - " +
                                j.get(Calendar.YEAR) + "\n";
                            numeroFecha++;
                    }
                    bufferRecepcion = enviarMensaje(106,"FECHAS",listaFechas);
                    System.out.println(bufferRecepcion);
                    while( bufferRecepcion.startsWith("006") ) // responde MASFECHAS
                    {
                        // adelantamos la fecha actual 10 días
                        fechaActual.add(Calendar.DATE, cant);
                        for(Calendar j : fechasPosibles){
                        // Formato de la fecha ->  \t Fecha nº: dia - mes - año \n
                            listaFechas += numeroFecha + " - Fecha: " +
                                j.get(Calendar.DATE) + " - " +
                                j.get(Calendar.MONTH) + " - " +
                                j.get(Calendar.YEAR) + "\n";
                            numeroFecha++;
                        }
                        bufferRecepcion = enviarMensaje(106,"FECHAS",listaFechas);
                        System.out.println(bufferRecepcion);
                    }
                    bufferRecepcion = enviarMensaje(106,"FECHAS",listaFechas);
                    System.out.println(bufferRecepcion);
                    if( bufferRecepcion.startsWith("005") ) // responde FECHA + INDICE
                    {   // Confío en que el índice es bueno
                        tipoint = Integer.parseInt( bufferRecepcion.substring(7) );
                        // "005FECHA".length = 8
                        Calendar seleccionada = fechasPosibles.get(tipoint);
                        // añado una cita en esa fecha
                        agendaCalendario.add(seleccionada);
                    }
                }
                else // respuesta EXIT | *
                {
                    // ESTADO: FIN
                    continua = false;
                    try{
                        socketServicio.close();
                    }catch( IOException e ){
                        System.err.println("Error, no se pudo cerrar el socket");
                    }   
                }
            }while(continua);
                
        }

}
