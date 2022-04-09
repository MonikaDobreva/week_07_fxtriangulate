package fxtriangulate;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import static org.assertj.core.api.Assertions.*;
import org.assertj.core.api.SoftAssertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 *
 * @author Pieter van den Hombergh
 */
@ExtendWith( ApplicationExtension.class )
@TestMethodOrder( MethodOrderer.OrderAnnotation.class )
public class GUITest {

    /**
     * Boilerplate code to make the tests run in headless mode too.
     */
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

    /**
     * The test instance.
     */
    TriangulatorController triangulator;
    Stage stage;

    /**
     * Start called by test, to prepare stage and scene.
     *
     * @param stage to use
     *
     * @throws IOException
     */
    @Start
    void start( Stage stage ) {
    
        triangulator = new TriangulatorController();
        triangulator.setSnapStrategy(TriangulatorController.SnapStrategy.NONE);
        this.stage = stage;
        stage.setScene( new Scene( triangulator, 600, 600 ) );
        triangulator.addPoints(
                60.0, 10.0,
                170.0, 10.0,
                550.0, 123.0,
                200.0, 120.0,
                70.0, 450.0
        );

        stage.show();
    }

    /**
     * Test the area computation by moving the three circles to specific points.
     * The points are at 0.0, 300,0 and 0, 400 from a common offset of 100,100.
     */
    @Test
    public void tArea() {
        
        FxRobot rob = new FxRobot();
        
        double xOrg = stage.getX();
        double yOrg = stage.getY();
        
        rob.drag( "#redCircle" ).dropTo( xOrg + 100.0, yOrg + 100.0 );
        rob.drag( "#greenCircle" ).dropTo( xOrg + 100.0 + 300.0, yOrg + 100.0 );
        rob.drag( "#blueCircle" ).dropTo( xOrg + 100.0, yOrg + 100.0 + 400.0 );
        
        double area = triangulator.areaBinding().get();
        
        assertThat( area ).isCloseTo( 60000.0D, within( 0.1 ) );
    }

    /**
     * Translate name to label.
     */
    Map<String, Function<TriangulatorController, Label>> labelMap = Map.of(
            "a", t -> t.redLabel,
            "b", t -> t.greenLabel,
            "c", t -> t.blueLabel
    );

    /**
     * Read the text off the labels on the lines.
     *
     * @param line     to inspect
     * @param p1       circle 1
     * @param p2       circle 2
     * @param expected value
     * @param x1       to move to
     * @param y1       to move to
     * @param x2       to move to
     * @param y2       to move to
     * @throws java.text.ParseException
     */
    @ParameterizedTest
    @CsvSource( {
        "a, greenCircle, blueCircle, 500.0, 400.0, 100.0, 100.0, 500.0",
        "b, redCircle  , blueCircle, 400.0, 100.0, 100.0, 100.0, 500.0",
        "c, greenCircle, redCircle , 300.0, 100.0, 100.0, 400.0, 100.0", } )
    public void tLength( String line, String p1, String p2, double expected,
            double x1, double y1, double x2, double y2 ) throws ParseException {
       
        double xOrg = stage.getX();
        double yOrg = stage.getY();
        
        FxRobot rob = new FxRobot();
        
        rob.drag( '#' + p1 ).dropTo( xOrg + x1, yOrg + y1 );
        rob.drag( '#' + p2 ).dropTo( xOrg + x2, yOrg + y2 );
        
        String ltext = labelMap.get( line ).apply( triangulator ).getText();
        
        double l = getDoubleConsideringLocale( ltext.split( ":" )[ 1 ] );
        
        assertThat( l ).isCloseTo( expected, within( 0.1 ) );
    }

    /**
     * Use the default locale to parse a double value from a string.
     * @param input string
     * @return the double
     * @throws ParseException if the string does not parse to double. 
     */
    static double getDoubleConsideringLocale( String input ) throws ParseException {
        return DecimalFormat.getNumberInstance().parse( input ).doubleValue();
    }

    /**
     * Use the given locale to parse a double value from a string.
     * @param locale to use.
     * @param input string.
     * @return the double.
     * @throws ParseException if the string does not parse to double. 
     */
    static double getDoubleConsideringLocale( Locale locale, String input ) throws ParseException {
        return DecimalFormat.getNumberInstance(locale).parse( input ).doubleValue();
    }

    /**
     * Test points of a polygon added to the triangulator.
     */
    @Test
    public void tAddPoints() {
        assertThat( triangulator.getTargets() ).hasSize( 5 );
    }

    /**
     * Test that lines can be added or removed from the active set.
     *
     * @param lines
     * @param expected
     */
    @ParameterizedTest
    @CsvSource( {
        "redLine,500.0",
        "redLine|blueLine,800.0",
        "redLine|greenLine|blueLine,1200.0", } )
    void tActiveLineLength( String lines, double expected ) {
        
        double xOrg = stage.getX();
        double yOrg = stage.getY();
        
        FxRobot rob = new FxRobot();
        
        rob.drag( "#redCircle" ).dropTo( xOrg + 100.0, yOrg + 100.0 );
        rob.drag( "#greenCircle" ).dropTo( xOrg + 400.0, yOrg + 100.0 );
        rob.drag( "#blueCircle" ).dropTo( xOrg + 100.0, yOrg + 500.0 );
        
        final String[] split = lines.split( "\\|" );
        
        if ( !lines.isEmpty() ) {
            for ( String line : split ) {
                triangulator.activateLine( true, line );
            }
        }

        double startValue = triangulator.lengthActiveLines().get();
        
        SoftAssertions.assertSoftly( softly -> {
        
            softly.assertThat( startValue ).isCloseTo( expected, within( 0.1 ) );

            rob.drag( "#blueCircle" ).dropTo( xOrg + 120.0, yOrg + 120.0 );
            
            softly.assertThat( triangulator.lengthActiveLines().get() )
                    .isNotCloseTo( startValue, within( 10.0 ) );
            
            for ( String line : split ) {
                triangulator.activateLine( false, line );
            }
            
            softly.assertThat( triangulator.lengthActiveLines().get() )
                    .isCloseTo( 0.0, within( 0.1 ) );
            
        } );
    }
}
