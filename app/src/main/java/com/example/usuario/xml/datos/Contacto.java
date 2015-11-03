package com.example.usuario.xml.datos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Contacto implements Serializable, Comparable<Contacto>{

    private String Nombre;
    private long Id;
    private List<String> telefonos;


    public Contacto(String nombre, long id, List<String> telefonos) {
        Nombre = nombre;
        Id = id;
        this.telefonos = telefonos;
    }
    public Contacto(){
        this("",0,new ArrayList<String>());
    }

    public boolean addTelefono(String object) {
        return telefonos.add(object);
    }

    public String getTelefono(int location) {
        return telefonos.get(location);
    }

    public void setTelefono(int location, String tlfn){
        this.telefonos.set(location,tlfn);
    }

    public int size() {
        return telefonos.size();
    }

    public boolean isEmpty() {
        return telefonos.isEmpty();
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public List<String> getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(List<String> telefonos) {
        this.telefonos = telefonos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contacto contacto = (Contacto) o;

        return Id == contacto.Id;

    }

    @Override
    public int hashCode() {
        return (int) (Id ^ (Id >>> 32));
    }


    @Override
    public int compareTo(Contacto contacto) {
        int r = this.Nombre.compareTo(contacto.Nombre);
        if(r==0){
            r=(int)(this.Id-contacto.Id);
        }
        return r;
    }

    @Override
    public String toString() {
        return "Contacto{" +
                "Nombre='" + Nombre + '\'' +
                ", Id=" + Id +
                ", telefonos=" + telefonos +
                '}';
    }
}
