module mainapp.lab01 {
    requires javafx.controls;
    requires javafx.fxml;


    opens mainapp.lab01 to javafx.fxml;
    exports mainapp.lab01;
}