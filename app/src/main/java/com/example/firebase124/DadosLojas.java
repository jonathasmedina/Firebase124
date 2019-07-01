package com.example.firebase124;

public class DadosLojas {
    String box;
    String categoria;

    public DadosLojas() {
    }

    public String getBox() {
        return box;
    }

    public void setBox(String box) {
        this.box = box;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public DadosLojas(String box, String categoria) {
        this.box = box;
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "Box: " + box + ". Categoria: " + categoria;
    }
}
