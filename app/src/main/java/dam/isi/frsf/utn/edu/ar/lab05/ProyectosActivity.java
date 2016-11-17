package dam.isi.frsf.utn.edu.ar.lab05;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoApiRest;

public class ProyectosActivity extends AppCompatActivity {

    private ListView lvProyectos;
    private Cursor cursor;
    private ProyectoCursorAdapter pca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proyectos);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.altaProyecto);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intProyectoAlta= new Intent(ProyectosActivity.this,AltaProyectoActivity.class);
                intProyectoAlta.putExtra("ID_PROYECTO", 0);
                startActivity(intProyectoAlta);
            }
        });
        FloatingActionButton volver = (FloatingActionButton) findViewById(R.id.volver);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intProyectoAlta= new Intent(ProyectosActivity.this,MainActivity.class);
                startActivity(intProyectoAlta);
            }
        });
        lvProyectos = (ListView) findViewById(R.id.listProyectos);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cursor = ProyectoApiRest.getInstance().getCursorProyectos();
        pca = new ProyectoCursorAdapter(ProyectosActivity.this,cursor,null);
        lvProyectos.setAdapter(pca);
    }

    protected void onPause() {
        super.onPause();
        if(cursor!=null) cursor.close();
    }
}
