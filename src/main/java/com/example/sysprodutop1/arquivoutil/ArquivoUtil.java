package com.example.sysprodutop1.arquivoutil;

import com.example.sysprodutop1.model.Produto;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ArquivoUtil {
    private static final String NOME_ARQUIVO = "produtos_registrados";

    public static void salvar(List produtos) throws IOException{
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOME_ARQUIVO))){
            for (Object obj : produtos){
                Produto p = (Produto) obj;
                writer.write(p.getCodigo() + "," + p.getNome() + "," + p.getDescricao() + "," + p.getDataFabricacao() + "," + p.getDataValidade()
                        + "," + p.getPrecoCompra()+ "," + p.getPrecoVenda()+ "," +p.getQuantidadeEstoque()+ "," + p.getCategoria());
                writer.newLine();
            }
        }
    }
    public static List carregar() throws IOException{
        List produtos = new ArrayList();
        File arquivo = new File(NOME_ARQUIVO);
        if (!arquivo.exists()) return produtos;

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))){
            String linha;
            while ((linha = reader.readLine()) != null){
                String[] dados = linha.split(";");
                if (dados.length == 5) {
                    Produto p = new Produto(Integer.parseInt(dados[0]), dados[1],
                            Double.parseDouble(dados[2]), Double.parseDouble(dados[3]));
                    p.setCodigo(Double.parseDouble(dados[4]));
                    produtos.add(p);
            }
        }
    }
}
