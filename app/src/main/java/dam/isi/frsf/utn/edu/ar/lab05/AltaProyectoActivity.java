package dam.isi.frsf.utn.edu.ar.lab05;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoApiRest;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;

public class AltaProyectoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CONTACT =999;

    private EditText txtTitulo;

    private Button btnGuardar;
    private Button btnCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_proyecto);

        txtTitulo = (EditText) findViewById(R.id.txtTituloProyecto);

        btnGuardar = (Button) findViewById(R.id.btnGuardarAltaProyecto);
        btnGuardar.setOnClickListener(this);
        btnCancelar = (Button) findViewById(R.id.btnCancelarAltaProyecto);
        btnCancelar.setOnClickListener(this);

         //Si es edición -> cargo los datos del proyecto.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Integer idProyecto = extras.getInt("ID_PROYECTO");
            if(idProyecto != null && !idProyecto.equals(0)){
                Proyecto proyecto = ProyectoApiRest.getInstance().buscarProyecto(idProyecto);
                txtTitulo.setText(proyecto.getNombre());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnGuardarAltaProyecto:
                guardar();
                break;
            case R.id.btnCancelarAltaProyecto:
                cancelar();
                break;
        }
    }


    private void guardar() {
        if(txtTitulo.getText() == null || txtTitulo.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Debe ingresa un nombre.", Toast.LENGTH_SHORT).show();
        } else {
            Proyecto proyecto = new Proyecto();
            proyecto.setNombre((String) txtTitulo.getText().toString());

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                Integer idProyecto = extras.getInt("ID_PROYECTO");
                if(idProyecto != null && !idProyecto.equals(0)){
                    proyecto.setId(idProyecto);
                    ProyectoApiRest.getInstance().actualizarProyecto(proyecto);
                    Toast.makeText(this, getString(R.string.msg_proyecto_modificada), Toast.LENGTH_SHORT).show();
                } else {
                    proyecto.setId(null);
                    ProyectoApiRest.getInstance().crearProyecto(proyecto);
                    Toast.makeText(this, getString(R.string.msg_proyecto_creada), Toast.LENGTH_SHORT).show();
                }
            }

            Intent mainActivity= new Intent(AltaProyectoActivity.this,ProyectosActivity.class);
            startActivity(mainActivity);
        }
    }

    private void cancelar() {
        Intent mainActivity= new Intent(AltaProyectoActivity.this,ProyectosActivity.class);
        startActivity(mainActivity);
    }
}
