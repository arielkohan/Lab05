package dam.isi.frsf.utn.edu.ar.lab05;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDBMetadata;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Prioridad;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Usuario;


public class SeleccionarNuevoResponsableActivity extends AppCompatActivity implements View.OnClickListener {

    private ProyectoDAO proyectoDAO;

    private Spinner spinner;

    private Button btnSeleccionar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        proyectoDAO = new ProyectoDAO(SeleccionarNuevoResponsableActivity.this);
        proyectoDAO.open();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_responsable);


        btnSeleccionar = (Button) findViewById(R.id.btnSeleccionar);
        btnSeleccionar.setOnClickListener(this);

        Cursor contacts = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        spinner = (Spinner) findViewById(R.id.spinnerResponsable);
        SimpleCursorAdapter  adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                contacts,
                new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME},
                new int[] {android.R.id.text1});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSeleccionar:
                seleccionar();
                break;
        }
    }

    private void seleccionar(){

        Cursor c=(Cursor) spinner.getSelectedItem();
        String nombre = c.getString(c.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        if(!proyectoDAO.existeResponsable(nombre)){
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            proyectoDAO.nuevoResponsable(usuario);
        }


        Intent intTarea= new Intent(SeleccionarNuevoResponsableActivity.this,AltaTareaActivity.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Integer idTarea = extras.getInt("ID_TAREA");
            intTarea.putExtra("ID_TAREA", idTarea);
        } else {
            intTarea.putExtra("ID_TAREA", 0);
        }

        startActivity(intTarea);
    }

}
