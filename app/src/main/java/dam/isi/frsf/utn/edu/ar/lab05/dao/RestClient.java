package dam.isi.frsf.utn.edu.ar.lab05.dao;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by martdominguez on 20/10/2016.
 */
public class RestClient {

    private final String IP_SERVER = "192.168.0.7";
    private final String PORT_SERVER = "4000";

    public JSONObject getById(Integer id, String path) {
        JSONObject resultado = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL("http://" + IP_SERVER + ":" + PORT_SERVER + "/" + path + "/" + id);

            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            InputStreamReader isw = new InputStreamReader(in);
            StringBuilder sb = new StringBuilder();

            int data = isw.read();
            while (data != -1) {
                char current = (char) data;
                sb.append(current);
                data = isw.read();
            }
            resultado = new JSONObject(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
        return resultado;
    }

    public JSONArray getByAll(String path) {
        JSONArray resultado = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL("http://" + IP_SERVER + ":" + PORT_SERVER + "/" + path);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            InputStreamReader isw = new InputStreamReader(in);
            StringBuilder sb = new StringBuilder();

            int data = isw.read();
            while (data != -1) {
                char current = (char) data;
                sb.append(current);
                data = isw.read();
            }
            resultado = new JSONArray(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
        return resultado;
    }

    public void crear(JSONObject objeto, String path) {
        try {
            String str = objeto.toString();
            byte[] data = str.getBytes("UTF-8");
            httpConnectionParaCrearOActualizar(data, "POST", path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actualizar(JSONObject objeto, String path) {
        try {
            String str = objeto.toString();
            byte[] data = str.getBytes("UTF-8");
            httpConnectionParaCrearOActualizar(data, "PUT", path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void borrar(Integer id, String path) {
        try {
            path = path + "/" + id;
            httpConnectionParaBusquedaOEliminacion("DELETE", path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void httpConnectionParaCrearOActualizar(byte[] datosAEnviar, String tipoRequest, String path) {
        if (tipoRequest.equals("POST") || tipoRequest.equals("PUT")) {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("http://" + IP_SERVER + ":" + PORT_SERVER + "/" + path);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod(tipoRequest);
                urlConnection.setFixedLengthStreamingMode(datosAEnviar.length);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                DataOutputStream flujoSalida = new DataOutputStream(urlConnection.getOutputStream());
                flujoSalida.write(datosAEnviar);
                flujoSalida.flush();
                flujoSalida.close();

                urlConnection.getResponseMessage();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }
    }

    private void httpConnectionParaBusquedaOEliminacion(String tipoRequest, String path) {
        if (tipoRequest.equals("GET") || tipoRequest.equals("DELETE")) {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("http://" + IP_SERVER + ":" + PORT_SERVER + "/" + path);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod(tipoRequest);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.getResponseMessage();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }
    }
}
