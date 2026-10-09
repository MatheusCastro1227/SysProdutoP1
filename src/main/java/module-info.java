module com.example.sysprodutop1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.sysprodutop1 to javafx.fxml;
    exports com.example.sysprodutop1;
    exports com.example.sysprodutop1.arquivoutil;
    opens com.example.sysprodutop1.arquivoutil to javafx.fxml;
}