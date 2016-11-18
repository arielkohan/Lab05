package dam.isi.frsf.utn.edu.ar.lab05.dao;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.MainActivity;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Prioridad;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Usuario;

/**
 * Created by martdominguez on 20/10/2016.
 */
public class ProyectoApiRest {

    private static ProyectoApiRest proyectoApiRest = new ProyectoApiRest();

    public ProyectoApiRest() {

    }

    public static ProyectoApiRest getInstance() {
        return proyectoApiRest;
    }

    public void crearProyecto(Proyecto p) {
        JSONObject jsonNuevoProyecto = new JSONObject();
        RestClient cliRest = new RestClient();
        try {
            jsonNuevoProyecto.put("titulo", p.getNombre());
            cliRest.crear(jsonNuevoProyecto, "proyectos");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void borrarProyecto(Integer id) {
        RestClient cliRest = new RestClient();
        try {
            cliRest.borrar(id, "proyectos");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actualizarProyecto(Proyecto p) {
        JSONObject jsonNuevoProyecto = new JSONObject();
        RestClient cliRest = new RestClient();
        try {
            jsonNuevoProyecto.put("id", p.getId());
            jsonNuevoProyecto.put("titulo", p.getNombre());
            cliRest.actualizar(jsonNuevoProyecto, "proyectos/" + p.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Cursor getCursorProyectos() {
        MatrixCursor mc = new MatrixCursor(new String[]{ProyectoDBMetadata.TablaProyectoMetadata._ID, ProyectoDBMetadata.TablaProyectoMetadata.TITULO});
        int id;
        String nombre;
        try {
            JSONArray listaProyectos = buscarProyectos();
            for (int i = 0; i < listaProyectos.length(); i++) {
                JSONObject proyecto = null;
                proyecto = listaProyectos.getJSONObject(i);
                id = proyecto.getInt("id");
                nombre = proyecto.getString("titulo");
                mc.addRow(new Object[]{id, nombre});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mc;
    }

    private JSONArray buscarProyectos() {
        RestClient cliRest = new RestClient();
        JSONArray proyectos = null;
        try {
            proyectos = cliRest.getByAll("proyectos");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return proyectos;
    }

    public void guardarUsuario(Usuario nuevoUsuario) {
        try {
            RestClient cliRest = new RestClient();
            JSONObject jsonUsuario = new JSONObject();
            jsonUsuario.put("nombre", nuevoUsuario.getNombre());
            cliRest.crear(jsonUsuario, "usuarios");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Proyecto buscarProyecto(Integer id) {
        RestClient cliRest = new RestClient();
        Proyecto proyecto = null;
        try {
            JSONObject t = cliRest.getById(id, "proyectos");
            proyecto = new Proyecto(t.getInt("id"), t.getString("titulo"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return proyecto;
    }

    public List<Tarea> getTareas(Integer idProyecto) {
        List<Tarea> listTareas = new ArrayList<>();
        try {
            JSONArray listaTareas = buscarTareas();
            for (int i = 0; i < listaTareas.length(); i++) {
                JSONObject tareaAux = null;
                tareaAux = listaTareas.getJSONObject(i);

                if (tareaAux.getInt("proyectoId") == idProyecto) {
                    Tarea tarea = new Tarea();
                    tarea.setId(tareaAux.getInt("id"));
                    tarea.setDescripcion(tareaAux.getString("descripcion"));
                    tarea.setHorasEstimadas(tareaAux.getInt("horasEstimadas"));
                    tarea.setMinutosTrabajados(tareaAux.getInt("minutosTrabajados"));
                    tarea.setFinalizada(Integer.valueOf(1).equals(tareaAux.getInt("finalizada")));
                    listTareas.add(tarea);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listTareas;
    }

    private JSONArray buscarTareas() {
        RestClient cliRest = new RestClient();
        JSONArray proyectos = null;
        try {
            proyectos = cliRest.getByAll("tareas");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return proyectos;
    }

}
