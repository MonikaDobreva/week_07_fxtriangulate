package surveyor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.image.Image;

/**
 * JavaFX App.
 */
public class SurveyorApp extends Application {

    private static Scene scene;

    @Override
    public void start( Stage stage ) throws IOException {
        scene = new Scene( loadFXML( "surveyor" ) );
        stage.setScene( scene );
        stage.setTitle( "Sebi Surveyor App" );
        stage.getIcons().add( new Image( getClass().getResourceAsStream(
                "/icon.png" ) ) );
        stage.show();
    }

    private static Parent loadFXML( String fxml ) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader( SurveyorApp.class.getResource( 
                fxml + ".fxml" ) );
        return fxmlLoader.load();
    }

    public static void main( String[] args ) {
        launch();
    }

}
