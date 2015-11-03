package com.example.usuario.xml;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.usuario.xml.datos.Contacto;
import com.example.usuario.xml.visibilidad.VisinVisible;

import java.util.List;

public class Adaptador extends ArrayAdapter<Contacto>{

    Context cx;
    private int res;
    private LayoutInflater lInflator;
    private List<Contacto> nombres;
    private ImageView img;

    public class ViewHolder{
        public TextView tvNom, tvTelf;
        public ImageView more;
    }

    public Adaptador(Context context, int resource, List<Contacto> nombres){
        super(context,resource,nombres);
        this.cx=context;
        this.res = resource;
        this.nombres=nombres;
        this.lInflator=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder gv = new ViewHolder();
        if(convertView==null){
            convertView=lInflator.inflate(res,null);
            TextView tv =(TextView)convertView.findViewById(R.id.tvNom);
            gv.tvNom=tv;
            TextView tvTelf=(TextView)convertView.findViewById(R.id.tvTelf);
            gv.tvTelf=tvTelf;
            ImageView img = (ImageView)convertView.findViewById(R.id.imgView);
            gv.more=img;
        }
        TextView tvN=(TextView)convertView.findViewById(R.id.tvNom);
        tvN.setText(nombres.get(position).getNombre());
        TextView tvT = (TextView) convertView.findViewById(R.id.tvTelf);
        tvT.setText(nombres.get(position).getTelefono(0));
        ImageView img = (ImageView)convertView.findViewById(R.id.imgView);

        if(nombres.get(position).size()>1){
            img.setVisibility(View.VISIBLE);
        }

        tvT=(TextView)convertView.findViewById(R.id.tvOculto);

        tvT.setText("");
        int cont=1;
        boolean esprimero=true;
        while(nombres.get(position).size()>cont){
            if(esprimero==true) {
                tvT.setText(""+nombres.get(position).getTelefono(cont));
                esprimero=false;
            }else{
                tvT.setText(tvT.getText().toString() + "\n" + nombres.get(position).getTelefono(cont));
            }
            cont++;
        }

        img.setOnClickListener(new VisinVisible(position,tvT,cx,img));

        return convertView;
    }
}
