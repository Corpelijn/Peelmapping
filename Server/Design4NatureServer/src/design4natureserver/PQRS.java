/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package design4natureserver;

/**
 *
 * @author Bas
 */
public class PQRS {

    public static PQRS[] XpqR() {
        PQRS[] values = new PQRS[10];
        values[1] = XpqR(0, 1, 190094.945);
        values[2] = XpqR(1, 1, -11832.228);
        values[3] = XpqR(2, 1, -114.221);
        values[4] = XpqR(0, 3, -32.391);
        values[5] = XpqR(1, 0, -0.705);
        values[6] = XpqR(3, 1, -2.34);
        values[7] = XpqR(1, 3, -0.608);
        values[8] = XpqR(0, 2, -0.008);
        values[9] = XpqR(2, 3, 0.148);

        return values;
    }

    public static PQRS[] YpqS() {
        PQRS[] values = new PQRS[11];
        values[1] = YpqS(1, 0, 309056.544);
        values[2] = YpqS(0, 2, 3638.893);
        values[3] = YpqS(2, 0, 73.077);
        values[4] = YpqS(1, 2, -157.984);
        values[5] = YpqS(3, 0, 59.788);
        values[6] = YpqS(0, 1, 0.433);
        values[7] = YpqS(2, 2, -6.439);
        values[8] = YpqS(1, 1, -0.032);
        values[9] = YpqS(0, 4, 0.092);
        values[10] = YpqS(1, 4, -0.054);

        return values;
    }

    public double p;
    public double q;
    public double R;
    public double S;

    private static PQRS XpqR(double p, double q, double R) {
        PQRS x = new PQRS();
        x.p = p;
        x.q = q;
        x.R = R;
        return x;
    }

    private static PQRS YpqS(double p, double q, double S) {
        PQRS y = new PQRS();
        y.p = p;
        y.q = q;
        y.S = S;
        return y;
    }
}
