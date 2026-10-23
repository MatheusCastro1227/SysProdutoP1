package com.example.sysprodutop1.service;

import com.example.sysprodutop1.arquivoutil.ArquivoUtil;
import com.example.sysprodutop1.model.Produto;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class produtoService {

    private final List<Produto> produtos = new ArrayList<>();

    public void carregar() throws IOException {
        produtos.clear();
        produtos.addAll(ArquivoUtil.carregar());
    }

    public void salvar() throws IOException {
        ArquivoUtil.salvar(produtos);
    }

    // ---------- CRUD ----------

    public void cadastrar(Produto p) throws IOException {
        validar(p);
        if (buscarPorCodigo(p.getCodigo()) != null) {
            throw new IllegalArgumentException("Já existe produto com o código " + p.getCodigo());
        }
        produtos.add(p);
        salvar();
    }

    public boolean excluir(String codigo) throws IOException {
        boolean removido = produtos.removeIf(p -> p.getCodigo().equals(codigo));
        if (removido) salvar();
        return removido;
    }

    public Produto buscarPorCodigo(String codigo) {
        return produtos.stream()
                .filter(p -> p.getCodigo().equals(codigo))
                .findFirst()
                .orElse(null);
    }
    public List<Produto> buscarPorNome(String termo){
        if (termo == null || termo.isBlank()){
            return List.of();
        }
        String busca = termo.toLowerCase();
        return produtos.stream()
                .filter(p -> p.getNome().toLowerCase().contains(busca))
                .collect(Collectors.toList());
    }

    public List<Produto> listarTodos() {
        return List.copyOf(produtos);
    }

    // ---------- VALIDAÇÃO ----------

    private void validar(Produto p) {
        String cod = p.getCodigo();
        if (cod == null || !cod.matches("[A-Za-z0-9]{8,}")) {
            throw new IllegalArgumentException(
                    "Código deve ter ao menos 8 caracteres alfanuméricos.");
        }
        if (p.getNome() == null || p.getNome().trim().length() < 3) {
            throw new IllegalArgumentException("Nome deve ter ao menos 3 caracteres.");
        }
        if (p.getDataFabricacao() != null && p.getDataFabricacao().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de fabricação não pode ser futura.");
        }
        if (p.getDataFabricacao() != null && p.getDataValidade() != null
                && p.getDataValidade().isBefore(p.getDataFabricacao())) {
            throw new IllegalArgumentException("Validade não pode ser anterior à fabricação.");
        }
        if (p.getPrecoCompra() == null || p.getPrecoCompra().signum() <= 0) {
            throw new IllegalArgumentException("Preço de compra deve ser positivo.");
        }
        if (p.getPrecoVenda() == null || p.getPrecoVenda().signum() <= 0) {
            throw new IllegalArgumentException("Preço de venda deve ser positivo.");
        }
        if (p.getPrecoVenda().compareTo(p.getPrecoCompra()) <= 0) {
            throw new IllegalArgumentException("Preço de venda deve ser maior que o de compra.");
        }
        if (p.getQuantidadeEstoque() < 0) {
            throw new IllegalArgumentException("Estoque não pode ser negativo.");
        }
    }

    // ---------- RELATÓRIOS (Stream API) ----------

    /** Vencimento nos próximos N dias (a partir de hoje). */
    public List<Produto> proximosVencimento(int dias) {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(dias);
        return produtos.stream()
                .filter(p -> p.getDataValidade() != null)
                .filter(p -> !p.getDataValidade().isBefore(hoje)
                        && !p.getDataValidade().isAfter(limite))
                .sorted(Comparator.comparing(Produto::getDataValidade))
                .collect(Collectors.toList());
    }

    /** Estoque abaixo do limite. */
    public List<Produto> estoqueBaixo(int limite) {
        return produtos.stream()
                .filter(p -> p.getQuantidadeEstoque() < limite)
                .sorted(Comparator.comparingInt(Produto::getQuantidadeEstoque))
                .collect(Collectors.toList());
    }

    /** Margem de lucro MÉDIA (venda − compra) por categoria. */
    public Map<String, BigDecimal> margemLucroMediaPorCategoria() {
        return produtos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCategoria().getNome(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                lista -> lista.stream()
                                        .map(p -> p.getPrecoVenda().subtract(p.getPrecoCompra()))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                                        .divide(BigDecimal.valueOf(lista.size()),
                                                2, RoundingMode.HALF_UP)
                        )
                ));
    }

    /** Produtos agrupados por setor da categoria. */
    public Map<String, List<Produto>> produtosPorSetor() {
        return produtos.stream()
                .collect(Collectors.groupingBy(
                        p -> {
                            String s = p.getCategoria().getSetor();
                            return (s == null || s.isBlank()) ? "(Sem setor)" : s;
                        }
                ));
    }
}