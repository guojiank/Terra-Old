package noise;

import java.awt.image.BufferedImage;
import java.util.Random;

public class PerlinNoise2D extends Noise2D {
    private Random random = new Random();

    private double frequency = 1;
    private int octaves;
    private double persistence;
    private double lacunarity;
    private double roughness;
    private long seed;

    private double meanNoiseValue = 0.5;
    private double maxNoiseValue = Double.MIN_VALUE;
    private double minNoiseValue = Double.MAX_VALUE;

    static final int PERMUTATION_TABLE[] = new int[512];
    static final int PERMUTATION_VALUES[] = {151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53,
            194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120,
            234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87,
            174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231,
            83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54,
            65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116,
            188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38,
            147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223,
            183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129,
            22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251,
            34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107,
            49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254,
            138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180};

    static {
        for (int i = 0; i < 256; i++) {
            PERMUTATION_TABLE[256 + i] = PERMUTATION_TABLE[i] = PERMUTATION_VALUES[i];
        }
    }

    public PerlinNoise2D() {
        super();
        this.octaves = 1;
        this.persistence = 1;
        this.lacunarity = 1;

        random.setSeed(1);
        this.seed = random.nextInt();
    }

    public PerlinNoise2D(int width, int height, double roughness, int octaves, double persistence, double lacunarity, long seed) {
        super(width, height);
        this.roughness = roughness;
        this.octaves = octaves;
        this.persistence = persistence;
        this.lacunarity = lacunarity;

        random.setSeed(seed);
        this.seed = random.nextInt();
    }

    public PerlinNoise2D(int width, int height, double roughness, int octaves, double persistence, double lacunarity, long seed, NoiseFallOffMap fallOffMap) {
        super(width, height, fallOffMap);
        this.roughness = roughness;
        this.octaves = octaves;
        this.persistence = persistence;
        this.lacunarity = lacunarity;

        random.setSeed(seed);
        this.seed = random.nextInt();
    }

    public double[][] getNoiseArray() {
        return generateNoiseArray();
    }

    public BufferedImage getNoiseImage() {
        return generateNoiseImage();
    }

    private double generateNoiseValue(double x, double y, double frequency) {
        double doubleX = (double) x / width;
        double doubleY = (double) y / height;

        double frequencyX = (doubleX * frequency) + seed;
        double frequencyY = (doubleY * frequency) + seed;

        int flooredX = (int) Math.floor(frequencyX) & 255;
        int flooredY = (int) Math.floor(frequencyY) & 255;

        int corner1 = PERMUTATION_TABLE[PERMUTATION_TABLE[flooredX] + flooredY];
        int corner2 = PERMUTATION_TABLE[PERMUTATION_TABLE[flooredX + 1] + flooredY];
        int corner3 = PERMUTATION_TABLE[PERMUTATION_TABLE[flooredX] + flooredY + 1];
        int corner4 = PERMUTATION_TABLE[PERMUTATION_TABLE[flooredX + 1] + flooredY + 1];

        double adjustedX = frequencyX - Math.floor(frequencyX);
        double adjustedY = frequencyY - Math.floor(frequencyY);

        double dotCorner1 = calculateDotProduct(corner1, adjustedX, adjustedY);
        double dotCorner2 = calculateDotProduct(corner2, adjustedX - 1, adjustedY);
        double dotCorner3 = calculateDotProduct(corner3, adjustedX, adjustedY - 1);
        double dotCorner4 = calculateDotProduct(corner4, adjustedX - 1, adjustedY - 1);

        double interpolatedX = interpolate(adjustedX);
        double interpolatedY = interpolate(adjustedY);

        double lerpedX1 = lerp(interpolatedX, dotCorner1, dotCorner2);
        double lerpedX2 = lerp(interpolatedX, dotCorner3, dotCorner4);
        double lerpedY = lerp(interpolatedY, lerpedX1, lerpedX2);

        return lerpedY;
    }

    protected double generateOctaveNoiseValue(double x, double y, double frequency) {
        double total = 0;
        double amplitude = 1;

        for (int i = 0; i < octaves; i++) {
            double perlinValue = generateNoiseValue(x, y, frequency);
            total += perlinValue * amplitude;

            amplitude *= persistence;
            frequency *= lacunarity;
        }

        return total;
    }

    @Override
    protected double[][] generateNoiseArray() {
        double[][] noiseValues = new double[width][height];

        noiseValues = generateBaseNoiseArray(noiseValues); // 生成perlin noise 并且记录最小和最大值
        noiseValues = addFallOffMapToNoiseArray(noiseValues); // 生成0~1之间的噪声值，并集中
        noiseValues = addRoughnessToNoiseArray(noiseValues); //添加白点噪声，并获取最大值和最小值
        noiseValues = smoothNoiseArray(noiseValues); // 规范噪声值在0~1之间，并获取平均噪声值

        return noiseValues;
    }

    /**
     * 生成灰度图的噪声图片
     *
     * @return
     */
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

    /**
     * 生成 perlin噪声,并记录最大值和最小值
     *
     * @param noiseValues
     * @return
     */
    private double[][] generateBaseNoiseArray(double[][] noiseValues) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                noiseValues[x][y] = generateOctaveNoiseValue(x, y, frequency);

                if (noiseValues[x][y] > maxNoiseValue) {
                    maxNoiseValue = noiseValues[x][y];
                } else if (noiseValues[x][y] < minNoiseValue) {
                    minNoiseValue = noiseValues[x][y];
                }
            }
        }

        return noiseValues;
    }

    /**
     * 将noise集中在地图中间，并获取noise中的最大值和最小值
     *
     * @param noiseValues
     * @return
     */
    private double[][] addFallOffMapToNoiseArray(double[][] noiseValues) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                noiseValues[x][y] = inverseLerp(minNoiseValue, maxNoiseValue, noiseValues[x][y]); // 将噪声值全部 规范到0~1之间

                if (noiseFallOffMap != null) {
                    noiseValues[x][y] = interpolate(noiseFallOffMap.getNoiseValue(x, y, noiseValues[x][y]));
                }

                if (noiseValues[x][y] > maxNoiseValue) {
                    maxNoiseValue = noiseValues[x][y];
                } else if (noiseValues[x][y] < minNoiseValue) {
                    minNoiseValue = noiseValues[x][y];
                }
            }
        }

        return noiseValues;
    }

    /**
     * 添加白点噪声，并获取最大值和最小值
     *
     * @param noiseValues
     * @return
     */
    private double[][] addRoughnessToNoiseArray(double[][] noiseValues) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                noiseValues[x][y] -= new WhiteNoise2D().getNoiseValue(x, y) * (roughness / 10);

                if (noiseValues[x][y] > maxNoiseValue) {
                    maxNoiseValue = noiseValues[x][y];
                } else if (noiseValues[x][y] < minNoiseValue) {
                    minNoiseValue = noiseValues[x][y];
                }
            }
        }

        return noiseValues;
    }

    private double[][] smoothNoiseArray(double[][] noiseValues) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                noiseValues[x][y] = inverseLerp(minNoiseValue, maxNoiseValue, noiseValues[x][y]);
                meanNoiseValue += noiseValues[x][y];
            }
        }

        meanNoiseValue /= (width * height);

        return noiseValues;
    }

    private double lerp(double amount, double left, double right) {
        return ((1 - amount) * left + amount * right);
    }

    /**
     * 规范数值为0~1之间
     *
     * @param dataLow
     * @param dataHigh
     * @param x
     * @return
     */
    private double inverseLerp(double dataLow, double dataHigh, double x) {
        return ((x - dataLow) / (dataHigh - dataLow));
    }

    private double interpolate(double noiseValue) {
        return noiseValue * noiseValue * noiseValue * (noiseValue * (noiseValue * 6 - 15) + 10);
    }

    private double calculateDotProduct(int corner, double x, double y) {
        switch (corner & 3) {
            case 0:
                return x + y;
            case 1:
                return -x + y;
            case 2:
                return x - y;
            case 3:
                return -x - y;
            default:
                return 0;
        }
    }

    public double getMeanNoiseValue() {
        return meanNoiseValue;
    }
}
