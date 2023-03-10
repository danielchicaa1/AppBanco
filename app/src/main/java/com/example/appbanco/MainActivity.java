package com.example.appbanco;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    //Instancias los datos del usuario
    EditText name , password , email ;
    RadioButton user , admin ;
    Button create , search , edit , delete , list , sign;
    //Instanciar la clase dbBanco para los diferentes botones
    bdBanco Banco = new bdBanco(this,"bancodb",null,1);
    String oldEmail="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Referenciar los objetos instanciados
        email = findViewById(R.id.etIngresarCorreo);
        name = findViewById(R.id.etIngresarNombre);
        password = findViewById(R.id.etIngresarContraseña);
        user = findViewById(R.id.rbUsuario);
        admin = findViewById(R.id.rbAdmin);
        create = findViewById(R.id.btCrear);
        search = findViewById(R.id.btBuscar);
        edit = findViewById(R.id.btEditar);
        delete = findViewById(R.id.btEliminar);
        sign = findViewById(R.id.btIngresar);
        list = findViewById(R.id.btEnlistar);
        //Deshabiilitar el boton de listar
        list.setEnabled(false);
        //Evento de editar

        //Evento de Guardar

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase database = Banco.getReadableDatabase();
                String nameOnScreen = user.getText().toString();
                String passwordOnScreen = password.getText().toString();

                if (nameOnScreen.isEmpty() || passwordOnScreen.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Debes ingresar usuario y contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                String query = "SELECT username, email, password, role FROM users WHERE username = '" + nameOnScreen + "'";
                Cursor cursor = database.rawQuery(query, null);
                if (!cursor.moveToFirst()) {
                    Toast.makeText(MainActivity.this, "No existe este usuario", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!passwordOnScreen.equals(cursor.getString(2))) {
                    Toast.makeText(MainActivity.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intentEdit = new Intent(getApplicationContext(), Edit.class);
                intentEdit.putExtra("username" , nameOnScreen);
                intentEdit.putExtra("email", cursor.getString(1));
                intentEdit.putExtra("password", cursor.getString(2));
                intentEdit.putExtra("role" , cursor.getInt(3));
                startActivity(intentEdit);
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stringPassword = password.getText().toString();
                String stringName = name.getText().toString();

                if (stringPassword.trim().length() == 0) {
                    Toast.makeText(MainActivity.this, "Digita la clave si deseas borrar el usuario", Toast.LENGTH_SHORT).show();
                    return;
                }

                SQLiteDatabase database = Banco.getReadableDatabase();
                String query = "SELECT username, password FROM users WHERE username = '" + stringName + "'";
                Cursor cursor = database.rawQuery(query, null);

                if (cursor.moveToFirst()) {
                    borrarUsuario(database, cursor, stringPassword);
                    return;
                }

                Toast.makeText(MainActivity.this, "Usuario no registrado", Toast.LENGTH_SHORT).show();

            }
        });

        create.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view){
                if (!email.getText().toString().isEmpty() && !name.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                    // Buscar el email en la tabla user
                    SQLiteDatabase sdBancoread = Banco.getReadableDatabase();
                    String query = "Select email From User Where email = '" + email.getText().toString() + "'";
                    Cursor cUser = sdBancoread.rawQuery(query, null);
                    if (!cUser.moveToFirst()) {
                        //No encuentra el email ingresado

                        // Instanciar la  clase de SQLiteDatabase en modo escritura
                        SQLiteDatabase sdBanco = Banco.getWritableDatabase();
                        // Contenedor de valores
                        ContentValues cvUser = new ContentValues();
                        cvUser.put("email", email.getText().toString());
                        cvUser.put("name", name.getText().toString());
                        cvUser.put("password", password.getText().toString());
                        cvUser.put("role", user.isChecked() ? 0 : 1);
                        sdBanco.insert("user", null, cvUser);
                        sdBanco.close();
                        Toast.makeText(getApplicationContext(), "Usuario Guardado Correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Usuario registrado , intente con otro email", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Debe ingresar todos los datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Evento para listado de usuarios
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Verificar si el usuario tiene rol de administrador
                if (admin.isChecked()){
                    // Pasar a la actividad que muestra los usuarios
                    startActivity(new Intent(getApplicationContext(),listUsers.class));
                }
                else{
                    Toast.makeText(getApplicationContext(), "El usuario activo no tiene privilegios para este informe", Toast.LENGTH_SHORT).show();
                }

            }

        });
        //Evento Click para el boton buscar
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchUser(email.getText().toString());
            }
        });
    }

    private void searchUser(String sEmail) {
        //Abrir la base de datos en modo lectura
        SQLiteDatabase bdsearchUser = Banco.getReadableDatabase();
        String sqlSearch = "SELECT email, name, role FROM user WHERE email = '"+sEmail+"'";
        Cursor cUser = bdsearchUser.rawQuery(sqlSearch, null);
        if (cUser.moveToFirst()){
            oldEmail=email.getText().toString();
            //Mostrar los datos del usuario en pantalla
            name.setText(cUser.getString(1));
            if (cUser.getInt(2)==1){
                admin.setChecked(true);
                //Habilitar el boton de listar
                list.setEnabled(true);
            }
            else{
                user.setChecked(true);
                //Deshabilitar el boton de listar
                list.setEnabled(false);
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "El email del usuario no se encontro", Toast.LENGTH_SHORT).show();
        }
        bdsearchUser.close();
    }

    private void borrarUsuario (SQLiteDatabase database, Cursor cursor, String stringPassword) {
        String dbPassword = cursor.getString(1);
        String name = cursor.getString(0);

        if (cursor.getString(1).equals(stringPassword)) {
            String query = "DELETE FROM users WHERE username = '"+ name + "'";
            database.execSQL(query);
            Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Clave incorrecta", Toast.LENGTH_SHORT).show();
    }
}