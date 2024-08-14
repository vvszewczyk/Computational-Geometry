module org.cg.advancingfont {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.cg.advancingfont to javafx.fxml;
    exports org.cg.advancingfont;
}