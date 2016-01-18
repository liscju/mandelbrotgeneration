package pl.edu.agh.mandelbrot;

import static java.util.stream.IntStream.range;

public class MandelbrotComputer {
    private static final double MAX_MODULE = 2.0;
    private int maxPrecision;

    public MandelbrotComputer(int maxPrecision) {
        this.maxPrecision = maxPrecision;
    }

    public int[][] compute(int width, int height, double top, double right, double bottom, double left) {
        int[][] mandelbrotSet = new int[width][];

        range(0, width).forEach(w -> {
            mandelbrotSet[w] = new int[height];
            range(0, height).forEach(h -> {
                double x = mapCoordinate(w, left, right, width);
                double y = mapCoordinate(h, top, bottom, height);

                ComplexNumber p = new ComplexNumber(x, y);
                ComplexNumber z = new ComplexNumber();
                int precision;
                for (precision = 0; z.getModule() < MAX_MODULE && precision < maxPrecision; ++precision) {
                    z.square();
                    z.add(p);
                }

                mandelbrotSet[w][h] = precision;
            });
        });

        return mandelbrotSet;
    }

    private double mapCoordinate(int coordinate, double min, double max, int resolution) {
        return (double) coordinate * (max - min) / resolution + min;
    }

    private static class ComplexNumber {
        double real;
        double imaginary;

        ComplexNumber() {
        }

        ComplexNumber(double real, double imaginary) {
            this.real = real;
            this.imaginary = imaginary;
        }

        void square() {
            double newReal = real * real - imaginary * imaginary;
            double newImaginary = 2 * real * imaginary;

            real = newReal;
            imaginary = newImaginary;
        }

        double getModule() {
            return Math.sqrt(real * real + imaginary * imaginary);
        }

        void add(ComplexNumber other) {
            this.real += other.real;
            this.imaginary += other.imaginary;
        }
    }
}
