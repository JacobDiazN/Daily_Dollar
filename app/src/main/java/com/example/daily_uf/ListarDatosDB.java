package com.example.daily_uf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.daily_uf.BaseDeDatos.AdminSQLiteOpenHelper;

public class ListarDatosDB extends AppCompatActivity {

    private TextView mostrarText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_datos_db);

        mostrarText = findViewById(R.id.mostrarText);
        mostrarDatosDeDB();
    }

    private void mostrarDatosDeDB() {
        try {
            AdminSQLiteOpenHelper adminSQLiteOpenHelper = new AdminSQLiteOpenHelper(this);
            SQLiteDatabase db = adminSQLiteOpenHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM DOLARTable", null);
            StringBuilder data = new StringBuilder();
            if (cursor.moveToFirst()) {
                do {
                    double dolarValue = cursor.getDouble(cursor.getColumnIndex("dolar_value"));
                    String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));
                    Log.d("Listar Datos DB", "Dolar: " + dolarValue + ", Fecha: " + timestamp);
                    data.append(String.format("Dolar: $ %.2f, Fecha: %s\n", dolarValue, timestamp));
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            mostrarText.setText(data.toString());
        } catch (Exception e) {
            Log.e("Listar Datos DB", "Error: " + e.getMessage());
        }
    }

    // Método para el botón regresar
    public void Regresar(View view){
        Intent regresar = new Intent(this, MainActivity.class);
        startActivity(regresar);
    }
}
