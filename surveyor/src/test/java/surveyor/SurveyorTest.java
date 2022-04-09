package surveyor;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.testfx.api.FxRobot;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import static org.assertj.core.api.Assertions.*;

/**
 *
 * @author "Pieter van den Hombergh {@code p.vandenhombergh@fontys.nl}"
 */
@ExtendWith( ApplicationExtension.class )
@TestMethodOrder( MethodOrderer.OrderAnnotation.class )
public class SurveyorTest {

    static {
        if ( Boolean.getBoolean( "SERVER" ) ) {
            System.setProperty( "java.awt.headless", "true" );
            System.setProperty( "testfx.robot", "glass" );
            System.setProperty( "testfx.headless", "true" );
            System.setProperty( "prism.order", "sw" );
            System.setProperty( "prism.text", "t2k" );
            System.setProperty( "glass.platform", "Monocle" );
            System.setProperty( "monocle.platform", "Headless" );
        }
    }

    SurveyorController controller;
    Stage stage;

    @Start
    void start( Stage stage ) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader( SurveyorApp.class.getResource(
                "surveyor.fxml" ) );
        Parent p = fxmlLoader.load();
        controller = fxmlLoader.getController();
//        controller.triangulator.setSnapStrategy( Triangulator.SnapStrategy.NONE );
        Scene scene = new Scene( p );
        stage.setScene( scene );
        stage.setTitle( "Sebi Surveyor App" );
        stage.getIcons().add( new Image( getClass().getResourceAsStream(
                "/icon.png" ) ) );
        stage.show();

    }
    //@Disabled("Think TDD")

    @ParameterizedTest
    @CsvSource( {
        "blueY, 500.0, blueCircle, greenLength, 480.0",
        "greenX,500.0, greenCircle,blueLength,480 "
    } )
    public void tSetCoordinates( String coordinate, double value, String circleN, String length, double expectedLength ) {
        FxRobot rob = new FxRobot();
        TextField tf = rob.lookup( "#" + coordinate ).query();
        rob.doubleClickOn( tf );
        rob.write( "500.0" );
        Circle circle = rob.lookup( "#" + circleN ).query();
//        assertThat( circle.getCenterY() ).isCloseTo( 500.0, within( 0.1 ) );
        Label gLength = rob.lookup( "#" + length ).query();
        assertThat( Double.parseDouble( gLength.getText() ) )
                .isCloseTo( expectedLength, within( 0.1 ) );
        rob.clickOn("#greenLineCheck");
        rob.clickOn( "#accept" );
        
        sleep( 2000 );
//        fail( "method method reached end. You know what to do." );
    }

    private void sleep( int i ) {
        try {
            Thread.sleep( i );
        } catch ( InterruptedException ex ) {
//            ex.printStackTrace();
        }
    }
}
