package noise;

import java.awt.image.BufferedImage;

public abstract class Noise2D {
    protected int width;
    protected int height;
    protected NoiseFallOffMap noiseFallOffMap;

    public Noise2D() {
        this.width = 0;
        this.height = 0;
        this.noiseFallOffMap = new NoiseFallOffMap(width, height, 0);
    }

    public Noise2D(int width, int height) {
        this.width = width;
        this.height = height;
        this.noiseFallOffMap = new NoiseFallOffMap(width, height);
    }

    public Noise2D(int width, int height, NoiseFallOffMap noiseFallOffMap) {
        this.width = width;
        this.height = height;
        this.noiseFallOffMap = noiseFallOffMap;
    }

    protected abstract double[][] generateNoiseArray();

    protected abstract BufferedImage generateNoiseImage();

    /**
     * 获取灰度值
     * @param noiseValue
     * @return
     */
    protected int getGreyscaleNoiseColor(double noiseValue) {
        int blue = (int) (noiseValue * 0xFF);
        int green = blue * 0x100;
        int red = blue * 0x10000;

        return red + green + blue;
    }

}
