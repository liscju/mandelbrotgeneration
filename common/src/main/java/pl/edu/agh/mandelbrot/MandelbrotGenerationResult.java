package pl.edu.agh.mandelbrot;


import java.io.Serializable;

public class MandelbrotGenerationResult implements Serializable {
    private int id;
    private int width;
    private int height;
    private double top;
    private double right;
    private double bottom;
    private double left;
    private int precision;
    private boolean ended;
    private int time;
    private String image;

    public MandelbrotGenerationResult(int id, int width, int height, double top, double right,
                                      double bottom, double left, int precision, boolean ended,
                                      int time, String image) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
        this.precision = precision;
        this.ended = ended;
        this.time = time;
        this.image = image;
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

    public boolean isEnded() {
        return ended;
    }

    public int getTime() {
        return time;
    }

    public String getImage() {
        return image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MandelbrotGenerationResult that = (MandelbrotGenerationResult) o;

        if (id != that.id) return false;
        if (width != that.width) return false;
        if (height != that.height) return false;
        if (Double.compare(that.top, top) != 0) return false;
        if (Double.compare(that.right, right) != 0) return false;
        if (Double.compare(that.bottom, bottom) != 0) return false;
        if (Double.compare(that.left, left) != 0) return false;
        if (precision != that.precision) return false;
        if (ended != that.ended) return false;
        if (time != that.time) return false;
        if (image != null ? !image.equals(that.image) : that.image != null) return false;

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
        result = 31 * result + (ended ? 1 : 0);
        result = 31 * result + time;
        result = 31 * result + (image != null ? image.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MandelbrotGenerationResult{" +
                "id=" + id +
                ", width=" + width +
                ", height=" + height +
                ", top=" + top +
                ", right=" + right +
                ", bottom=" + bottom +
                ", left=" + left +
                ", precision=" + precision +
                ", ended=" + ended +
                ", time=" + time +
                ", image='" + image + '\'' +
                '}';
    }
}
