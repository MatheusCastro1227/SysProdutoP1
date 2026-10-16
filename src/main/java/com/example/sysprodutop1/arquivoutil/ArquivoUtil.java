package com.example.sysprodutop1.arquivoutil;

import com.example.sysprodutop1.model.Categoria;
import com.example.sysprodutop1.model.Produto;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ArquivoUtil {

    // Nome do arquivo — agora .csv
    private static final String NOME_ARQUIVO = "produtos_registrados.csv";
    private static final String SEP = ";";
    private static final int TOTAL_CAMPOS = 12;

    // Pasta onde o arquivo será criado: <projeto>/dados/
    private static final Path PASTA_DADOS = Paths.get("dados");
    private static final Path CAMINHO_ARQUIVO = PASTA_DADOS.resolve(NOME_ARQUIVO);

    public static Path getCaminhoArquivo() {
        return CAMINHO_ARQUIVO.toAbsolutePath();
    }

    public static void salvar(List<Produto> produtos) throws IOException {
        // Cria a pasta "dados" se ela não existir
        Files.createDirectories(PASTA_DADOS);

        try (BufferedWriter writer = Files.newBufferedWriter(CAMINHO_ARQUIVO)) {
            for (Produto p : produtos) {
                Categoria c = p.getCategoria();

                String linha = String.join(SEP,
                        nvl(p.getCodigo()),
                        nvl(p.getNome()),
                        nvl(p.getDescricao()),
                        p.getDataFabricacao() == null ? "" : p.getDataFabricacao().toString(),
                        p.getDataValidade()   == null ? "" : p.getDataValidade().toString(),
                        p.getPrecoCompra()    == null ? "" : p.getPrecoCompra().toPlainString(),
                        p.getPrecoVenda()     == null ? "" : p.getPrecoVenda().toPlainString(),
                        String.valueOf(p.getQuantidadeEstoque()),
                        String.valueOf(c.getId()),
                        nvl(c.getNome()),
                        nvl(c.getDescricao()),
                        nvl(c.getSetor())
                );

                writer.write(linha);
                writer.newLine();
            }
        }
    }

    public static List<Produto> carregar() throws IOException {
        List<Produto> produtos = new ArrayList<>();
        if (!Files.exists(CAMINHO_ARQUIVO)) return produtos;

        try (BufferedReader reader = Files.newBufferedReader(CAMINHO_ARQUIVO)) {
            String linha;
            int numeroLinha = 0;

            while ((linha = reader.readLine()) != null) {
                numeroLinha++;
                if (linha.isBlank()) continue;

                String[] d = linha.split(SEP, -1);

                if (d.length != TOTAL_CAMPOS) {
                    System.err.println("Linha " + numeroLinha + " ignorada (esperado "
                            + TOTAL_CAMPOS + " campos, veio " + d.length + ")");
                    continue;
                }

                try {
                    Categoria categoria = new Categoria(
                            Integer.parseInt(d[8]),
                            d[9],
                            vazioParaNull(d[10]),
                            vazioParaNull(d[11])
                    );

                    Produto p = new Produto(
                            d[0],
                            d[1],
                            vazioParaNull(d[2]),
                            vazioParaNull(d[3]) == null ? null : LocalDate.parse(d[3]),
                            vazioParaNull(d[4]) == null ? null : LocalDate.parse(d[4]),
                            vazioParaNull(d[5]) == null ? null : new BigDecimal(d[5]),
                            vazioParaNull(d[6]) == null ? null : new BigDecimal(d[6]),
                            Integer.parseInt(d[7]),
                            categoria
                    );

                    produtos.add(p);

                } catch (RuntimeException ex) {
                    System.err.println("Linha " + numeroLinha + " inválida: " + ex.getMessage());
                }
            }
        }
        return produtos;
    }

    private static String nvl(String s) { return s == null ? "" : s; }

    private static String vazioParaNull(String s) {
        return (s == null || s.isEmpty()) ? null : s;
    }
}