package fxtriangulate;

import javafx.beans.binding.DoubleBinding;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import static org.assertj.core.api.Assertions.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Pieter van den Hombergh / Richard van den Ham
 */
public class BindingBusinessTest {

    /**
     * Assert that the center points land at the expected place.
     */
    @Test
    public void testCreateCircle() {
        Circle c = new Circle(10, 20, 10, Color.CORAL);

        double centerX = c.getCenterX();
        double centerY = c.getCenterY();
        assertThat(centerX).isCloseTo(10.0, within(0.1));
        assertThat(centerY).isCloseTo(20.0, within(0.1));
    }

    /**
     * Test connecting two circles by a straight line. Test that line starts at
     * first circle and ends at last.
     */
    @Test
    public void testConnect() {

        BindingBusiness bb = new BindingBusiness();

        Circle c1 = new Circle(10, 10, 10, Color.CORAL);
        Circle c2 = new Circle(10, 50, 10, Color.CORAL);

        Line line = new Line();
        bb.connect(line, c1, c2);

        double startX1 = line.getStartX();
        double startY1 = line.getStartY();
        double endX1 = line.getEndX();
        double endY1 = line.getEndY();

        assertThat(startX1).isCloseTo(10.0, within(0.1));
        assertThat(startY1).isCloseTo(10.0, within(0.1));
        assertThat(endX1).isCloseTo(10.0, within(0.1));
        assertThat(endY1).isCloseTo(50.0, within(0.1));
    }

    /**
     * assert that the midpoint of a line is where it is supposed to be.
     */
    @Test
    public void testMidPointBinding() {

        BindingBusiness bb = new BindingBusiness();

        Circle c1 = new Circle(10, 10, 10, Color.CORAL);
        Circle c2 = new Circle(10, 50, 10, Color.CORAL);
        Line line = new Line();

        bb.connect(line, c1, c2);

        DoubleBinding midpointXBinding
                = bb.midpointBinding(line.startXProperty(), line.
                        endXProperty());

        DoubleBinding midpointYBinding
                = bb.midpointBinding(line.startYProperty(), line.
                        endYProperty());

        double midX = midpointXBinding.get();
        double midY = midpointYBinding.get();

        assertThat(midX).isCloseTo(10.0, within(0.1));
        assertThat(midY).isCloseTo(30.0, within(0.1));
    }

    /**
     * assert that the area computation produces the correct result.
     */
    @Test
    public void testAreaBinding() {

        // Create a BindingBusiness object
        // Create three Circle objects of choice (the first two parameters are
        //   x and y value of center, 3rd parameter is radius, 4th parameter
        //   is Color) For this test, only the first two are relevant. 
        // Create three Line objects
        // Use setCornerCirclesAndConnectLines on the bindingBusiness
        // Ask the bindingBusiness for an areaBinding
        // Assert that the areaBinding value is equal to the expected area of 
        //   your triangle (depending on the chosen Circles). Use isCloseTo.
        //TODO 1 Test the areaBinding
        fail( "testAreaBinding not yet implemented. Review the code and comment or delete this line" );
    }

    /**
     * Assert that the length computation of the binding produces the correct
     * results.
     */
    @Test
    public void testLengthBinding() {

        String redLineID = "REDLINE";
        String greenLineID = "GREENLINE";
        String blueLineID = "BLUELINE";

        // Create a BindingBusiness object
        // Create three Circle objects of choice, named redCircle, greenCircle
        //   and blueCircle respectively.    
        // Create three Line objects and set their ID (setId) to redLineID,
        //   greenLineID and blueLineID respectively.
        // Use the connect method to:
        //   connect the red line to the blue and green Circle
        //   connect the green line to the blue and red Circle
        //   connect the blue line to the red and green Circle
        // Create three lengthBindings based on their id
        // Assert with a SoftAssertion that the value of the three length bindings
        //   is equal to their expected length (depending on your chosen Circles). 
        //   Use isCloseTo for your assertion.
        
        //TODO 3 Test lengthBinding( String lineName)
        fail( "testLengthBinding not yet implemented. Review the code and comment or delete this line" );
    }
}
