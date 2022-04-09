package fxtriangulate;

import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Just a demo, not part of the public interface.
 *
 * @author Pieter van den Hombergh {@code Pieter.van.den.Hombergh@gmail.com}
 */
public class App extends Application {

    /**
     * Start the java fx app.Does most of the work.
     *
     * @param primaryStage to show GUI
     */
    @Override
    public void start( Stage primaryStage ) {
        
        TriangulatorController triangulator = new TriangulatorController();
        AnchorPane root = new AnchorPane();
        root.getChildren().add( triangulator );
        AnchorPane.setBottomAnchor( triangulator, 20.0 );
        AnchorPane.setLeftAnchor( triangulator, 120.0 );
        AnchorPane.setRightAnchor( triangulator, 120.0 );

        Scene scene = new Scene( root, 800, 600 );

        triangulator.addPoints(
                60.0, 10.0,
                170.0, 10.0,
                550.0, 123.0,
                200.0, 120.0,
                70.0, 450.0
        );
        primaryStage.setTitle( "move the points to recalculate" );
        primaryStage.setScene( scene );
        primaryStage.show();
        primaryStage.getIcons().add( new Image( getClass().getResourceAsStream(
                "/icon.png" ) ) );

    }

//    Callback<Class<?>, Object> controllerFactory
//            = ( Class<?> c ) -> new TriangulatorController();

    /**
     * Get the sow running.
     *
     * @param args ignored
     */
    public static void main( String[] args ) {
        launch( args );
    }
}
