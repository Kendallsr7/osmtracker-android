// Este archivo es nuevo y lo vamos a crear dentro de la carpeta listener, al lado de
// TagButtonOnClickListener y de GroupButtonOnClickListener
// (app/src/main/java/net/osmtracker/listener/). Por ahora todo esta comentado a proposito,
// para que no se rompa la compilacion del proyecto. Lo que hay aqui es la guia de como
// quedaria la clase cuando se implemente la solucion.

// Propuesta de solucion (boton numerico): es el listener de los botones type="number" del XML,
// por ejemplo <button type="number" field="Altura" label="Altura (m)" />. UserDefinedLayoutReader
// solo reconoce el tipo y lee el atributo field; lo que pasa al pulsar el boton se define aqui.
//
// Por que hace falta una clase aparte: la altura de un arbol no se puede resolver con botones
// fijos ("10 m", "11 m", "12 m"...), el valor es libre. Y por que no meterlo en
// TagButtonOnClickListener: un tag usa el texto del boton como valor, este pide el valor a la
// persona. Mezclarlos haria el listener de tag mucho mas confuso.

// Lo primero seria declarar el paquete, igual que los demas listeners de esta carpeta:
//
// package net.osmtracker.listener;

// Despues vendrian los imports:
//
// import android.app.AlertDialog;
// import android.content.Context;
// import android.content.DialogInterface;
// import android.content.Intent;
// import android.text.InputType;
// import android.view.View;
// import android.view.View.OnClickListener;
// import android.widget.EditText;
// import android.widget.Toast;
// import net.osmtracker.util.GroupState;

// La clase y sus datos. Recibe el id del track (igual que TagButtonOnClickListener) y el field
// que Persona 1 lee del XML (por ejemplo "Altura"):
//
// public class NumberButtonOnClickListener implements OnClickListener {
//
//     private long currentTrackId;
//     private String field;
//
//     public NumberButtonOnClickListener(long trackId, String field) {
//         currentTrackId = trackId;
//         this.field = field;
//     }

// El metodo onClick haria tres cosas, en este orden:
//
//     @Override
//     public void onClick(View v) {
//
// Paso 1: revisar si hay un grupo abierto con GroupState.isOpen(context). Si no lo hay, un
// numero suelto no tiene a que elemento pertenecer, asi que se muestra un Toast corto
// ("Abra un elemento primero") y no se hace nada mas. Esto cubre el caso de un XML mal armado
// con un boton number fuera de una pagina de grupo.
//
// Paso 2: abrir un AlertDialog con un EditText. El teclado numerico se pide con
// setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL), para que
// acepte decimales (12.5 m). El titulo del dialogo seria el field, para que la persona sepa
// que esta escribiendo.
//
// Paso 3: al aceptar, validar que el texto no este vacio y mandar el mismo mensaje de
// "agregar dato" que usa TagButtonOnClickListener cuando hay un grupo abierto:
//
//         Intent intent = new Intent(OSMTracker.INTENT_ADD_GROUP_DATA);
//         intent.putExtra(TrackContentProvider.Schema.COL_TRACK_ID, currentTrackId);
//         intent.putExtra(OSMTracker.INTENT_KEY_GROUP_UUID, GroupState.getUuid(context));
//         intent.putExtra(OSMTracker.INTENT_KEY_FIELD, field);
//         intent.putExtra(OSMTracker.INTENT_KEY_VALUE, textoEscrito);
//         intent.setPackage(context.getPackageName());
//         context.sendBroadcast(intent);
//
//     }
//
// }
//
// Los nombres INTENT_ADD_GROUP_DATA, INTENT_KEY_GROUP_UUID, INTENT_KEY_FIELD e
// INTENT_KEY_VALUE son provisionales: los declara Persona 3 en OSMTracker.java y hay que
// ponerse de acuerdo con ella antes de implementar para que coincidan.

// Dudas que quedan:
//
//   - Si la persona deja el campo vacio o cancela, no se manda nada. Habria que decidir si se
//     avisa con un Toast.
//   - Si queremos guardar la unidad ("m") junto al numero o solo el numero. Para el GPX es mas
//     limpio guardar solo el numero (<data field="Altura">12</data>) y dejar la unidad en el
//     label del boton, pero eso lo decide quien arma el XML.
