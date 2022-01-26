package ivanWinterIntern.photoEditingPage;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class MyImgProcessor {
    private ArrayList<Double> temperatureArray;
    private int temperatureSpaceWidth;
    private int temperatureSpaceHeight;
    private float temperatureSpaceToImageRatio;
    public double[][] temperatureMatrix;
    // index, element
    public double[] min;
    public double[] max;

    public MyImgProcessor(ArrayList<Double> tp, int tpWidth, int tpHeight, float temperatureSpaceToImageRatio) {
        temperatureArray = tp;
        temperatureSpaceWidth = tpWidth;
        temperatureSpaceHeight = tpHeight;
        this.temperatureSpaceToImageRatio = temperatureSpaceToImageRatio;
        temperatureMatrix = listTo2DArray();
        min = findMinMax(true);
        max = findMinMax(false);
    }

    public static Bitmap bitmapFromArray(double[][] pixels2d){
        int width = pixels2d.length;
        int height = pixels2d[0].length;
        int[] pixels = new int[width * height];
        int pixelsIndex = 0;
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                pixels[pixelsIndex] = (int)pixels2d[i][j];
                pixelsIndex++;
            }
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }


    public void printMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            Log.i("mdb", " " + Arrays.toString(matrix[i]));
        }
        Log.i("mdb", "------");
    }

    public double[][] blurImage(double[][] matrix) {
        double[][] kernel = {{1.0,4.0,6.0,4.0,1.0}, {4.0,16.0,24.0,16.0,4.0},
                {6.0,24.0,36.0,24.0,6.0}, {4.0,16.0,24.0,16.0,4.0}, {1.0,4.0,6.0,4.0,1.0} };
        double[][] tempMatrix = new double[matrix.length+4][matrix[0].length+4];
        double[][] outputMatrixWithPadding = new double[tempMatrix.length+4][tempMatrix[0].length+4];

        for (int i = 0; i < matrix.length; i++) {
            System.arraycopy(matrix[i], 0, tempMatrix[i+2], 2, matrix[i].length);
            tempMatrix[i+2][0] = 0;
            tempMatrix[i+2][1] = 0;
            tempMatrix[i+2][matrix[i].length+2] = 0;
            tempMatrix[i+2][matrix[i].length+3] = 0;
        }

        for (int i = 0; i < tempMatrix.length; i++) {
            for (int j = 0; j < tempMatrix[0].length; j++) {
                if (i-2 < 0 || i+2 >= tempMatrix.length) {
                    outputMatrixWithPadding[i][j] = 0d;
                    continue;
                }
                if (j-2 < 0 || j+2 >= tempMatrix[0].length) {
                    outputMatrixWithPadding[i][j] = 0d;
                    continue;
                }
                double[][] tempArray = new double[5][5];
                for (int x = i-2, y = 0; x < i+3; x++, y++) {
                    tempArray[y] = Arrays.copyOfRange(tempMatrix[x], j-2, j+3);
                }
                outputMatrixWithPadding[i][j] = multiplyNSum(tempArray, kernel)/256d;
            }
        }

        double[][] outputMatrixNoPadding = new double[tempMatrix.length-4][tempMatrix[0].length-4];
        Log.i("mdb", "blurImage: " + outputMatrixNoPadding.length + " " + outputMatrixNoPadding[0].length);
        for (int i = 2; i < tempMatrix.length-2; i++) {
            outputMatrixNoPadding[i-2] = Arrays.copyOfRange(outputMatrixWithPadding[i], 2, outputMatrixWithPadding.length-2);
        }

        printMatrix(outputMatrixNoPadding);

        return outputMatrixNoPadding;
    }
    private double multiplyNSum(double[][] a, double[][] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                sum += a[i][j]*b[i][j];
            }
        }
        return sum;
    }

    private double[][] listTo2DArray() {
        double[][] myMatrix = new double[temperatureSpaceHeight][temperatureSpaceWidth];
        for (int i = 0; i < temperatureSpaceHeight; i++) {
            for (int j = 0; j < temperatureSpaceWidth; j++) {
                myMatrix[i][j] = temperatureArray.get(i*temperatureSpaceWidth+j);
            }
        }
        return myMatrix;
    }

    private double[] findMinMax(boolean findMin) {
        // index, element
        double[] container = {0, temperatureArray.get(0)};
        double m = temperatureArray.get(0);
        int n = temperatureArray.size();
        if (findMin) {
            for (int i = 1; i < n; i++) {
                if (temperatureArray.get(i) < m) {
                    if (temperatureArray.get(i) < 0) { continue; }
                    container[0] = i;
                    container[1] = m;
                    m = temperatureArray.get(i);
                }
            }
        }
        else {
            for (int i = 1; i < n; i++) {
                if (temperatureArray.get(i) > m) {
                    container[0] = i;
                    container[1] = m;
                    m = temperatureArray.get(i);
                }
            }
        }
        return container;
    }

    public PointF arraySpaceToImageSpace(double indexInTemperatureSpace) {
        return new PointF(
                ((float)indexInTemperatureSpace%temperatureSpaceWidth)*temperatureSpaceToImageRatio,
                ((float)indexInTemperatureSpace/temperatureSpaceWidth)*temperatureSpaceToImageRatio
        );
    }

    public PointF matrixSpaceToImageSpace(int x, int y) {
        return new PointF(
                x * temperatureSpaceToImageRatio, y * temperatureSpaceToImageRatio
        );
    }

    public double[][] getColorDifference(int sampleSize) {
        double[][] colordiff = new double[temperatureMatrix.length][temperatureMatrix[0].length];
//        double offset = (max[1]-min[1])/10;
        double offset = 0;
        double rangeStart = (max[1]+min[1])/2f-offset;
        double rangeEnd = (max[1]+min[1])/2f+offset;
        double interval = (rangeEnd - rangeStart)/4;
        double step = 1;
        for (int i = 0; i < sampleSize; i++) {
            double cutOff = rangeStart + i*interval;
            for (int j = 0; j < temperatureMatrix.length; j++) {
                for (int k = 0; k < temperatureMatrix[0].length; k++) {
                    if (temperatureMatrix[j][k] < cutOff) {
                        colordiff[j][k] += step;
                    }
                }
            }
        }
        double[][] blurredColordiff = blurImage(colordiff);
        // printMatrix(blurredColordiff);
        return blurredColordiff;
    }

    public ArrayList<double[]> getCriticalAreaStartAt(double[][] matrix) {
        ArrayList<double[]> pts = new ArrayList<>();
        double[][] cellIsTraveled = new double[matrix.length][matrix[0].length];
        // cutoff can be changed with respect to 'step' in 'getColorDifference()'
        double cutoff = 3;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] < cutoff) {
                    cellIsTraveled[i][j] = 1;
                    continue;
                }
                if (cellIsTraveled[i][j] == 1) {
                    continue;
                }
                double areaSum = findNeighbor(i, j, matrix, cellIsTraveled, cutoff);
                Log.i("mdb", "getCriticalPts: " + areaSum + " " + i + " " + j +  " " + matrix[i][j]);
                if (areaSum > 100) {
                    double[] temp = { i, j, areaSum };
                    pts.add(temp);
                }
            }
        }
        return pts;
    }

    private double findNeighbor(int i, int j, double[][] matrix, double[][] cellIsTraveled, double cutoff) {
        if (i < 0 || i > matrix.length) { return 0; }
        if (j < 0 || j >= matrix[0].length) { return 0; }
        if (cellIsTraveled[i][j] == 1) {
                return 0;
        }
        if (matrix[i][j] < cutoff) {
            cellIsTraveled[i][j] = 1;
            return 0;
        }
        cellIsTraveled[i][j] = 1;
        return matrix[i][j] +
                findNeighbor(i - 1, j, matrix, cellIsTraveled, cutoff) +
                findNeighbor(i, j + 1, matrix, cellIsTraveled, cutoff) +
                findNeighbor(i + 1, j, matrix, cellIsTraveled, cutoff) +
                findNeighbor(i, j - 1, matrix, cellIsTraveled, cutoff);
    }


    public PointF getCriticalAreaCenter(int x, int y) {
        if (y-1 >= 0 && temperatureMatrix[y-1][x] > temperatureMatrix[y][x]) {
            return getCriticalAreaCenter(x, y - 1);
        } else if (x+1 < temperatureMatrix[0].length && temperatureMatrix[y][x+1] > temperatureMatrix[y][x]) {
            return getCriticalAreaCenter(x+1, y);
        } else if (y+1 < temperatureMatrix.length && temperatureMatrix[y+1][x] > temperatureMatrix[y][x]) {
            return getCriticalAreaCenter(x, y+1);
        } else if (x-1 >= 0 && temperatureMatrix[y][x-1] > temperatureMatrix[y][x]) {
            return getCriticalAreaCenter(x-1, y);
        }
        return new PointF(x, y);
    }
}
