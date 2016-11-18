package dam.isi.frsf.utn.edu.ar.lab05;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDBMetadata;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Prioridad;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Usuario;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;


public class AltaTareaActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CONTACT =999;

    private ProyectoDAO proyectoDAO;
    List<Prioridad> prioridadesList;

    private Spinner spinner;

    private EditText txtDescripcion;
    private EditText txtHoras;
    private TextView txtPrioridad;

    private SeekBar seekBar;

    private Button btnGuardar;
    private Button btnCancelar;
    private Button btnNuevoResponsable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        proyectoDAO = new ProyectoDAO(AltaTareaActivity.this);
        proyectoDAO.open();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_tarea);

        txtDescripcion = (EditText) findViewById(R.id.txtDescripcion);
        txtHoras = (EditText) findViewById(R.id.txtHorasEstimadas);

        btnGuardar = (Button) findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(this);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(this);
        btnNuevoResponsable = (Button) findViewById(R.id.btnNuevoResponsable);
        btnNuevoResponsable.setOnClickListener(this);

        prioridadesList = proyectoDAO.listarPrioridades();

        spinner = (Spinner) findViewById(R.id.spinner);
        SimpleCursorAdapter  adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                proyectoDAO.listarUsuarios(),
                new String[] {ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO},
                new int[] {android.R.id.text1});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        txtPrioridad = (TextView) findViewById(R.id.txtPrioridad);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtPrioridad.setText(getPrioridad(progress));
            }
        });

        //Si es edición -> cargo los datos de la Tarea.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Integer idTarea = extras.getInt("ID_TAREA");
            if(idTarea != null && !idTarea.equals(0)){
                Tarea tarea = proyectoDAO.getTarea(idTarea);
                txtDescripcion.setText(tarea.getDescripcion());
                txtHoras.setText(tarea.getHorasEstimadas().toString());
                seekBar.setProgress(tarea.getPrioridad().getId() - 1);
                for (int i = 0; i < spinner.getCount(); i++) {
                    Cursor value = (Cursor) spinner.getItemAtPosition(i);
                    Integer id = value.getInt(value.getColumnIndex("_id"));
                    if (id.equals(tarea.getResponsable().getId())) {
                        spinner.setSelection(i);
                    }
                }
                btnNuevoResponsable.setVisibility(View.INVISIBLE);
            } else {
                seekBar.setProgress(3); // Por defecto la seteo en Baja a la prioridad
                btnNuevoResponsable.setVisibility(View.VISIBLE);
            }

        }
        txtPrioridad.setText(getPrioridad(seekBar.getProgress()));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnGuardar:
                guardar();
                break;
            case R.id.btnNuevoResponsable:
                nuevoResponsable();
                break;
            case R.id.btnCancelar:
                cancelar();
                break;
        }
    }

    private void nuevoResponsable() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_CONTACTS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                showExplanation("Autorizar permisos.", "Para crear un nuevo responsable debe darnos permisos para poder acceder a su lista de contactos.", Manifest.permission.READ_CONTACTS, PERMISSION_REQUEST_CONTACT);
            } else {
                requestPermission(Manifest.permission.READ_CONTACTS, PERMISSION_REQUEST_CONTACT);
            }
        } else {
            crearNuevoResponsable();
        }
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CONTACT: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    crearNuevoResponsable();
                } else {
                    Toast.makeText(this, "Se requieren los permisos para continuar con el alta de un nuevo responsable. ", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void crearNuevoResponsable(){

        Intent intTarea= new Intent(AltaTareaActivity.this,SeleccionarNuevoResponsableActivity.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Integer idTarea = extras.getInt("ID_TAREA");
            intTarea.putExtra("ID_TAREA", idTarea);
        } else {
            intTarea.putExtra("ID_TAREA", 0);
            intTarea.putExtra("DESCRIPCION", txtDescripcion.getText().toString());
            intTarea.putExtra("HORAS_ESTIMADAS", txtHoras.getText().toString());
            intTarea.putExtra("PRIORIDAD",  seekBar.getProgress());
        }
        startActivity(intTarea);
    }

    private void guardar() {

        if(txtDescripcion.getText() == null || txtDescripcion.getText().toString().trim().isEmpty()){
            Toast.makeText(this, R.string.msg_ingrese_descripcion, Toast.LENGTH_SHORT).show();
        } else if(txtHoras.getText() == null || txtHoras.getText().toString().trim().isEmpty()){
            Toast.makeText(this, R.string.msg_ingrese_horas_estimadas, Toast.LENGTH_SHORT).show();
        } else {
            Tarea tarea = new Tarea();
            tarea.setDescripcion((String) txtDescripcion.getText().toString());
            tarea.setHorasEstimadas(Integer.parseInt(txtHoras.getText().toString()));

            Cursor c=(Cursor) spinner.getSelectedItem();
            Integer id = c.getInt(c.getColumnIndex("_id"));
            tarea.setResponsable(new Usuario(id,"",""));

            Integer prioridad = seekBar.getProgress() + 1;
            tarea.setPrioridad(new Prioridad(prioridad,""));
            tarea.setProyecto(new Proyecto(1,""));

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                Integer idTarea = extras.getInt("ID_TAREA");
                if(idTarea != null && !idTarea.equals(0)){
                    tarea.setId(idTarea);
                    proyectoDAO.actualizarTarea(tarea);
                    Toast.makeText(this, getString(R.string.msg_tarea_modificada), Toast.LENGTH_SHORT).show();
                } else {
                    tarea.setMinutosTrabajados(0);
                    tarea.setFinalizada(Boolean.FALSE);
                    proyectoDAO.nuevaTarea(tarea);
                    Toast.makeText(this, getString(R.string.msg_tarea_creada), Toast.LENGTH_SHORT).show();
                }
            }

            Intent mainActivity= new Intent(AltaTareaActivity.this,MainActivity.class);
            startActivity(mainActivity);
        }
    }

    private void cancelar() {
        Intent mainActivity= new Intent(AltaTareaActivity.this,MainActivity.class);
        startActivity(mainActivity);
    }

    private String getPrioridad(int progress) {
        String prioridadDesc = "";
        for(Prioridad prioridad: prioridadesList){
            if((progress + 1) == prioridad.getId().intValue()){
                prioridadDesc = prioridad.getPrioridad();
            }
        }
        return prioridadDesc;
    }
}
