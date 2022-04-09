package surveyor;

/**
 * Data carrier with builder example.
 * 
 * Private constructor and Builder are used to make a more user friendly API,
 * in where builder methods with appropriate name take the double values.
 * 
 * @author Pieter van den Hombergh
 */
public class Measurement {

    final double length;
    final double area;

    private Measurement( double length, double area ) {
        this.length = length;
        this.area = area;
    }

    public double getLength() {
        return length;
    }

    public double getArea() {
        return area;
    }

    public static class Builder {

        private double area;
        private double length;

        public Builder area( double area ) {
            this.area = area;
            return this;
        }

       public Builder length( double length ) {
            this.length = length;
            return this;
        }

        public Measurement build() {
            return new Measurement( length, area );
        }
    }
    
    public static Builder builder(){
        return new Builder();
    }
}
