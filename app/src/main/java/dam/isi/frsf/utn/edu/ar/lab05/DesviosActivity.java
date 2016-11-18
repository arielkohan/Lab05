package dam.isi.frsf.utn.edu.ar.lab05;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;

public class DesviosActivity extends AppCompatActivity {

    private ProyectoDAO proyectoDAO;

    private EditText txtMinutosDesvio;
    private Button btnBuscar;
    private CheckBox finalizadaCheckBox;
    private TextView txtResultado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desvio);

        proyectoDAO = new ProyectoDAO(DesviosActivity.this);
        proyectoDAO.open();

        txtMinutosDesvio = (EditText) findViewById(R.id.editText_minutos_desvio);
        btnBuscar = (Button) findViewById(R.id.buttonBuscar);
        finalizadaCheckBox = (CheckBox) findViewById(R.id.tareaFinalizada);
        txtResultado = (TextView) findViewById(R.id.txtResultado);
        txtResultado.setMovementMethod(new ScrollingMovementMethod());

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtMinutosDesvio.getText().toString().isEmpty()) {
                    Toast.makeText(DesviosActivity.this, R.string.msg_ingrese_minutos_desvio , Toast.LENGTH_SHORT).show();
                } else {
                    Integer minutosDESVIO = Integer.parseInt(txtMinutosDesvio.getText().toString());
                    Boolean finalizada = finalizadaCheckBox.isChecked();

                    List<Tarea> listTareas = proyectoDAO.listarDesviosPlanificacion(finalizada, minutosDESVIO);
                    txtResultado.setText("");
                    for (Tarea tarea : listTareas) {

                        if(txtResultado.getText() == null || txtResultado.getText().toString().isEmpty()){
                            txtResultado.setText( tarea.getString());
                        } else {
                            txtResultado.setText( txtResultado.getText() + "\n\n" + tarea.getString());
                        }
                    }
                }
            }
        });

    }
}
