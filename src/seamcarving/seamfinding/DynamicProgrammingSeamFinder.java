package seamcarving.seamfinding;

import graphs.Edge;
import seamcarving.Picture;
import seamcarving.SeamCarver;
import seamcarving.energy.EnergyFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Dynamic programming implementation of the {@link SeamFinder} interface.
 *
 * @see SeamFinder
 * @see SeamCarver
 */
public class DynamicProgrammingSeamFinder implements SeamFinder {

    @Override
    public List<Integer> findHorizontal(Picture picture, EnergyFunction f) {
        // TODO: Replace with your code
        double[][] dp = new double[picture.width()][picture.height()];
        for (int y = 0; y < picture.height(); y += 1) {
            dp[0][y] = f.apply(picture, 0, y);
        }
        for (int x = 1; x < picture.width(); x++) {
            for (int y = 0; y < picture.height(); y++) {
                double cost = f.apply(picture, x, y);
                if(y == 0) {
                    double temp = Math.min(dp[x-1][y], dp[x-1][y+1]);
                    dp[x][y] = cost + temp;
                } else if (y+1 == picture.height()) {
                    double temp = Math.min(dp[x-1][y], dp[x-1][y-1]);
                    dp[x][y] = cost + temp;
                } else {
                    double temp = min(dp[x-1][y], dp[x-1][y-1], dp[x-1][y+1]);
                    dp[x][y] = cost + temp;
                }
            }
        }
        List<Integer> result = new ArrayList<>(picture.width());
        double tempMin = Double.MAX_VALUE;
        int tempY = 0;
        for (int y = 0; y < picture.height(); y++) {
            if (dp[picture.width()-1][y] < tempMin) {
                tempMin = dp[picture.width()-1][y];
                tempY = y;
            }
        }
        result.add(tempY);

        for(int x = picture.width() - 1; x > 0; x--) {
            if(tempY == 0) {
                double temp = Math.min(dp[x-1][tempY], dp[x-1][tempY+1]);
                if (dp[x-1][tempY+1] == temp) {
                    tempY = tempY+1;
                }
            } else if (tempY+1 == picture.height()) {
                double temp = Math.min(dp[x-1][tempY], dp[x-1][tempY-1]);
                if (dp[x-1][tempY-1] == temp) {
                    tempY = tempY-1;
                }
            } else {
                double temp = min(dp[x-1][tempY], dp[x-1][tempY-1], dp[x-1][tempY+1]);
                if (dp[x-1][tempY-1] == temp) {
                    tempY = tempY-1;
                }
                if (dp[x-1][tempY+1] == temp) {
                    tempY = tempY+1;
                }
            }
            result.add(tempY);
        }
        Collections.reverse(result);
        return result;
        //throw new UnsupportedOperationException("Not implemented yet");
    }
    public static double min(double a, double b, double c) {
        return Math.min(Math.min(a, b), c);
    }
}
