module surveyor {
    
    requires fxtriangulate;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;

    opens surveyor to javafx.fxml;
    exports surveyor;
}
