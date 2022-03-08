package com.davidbanda.peluqueria;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import ServiciosWeb.Servicios;
import ServiciosWeb.Servidor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiciosActivity extends AppCompatActivity {


    Retrofit objetoRetrofit;
    Servicios peticionesWeb;
    Servidor miServidor;

    ListView lstServicios;
    ArrayList<String> listaServicios = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicios);

        lstServicios=findViewById(R.id.lstServicios);
        miServidor=new Servidor();
        objetoRetrofit=new Retrofit.Builder()
                .baseUrl(miServidor.obtenerurlBase())
                .addConverterFactory(GsonConverterFactory.create()).build();
        peticionesWeb=objetoRetrofit.create(Servicios.class);
    }

    public void consultarServ(View view){
        listaServicios.clear();
        Call llamadaHTTP=peticionesWeb.consultarServi();
        llamadaHTTP.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if(response.isSuccessful()){
                        String resultadoJson = new Gson().toJson(response.body());
                        JsonObject objetoJson = new JsonParser().parse(resultadoJson).getAsJsonObject();
                        if(objetoJson.get("estado").getAsString().equalsIgnoreCase("ok")){
                            JsonArray listadoObtenido = objetoJson.getAsJsonArray("datos");

                            for(JsonElement servicioTemporal:listadoObtenido){
                                String nombre = servicioTemporal.getAsJsonObject().get("nombre_ser").toString();
                                String descripcion = servicioTemporal.getAsJsonObject().get("descripcion_ser").toString();
                                String precio = servicioTemporal.getAsJsonObject().get("precio_ser").toString();
                                String foto = servicioTemporal.getAsJsonObject().get("foto_ser").toString();


                                listaServicios.add(nombre.replace("\"","") + " | " + descripcion.replace("\"","") + " | " + precio.replace("\"","") + " | " + foto.replace("\"","") + " | ");


                            }
                            ArrayAdapter<String> adaptadorServicios = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, listaServicios);
                            lstServicios.setAdapter(adaptadorServicios);
                        } else {
                            Toast.makeText(getApplicationContext(), "No se encontraron Usuarios",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Error al traer los DATOS",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex){
                    Toast.makeText(getApplicationContext(), "Error en la App Web -> " +ex.toString(),
                            Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXION (IP)",
                        Toast.LENGTH_LONG).show();
            }
        });

    }


    public void abrirFormularioIngreso(View view){
        finish();// Cerrando ventana actual
        //Objeto para manipular la actividad Menu
        Intent ventanIngreso = new Intent(getApplicationContext(), FormularioServiciosActivity.class);
        startActivity(ventanIngreso); //Solicitando que se abra el Menu
    }


}