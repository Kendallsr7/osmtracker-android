package net.osmtracker.listener;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.util.UUID;

import net.osmtracker.OSMTracker;
import net.osmtracker.R;
import net.osmtracker.db.TrackContentProvider;


/**
 * Listener for standard waypoint tag button.
 * Sends an Intent to track waypoint. Waypoint name is the
 * label of the button.
 * 
 * @author Nicolas Guillaumin
 *
 */
// Propuesta de solucion (agregar datos al grupo): hoy, cada clic de este listener genera un
// UUID.randomUUID() distinto y manda INTENT_TRACK_WP, o sea un waypoint nuevo por pulsacion. Esa
// es la causa del problema: no hay ningun identificador compartido entre pulsaciones. Los cambios
// concretos estan marcados con "Propuesta" en el constructor y en onClick().
public class TagButtonOnClickListener implements OnClickListener {

	private long currentTrackId;
	
	// Propuesta: el constructor recibiria un segundo dato, el field, que UserDefinedLayoutReader lee
	// del XML (ver la nota "Propuesta" en inflateButton(), donde hoy se hace
	// new TagButtonOnClickListener(currentTrackId)). Por ejemplo:
	//     private String field;
	//     public TagButtonOnClickListener(long trackId, String field)
	// El constructor actual de un solo parametro se conservaria, llamando al nuevo con field = null,
	// para que los botones tag sin field (los que ya existen) y cualquier otro lugar que lo use
	// sigan compilando y funcionando igual que hoy.
	public TagButtonOnClickListener(long trackId) {
		currentTrackId = trackId;
	}
	
	@Override
	public void onClick(View view) {
		Button button = (Button) view;
		String label = button.getText().toString().replaceAll("\n", " ");

		// Propuesta (agregar el dato al grupo abierto): aqui es donde hoy se crea el waypoint. Antes del
		// "Send an intent" habria que preguntar si hay un grupo abierto, con GroupState.isOpen(...)
		// usando view.getContext(). Hay tres casos:
		//
		//   1. Sin grupo abierto, o field == null (los botones tag que ya existen): se sigue igual que
		//      hoy, con el UUID.randomUUID() de mas abajo y el Intent OSMTracker.INTENT_TRACK_WP.
		//
		//   2. Con grupo abierto y field != null: NO se genera UUID ni se manda INTENT_TRACK_WP. Se manda
		//      un Intent nuevo (provisional: OSMTracker.INTENT_ADD_GROUP_DATA, lo define Persona 3) con:
		//        - TrackContentProvider.Schema.COL_TRACK_ID: currentTrackId, igual que hoy.
		//        - INTENT_KEY_GROUP_UUID: GroupState.getUuid(...), el waypoint padre. No se reutiliza
		//          INTENT_KEY_UUID porque hoy significa "uuid del waypoint nuevo" y se prestaria a confusion.
		//        - INTENT_KEY_FIELD: el field recibido en el constructor (Especie).
		//        - INTENT_KEY_VALUE: el label (Roble), que ya se calcula arriba.
		//      Tambien con intent.setPackage(packageName), igual que el codigo actual, y luego return.
		//
		//   3. Con grupo abierto y field == null: boton tag viejo dentro de una pagina de grupo. Duda
		//      para el grupo: crear el waypoint suelto como hoy (opcion A) o avisar con un Toast y no
		//      hacer nada (opcion B). Propuesta: B, para no crear sin querer un punto con un nombre ambiguo.
		//
		// Por que: asi el dato queda atado al elemento y, como no se vuelve a pedir la ubicacion al GPS,
		// cada elemento conserva una sola coordenada. Eso elimina la diferencia de varios metros entre
		// los datos de un mismo arbol, que era el problema de la evaluacion 1.
		//
		// Nota sobre el label: la linea de arriba reemplaza los saltos de linea por espacios. Eso importa
		// porque el boton compuesto muestra una segunda linea ("boton compuesto") debajo del nombre
		// (ver UserDefinedLayoutReader), pero los botones de datos no la tienen. El Toast de mas abajo
		// (R.string.tracklogger_tracked) se puede conservar tambien en el caso 2 para confirmar el dato.
		// Send an intent to inform service to track the waypoint.
		Intent intent = new Intent(OSMTracker.INTENT_TRACK_WP);
		intent.putExtra(TrackContentProvider.Schema.COL_TRACK_ID, currentTrackId);
		intent.putExtra(OSMTracker.INTENT_KEY_NAME, label);
		intent.putExtra(OSMTracker.INTENT_KEY_UUID, UUID.randomUUID().toString());

		String packageName = view.getContext().getPackageName();
		intent.setPackage(packageName);

		view.getContext().sendBroadcast(intent);
		
		// Inform user that the waypoint was tracked
		Toast.makeText(view.getContext(), view.getContext().getResources().getString(R.string.tracklogger_tracked) + " " + label, Toast.LENGTH_SHORT).show();

	}

}