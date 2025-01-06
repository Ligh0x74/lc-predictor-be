package com.example.lcpredictor.utils.crawler;

import com.example.lcpredictor.domain.LcPredict;

import java.util.List;

public class PredictorFFT {

    private static final int N = 5000;

    private static int convert(double rt) {
        return (int) Math.round(rt);
    }

    private static double[] P;

    // 预测算法, 经过测试误差小于 1, 预测时间从 5min 降低为 100ms 以内
    // https://leetcode.cn/circle/discuss/neTUV4/
    // https://leetcode.cn/circle/discuss/TbWS5j/
    // 将评分转换为 [0, N] 之间的整数, f(x) 表示评分为 x 的人数
    // g(x) = 1 / (1 + 10^(x / 400)), 其中 x 的取值范围是 [-N, N]
    // 设 F(x) 和 G(x) 为:
    // F(x) = f(0)x^0 + f(1)x^1 + ... + f(N)x^N
    // G(x) = g(-N)x^-N + g(-N + 1)x^-N+1 + ... + g(N)x^N
    // 那么, Seed(i) 就是 P(x) = F(x) * G(x) 中 x^i 项的系数, 其中 0 <= i <= N
    // 为了能够使用 FFT, 将 G(x) 乘以 x^N, 那么 Seed(i) 对应 P(x) 中 x^(i + N) 项的系数
    public static void execute(List<LcPredict> predictList) {
        double[] f = new double[N + 1];
        for (var p : predictList) {
            f[convert(p.getOldRating())]++;
        }
        double[] g = new double[2 * N + 1];
        for (int i = -N; i <= N; i++) {
            g[i + N] = 1 / (1 + Math.pow(10, i / 400.0));
        }
        P = FFT.mul(f, g);

        for (LcPredict predict : predictList) {
            double oldRating = predict.getOldRating();
            double eRank = seed(oldRating) + 0.5;
            double m = Math.sqrt(eRank * predict.getRanking());
            double eRating = ert(m);
            double delta = f(predict.getAttendedCount()) * (eRating - oldRating);
            double newRating = oldRating + delta;
            predict.setNewRating(newRating);
        }
    }

    private static double seed(double x) {
        return P[convert(x) + N];
    }

    private static double ert(double m) {
        double l = 0, r = 5000;
        while (r - l > 1e-9) {
            double mid = (l + r) / 2;
            if (seed(mid) + 1 < m) {
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
