module fxtriangulate {
    requires javafx.controls;
    requires javafx.fxml;

    opens fxtriangulate to javafx.fxml;
    exports fxtriangulate;
}
