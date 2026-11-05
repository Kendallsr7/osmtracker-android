// Este archivo es nuevo y lo vamos a crear dentro de la carpeta util, al lado de
// UserDefinedLayoutReader (app/src/main/java/net/osmtracker/util/). Por ahora todo esta
// comentado a proposito, para que no se rompa la compilacion del proyecto. Lo que hay aqui
// es la guia de como quedaria la clase cuando se implemente la solucion.

// Propuesta de solucion (estado del grupo abierto): esta clase es la respuesta a la pregunta
// que dejaron abierta los comentarios de UserDefinedLayoutReader y de
// GroupButtonOnClickListener: donde vive el dato de "ahora estoy llenando el Arbol #001".
// La app necesita acordarse de eso desde que se pulsa el boton compuesto hasta que se pulsa
// Terminar (o se sale de la pagina con "atras").

// Lo primero seria declarar el paquete, igual que UserDefinedLayoutReader:
//
// package net.osmtracker.util;

// Despues vendrian los imports. Para guardar el estado con SharedPreferences harian falta:
//
// import android.content.Context;
// import android.content.SharedPreferences;

// Donde podria vivir el estado. Evaluamos tres opciones:
//
//   1. Un campo dentro de TrackLogger. Es lo mas simple, pero se pierde si Android destruye y
//      vuelve a crear la actividad. Eso pasa al rotar la pantalla y tambien al abrir la
//      camara, y la foto es justo parte del flujo del grupo. Descartada.
//
//   2. Una variable estatica. Sobrevive a la rotacion, pero no si Android mata el proceso
//      mientras la camara esta abierta, y es dificil de probar. Descartada.
//
//   3. SharedPreferences (la propuesta). Sobrevive a rotacion, camara y proceso muerto, y se
//      puede leer desde cualquier lado que tenga un Context: los listeners (Tag, Number,
//      Group, Finish) y TrackLogger. Es lo que usa el resto de la app para guardar ajustes.
//
// Importante: el lector de XML no guarda este estado (lo dejo escrito Persona 1), porque corre
// una sola vez al inflar los botones y el grupo cambia mientras la persona usa la app.

// Lo que habria que guardar son tres datos:
//
//     private static final String PREFS_NAME = "group_state";
//     private static final String KEY_UUID = "open_group_uuid";       // uuid del waypoint padre
//     private static final String KEY_TRACK_ID = "open_group_track";  // track al que pertenece
//
// El uuid es lo importante: es el que ata cada dato (Especie, Altura, Foto) al elemento. El
// track id sirve para no mezclar un grupo viejo con un track nuevo.

// La clase seria un conjunto de metodos estaticos que reciben un Context. Se veria asi:
//
// public class GroupState {
//
//     // Marca un grupo como abierto. Lo llama GroupButtonOnClickListener (paso 3 de su onClick)
//     // justo despues de generar el uuid con UUID.randomUUID() y mandar el broadcast que crea
//     // el waypoint padre. El uuid se genera en el listener y no en GPSLogger, para poder
//     // guardarlo aqui de inmediato sin esperar al servicio.
//     public static void open(Context context, String groupUuid, long trackId) { ... }
//
//     // Cierra el grupo. Lo llamarian el boton Terminar y el boton "atras" cuando la persona
//     // elige guardar o descartar. Tambien debe llamarse al iniciar o detener un track.
//     public static void close(Context context) { ... }
//
//     // Dice si hay un grupo abierto. Es lo que consulta TagButtonOnClickListener antes de
//     // decidir si crea un waypoint nuevo o agrega un dato, y tambien TrackLogger para la foto
//     // y para el boton "atras".
//     public static boolean isOpen(Context context) { ... }
//
//     // Devuelve el uuid del grupo abierto, o null si no hay ninguno. Es el dato que viaja en
//     // el mensaje de "agregar dato" para que el servicio sepa a que waypoint pertenece.
//     public static String getUuid(Context context) { ... }
//
// }

// Dos dudas que quedan para decidir como grupo:
//
//   - Que pasa si la app se reinicia con un grupo abierto. La pila de paginas del layout vuelve
//     a la pagina principal, pero el grupo seguiria abierto, y entonces los botones tag de la
//     pagina principal agregarian datos al arbol en vez de crear un punto. La propuesta es
//     cerrar el grupo cuando TrackLogger arranca de cero (savedInstanceState == null) y
//     conservarlo solo cuando la actividad se recrea (rotacion, regreso de la camara).
//
//   - El numero de elemento ("Arbol #001"): si queremos mostrarlo en la pagina hija, el
//     nombre se podria guardar aqui tambien (KEY_NAME). Depende de lo que defina la parte de
//     la pantalla del grupo (Persona 4).
