package dam.isi.frsf.utn.edu.ar.lab05;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.HashMap;
import java.util.Map;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDBMetadata;

/**
 * Created by mdominguez on 06/10/16.
 */
public class TareaCursorAdapter extends CursorAdapter {
    private LayoutInflater inflador;
    private ProyectoDAO myDao;
    private Context contexto;
    private Map<Integer, Long> mapTiemposInicioTrabajo;
    private View selectedView;
    public TareaCursorAdapter (Context contexto, Cursor c, ProyectoDAO dao) {
        super(contexto, c, false);
        myDao= dao;
        this.contexto = contexto;
        mapTiemposInicioTrabajo = new HashMap<>();

    }

    @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        inflador = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vista = inflador.inflate(R.layout.fila_tarea,viewGroup,false);
        return vista;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        //obtener la posicion de la fila actual y asignarla a los botones y checkboxes
        int pos = cursor.getPosition();

        // Referencias UI.
        TextView nombre= (TextView) view.findViewById(R.id.tareaTitulo);
        TextView tiempoAsignado= (TextView) view.findViewById(R.id.tareaMinutosAsignados);
        TextView tiempoTrabajado= (TextView) view.findViewById(R.id.tareaMinutosTrabajados);
        TextView prioridad= (TextView) view.findViewById(R.id.tareaPrioridad);
        TextView responsable= (TextView) view.findViewById(R.id.tareaResponsable);
        CheckBox finalizada = (CheckBox)  view.findViewById(R.id.tareaFinalizada);

        final Button btnFinalizar = (Button)   view.findViewById(R.id.tareaBtnFinalizada);
        final Button btnEditar = (Button)   view.findViewById(R.id.tareaBtnEditarDatos);
        ToggleButton btnEstado = (ToggleButton) view.findViewById(R.id.tareaBtnTrabajando);

        nombre.setText(cursor.getString(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.TAREA)));
        Integer horasAsigandas = cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS));
        tiempoAsignado.setText(horasAsigandas*60 + " minutos");

        final Integer minutosAsigandos = cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS));
        tiempoTrabajado.setText(minutosAsigandos+ " minutos");
        String p = cursor.getString(cursor.getColumnIndex(ProyectoDBMetadata.TablaPrioridadMetadata.PRIORIDAD_ALIAS));
        prioridad.setText(p);
        responsable.setText(cursor.getString(cursor.getColumnIndex(ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO_ALIAS)));
        Boolean isFinalizada = cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA))==1;
        finalizada.setChecked(isFinalizada);
        finalizada.setTextIsSelectable(false);

        //Deshabilito las tareas finalizadas
        finalizada.setEnabled(!isFinalizada);
        btnEstado.setEnabled(!isFinalizada);
        btnEditar.setEnabled(!isFinalizada);
        btnFinalizar.setEnabled(!isFinalizada);

        btnEditar.setTag(cursor.getInt(cursor.getColumnIndex("_id")));
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Integer idTarea= (Integer) view.getTag();
                Intent intEditarAct = new Intent(contexto,AltaTareaActivity.class);
                intEditarAct.putExtra("ID_TAREA",idTarea);
                context.startActivity(intEditarAct);

            }
        });

        btnFinalizar.setTag(cursor.getInt(cursor.getColumnIndex("_id")));
        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Integer idTarea= (Integer) view.getTag();
                Thread backGroundUpdate = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("LAB05-MAIN","finalizar tarea : --- "+idTarea);
                        myDao.finalizar(idTarea);
                        handlerRefresh.sendEmptyMessage(1);
                    }
                });
                backGroundUpdate.start();
            }
        });

        btnEstado.setTag(cursor.getInt(cursor.getColumnIndex("_id")));
        btnEstado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Integer idTarea= (Integer) view.getTag();
                if(((ToggleButton)view).isChecked()){
                    mapTiemposInicioTrabajo.put(idTarea,System.currentTimeMillis());
                }else{
                    Log.d("LAB05-MAIN","Actualizar minutos trabajados : --- "+idTarea);
                    Long timesMillisFin = System.currentTimeMillis();
                    Long timesMillisInicio = mapTiemposInicioTrabajo.get(idTarea);
                    Long tiempoTrabajado =  minutosAsigandos + (timesMillisFin - timesMillisInicio) / 5000;
                    myDao.actualizarMinutosTrabajados(idTarea,tiempoTrabajado);
                    handlerRefresh.sendEmptyMessage(1);
                }
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                selectedView = view;
                AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(
                        context);
                alert.setTitle(view.getResources().getString(R.string.tittle_dialog_eliminar));
                alert.setMessage(view.getResources().getString(R.string.msg_dialog_eliminar));
                String msgTareaEliminada = view.getResources().getString(R.string.msg_dialog_eliminar);
                alert.setPositiveButton(view.getResources().getString(R.string.button_positive_dialog_eliminar), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Integer idTarea = (Integer)((Button) selectedView.findViewById(R.id.tareaBtnEditarDatos)).getTag();
                        Toast.makeText(context, context.getString(R.string.msg_tarea_eliminada), Toast.LENGTH_SHORT).show();
                        myDao.borrarTarea(idTarea);
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
            myDao.open();
            TareaCursorAdapter.this.changeCursor(myDao.listaTareas(1));
            myDao.close();
        }
    };

}

