package com.example.sysprodutop1.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Produto {
    private final String codigo;
    private String nome;
    private String descricao;
    private LocalDate dataFabricacao;
    private LocalDate dataValidade;
    private BigDecimal precoCompra;
    private BigDecimal precoVenda;
    private int quantidadeEstoque;
    private Categoria categoria;

    //CONSTRUTORES
    /*public Produto() {
    }*/
//UUID automatico
    public Produto(String nome, String descricao, LocalDate dataFabricacao, LocalDate dataValidade,
                   BigDecimal precoCompra, BigDecimal precoVenda, int quantidadeEstoque, Categoria categoria) {
    this (UUID.randomUUID().toString(), nome, descricao, dataFabricacao, dataValidade, precoCompra, precoVenda,
            quantidadeEstoque, categoria);
    }

//guarda o que ja recebeu
    public Produto(String codigo, String nome, String descricao, LocalDate dataFabricacao, LocalDate dataValidade,
                   BigDecimal precoCompra, BigDecimal precoVenda, int quantidadeEstoque, Categoria categoria){
        this.codigo            = Objects.requireNonNull(codigo, "codigo");
        this.nome              = Objects.requireNonNull(nome, "nome");
        this.descricao         = descricao;
        this.dataFabricacao    = dataFabricacao;
        this.dataValidade      = dataValidade;
        this.precoCompra       = precoCompra;
        this.precoVenda        = precoVenda;
        this.quantidadeEstoque = quantidadeEstoque;
        this.categoria         = Objects.requireNonNull(categoria, "categoria");
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataFabricacao() {
        return dataFabricacao;
    }

    public void setDataFabricacao(LocalDate dataFabricacao) {
        this.dataFabricacao = dataFabricacao;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public BigDecimal getPrecoCompra() {
        return precoCompra;
    }

    public void setPrecoCompra(BigDecimal precoCompra) {
        this.precoCompra = precoCompra;
    }

    public BigDecimal getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(BigDecimal precoVenda) {
        this.precoVenda = precoVenda;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = Objects.requireNonNull(categoria, "Categoria");
    }
    @Override
    public boolean equals(Object o) {
        return (o instanceof Produto p) && codigo.equals(p.codigo);
    }

    @Override
    public int hashCode() { return codigo.hashCode(); }

    @Override
    public String toString() {
        return "Produto{codigo='%s', nome='%s', categoria=%s}"
                .formatted(codigo, nome, categoria.getNome());
    }
}

