package com.example.usuario.xml;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.usuario.xml.datos.Contacto;
import com.example.usuario.xml.xml.VerXML;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Principal extends AppCompatActivity {

    private List<Contacto> lContactos;
    private List<String> lTelefonos;
    private ListView lv;
    private Adaptador ad;
    private Bundle b;
    private Contacto nuevo;

    /************ @OVERRIDES *********************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.mnAcercaDe:
                Toast.makeText(this,"Proyecto XML 2º DAM:\n Isaac López Delgado",Toast.LENGTH_SHORT).show();
                break;
            case R.id.mnAdd:
                Intent i = new Intent(this,ContactoNuevo.class);
                b=i.getExtras();
                /*if(b!=null){
                    b.putSerializable("mismo",b.getSerializable("contacto"));
                }*/
                startActivity(i);
                break;
            case R.id.mnCopiaS:
                try {
                    escribir();
                    Toast.makeText(this,"Copia de seguridad creada: XML generado",Toast.LENGTH_SHORT).show();
                }catch (IOException e){

                }
                break;
            case R.id.mnCopiaI:
                try {
                    incremental();
                    Toast.makeText(this,"Copia incremental creada",Toast.LENGTH_SHORT).show();
                }catch (IOException e){

                }
                break;
            case R.id.mnOpciones:
                Intent intentOpciones = new Intent(this, Options.class);
                startActivity(intentOpciones);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contextual, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() == R.id.mnBorrar) {
            lContactos.remove(info.position);
        }
        ad.notifyDataSetChanged();
        return super.onContextItemSelected(item);
    }

    /***************** INIT() ********************************************/

    public void init() {

        //Aquí se crea la lista de contactos con sus tlfns
        lContactos = getListaContactos(this);
        for(Contacto c : lContactos){
            long id = c.getId();
            lTelefonos = getListaTelefonos(this,id);
            c.setTelefonos(lTelefonos);
        }

        //Recibe el intent con el nuevo contacto
        Intent i=this.getIntent();
        b=i.getExtras();
        /*if(b!=null){
            Contacto nuevo = (Contacto)b.getSerializable("contacto");
            lContactos.add(nuevo);

            Contacto otro = (Contacto)b.getSerializable("mismo");
            if(otro!=null){
                lContactos.add(otro);
            }

        }*/
        if(b!=null) {
            nuevo = (Contacto) b.getSerializable("contacto");
            lContactos.add(nuevo);
        }

        lv = (ListView)findViewById(R.id.lvMostrar);
        ad = new Adaptador(this,R.layout.elemento_lista, lContactos);
        lv.setAdapter(ad);
        registerForContextMenu(lv);

    }

    /******************** XML ********************************************************/

    public void escribir() throws IOException{
        FileOutputStream fosxml = new FileOutputStream(new File(getExternalFilesDir(null), "archivo.xml"));
        XmlSerializer docxml = Xml.newSerializer();
        docxml.setOutput(fosxml, "UTF-8");
        docxml.startDocument(null, Boolean.valueOf(true));
        docxml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

        docxml.startTag(null, "contactos");
        for (Contacto s : lContactos) {
            docxml.startTag(null, "contacto");
            docxml.startTag(null, "nombre");
            docxml.attribute(null, "id", ""+s.getId());
            docxml.text(s.getNombre());
            docxml.endTag(null, "nombre");
            for (int i=0;i<s.size();i++) {
                docxml.startTag(null, "telefono");
                docxml.text(s.getTelefono(i));
                docxml.endTag(null, "telefono");
            }
            docxml.endTag(null, "contacto");
        }
        docxml.endDocument();
        docxml.flush();
        fosxml.close();
    }

    public void incremental() throws IOException{
        FileOutputStream fosxml = new FileOutputStream(new File(getExternalFilesDir(null), "incremental.xml"));
        XmlSerializer docxml = Xml.newSerializer();
        docxml.setOutput(fosxml, "UTF-8");
        docxml.startDocument(null, Boolean.valueOf(true));
        docxml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        docxml.startTag(null, "contactos");
                docxml.startTag(null, "contacto");
                docxml.startTag(null, "nombre");
                docxml.attribute(null, "id", "" + nuevo.getId());
                docxml.text(nuevo.getNombre());
                docxml.endTag(null, "nombre");
                for (int i=0;i<nuevo.size();i++) {
                    docxml.startTag(null, "telefono");
                    docxml.text(nuevo.getTelefono(i));
                docxml.endTag(null, "telefono");
            }
            docxml.endTag(null, "contacto");

        docxml.endDocument();
        docxml.flush();
        fosxml.close();
    }

    /************ CONTACTOS TELF ***********************************************/

    public List<Contacto> getListaContactos(Context contexto){
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String proyeccion[] = null;
        String seleccion = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? and " +
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "= ?";
        String argumentos[] = new String[]{"1","1"};
        String orden = ContactsContract.Contacts.DISPLAY_NAME + " collate localized asc";
        Cursor cursor = contexto.getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        int indiceId = cursor.getColumnIndex(ContactsContract.Contacts._ID);
        int indiceNombre = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        List<Contacto> lista = new ArrayList<>();
        Contacto contacto;
        while(cursor.moveToNext()){
            contacto = new Contacto();
            contacto.setId(cursor.getLong(indiceId));
            contacto.setNombre(cursor.getString(indiceNombre));
            lista.add(contacto);
        }
        return lista;
    }

    public List<String> getListaTelefonos(Context contexto, long id){
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String proyeccion[] = null;
        String seleccion = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String argumentos[] = new String[]{id+""};
        String orden = ContactsContract.CommonDataKinds.Phone.NUMBER;
        Cursor cursor = contexto.getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        int indiceNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        List<String> lista = new ArrayList<>();
        String numero;
        while(cursor.moveToNext()){
            numero = cursor.getString(indiceNumero);
            lista.add(numero);
        }
        return lista;
    }
    /****************************************************************************/
}