package fxtriangulate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 *
 * @author Pieter van den Hombergh / Richard van den Ham
 */
class BindingBusiness {

    // Fields
    /**
     * Circles that represent corners of triangle.
     */
    private Circle redCircle, greenCircle, blueCircle;

    /**
     * Mapping between names of lines (typically fx:id from fxml) and the javaFX
     * Line object.
     */
    final Map<String, Line> lines = new HashMap<>();

    /**
     * Initial areaBinding. Will be set as soon as the areaBinding is created.
     */
    private DoubleBinding area = null;

    /**
     * Mapping between Line objects and their length binding. Avoids the
     * creation of (unnecessary) line length bindings at multiple places.
     */
    private final Map<Line, DoubleBinding> lengthBindings = new HashMap<>();

    /**
     * An active line is a line that is 'selected'. The use case is that you can
     * determine the sum of all active lines. If you'd like to calculate the
     * circumference of a triangle, typically all lines should be activated.
     * It's a ModifiableSumBinding, because if we deactivate a line, the binding
     * must be recalculated.
     */
    final ModifyableSumBinding lengthActiveLines = new ModifyableSumBinding();

    /**
     * Initialize the starting triangle. Makes the position of the lines
     * dependent on the position of the corner coordinates.
     *
     * @param red circle
     * @param green circle
     * @param blue circle
     * @param redLine
     * @param greenLine
     * @param blueLine
     */
    final void setCornerCirclesAndConnectLines(Circle red, Circle green, Circle blue, Line redLine, Line greenLine, Line blueLine) {

        this.redCircle = red;
        this.greenCircle = green;
        this.blueCircle = blue;

        connect(redLine, greenCircle, blueCircle);
        connect(greenLine, blueCircle, redCircle);
        connect(blueLine, redCircle, greenCircle);
    }

    /**
     * Connect two circles with a line. Sets the line start and line end
     * properties, based on the center coordinates of the passed circles.
     *
     * @param line to connectLabelToLine
     * @param c1 first circle
     * @param c2 second circle
     *
     */
    final void connect(Line line, Circle c1, Circle c2) {
        lines.put(line.getId(), line);

        line.startXProperty().bind(c1.centerXProperty());
        line.startYProperty().bind(c1.centerYProperty());

        line.endXProperty().bind(c2.centerXProperty());
        line.endYProperty().bind(c2.centerYProperty());
    }

    /**
     * Helper to get the center-y of a line.
     *
     * @param line the line
     * @return the center-y coordinate value as DoubleBinding
     */
    DoubleBinding lineCenterY(Line line) {
        DoubleBinding lineCenterYBinding = midpointBinding(line.
                startYProperty(), line.endYProperty());
        return lineCenterYBinding;
    }

    /**
     * Helper to get the center-x of a line.
     *
     * @param line the line
     * @return the center-x coordinate value as DoubleBinding
     */
    DoubleBinding lineCenterX(Line line) {
        DoubleBinding lineCenterXBinding = midpointBinding(line.
                startXProperty(), line.endXProperty());
        return lineCenterXBinding;
    }

    /**
     * Center a label vertically around the given binding. Binds the vertical
     * line center value to the Y-position of the label. Subtracts a bit to
     * avoid the label to be exactly on the line.
     *
     * @param label label to center
     * @param lineCenterY to bind label Y position to
     */
    Label centerLabelVertically(Label label, DoubleBinding lineCenterY) {
        label.layoutYProperty().bind(lineCenterY.subtract(label.
                heightProperty().divide(2)));
        return label;
    }

    /**
     * Center a label horizontally around the given binding. Binds the
     * horizontal line center value to the X-position of the label. Subtracts a
     * bit to avoid the label to be exactly on the line.
     *
     * @param label label to center
     * @param lineCenterX to bind
     */
    Label centerLabelHorizontally(Label label, DoubleBinding lineCenterX) {
        label.layoutXProperty().bind(lineCenterX.subtract(label.
                widthProperty().divide(2)));
        return label;
    }

    /**
     * Create a binding to compute the area with a triangle determined by side
     * lengths a, b and c. This method consults the cache (the field
     * areaBinding) and computes the value if necessary.
     *
     * @return the area value as DoubleBinding
     */
    final DoubleBinding areaBinding() {
        if (area == null) {
            area = areaBinding(lengthBinding(redCircle, greenCircle),
                    lengthBinding(greenCircle, blueCircle),
                    lengthBinding(blueCircle, redCircle));
        }
        return area;
    }

    /**
     * Create the area binding using herons formula. Have a look at Herons
     * formula at the website referenced here:
     *
     * @see https://en.wikipedia.org/wiki/Triangle
     *
     * @param lengthA length of side a of triangle
     * @param lengthB length of side b of triangle
     * @param lengthC length of side c of triangle
     *
     * @return the area value as DoubleBinding
     */
    final DoubleBinding areaBinding(DoubleBinding lengthA, DoubleBinding lengthB,
            DoubleBinding lengthC) {

        DoubleBinding areaBinding = new DoubleBinding() {
            {
                bind(lengthA, lengthB, lengthC);
            }

            @Override
            protected double computeValue() {
                //TODO 2 Implement method based on lengthA, lengthB and lengthC
                return 0;
            }
        };

        return areaBinding;
    }

    /**
     * Return the binding that represents the length of the currently active
     * lines. The binding itself is final, but its dependencies can of course
     * change over time.
     *
     * @return the lengthActiveLines binding.
     */
    DoubleBinding activeLinesLength() {
        return lengthActiveLines;
    }

    /**
     * Active or Deactivate a line, so its length is taken into account in the
     * lengthActiveLines binding, or it is not taken into account anymore.
     *
     * @param activate is true if line must be activated, is false if it must be
     * deactivated.
     * @param lineName
     */
    void activateLine(boolean activate, String lineName) {

        Line l = lines.get(lineName);

        if (activate) {
            lengthActiveLines.add(lengthBinding(l));
            l.setStrokeWidth(6.0);
        } else {
            lengthActiveLines.remove(lengthBinding(l));
            l.setStrokeWidth(3.0);
        }
    }

    /**
     * Create a binding for the length of a line. The method does a lookup of an
     * existing length binding in the lengthBindings map (cache). If there is no
     * binding found, it is created and added to the cache. The creation is
     * delegated to the method lengthBinding(startX, startY, endX, endY)
     *
     * @param line
     *
     * @return a double binding that computes the line length.
     */
    DoubleBinding lengthBinding(Line line) {
        return lengthBindings
                .computeIfAbsent(line,
                        l -> lengthBinding(
                                l.startXProperty(),
                                l.startYProperty(),
                                l.endXProperty(),
                                l.endYProperty()
                        )
                );
    }

    /**
     * Convenience method that enables to get the lengthBinding by name. The
     * call is delegated to the method lenghBinding( Line line).
     *
     * @param lineName
     * @return
     */
    DoubleBinding lengthBinding(String lineName) {
        return lengthBinding(lines.get(lineName));
    }

    /**
     * Create a lenthBinding that computes the distance between the two
     * endpoints of a line segment using Pythagoras formula. This method is the
     * actual work horse of all lengthBinding method overloads.
     *
     * @param startX being x coordinate of start
     * @param startY being y coordinate of start
     * @param endX being x coordinate of end
     * @param endY being y coordinate of end
     *
     * @return the binding
     */
    DoubleBinding lengthBinding(
            DoubleProperty startX,
            DoubleProperty startY,
            DoubleProperty endX,
            DoubleProperty endY
    ) {
        // Create a new DoubleBinding like in the areaBinding method
        // In the computeValue-method, calculate the length of a line
        // based on its end points using Pythagoras.

        //TODO 4 Create and return the binding
        return Bindings.createDoubleBinding(() -> 0d); // Return your binding instead!
    }

    /**
     * Convenience method to create a lengthBinding between two circles. The
     * actual work is delegated to the method lengthBinding(startX, startY,
     * endX, endY). The center properties of the circles are simply passed as
     * parameter.
     *
     * @param a circle that represents start coordinate
     * @param b circle that represents end coordinate
     * @return DoubleBinding representing the length between both coordinates.
     */
    DoubleBinding lengthBinding(Circle a, Circle b) {
        return lengthBinding(a.centerXProperty(), b.centerXProperty(), a
                .centerYProperty(), b.centerYProperty());
    }

    /**
     * Creates binding that computes the average of the given properties. This
     * method can be used to find the midPoint of a line, by invoking it with
     * the x-values of start and end, followed by invoking it with the y-values
     * of start and end. The two resulting DoubleBindings determine the
     * coordinate that represents the middle of the line.
     *
     * The method can also be used to find the center of gravity of a triangle,
     * since that's simply represented by the average of the x-values of the
     * corner coordinates and the average of the y-values of the corner
     * coordinates.
     *
     * @param doubleProperties to average
     *
     * @return the average value
     */
    DoubleBinding midpointBinding(final DoubleProperty... doubleProperties) {

        DoubleBinding midPointBinding;

        midPointBinding = new DoubleBinding() {

            {
                bind(doubleProperties);
            }

            @Override
            protected double computeValue() {
                return Arrays.stream(doubleProperties).mapToDouble(
                        DoubleProperty::doubleValue).average().getAsDouble();
            }

        };

        return midPointBinding;
    }

    /**
     * Sets up the label belonging to a line. Makes the position of the label
     * dependent on the position of the line and makes the content of the label
     * displaying the lenth of the line.
     *
     * @param line
     * @param label
     */
    void connectLabelToLine(Line line, Label label) {

        centerLabelHorizontally(label, lineCenterX(line));
        centerLabelVertically(label, lineCenterY(line));

        label.textProperty().bind(lengthBinding(line).asString(label
                .getText() + ":%6.2f"));
    }

    /**
     * Sets up the label belonging to a corner coordinate (represented by
     * Circle).
     *
     * @param cogCircle representing the Center Of Gravity (COG)
     * @param cogLabel label to show the area of the triangle at the cog-circle.
     */
    void connectLabelToCenterOfGravityCircle(Circle cogCircle, Label cogLabel) {

        DoubleBinding cogXBinding
                = midpointBinding(
                        redCircle.centerXProperty(),
                        greenCircle.centerXProperty(),
                        blueCircle.centerXProperty()
                );
        DoubleBinding cogYBinding
                = midpointBinding(
                        redCircle.centerYProperty(),
                        greenCircle.centerYProperty(), blueCircle
                        .centerYProperty()
                );
        
        cogCircle.centerXProperty().bind(cogXBinding);
        cogCircle.centerYProperty().bind(cogYBinding.add(10.0));
        
        centerLabelHorizontally(cogLabel, cogXBinding);
        centerLabelVertically(cogLabel, cogYBinding);

        cogLabel.textProperty()
                .bind(areaBinding().asString("Area: %6.2f"));
    }
}
