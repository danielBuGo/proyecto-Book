package com.example.appcrud;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.AlertDialog;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    EditText Codigo, Descripcion, Precio, Disponible;
    Button Insertar, Buscar, Modificar, Eliminar;
    TextView mensajeTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mensajeTextView = findViewById(R.id.mensajeTextView);
        Codigo = findViewById(R.id.codigo);
        Descripcion = findViewById(R.id.descripcion);
        Precio = findViewById(R.id.precio);
        Insertar = findViewById(R.id.insertar);
        Buscar = findViewById(R.id.leer);
        Modificar = findViewById(R.id.modificar);
        Eliminar = findViewById(R.id.eliminar);

        Insertar.setOnClickListener(view -> {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "tienda", null, 1);
            SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

            String codigo = Codigo.getText().toString();
            String descripcion = Descripcion.getText().toString();
            String precio = Precio.getText().toString();
            int disponible = Integer.parseInt(Disponible.getText().toString());

            if (!codigo.isEmpty() && !descripcion.isEmpty() && !precio.isEmpty()) {
                ContentValues registro = new ContentValues();
                registro.put("codigo", codigo);
                registro.put("descripcion", descripcion);
                registro.put("precio", precio);
                registro.put("disponible", disponible);

                Codigo.setText("");
                Descripcion.setText("");
                Precio.setText("");
                Disponible.setText("");
                mensajeTextView.setText("INFORMACIÓN GUARDADA");

                BaseDeDatos.insert("articulos", null, registro);
                BaseDeDatos.close();
            } else {
                mensajeTextView.setText("LLENA LOS CAMPOS");
            }
        });

        Buscar.setOnClickListener(v -> {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(MainActivity.this, "tienda", null, 1);
            SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

            String codigo = Codigo.getText().toString();

            if (!codigo.isEmpty()) {
                Cursor fila = BaseDeDatos.rawQuery("SELECT descripcion, precio FROM articulos WHERE codigo =" + codigo, null);

                if (fila.moveToFirst()) {
                    Descripcion.setText(fila.getString(0));
                    Precio.setText(fila.getString(1));

                    Cursor contador = BaseDeDatos.rawQuery("SELECT COUNT(*) FROM articulos WHERE codigo =" + codigo, null);
                    contador.moveToFirst();
                    int cantidadRegistros = contador.getInt(0);
                    contador.close();

                    // Modificación: Establecer el mensaje en el TextView
                    mensajeTextView.setText("Existe " + cantidadRegistros + " ejemplares disponibles con ID: " + codigo);
                } else {
                    // Modificación: Establecer el mensaje en el TextView
                    mensajeTextView.setText("No existen ejemplares disponibles");
                }

                BaseDeDatos.close();
            } else {

                mensajeTextView.setText("Debes introducir el código del libro");
            }
        });


        Modificar.setOnClickListener(v -> {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "tienda", null, 1);
            SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

            String codigo = Codigo.getText().toString();
            String descripcion = Descripcion.getText().toString();
            String precio = Precio.getText().toString();

            if (!codigo.isEmpty() && !descripcion.isEmpty() && !precio.isEmpty()) {
                ContentValues registro = new ContentValues();
                registro.put("descripcion", descripcion);
                registro.put("precio", precio);

                int validar = BaseDeDatos.update("articulos", registro, "codigo =" + codigo, null);
                BaseDeDatos.close();

                if (validar == 1) {
                    mensajeTextView.setText("Registro modificado");
                } else {
                    mensajeTextView.setText("No se pudo modificar");
                }
            } else {
                mensajeTextView.setText("Debes llenar los datos");
            }
        });



        Eliminar.setOnClickListener(v -> {
            // Crear un AlertDialog de confirmación
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("¿Está seguro de borrar el registro?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        // Si el usuario confirma, procede a borrar el registro
                        eliminarRegistro();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // Si el usuario cancela, cierra el diálogo y no hace nada
                        dialog.dismiss();
                    })
                    .show();
        });

    }
    private void eliminarRegistro() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "tienda", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();
        String codigo = Codigo.getText().toString();

        if (!codigo.isEmpty()) {
            int validacion = BaseDeDatos.delete("articulos", "codigo=" + codigo, null);
            BaseDeDatos.close();

            Codigo.setText("");
            Descripcion.setText("");
            Precio.setText("");

            if (validacion == 1) {
                mensajeTextView.setText("Registro Eliminado");
            } else {
                mensajeTextView.setText("No se elimino");
            }
        } else {
            mensajeTextView.setText("Debes ingresar el código");
        }
    }
}