package noise;

import java.awt.image.BufferedImage;
import java.util.Random;

public class WhiteNoise2D extends Noise2D {
    private Random random = new Random();

    public WhiteNoise2D() {
        super();
    }

    public WhiteNoise2D(int width, int height) {
        super(width, height);
    }

    public WhiteNoise2D(int width, int height, NoiseFallOffMap noiseFallOffMap) {
        super(width, height, noiseFallOffMap);
    }

    public double getNoiseValue(int x, int y) {
        return random.nextDouble();
    }

    public double[][] getNoiseArray() {
        return generateNoiseArray();
    }

    public BufferedImage getNoiseImage() {
        return generateNoiseImage();
    }

    /**
     * 生成随机值二维数组
     *
     * @return
     */
    @Override
    protected double[][] generateNoiseArray() {
        double[][] noiseValues = new double[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                noiseValues[x][y] = noiseFallOffMap.getNoiseValue(x, y, random.nextDouble());
            }
        }

        return noiseValues;
    }

    @Override
    protected BufferedImage generateNoiseImage() {
        BufferedImage noiseImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        double[][] noiseValues = generateNoiseArray();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                noiseImage.setRGB(x, y, getGreyscaleNoiseColor(noiseValues[x][y]));
            }
        }

        return noiseImage;
    }
}
