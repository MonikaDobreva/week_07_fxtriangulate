package fxtriangulate;

/**
 * Snap to a target, to make a UI more usable, if small position differences
 * matter. When the mouse drags a corner of the triangle near to this SnapTarget,
 * the corner will be set to this SnapTarget location.
 *
 * @author Pieter van den Hombergh / Richard van den Ham
 */
public interface SnapTarget {

    /**
     * What range is considered nearby?.
     * @return rangeRadius
     */
    default double rangeRadius() {
        return 50.0;
    }

    /**
     * Checks if a mouse coordinate is in the range of this SnapTarget. 
     * @param x x-value of mouse coordinate
     * @param y y-value of mouse coordinate
     * @return  true if the mouse is in the range of this SnapTarget.
     */
    default boolean inRange( double x, double y ) {
        return distanceTo( x, y ) < rangeRadius();
    }

    /**
     * The Pythagorean distance between this and the given coordinate. 
     * @param x to measure
     * @param y to measure
     * @return the distance
     */
    default double distanceTo( double x, double y ) {
        double xd = x - getLayoutX();
        double yd = y - getLayoutY();
        return Math.sqrt( xd * xd + yd * yd );
    }

    /**
     * Change the node to appear different, so the UI can show it is targeted.
     * 
     * @param setFocus determines if this snapTarget should get focus or not
     * in the GUI. If invoked with false, the snapTarget will loose focus.
     * 
     * @return return this
     */
    SnapTarget focussed( boolean setFocus );

    /**
     * Return the x-value of the SnapTarget coordinate.
     * @return the x-value
     */
    double getLayoutX();

    /**
     * Return the y-value of the SnapTarget coordinate.
     * @return the y-value
     */
    double getLayoutY();
}
