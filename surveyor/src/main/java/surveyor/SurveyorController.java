package surveyor;

import fxtriangulate.TriangulatorController;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.converter.NumberStringConverter;

/**
 * FXML Controller class
 *
 * @author Pieter van den Hombergh {@code Pieter.van.den.Hombergh@gmail.com}
 */
public class SurveyorController implements Initializable {

    @FXML
    AnchorPane root;
    @FXML
    Button accept;
    @FXML
    TextField redX;
    @FXML
    TextField redY;
    @FXML
    TextField greenX;
    @FXML
    TextField greenY;
    @FXML
    TextField blueX;
    @FXML
    TextField blueY;
    @FXML
    TriangulatorController triangulator;
    @FXML
    Label redLength;
    @FXML
    Label area;
    @FXML
    Label greenLength;
    @FXML
    Label blueLength;
    @FXML
    CheckBox redLineCheck;
    @FXML
    Label redLabel;
    @FXML
    Label greenLabel;
    @FXML
    CheckBox greenLineCheck;
    @FXML
    Label blueLabel;
    @FXML
    CheckBox blueLineCheck;
    @FXML
    Label sum;
    @FXML
    TableView<Measurement> tv;
    @FXML
    Label totalAera;
    @FXML
    Label borderLength;
    @FXML
    RadioMenuItem snapNone;
    @FXML
    RadioMenuItem snapNearest;
    @FXML
    RadioMenuItem snapNearby;
    @FXML
    ToggleGroup snapMode;
    @FXML
    MenuItem clear;
    @FXML
    MenuItem unpin;

    DoubleProperty redXProp = new SimpleDoubleProperty();
    DoubleProperty redYProp = new SimpleDoubleProperty();
    DoubleProperty greenXProp = new SimpleDoubleProperty();
    DoubleProperty greenYProp = new SimpleDoubleProperty();
    DoubleProperty blueXProp = new SimpleDoubleProperty();
    DoubleProperty blueYProp = new SimpleDoubleProperty();

    NumberStringConverter converter = new NumberStringConverter();
    MenuItem openFile;
    final SurveyorBusiness business;

    public SurveyorController() {
        this(new SurveyorBusiness());
    }

    SurveyorController(SurveyorBusiness business) {
        this.business = business;
    }

    /**
     * Initializes the controller class.
     *
     * @param url ignored
     * @param rb ignored
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        triangulator.addPoints(
                60.0, 10.0,
                170.0, 10.0,
                550.0, 123.0,
                200.0, 120.0,
                350.0, 160.0,
                70.0, 450.0
        );

        redX.textProperty().bindBidirectional(triangulator.redXProperty(), converter);
        redY.textProperty().bindBidirectional(triangulator.redYProperty(), converter);

        greenX.textProperty().bindBidirectional(triangulator.greenXProperty(), converter);
        greenY.textProperty().bindBidirectional(triangulator.greenYProperty(), converter);

        blueX.textProperty().bindBidirectional(triangulator.blueXProperty(), converter);
        blueY.textProperty().bindBidirectional(triangulator.blueYProperty(), converter);

        area.textProperty().bind(triangulator.areaBinding().asString("%6.2f"));

        redLength.textProperty().bind(triangulator
                .lengthBinding("redLine").asString("%6.2f"));

        greenLength.textProperty().bind(triangulator
                .lengthBinding("greenLine").asString("%6.2f"));

        blueLength.textProperty().bind(triangulator.lengthBinding("blueLine")
                .asString("%6.2f"));

        sum.textProperty().bind(triangulator.lengthActiveLines().asString(
                "%6.2f"));

        totalAera.textProperty().bind(business.totalAreaBinding().asString("%6.2f"));

        borderLength.textProperty().bind(business.borderLengthBinding().asString("%6.2f"));

        defineTable();

        tv.setItems(business.measurements);

        triangulator.setSnapStrategy(TriangulatorController.SnapStrategy.SNAP_TO_NEAREST);
    }

    @FXML
    void accept(ActionEvent event) {

        double areaR = triangulator.areaBinding().get();
        double length = triangulator.lengthActiveLines().get();

        business.accept(
                Measurement.builder()
                        .area(areaR)
                        .length(length)
                        .build()
        );
    }

    public void addPoints(Double... points) {
        triangulator.addPoints(points);
    }

    void defineTable() throws SecurityException {
        
        Field[] declaredFields = Measurement.class.getDeclaredFields();

        int colCount = declaredFields.length;
        for (Field declaredField : declaredFields) {
            String clsName = declaredField.getName();
            TableColumn<Measurement, Object> tc = new TableColumn<>(clsName);
            tc.setCellValueFactory(new PropertyValueFactory<>(clsName));
            tc.prefWidthProperty().bind(tv.widthProperty().divide(colCount));
            tv.getColumns().add(tc);
        }
    }

    @FXML
    void checkLines(ActionEvent event) {
        
        System.out.println("checked event = " + event);
        
        CheckBox source = (CheckBox) event.getSource();
        boolean selected = source.isSelected();

        if (source == redLineCheck) {
            checkLine(selected, redLabel, redLength, "redLine");
        } else if (source == greenLineCheck) {
            checkLine(selected, greenLabel, greenLength, "greenLine");
        } else if (source == blueLineCheck) {
            checkLine(selected, blueLabel, blueLength, "blueLine");
        }
    }

    void checkLine(boolean selected, Label label, Label length, String lineName) {

        System.out.println("label = " + label);
        System.out.println("length = " + length);
        System.out.println("checkLine = " + lineName);

        if (selected) {
            label.getStyleClass().add("selected");
            length.getStyleClass().add("selected");
        } else {
            label.getStyleClass().remove("selected");
            length.getStyleClass().remove("selected");
        }
        
        triangulator.activateLine(selected, lineName);
    }

    @FXML
    void snapNone(ActionEvent event) {
        RadioMenuItem source = (RadioMenuItem) event.getSource();
        if (source.isSelected()) {
            triangulator.setSnapStrategy(TriangulatorController.SnapStrategy.NONE);
        }
    }

    @FXML
    void snapNearest(ActionEvent event) {
        RadioMenuItem source = (RadioMenuItem) event.getSource();
        if (source.isSelected()) {
            triangulator.setSnapStrategy(TriangulatorController.SnapStrategy.SNAP_TO_NEAREST);
        }
    }

    @FXML
    void snapNearby(ActionEvent event) {
        RadioMenuItem source = (RadioMenuItem) event.getSource();
        if (source.isSelected()) {
            triangulator
                    .setSnapStrategy(TriangulatorController.SnapStrategy.SNAP_NEARBY);
        }
    }

    @FXML
    void clear(ActionEvent event) {
        this.business.clear();
    }

    @FXML
    void unpin(ActionEvent event) {
    }

}
