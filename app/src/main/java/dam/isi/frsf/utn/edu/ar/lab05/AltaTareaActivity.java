package dam.isi.frsf.utn.edu.ar.lab05;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Prioridad;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Usuario;

public class AltaTareaActivity extends AppCompatActivity implements View.OnClickListener {

    private ProyectoDAO proyectoDAO;
    List<Prioridad> prioridadesList;

    private Spinner spinner;
    private List<Usuario> usuarioArrayList;
    private ArrayAdapter<Usuario> spinnerAdapter;

    private EditText txtDescripcion;
    private EditText txtHoras;
    private TextView txtPrioridad;

    private SeekBar seekBar;

    private Button btnGuardar;
    private Button btnCancelar;


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

        prioridadesList = proyectoDAO.listarPrioridades();

        spinner = (Spinner) findViewById(R.id.spinner);
        usuarioArrayList = proyectoDAO.listarUsuarios();
        ArrayAdapter<Usuario> adapter = new ArrayAdapter<Usuario>(this, android.R.layout.simple_spinner_dropdown_item, usuarioArrayList);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Usuario usuario = (Usuario) parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

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
                for(int i = 0; i < adapter.getCount(); i++)
                {
                    if (tarea.getResponsable().getId().equals(adapter.getItem(i).getId()))
                    {
                        spinner.setSelection(i);
                        break;
                    }
                }
            } else {
                seekBar.setProgress(3); // Por defecto la seteo en Baja a la prioridad
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
            case R.id.btnCancelar:
                cancelar();
                break;
        }
    }

    private void guardar() {

        Tarea tarea = new Tarea();
        tarea.setDescripcion((String) txtDescripcion.getText().toString());
        tarea.setHorasEstimadas(Integer.parseInt(txtHoras.getText().toString()));
        Usuario usuario = (Usuario) spinner.getSelectedItem();
        tarea.setResponsable(usuario);
        Integer prioridad = seekBar.getProgress() + 1;
        tarea.setPrioridad(new Prioridad(prioridad,""));
        tarea.setProyecto(new Proyecto(1,""));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Integer idTarea = extras.getInt("ID_TAREA");
            if(idTarea != null && !idTarea.equals(0)){
                tarea.setId(idTarea);
                proyectoDAO.actualizarTarea(tarea);
                Toast.makeText(this, "Se ha modificado la tarea", Toast.LENGTH_SHORT).show();
            } else {
                tarea.setMinutosTrabajados(0);
                tarea.setFinalizada(Boolean.FALSE);
                proyectoDAO.nuevaTarea(tarea);
                Toast.makeText(this, "Se ha creado la tarea", Toast.LENGTH_SHORT).show();
            }
        }
        Intent mainActivity= new Intent(AltaTareaActivity.this,MainActivity.class);
        startActivity(mainActivity);
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
