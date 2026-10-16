package com.example.sysprodutop1.model;

import java.util.Objects;

public class Categoria {
    private final int id;
    private final String nome;
    private String descricao;
    private String setor;

    //CONSTRUTORES
    /*public Categoria() {
    }*/

    public Categoria(int id, String nome, String descricao, String setor) {
        if (id <= 0) throw new IllegalArgumentException("id deve ser > 0");
        this.id = id;
        this.nome = Objects.requireNonNull(nome, "nome");
        this.descricao = descricao;
        this.setor = setor;
    }
    //GET AND SETTERS
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }
    @Override
    public boolean equals(Object o){
        return (o instanceof Categoria c) && id == c.id;
    }
    @Override
    public int hashCode(){
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", setor='" + setor + '\'' +
                '}';
    }
}


