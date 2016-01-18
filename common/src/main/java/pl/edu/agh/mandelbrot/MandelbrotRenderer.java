package pl.edu.agh.mandelbrot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.stream.Stream;

import static java.util.stream.IntStream.range;

public class MandelbrotRenderer {
    private static final int MIN_COLOR_RANGE = 0;
    private static final int MAX_COLOR_RANGE = 255;
    private static final int BYTES_PER_PIXEL = 3;
    private static final String FORMAT_NAME = "png";

    public String render(int[][] mandelbrotSet, int maxPrecision) throws IOException {
        int width = mandelbrotSet.length;
        int height = Stream.of(mandelbrotSet).findFirst().map(x -> x.length).orElse(0);

        byte[] rawImage = buildRawImage(mandelbrotSet, maxPrecision, width, height);

        return renderBase64PngImage(rawImage, width, height);
    }

    private byte[] buildRawImage(int[][] mandelbrotSet, int maxPrecision, int width, int height) {
        byte[] rawImage = new byte[width * height * BYTES_PER_PIXEL];

        range(0, width).forEach(w -> {
            range(0, height).forEach(h -> {
                int color = MIN_COLOR_RANGE + (MAX_COLOR_RANGE - MIN_COLOR_RANGE) * (mandelbrotSet[w][h] / maxPrecision);
                int pos = (h * width + w) * BYTES_PER_PIXEL;

                range(pos, pos + BYTES_PER_PIXEL).forEach(p -> rawImage[p] = (byte) (0xff - color));
            });
        });
        return rawImage;
    }

    private String renderBase64PngImage(byte[] rawImage, int width, int height) throws IOException {
        DataBuffer buffer = new DataBufferByte(rawImage, rawImage.length);
        WritableRaster raster = Raster.createInterleavedRaster(buffer, width, height, BYTES_PER_PIXEL * width, BYTES_PER_PIXEL, new int[]{0, 1, 2}, null);
        ColorSpace colorSpace = ColorModel.getRGBdefault().getColorSpace();
        ColorModel colorModel = new ComponentColorModel(colorSpace, false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        BufferedImage bufferedImage = new BufferedImage(colorModel, raster, true, null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, FORMAT_NAME, Base64.getEncoder().wrap(outputStream));

        return outputStream.toString(StandardCharsets.UTF_8.name());
    }

}
