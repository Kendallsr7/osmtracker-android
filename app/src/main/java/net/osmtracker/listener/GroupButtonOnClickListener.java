// Este archivo es nuevo y lo vamos a crear dentro de la carpeta listener, al lado de
// PageButtonOnClickListener. Por ahora todo esta comentado a proposito, para que no se
// rompa la compilacion del proyecto. Lo que hay aqui es la guia de como quedaria la clase
// cuando se implemente la solucion.

// Lo primero seria declarar el paquete, igual que los demas listeners de esta carpeta:
//
// package net.osmtracker.listener;

// Despues vendrian los imports. Serian los mismos que usa PageButtonOnClickListener,
// porque esta clase se parece mucho a esa: necesitamos el layout principal para poder
// abrir la pagina hija, y las clases de Android para manejar el click del boton.
//
// import net.osmtracker.layout.UserDefinedLayout;
// import android.view.View;
// import android.view.View.OnClickListener;

// Aqui creariamos una clase llamada GroupButtonOnClickListener. Es el listener del boton
// compuesto, o sea el boton de tipo "group" que se declararia en el XML de botones.
// Se veria algo asi:
//
// public class GroupButtonOnClickListener implements OnClickListener {
//
// La idea de esta clase es que haga lo mismo que un boton de pagina (como Misc o Car),
// que es abrir otra pantalla con mas botones, pero con una diferencia importante: antes
// de abrir esa pantalla tiene que crear el waypoint del elemento (por ejemplo el arbol) y
// dejar un grupo abierto. Asi, todo lo que la persona capture dentro de la pagina hija
// se guarda en ese mismo waypoint y no se crean puntos nuevos.
//
// Esta clase todavia no esta conectada a ningun boton. La parte del lector de XML que la
// usaria tambien esta comentada, en UserDefinedLayoutReader, dentro de inflateButton().

// Dentro de la clase necesitariamos guardar tres datos que le llegan al crearla.
//
// El primero seria el nombre de la pagina hija, que es el targetlayout que viene del XML.
// Por ejemplo "arbol", que seria la pagina con los botones de especie, altura y foto:
//
//     private String targetLayoutName;
//
// El segundo seria el layout principal, que lo necesitamos para llamar a push() y abrir
// esa pagina hija. Es el mismo dato que guarda PageButtonOnClickListener:
//
//     private UserDefinedLayout rootLayout;
//
// El tercero seria el id del track actual, para que el waypoint del elemento quede
// asociado al recorrido que se esta grabando. Es el mismo que usa TagButtonOnClickListener:
//
//     private long currentTrackId;

// Luego vendria el constructor, que recibe esos tres datos y los guarda en los campos.
// Seria asi:
//
//     public GroupButtonOnClickListener(UserDefinedLayout layout, String target, long trackId) {
//         rootLayout = layout;
//         targetLayoutName = target;
//         currentTrackId = trackId;
//     }
//
// Si a la hora de implementar hace falta otro dato (por ejemplo el nombre del elemento
// para armar algo como "Arbol #001"), se agregaria aqui y tambien en la parte del lector
// de XML que crea este listener, para que las dos coincidan.

// Por ultimo estaria el metodo onClick, que es lo que pasa cuando la persona toca el
// boton compuesto. Se declararia asi:
//
//     @Override
//     public void onClick(View v) {
//
// Dentro de ese metodo harian falta cuatro pasos, en este orden.
//
// Paso 1: revisar si ya hay un grupo abierto. Si la persona ya esta llenando un elemento
// y toca otro boton compuesto, no deberiamos abrir un segundo grupo encima. Habria que
// decidir si simplemente se ignora el toque o si se le avisa a la persona. Para saber si
// hay un grupo abierto se consultaria el estado del grupo, que no vive en esta clase sino
// en la parte de captura (lo define la persona encargada de esa parte).
//
// Paso 2: crear el waypoint del elemento. Esta clase no sabe cual es la ubicacion actual,
// igual que no lo sabe TagButtonOnClickListener. Lo que hace ese listener es mandar un
// broadcast, y es el servicio GPSLogger el que pide la ultima ubicacion y guarda el
// waypoint. Aqui hariamos lo mismo: mandar un broadcast con el id del track, usando
// setPackage como hacen los otros listeners, y avisando en el mensaje que este waypoint
// pertenece a un grupo y no es un punto suelto. Para ver como se podria hacer lo del
// aviso, nos sirve de referencia la rama feature/form de Andrew, donde usa
// INTENT_KEY_IS_GROUPED y la clase GroupedDataManager.
//
// Paso 3: guardar el uuid del waypoint que acabamos de crear como "grupo abierto". Esto
// es necesario para que, cuando la persona toque los botones de la pagina hija, cada uno
// sepa a que waypoint tiene que agregar su dato.
//
// Paso 4: abrir la pagina hija con rootLayout.push(targetLayoutName). Es exactamente lo
// que hace PageButtonOnClickListener. Tambien convendria revisar antes que
// targetLayoutName no sea null, como lo hace ese otro listener, para que no falle si el
// XML esta mal escrito.
//
//     }
//
// Queda una duda que tenemos que resolver como grupo: que pasa si al tocar el boton no hay
// senal de GPS y no se puede crear el waypoint. Podriamos abrir la pagina hija igual y
// dejar que la persona capture los datos, o no abrirla y mostrar un aviso. Todavia no lo
// definimos.
//
// }