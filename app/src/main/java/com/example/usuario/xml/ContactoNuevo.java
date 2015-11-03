package com.example.usuario.xml;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.usuario.xml.datos.Contacto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ContactoNuevo extends AppCompatActivity{

    private EditText etNom, etNum, etNum2;
    private List<String> telfns= new ArrayList<>();
    private Contacto otro;
    private Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agregar_contacto);
        etNom= (EditText)findViewById(R.id.etNombre);
        etNum=(EditText)findViewById(R.id.etNumero);
        etNum2=(EditText)findViewById(R.id.etNumero2);
    }

    public void guardarContacto(View v){
        telfns.add(etNum.getText().toString());
        telfns.add(etNum2.getText().toString());
        Random r = new Random(200);
        int id = r.nextInt();

        Contacto nuevo = new Contacto(etNom.getText().toString(),id,telfns);

        //Intent intent = this.getIntent();
        //bundle = intent.getExtras();

        Intent i = new Intent(this,Principal.class);
        Bundle b = new Bundle();

        /*if(bundle!=null){
            otro = (Contacto)bundle.getSerializable("mismo");
            b.putSerializable("mismo", otro);
        }*/

        b.putSerializable("contacto",nuevo);
        i.putExtras(b);
        startActivity(i);
    }
}