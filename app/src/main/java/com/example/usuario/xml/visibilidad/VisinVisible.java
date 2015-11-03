package com.example.usuario.xml.visibilidad;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.usuario.xml.R;

/**
 * Created by USUARIO on 03/11/2015.
 */
public class VisinVisible implements View.OnClickListener{
    int position;
    TextView tv;
    Context contexto;
    ImageView imagen;

    public VisinVisible(int Position, TextView tv, Context Contexto, ImageView img){
        this.position=Position;
        this.tv=tv;
        this.contexto=Contexto;
        this.imagen=img;
    }

    @Override
    public void onClick(View v) {
        if(tv.getVisibility()==View.VISIBLE){
            tv.setVisibility(View.GONE);
            imagen.setImageResource(R.drawable.icon_more);
        }else{
            tv.setVisibility(View.VISIBLE);
            imagen.setImageResource(R.drawable.icon_less);
        }
    }
}


