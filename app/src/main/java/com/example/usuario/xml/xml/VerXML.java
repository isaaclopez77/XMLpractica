package com.example.usuario.xml.xml;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.xml.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class VerXML extends AppCompatActivity {

    private TextView tv;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prueba);
        Intent i =this.getIntent();
        Bundle b=i.getExtras();

        result=  b.getString("total");

        Toast.makeText(this,result,Toast.LENGTH_LONG).show();
        try {
            leer(result);
        } catch (Exception e) {
        }
    }

    public void leer(String result) throws IOException, XmlPullParserException {
        tv = (TextView) findViewById(R.id.tvPrueba);
        XmlPullParser lectorxml = Xml.newPullParser();
        lectorxml.setInput(new FileInputStream(new File(getExternalFilesDir(null),result)),"utf-8");
        int evento = lectorxml.getEventType();
        while (evento != XmlPullParser.END_DOCUMENT){
            if(evento == XmlPullParser.START_TAG){
                String etiqueta = lectorxml.getName();
                if(etiqueta.compareTo("nombre")==0){
                    String atrib = lectorxml.getAttributeValue(null, "id");
                    String texto = lectorxml.nextText();
                    tv.append("id: " + atrib + " nombre: " + texto + "\n");
                } else if(etiqueta.compareTo("telefono")==0){
                    String texto = lectorxml.nextText();
                    tv.append("    telefono: " + texto + "\n");
                }
            }
            evento = lectorxml.next();
        }
    }
}
