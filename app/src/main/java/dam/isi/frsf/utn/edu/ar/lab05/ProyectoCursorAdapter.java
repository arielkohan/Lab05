package dam.isi.frsf.utn.edu.ar.lab05;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoApiRest;
import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDBMetadata;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;

/**
 * Created by mdominguez on 06/10/16.
 */
public class ProyectoCursorAdapter extends CursorAdapter {
    private LayoutInflater inflador;
    private Context contexto;
    private View selectedView;

    public ProyectoCursorAdapter(Context contexto, Cursor c, ProyectoDAO dao) {
        super(contexto, c, false);
        this.contexto = contexto;
    }

    @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        inflador = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vista = inflador.inflate(R.layout.fila_proyecto,viewGroup,false);
        return vista;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        int pos = cursor.getPosition();

        // Referencias UI.
        TextView proyectoNombre= (TextView) view.findViewById(R.id.proyectoNombre);
        proyectoNombre.setText(cursor.getString(cursor.getColumnIndex(ProyectoDBMetadata.TablaProyectoMetadata.TITULO)));

        final Button btnVerTareasAsociadas = (Button) view.findViewById(R.id.btnVerTareasAsociadas);
        btnVerTareasAsociadas.setTag(cursor.getInt(cursor.getColumnIndex("_id")));
        btnVerTareasAsociadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Integer idProyecto= (Integer) view.getTag();
                List<Tarea> listTareas = ProyectoApiRest.getInstance().getTareas(idProyecto);
                for(Tarea tarea: listTareas){
                    Log.i("Tarea ->",tarea.getDescripcion() + " Horas Estimadas: " + tarea.getHorasEstimadas() + " Minutos Trabajados: " + tarea.getMinutosTrabajados());
                }


            }
        });

        final Button btnEditar = (Button)   view.findViewById(R.id.btnEditarProyecto);

        btnEditar.setTag(cursor.getInt(cursor.getColumnIndex("_id")));
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Integer idProyecto= (Integer) view.getTag();
                Intent intEditarProyecto = new Intent(contexto,AltaProyectoActivity.class);
                intEditarProyecto.putExtra("ID_PROYECTO",idProyecto);
                context.startActivity(intEditarProyecto);

            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                selectedView = view;
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        context);
                alert.setTitle(view.getResources().getString(R.string.tittle_dialog_eliminar));
                alert.setMessage(view.getResources().getString(R.string.msg_dialog_eliminar_proyecto));
                alert.setPositiveButton(view.getResources().getString(R.string.button_positive_dialog_eliminar), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Integer idProyecto = (Integer)((Button) selectedView.findViewById(R.id.btnEditarProyecto)).getTag();
                        Toast.makeText(context, context.getString(R.string.msg_proyecto_eliminado), Toast.LENGTH_SHORT).show();
                        ProyectoApiRest.getInstance().borrarProyecto(idProyecto);
                        handlerRefresh.sendEmptyMessage(1);
                        dialog.dismiss();

                    }
                });
                alert.setNegativeButton(view.getResources().getString(R.string.button_negative_dialog_eliminar), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
                return true;
            }
        });


    }

    Handler handlerRefresh = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message inputMessage) {
            ProyectoCursorAdapter.this.changeCursor(ProyectoApiRest.getInstance().getCursorProyectos());
        }
    };
}

