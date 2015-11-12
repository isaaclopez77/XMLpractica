package com.example.usuario.xml;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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
    private List<Contacto> nuevos;

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
                añadirContacto();
                break;
            case R.id.mnCopiaS:
                try {
                    Log.v("COPIA","ha entrado");
                    escribir(lContactos);
                    Toast.makeText(this,"Copia de seguridad creada: XML generado",Toast.LENGTH_SHORT).show();
                }catch (IOException e){
                    Toast.makeText(this,"Cnooooooooo",Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.mnCopiaI:
                try {
                    incremental(nuevos);
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
        }else if(item.getItemId() == R.id.mnEditar){
            try {
                editar(info.position);
            } catch (IOException e) {

            }
        }
        ad.notifyDataSetChanged();
        return super.onContextItemSelected(item);
    }

    /***************** INIT() ********************************************/

    public void init() {
        //Inicialicamos el array que recogera los contactos que se inserten
        nuevos=new ArrayList<>();
        //Aquí se crea la lista de contactos con sus tlfns
        lContactos = getListaContactos(this);
        for(Contacto c : lContactos){
            long id = c.getId();
            lTelefonos = getListaTelefonos(this,id);
            c.setTelefonos(lTelefonos);
        }

        lv = (ListView)findViewById(R.id.lvMostrar);
        ad = new Adaptador(this,R.layout.elemento_lista, lContactos);
        lv.setAdapter(ad);
        registerForContextMenu(lv);

    }

    /******************** XML ********************************************************/

    public void escribir(List<Contacto> x) throws IOException{
        FileOutputStream fosxml = new FileOutputStream(new File(getExternalFilesDir(null), "archivo.xml"));
        XmlSerializer docxml = Xml.newSerializer();
        docxml.setOutput(fosxml, "UTF-8");
        docxml.startDocument(null, Boolean.valueOf(true));
        docxml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

        docxml.startTag(null, "contactos");

        for (Contacto s : x) {
            docxml.startTag(null, "contacto");
            docxml.startTag(null, "nombre");
            docxml.attribute(null, "id", "" + s.getId());
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

    public void incremental(List<Contacto> nuevos) throws IOException{
        FileOutputStream fosxml = new FileOutputStream(new File(getExternalFilesDir(null), "incremental.xml"));
        XmlSerializer docxml = Xml.newSerializer();
        docxml.setOutput(fosxml, "UTF-8");
        docxml.startDocument(null, Boolean.valueOf(true));
        docxml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

        docxml.startTag(null, "contactos");

        for (Contacto s : nuevos) {
            docxml.startTag(null, "contacto");
            docxml.startTag(null, "nombre");
            docxml.attribute(null, "id", "" + s.getId());
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
    public void añadirContacto(){

        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        alert.setTitle("Insertar");
        LayoutInflater inflater= LayoutInflater.from(this);

        final View vista = inflater.inflate(R.layout.dialogo_insertar_contacto, null);
        alert.setView(vista);
        alert.setPositiveButton("Insertar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        long id = lContactos.size() - 1;
                        EditText et1, et2;
                        et1 = (EditText) vista.findViewById(R.id.etInsertarNombre);
                        et2 = (EditText) vista.findViewById(R.id.etInsertarTelefono);

                        List<String> telf = new ArrayList<>();
                        telf.add(et2.getText().toString());

                        Contacto c = new Contacto(et1.getText().toString(),id,telf);
                        lContactos.add(c);
                        nuevos.add(c);
                        ad = new Adaptador(Principal.this, R.layout.elemento_lista, lContactos);
                        ad.notifyDataSetChanged();
                        lv.setAdapter(ad);
                    }
                });
        alert.setNegativeButton("cancelar", null);
        alert.show();

    }

    public void editar(final int posicion) throws IOException {
        Log.v("EDITAR","1");
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Editar contacto");
        LayoutInflater inflater = LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.dialogo_editar_contacto, null);
        final EditText etNome, etNume,etNume2,etNume3;
        String nom, num="",num2="Añade un nuevo número",num3="Añade un nuevo número";

        etNome=(EditText)vista.findViewById(R.id.etNome);
        etNume=(EditText)vista.findViewById(R.id.etNume);
        etNume2=(EditText)vista.findViewById(R.id.etNume2);
        etNume3=(EditText)vista.findViewById(R.id.etNume3);

        nom = lContactos.get(posicion).getNombre();
        if(lContactos.get(posicion).size()<2) {
            num = lContactos.get(posicion).getTelefono(0);
        }else {
            if(lContactos.get(posicion).size()<3){
                num = lContactos.get(posicion).getTelefono(0);
                num2 =lContactos.get(posicion).getTelefono(1);
            }else {
                num = lContactos.get(posicion).getTelefono(0);
                num2 =lContactos.get(posicion).getTelefono(1);
                num3 = lContactos.get(posicion).getTelefono(2);
            }
        }

        etNome.setText(nom);
        etNume.setText(num);
        etNume2.setHint(num2);
        etNume3.setHint(num3);

        alert.setView(vista);
        alert.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int idd = (int) lContactos.get(posicion).getId();
                        lContactos.remove(posicion);
                        EditText etnom, etnum;
                        ArrayList<String> telf = new ArrayList<String>();

                        if (!(etNume.getText().toString().equals(""))) {
                            telf.add(etNume.getText().toString());
                        }
                        if (!(etNume2.getText().toString().equals(""))) {
                            telf.add(etNume2.getText().toString());
                        }
                        if (!(etNume3.getText().toString().equals(""))) {
                            telf.add(etNume3.getText().toString());
                        }
                        Contacto c = new Contacto( etNome.getText().toString(),idd, telf);
                        lContactos.add(c);//Añadirmos el contacto a la lista
                        Log.v("INSERTO TLF", c.toString());
                        ad.notifyDataSetChanged();
                    }
                });

        alert.setView(vista);
        alert.setNegativeButton("Cancelar", null);
        alert.show();
    }
}