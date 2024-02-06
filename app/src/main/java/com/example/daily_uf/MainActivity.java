package com.example.daily_uf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daily_uf.BaseDeDatos.AdminSQLiteOpenHelper;
import com.example.daily_uf.Interfaz.ServicioWebPosts;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView TextView_Dolar;
    private Button consultarDolar;
    private Button listarDB;
    private Button eliminarDatosDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView_Dolar = (TextView) findViewById(R.id.TextView_Dolar);
        consultarDolar = (Button)findViewById(R.id.consultarDolar);
        listarDB = (Button)findViewById(R.id.listarDB);
        eliminarDatosDB = (Button)findViewById(R.id.eliminarDatosDB);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.mindicador.cl/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioWebPosts servicioWebPosts = retrofit.create(ServicioWebPosts.class);

        consultarDolar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                consultarDolar(servicioWebPosts);
            }
        });

        listarDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListarDatosDB.class);
                startActivity(intent);
            }
        });

        eliminarDatosDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarDatosDeDB();
            }
        });
    }

    public void consultarDolar(ServicioWebPosts servicioWebPosts) {

        Call<JsonObject> call = servicioWebPosts.getDolarDelDia();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null && jsonObject.has("serie") && jsonObject.getAsJsonArray("serie").size() > 0) {
                        // Obtener el valor del dolar
                        JsonObject DolarData = jsonObject.getAsJsonArray("serie").get(0).getAsJsonObject();
                        if (DolarData.has("valor")) {
                            double dolar_value = DolarData.get("valor").getAsDouble();
                            TextView_Dolar.setText(String.format("Dolar del día: $ %.2f", dolar_value));

                            //Guardar en la base de datos local
                            guardarEnBaseDeDatos(dolar_value);
                        } else {
                            TextView_Dolar.setText("error al cargar los datos");
                        }
                    } else {
                        TextView_Dolar.setText("error al cargar los datos");
                    }
                } else {
                    TextView_Dolar.setText("error al cargar los datos");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Disculpe, hubo un error al cargar los datos", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void guardarEnBaseDeDatos(double dolarValue) {
        try {
            AdminSQLiteOpenHelper adminSQLiteOpenHelper = new AdminSQLiteOpenHelper(this);
            SQLiteDatabase db = adminSQLiteOpenHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("dolar_value", dolarValue);

            db.insert("DOLARTable", null, values);
            db.close();

            Toast.makeText(getApplicationContext(), "Valor del dolar guardado exitosamente", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("Guardar en BD", "Error: " + e.getMessage());
        }
    }

    private void eliminarDatosDeDB() {
        try {
            AdminSQLiteOpenHelper adminSQLiteOpenHelper = new AdminSQLiteOpenHelper(this);
            SQLiteDatabase db = adminSQLiteOpenHelper.getWritableDatabase();

            // Eliminar todos los datos de la tabla
            db.delete("DOLARTable", null, null);
            db.close();

            Toast.makeText(getApplicationContext(), "Datos eliminados exitosamente", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("Eliminar Datos DB", "Error: " + e.getMessage());
        }
    }

    //Método para el botón regresar
    public void Regresar(View view){
        Intent regresar = new Intent(this, MainActivity.class);
        startActivity(regresar);
    }

}
