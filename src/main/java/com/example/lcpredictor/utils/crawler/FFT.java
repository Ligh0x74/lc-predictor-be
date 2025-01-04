package com.example.lcpredictor.utils.crawler;

public class FFT {

    private static int[] r;

    public static double[] mul(double[] a, double[] b) {
        int n = a.length - 1, m = b.length - 1;
        int k = 1 << (32 - Integer.numberOfLeadingZeros(n + m));
        double[] Ax = new double[k];
        double[] Ay = new double[k];
        double[] Bx = new double[k];
        double[] By = new double[k];
        System.arraycopy(a, 0, Ax, 0, n + 1);
        System.arraycopy(b, 0, Bx, 0, m + 1);

        r = new int[k];
        for (int i = 0; i < k; i++) {
            r[i] = r[i >> 1] >> 1 | (i % 2 == 0 ? 0 : k >> 1);
        }

        fft(Ax, Ay, k, 1);
        fft(Bx, By, k, 1);
        for (int i = 0; i < k; i++) {
            double x = Ax[i] * Bx[i] - Ay[i] * By[i];
            double y = Ax[i] * By[i] + Ay[i] * Bx[i];
            Ax[i] = x;
            Ay[i] = y;
        }
        fft(Ax, Ay, k, -1);
        double[] ans = new double[n + m + 1];
        for (int i = 0; i <= n + m; i++) {
            ans[i] = Ax[i] / k;
        }
        return ans;
    }

    public static void fft(double[] Px, double[] Py, int n, int inv) {
        for (int i = 0; i < n; i++) {
            if (i < r[i]) {
                swap(Px, i, r[i]);
                swap(Py, i, r[i]);
            }
        }

        for (int len = 2; len <= n; len <<= 1) {
            double wnx = Math.cos(2 * Math.PI / len);
            double wny = Math.sin(2 * Math.PI / len) * inv;
            for (int i = 0; i < n; i += len) {
                double wkx = 1;
                double wky = 0;
                for (int j = i; j < i + len / 2; j++) {
                    double ex = Px[j];
                    double ey = Py[j];
                    double wox = wkx * Px[j + len / 2] - wky * Py[j + len / 2];
                    double woy = wkx * Py[j + len / 2] + wky * Px[j + len / 2];
                    Px[j] = ex + wox;
                    Py[j] = ey + woy;
                    Px[j + len / 2] = ex - wox;
                    Py[j + len / 2] = ey - woy;
                    double x = wnx * wkx - wny * wky;
                    double y = wnx * wky + wny * wkx;
                    wkx = x;
                    wky = y;
                }
            }
        }
    }

    public static void swap(double[] aux, int i, int j) {
        double t = aux[i];
        aux[i] = aux[j];
        aux[j] = t;
    }
}
