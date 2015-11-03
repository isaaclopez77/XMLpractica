package com.example.usuario.xml;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.usuario.xml.xml.VerXML;

public class Options extends AppCompatActivity {

    private TextView tvFecha;
    private ToggleButton tbt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opciones);
        tvFecha = (TextView)findViewById(R.id.tvFech);
        tbt=(ToggleButton)findViewById(R.id.toggleButton);

    }

    public void verCopia(View v){
        Intent i = new Intent(this, VerXML.class);
        Bundle b = new Bundle();
        b.putString("total","archivo.xml");
        i.putExtras(b);
        startActivity(i);
    }

    public void copiaIncremental(View v){
        Intent i = new Intent(this, VerXML.class);
        Bundle b = new Bundle();
        b.putString("total","incremental.xml");
        i.putExtras(b);
        startActivity(i);
    }
}
