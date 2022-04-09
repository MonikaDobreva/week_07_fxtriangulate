package fxtriangulate;

import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 * SnapTarget as Label.
 * 
 * @author Pieter van den Hombergh / Richard van den Ham
 */
public class SnapLabel extends Label implements SnapTarget {

    public SnapLabel( String text ) {
        super( text );
    }

    public SnapLabel( String text, Node node ) {
        super( text, node );
    }

    @Override
    public String toString() {
        return "SnapLabel{" + getText() + " at(" + getLayoutX()
                + "," + getLayoutY()
                + ")" + '}';
    }

    @Override
    public SnapLabel focussed( boolean focus ) {
        
        if ( focus ) {
            getStyleClass().add( "hot" );
        } else {
            getStyleClass().remove( "hot" );
        }
        
        return this;
    }
}
