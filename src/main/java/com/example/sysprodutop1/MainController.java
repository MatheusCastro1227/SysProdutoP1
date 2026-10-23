package com.example.sysprodutop1;

import com.example.sysprodutop1.model.Categoria;
import com.example.sysprodutop1.model.Produto;
import com.example.sysprodutop1.service.produtoService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class MainController {

    // Campos do formulário
    @FXML private TextField tfCodigo, tfNome, tfDescricao;
    @FXML private DatePicker dpFabricacao, dpValidade;
    @FXML private TextField tfPrecoCompra, tfPrecoVenda, tfEstoque;
    @FXML private TextField tfCatId, tfCatNome, tfCatDescricao, tfCatSetor;

    // Tabela
    @FXML private TableView<Produto> tabela;
    @FXML private TableColumn<Produto, String> colCodigo, colNome, colCategoria, colSetor;
    @FXML private TableColumn<Produto, LocalDate> colFab, colVal;
    @FXML private TableColumn<Produto, BigDecimal> colCompra, colVenda;
    @FXML private TableColumn<Produto, Integer> colEstoque;

    private final produtoService service = new produtoService();
    private final ObservableList<Produto> dados = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colCategoria.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategoria().getNome()));
        colSetor.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategoria().getSetor()));
        colFab.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getDataFabricacao()));
        colVal.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getDataValidade()));
        colCompra.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getPrecoCompra()));
        colVenda.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getPrecoVenda()));
        colEstoque.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getQuantidadeEstoque()));

        tabela.setItems(dados);

        tabela.getSelectionModel().selectedItemProperty().addListener((o, a, sel) -> {
            if (sel != null) preencherForm(sel);
        });

        try {
            service.carregar();
            atualizarTabela(service.listarTodos());
        } catch (Exception e) {
            mostrarErro("Erro ao carregar arquivo", e.getMessage());
        }
    }

    // ---------- ações dos botões ----------

    @FXML private void onCadastrar() {
        try {
            service.cadastrar(lerForm());
            atualizarTabela(service.listarTodos());
            onLimpar();
        } catch (Exception e) {
            mostrarErro("Não foi possível cadastrar", e.getMessage());
        }
    }

    @FXML private void onExcluir() {
        Produto sel = tabela.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarErro("Excluir", "Selecione um produto na tabela."); return; }
        try {
            service.excluir(sel.getCodigo());
            atualizarTabela(service.listarTodos());
            onLimpar();
        } catch (Exception e) {
            mostrarErro("Erro ao excluir", e.getMessage());
        }
    }

    @FXML private void onBuscar() {
        String codigo = tfCodigo.getText().trim();
        String nome = tfNome.getText().trim();

        if (!codigo.isEmpty()){
            Produto p = service.buscarPorCodigo(codigo);
            if (p == null){
                mostrarErro("Buscar", "Nenhum produto com este código!");
                atualizarTabela(List.of());
            } else {
                preencherForm(p);
                atualizarTabela(List.of(p));
            }
            return;
        }
        if (!nome.isEmpty()){
            List<Produto> resultados = service.buscarPorNome(nome);
            if (resultados.isEmpty()){
                mostrarErro("Buscar", "Nenhum produto com este nome!");
            } else if (resultados.size() == 1){
                preencherForm(resultados.get(0));
            }
            atualizarTabela(resultados);
            return;
        }
        mostrarErro("Buscar", "Digite um codigo ou um nome no formulario para buscar o produto");
    }

    @FXML private void onLimpar() {
        tfCodigo.clear(); tfNome.clear(); tfDescricao.clear();
        dpFabricacao.setValue(null); dpValidade.setValue(null);
        tfPrecoCompra.clear(); tfPrecoVenda.clear(); tfEstoque.clear();
        tfCatId.clear(); tfCatNome.clear(); tfCatDescricao.clear(); tfCatSetor.clear();
        tabela.getSelectionModel().clearSelection();
    }

    @FXML private void onMostrarTodos() {
        atualizarTabela(service.listarTodos());
    }

    // ---------- relatórios ----------

    @FXML private void onRelVencimento() {
        atualizarTabela(service.proximosVencimento(60));
    }

    @FXML private void onRelEstoque() {
        atualizarTabela(service.estoqueBaixo(10));
    }

    @FXML private void onRelMargem() {
        Map<String, BigDecimal> r = service.margemLucroMediaPorCategoria();
        StringBuilder sb = new StringBuilder("Margem média por categoria:\n\n");
        r.forEach((k, v) -> sb.append(k).append(": R$ ").append(v).append('\n'));
        new Alert(AlertType.INFORMATION, sb.toString()).showAndWait();
    }

    @FXML private void onRelSetor() {
        Map<String, List<Produto>> r = service.produtosPorSetor();
        StringBuilder sb = new StringBuilder();
        r.forEach((setor, lista) -> {
            sb.append("▶ Setor: ").append(setor).append('\n');
            lista.forEach(p -> sb.append("   - ").append(p.getNome())
                    .append(" (est. ").append(p.getQuantidadeEstoque()).append(")\n"));
            sb.append('\n');
        });
        new Alert(AlertType.INFORMATION, sb.toString()).showAndWait();
    }

    // ---------- helpers ----------

    private Produto lerForm() {
        int catId = Integer.parseInt(tfCatId.getText().trim());
        Categoria cat = new Categoria(
                catId,
                tfCatNome.getText().trim(),
                emptyToNull(tfCatDescricao.getText()),
                emptyToNull(tfCatSetor.getText()));

        return new Produto(
                tfCodigo.getText().trim(),
                tfNome.getText().trim(),
                emptyToNull(tfDescricao.getText()),
                dpFabricacao.getValue(),
                dpValidade.getValue(),
                new BigDecimal(tfPrecoCompra.getText().trim()),
                new BigDecimal(tfPrecoVenda.getText().trim()),
                Integer.parseInt(tfEstoque.getText().trim()),
                cat);
    }

    private void preencherForm(Produto p) {
        tfCodigo.setText(p.getCodigo());
        tfNome.setText(p.getNome());
        tfDescricao.setText(p.getDescricao());
        dpFabricacao.setValue(p.getDataFabricacao());
        dpValidade.setValue(p.getDataValidade());
        tfPrecoCompra.setText(p.getPrecoCompra() == null ? "" : p.getPrecoCompra().toPlainString());
        tfPrecoVenda.setText(p.getPrecoVenda() == null ? "" : p.getPrecoVenda().toPlainString());
        tfEstoque.setText(String.valueOf(p.getQuantidadeEstoque()));
        Categoria c = p.getCategoria();
        tfCatId.setText(String.valueOf(c.getId()));
        tfCatNome.setText(c.getNome());
        tfCatDescricao.setText(c.getDescricao());
        tfCatSetor.setText(c.getSetor());
    }

    private void atualizarTabela(List<Produto> lista) {
        dados.setAll(lista);
    }

    private static String emptyToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private void mostrarErro(String titulo, String msg) {
        new Alert(AlertType.ERROR, titulo + "\n\n" + msg).showAndWait();
    }
}