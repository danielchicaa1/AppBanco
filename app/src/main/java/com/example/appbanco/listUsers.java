package com.example.appbanco;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class listUsers extends AppCompatActivity {
    ListView listUsers;
    ArrayList<String> arrUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);
        listUsers = findViewById(R.id.lvUsers);
        Button btnBack = findViewById(R.id.btnback);
        loadUsers();
    }

    private void loadUsers() {
        arrUsers  = retrieveUsers();
        // Generar el adaptador que paara los datos al ListView
        ArrayAdapter<String> adpUser = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,arrUsers);
        listUsers.setAdapter(adpUser);


    }

    private ArrayList<String> retrieveUsers() {
        ArrayList <String> userData = new ArrayList<String>();
        // Cargar los usuarios en el arraylist arrUsers;
        bdBanco sohdbBanco = new bdBanco(getApplicationContext(),"bancodb",null,1);
        SQLiteDatabase dbBancoread = sohdbBanco.getReadableDatabase();
        String qAllUsers = "Select email, name , password, role From User";
        Cursor cUsers =  dbBancoread.rawQuery(qAllUsers,null);
        if(cUsers.moveToFirst()){
            do{
                // Generar un String para almacenar toda la información de cada usuario
                // y guardarlo en el arrayList
                //String mRole = cUsers.getInt(3) == 0 ? "usuario" : "Administrador";
                String recUser = cUsers.getString(1)+" - " + cUsers.getString(0) + " - " + cUsers.getString(3) ;
                userData.add(recUser);
            }while(cUsers.moveToNext());
        }
        dbBancoread.close();
        return userData;
    }
}