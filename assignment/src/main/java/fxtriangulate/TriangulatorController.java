package fxtriangulate;

import static javafx.scene.paint.Color.GRAY;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 * FXML Controller class
 *
 * @author Pieter van den Hombergh / Richard van den Ham
 */
public class TriangulatorController extends Group implements Initializable {

    @FXML
    Circle redCircle, blueCircle, greenCircle;

    @FXML
    Circle centerCircle;

    @FXML
    Line redLine, greenLine, blueLine;

    @FXML
    Label redLabel, greenLabel, blueLabel;

    @FXML
    Label areaLabel;

    /**
     * The 'business logic' for calculations. 
     */
    final BindingBusiness bb;

    /**
     * Polygon that can be measured. 
     */
    Polygon polygon = new Polygon();

    /**
     * Array of SnapLables for all corner coordinates of the polygon.
     */
    SnapLabel[] pointLabels;

    /**
     * CenterOfGravity polygon, x and y value as DoubleBinding.
     */
    DoubleBinding polCogX, polCogY;
    
    /**
     * X and Y position of last mouse click.
     */
    double originalMouseXPos, originalMouseYPosY;
    
    /**
     * List of SnapTargets.
     */
    private List<SnapTarget> targets = List.of();
    
    /**
     * Comparator to find out which of the SnapTargets is nearest.
     */
    Comparator<SnapTarget> nearestSnapTarget = ( a, b ) -> Double.compare(a.distanceTo(originalMouseXPos, originalMouseYPosY ), b.distanceTo(originalMouseXPos,
            originalMouseYPosY ) );
    
    /**
     * SnapStrategies.
     */
    public enum SnapStrategy {
        NONE,
        SNAP_NEARBY,
        SNAP_TO_NEAREST;
    }

    /**
     * Map SnapStrategy to Consumer<MouseEvent>.
     */
    final Map<SnapStrategy, Consumer<MouseEvent>> strats = Map.of(
            SnapStrategy.NONE, ( e ) -> {},
            SnapStrategy.SNAP_NEARBY, this::snapNearBy,
            SnapStrategy.SNAP_TO_NEAREST, this::snapToNearest
    );

    /**
     * Default snapStrategy.
     */
    //private Consumer<MouseEvent> snapStrategy = ( e ) -> {}; 
    private Consumer<MouseEvent> snapStrategy = this::snapToNearest;

    /**
     * Constructor that gets business logic injected.
     * @param bb 
     */
    TriangulatorController( BindingBusiness bb ) {
        this.bb = bb;
        FXMLLoader fxmlLoader = new FXMLLoader( getClass().getResource(
                "triangulator.fxml" ) );
        connectLoader( fxmlLoader );

        try {
            fxmlLoader.load();
        } catch ( IOException exception ) {
            throw new RuntimeException( exception );
        }
    }

    final void connectLoader( FXMLLoader fxmlLoader ) {
        fxmlLoader.setRoot( this );
        fxmlLoader.setController( this );
    }

    public TriangulatorController() {
        this( new BindingBusiness() );
    }

    /**
     * Initializes the controller class.
     *
     * @param url for resource
     * @param rb  internationalization
     */
    @Override
    public void initialize( URL url, ResourceBundle rb ) {
        
        setLineStyle( redLine );
        setLineStyle( greenLine );
        setLineStyle( blueLine );

        bb.setCornerCirclesAndConnectLines( redCircle, greenCircle, blueCircle, redLine, greenLine, blueLine );
        
        addMouseActions( redCircle, Cursor.HAND );
        addMouseActions( greenCircle, Cursor.HAND );
        addMouseActions( blueCircle, Cursor.HAND );
        
        addMouseActions( polygon, Cursor.MOVE );

        bb.connectLabelToLine( redLine, redLabel );
        bb.connectLabelToLine( greenLine, greenLabel );
        bb.connectLabelToLine( blueLine, blueLabel );
        
        bb.connectLabelToCenterOfGravityCircle( centerCircle, areaLabel );
        
        polygon.setFill( Color.rgb( 128, 192, 255, 0.2 ) );
        polygon.setStroke( GRAY );
        polygon.setStrokeWidth( 2.0 );
        polygon.setStrokeLineJoin( StrokeLineJoin.BEVEL );
        
        redCircle.toFront();
        blueCircle.toFront();
        greenCircle.toFront();
    }

    void setLineStyle( Line line ) {
        line.setStrokeWidth( 3 );
        line.setStrokeLineCap( StrokeLineCap.BUTT );
        line.getStrokeDashArray().setAll( 1.0, 4.0, 5.0 );
    }

    public void addPoints( Double... points ) {

        Platform.runLater( () -> {
            
            if ( pointLabels != null ) {
                getChildren().removeAll( pointLabels );
            }
            
            this.getChildren().remove( polygon );
            polygon.getPoints().clear();
            polygon.getPoints().addAll( points );

            ObservableList<Double> polPoints = polygon.getPoints();
            
            pointLabels = new SnapLabel[ polPoints.size() / 2 ];
            
            char letter = 'D';
            
            for ( int i = 0; i < polPoints.size(); i += 2 ) {

                double x = polPoints.get( i + 0 );
                double y = polPoints.get( i + 1 );

                SnapLabel l = new SnapLabel( "" + letter++ );
                Tooltip tt = new Tooltip();
                tt.setText( "x=" + x + "\ny=" + y );
                l.setTooltip( tt );
                l.layoutXProperty().bind( polygon.layoutXProperty().add( x ) );
                l.layoutYProperty().bind( polygon.layoutYProperty().add( y ) );
                pointLabels[ i / 2 ] = l;
            }
            
            getChildren().add( polygon );
            getChildren().addAll( pointLabels );
            setTargets( List.of( pointLabels ) );
            polygon.toBack();
            
            ImageView pin = new ImageView( new Image( getClass()
                    .getResourceAsStream( "red_pin.png" ) ) );
            
            pin.setLayoutX( 100.0 );
            pin.setLayoutY( 100.0 );
            
            getChildren().add( pin );
        } );
    }

    @FXML
    void mouseDragged( MouseEvent ev ) {
        
        double offsetX = ev.getSceneX() - originalMouseXPos;
        double offsetY = ev.getSceneY() - originalMouseYPosY;
        Object source = ev.getSource();
        
        if ( source instanceof Circle ) {
            
            Circle c = (Circle) ev.getSource();
            c.setCenterX( c.getCenterX() + offsetX );
            c.setCenterY( c.getCenterY() + offsetY );
            makeNearestFocussed( ev );
            
        } else if ( source instanceof Polygon ) {
            
            Polygon pol = (Polygon) source;
            pol.setLayoutX( pol.getLayoutX() + offsetX );
            pol.setLayoutY( pol.getLayoutY() + offsetY );
        }
        
        originalMouseXPos = ev.getSceneX();
        originalMouseYPosY = ev.getSceneY();
    }

    @FXML
    void mousePressed( MouseEvent ev ) {
        
        System.out.println( "ev.getSource() = " + ev.getSource() );
        
        originalMouseXPos = ev.getSceneX();
        originalMouseYPosY = ev.getSceneY();
        
        ( (Node) ev.getSource() ).toFront();
    }

    @FXML
    void mouseReleased( MouseEvent ev ) {
        snapStrategy.accept( ev );
        
        if ( ev.getSource() instanceof Polygon ) {
            ( (Polygon) ev.getSource() ).toBack();
        }
    }

    public TriangulatorController setSnapStrategy( SnapStrategy snps ) {
        snapStrategy = strats.get( snps );
        return this;
    }

    void snapNearBy( MouseEvent ev ) {
        
        originalMouseXPos = ev.getX();
        originalMouseYPosY = ev.getY();
        Object source = ev.getSource();
        
        if ( source instanceof Circle ) {
            Circle c = (Circle) ( ev.getSource() );
            targets.stream()
                    .filter(t -> t.inRange(originalMouseXPos, originalMouseYPosY ) )
                    .findFirst()
                    .ifPresentOrElse(
                            (t) -> snapToTarget( t, c ),
                            ( ) -> System.out.println("no snap target nearby") 
                    );
        }
    }

    void snapToNearest( MouseEvent ev ) {

        originalMouseXPos = ev.getX();
        originalMouseYPosY = ev.getY();
        Object source = ev.getSource();

        if ( source instanceof Circle ) {
            Circle c = (Circle) ( ev.getSource() );
            targets.stream()
                    .min(nearestSnapTarget )
                    .ifPresentOrElse( 
                            (t) -> snapToTarget( t, c ),
                            ( ) -> System.out.println("no snap target nearby")
                    );
        }
    }

    void makeNearestFocussed( MouseEvent ev ) {
        
        originalMouseXPos = ev.getX();
        originalMouseYPosY = ev.getY();
        
        targets.stream()
                .peek( t -> t.focussed( false ))
                .min( nearestSnapTarget )
                .ifPresent( t -> t.focussed( true ) );
    }

    void snapToTarget( SnapTarget t, Circle c ) {
        
        c.setCenterX( t.getLayoutX() );
        c.setCenterY( t.getLayoutY() );
        
        System.out.println( "snapped " + c.getId() + " at (" + t.getLayoutX()
                + "," + t.getLayoutY() + ")" );
        
        Node n = ( (Node) t );
        n.setStyle( "-fx-font-weight:bold" );
    }

    public void setTargets( List<SnapTarget> targets ) {
        this.targets = targets;
    }

    private void addMouseActions( Node n, Cursor cursor ) {
        n.setOnMousePressed( this::mousePressed );
        n.setOnMouseDragged( this::mouseDragged );
        n.setOnMouseReleased( this::mouseReleased );
        n.setCursor( cursor );
    }

    public DoubleBinding areaBinding() {
        return bb.areaBinding();
    }

    public DoubleBinding lengthActiveLines() {
        return bb.activeLinesLength();
    }

    public void activateLine( boolean active, String lineName ) {
        bb.activateLine( active, lineName );
    }

    public DoubleBinding lengthBinding( String name ) {
        return bb.lengthBinding( name );
    }

    public List<? extends SnapTarget> getTargets() {
        return targets;
    }

    public DoubleProperty redXProperty() {
        return this.redCircle.centerXProperty();
    }

    public DoubleProperty redYProperty() {
        return this.redCircle.centerYProperty();
    }

    public DoubleProperty greenXProperty() {
        return this.greenCircle.centerXProperty();
    }

    public DoubleProperty greenYProperty() {
        return this.greenCircle.centerYProperty();
    }

    public DoubleProperty blueXProperty() {
        return this.blueCircle.centerXProperty();
    }

    public DoubleProperty blueYProperty() {
        return this.blueCircle.centerYProperty();
    }
}
