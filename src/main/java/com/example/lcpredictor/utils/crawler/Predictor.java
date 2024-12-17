package com.example.lcpredictor.utils.crawler;

import com.example.lcpredictor.domain.LcPredict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Predictor {

    private static final Logger logger = LoggerFactory.getLogger(Predictor.class);

    // 预测算法
    // https://leetcode.cn/circle/discuss/neTUV4/
    // https://leetcode.cn/circle/discuss/TbWS5j/
    public static void predict(List<LcPredict> predictList) {
        long start = System.currentTimeMillis();
        int n = predictList.size();
        for (int i = 0; i < n; i++) {
            LcPredict user = predictList.get(i);
            double oldRating = user.getOldRating();
            double eRank = erk(predictList, i, oldRating);
            double m = Math.sqrt(eRank * user.getRank());
            double eRating = ert(predictList, m);
            double delta = f(user.getAttendedCount()) * (eRating - oldRating);
            double newRating = oldRating + delta;
            user.setNewRating(newRating);
        }
        long end = System.currentTimeMillis();
        logger.info("PREDICT ELAPSED Time: " + (end - start) / 1000.0);
    }

    private static double erk(List<LcPredict> predictList, int i, double ri) {
        double res = 1;
        int n = predictList.size();
        for (int j = 0; j < n; j++) {
            if (j != i) {
                Double rj = predictList.get(j).getOldRating();
                res += 1 / (1 + Math.pow(10, (ri - rj) / 400));
            }
        }
        return res;
    }

    private static double ert(List<LcPredict> predictList, double m) {
        int n = predictList.size();
        double l = 0, r = 10000;
        while (r - l > 1e-9) {
            double mid = (l + r) / 2;
            if (erk(predictList, n, mid) < m) {
                r = mid;
            } else {
                l = mid;
            }
        }
        return l;
    }

    private static double f(int k) {
        double res = 0;
        for (int i = 0; i <= k; i++) {
            res += Math.pow(5.0 / 7, i);
        }
        res = 1 / (1 + res);
        return res;
    }
}
