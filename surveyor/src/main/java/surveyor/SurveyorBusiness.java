package surveyor;

import java.util.List;
import java.util.function.ToDoubleFunction;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Pieter van den Hombergh
 */
public class SurveyorBusiness {

    String resultFile;
    String pointsFile;
    boolean unSaved;
    ObservableList<Measurement> measurements = FXCollections
            .observableArrayList();

    public SurveyorBusiness() {
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    void accept( Measurement measurement ) {
        this.measurements.add( 0, measurement );
    }

    DoubleBinding totalAreaBinding() {
        return totalBinding( measurements, Measurement::getArea );
    }

    DoubleBinding borderLengthBinding() {
        return totalBinding( measurements, Measurement::getLength );
    }

    DoubleBinding totalBinding( ObservableList<Measurement> m, ToDoubleFunction<Measurement> fun ) {
        return new DoubleBinding() {
            {
                bind( m );
            }

            @Override
            protected double computeValue() {
                return m.stream()
                        .mapToDouble( fun )
                        .sum();
            }
        };
    }

    void clear() {
        this.measurements.clear();
    }
}
