package pl.edu.agh.mandelbrot;


import java.io.Serializable;

public class MandelbrotGenerationRequest implements Serializable {
    private static final long serialVersionUID = -403250971215465050L;

    private int id;
    private int width;
    private int height;
    private double top;
    private double right;
    private double bottom;
    private double left;
    private int precision;

    public MandelbrotGenerationRequest(int id, int width, int height, double top, double right, double bottom, double left, int precision) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
        this.precision = precision;
    }

    public int getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getTop() {
        return top;
    }

    public double getRight() {
        return right;
    }

    public double getBottom() {
        return bottom;
    }

    public double getLeft() {
        return left;
    }

    public int getPrecision() {
        return precision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MandelbrotGenerationRequest that = (MandelbrotGenerationRequest) o;

        if (id != that.id) return false;
        if (width != that.width) return false;
        if (height != that.height) return false;
        if (Double.compare(that.top, top) != 0) return false;
        if (Double.compare(that.right, right) != 0) return false;
        if (Double.compare(that.bottom, bottom) != 0) return false;
        if (Double.compare(that.left, left) != 0) return false;
        if (precision != that.precision) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + width;
        result = 31 * result + height;
        temp = Double.doubleToLongBits(top);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(right);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(bottom);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(left);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + precision;
        return result;
    }

    @Override
    public String toString() {
        return "MandelbrotGenerationRequest{" +
                "id=" + id +
                ", width=" + width +
                ", height=" + height +
                ", top=" + top +
                ", right=" + right +
                ", bottom=" + bottom +
                ", left=" + left +
                ", precision=" + precision +
                '}';
    }
}
