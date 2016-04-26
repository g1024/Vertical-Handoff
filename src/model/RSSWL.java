/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

/**
 *
 * @author bani hani
 */
public class RSSWL {

    public RSSWL() {
    }

    //PW = Pt - PL
    public static double RSS(double d){
        return Pt - PL(d);
    }

    //PL = L +10 nlog(d) + S
    public static double PL(double d){
        return L + 10 * N * Math.log10(d) + S;
    }



    public static double L;
    public static double N;
    public static double S;
    public static double Pt;
}
