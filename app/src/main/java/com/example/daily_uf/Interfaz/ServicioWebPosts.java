package com.example.daily_uf.Interfaz;

import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ServicioWebPosts {

    //con este metodo obtenemos toda la información de la URL
    //La anotación @GET("uf") indica que esta solicitud HTTP es un método GET a la ruta "uf".
    @GET("dolar")
    Call<JsonObject> getDolarDelDia();
}
