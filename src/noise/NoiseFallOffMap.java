package noise;

import java.awt.image.BufferedImage;

/**
 * 填充地图
 */
public class NoiseFallOffMap {
    private int width;
    private int height;
    private double intensity; // 强度值最小为1

    public NoiseFallOffMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.intensity = 1;
    }

    public NoiseFallOffMap(int width, int height, double intensity) {
        this.width = width;
        this.height = height;
        this.intensity = intensity;
    }

    /**
     * 根据强度值 将点集中在地图的中间
     *
     * @param x
     * @param y
     * @param value
     * @return
     */
    public double getNoiseValue(int x, int y, double value) {
        double minVal = (((height + width) / 2) / 100) / intensity; // 平均边长的百分之1的intensity分大小
        double maxVal = (((height + width) / 2) / 100 * intensity); // 平均边长的百分之 intensity 的大小

        if (intensity > 1) {
            int dist2edge = calculateDistanceToEdge(x, y);
            if (dist2edge <= minVal) {
                return 0;
            } else if (dist2edge >= maxVal) {
                return value;
            } else {
                // 由于minVal太小几乎可以 忽略不计,
                // 由于dist2edge小于maxVal，fadeFactor小于1
                // 点离边越近,最后返回的值越小
                double fadeFactor = (dist2edge - minVal) / (maxVal - minVal);

                return (value * fadeFactor);
            }
        } else {
            return value;
        }
    }

    public BufferedImage getNoiseImage() {
        return generateNoiseImage();
    }

    /**
     * 生成将点集中在中间的 滤波器
     * @return
     */
    protected double[][] generateNoiseArray() {
        double[][] noiseValues = new double[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double result;
                double minVal = (((height + width) / 2) / 100) / intensity;
                double maxVal = (((height + width) / 2) / 100 * intensity);

                if (intensity > 1) {
                    if (calculateDistanceToEdge(x, y) <= minVal) {
                        result = 0;
                    } else if (calculateDistanceToEdge(x, y) >= maxVal) {
                        result = 1;
                    } else {
                        double possibleMax = maxVal - minVal;
                        double currentValue = calculateDistanceToEdge(x, y) - minVal;
                        double fadeFactor = currentValue / possibleMax;

                        result = ((double) 1 * fadeFactor);
                    }
                } else {
                    result = 1;
                }
                noiseValues[x][y] = result;
            }
        }

        return noiseValues;
    }

    protected BufferedImage generateNoiseImage() {
        BufferedImage noiseImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        double[][] noise = generateNoiseArray();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double noiseValue = noise[x][y];

                int blue = (int) (noiseValue * 0xFF);
                int green = blue * 0x100;
                int red = blue * 0x10000;
                int finalColor = red + green + blue;

                noiseImage.setRGB(x, y, finalColor);
            }
        }

        return noiseImage;
    }

    /**
     * 计算x,y到边缘的最小距离
     *
     * @param x
     * @param y
     * @return
     */
    private int calculateDistanceToEdge(int x, int y) {
        // 为什么要减7 和 减 28, 这里的width 和 height 不是整个可用区域吗
        int[] distances = { // 假设坐标系为原点在左下角
                x, y,
                (width), (height),
                (width - x - 7), // x到右边的距离
                (height - y - 28) // y到上边的距离
        };

        // 求distances中的最小值
        int min = distances[0];

        for (int value : distances) {
            if (value < min) {
                min = value;
            }
        }

        return min;
    }

}
