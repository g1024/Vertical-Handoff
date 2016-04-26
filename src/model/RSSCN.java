/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

/**
 *
 * @author bani hani
 */
public class RSSCN {

    public RSSCN() {
    }

    // d = distance BS and MS
    public static double RSS(double d){
        //Debug
//        System.err.println("d = "+d);
//        System.err.println("PL(d) = "+PL(d));
        return Pt + Gt - PL(d) - A;
    }

    // d = distance BS and MS
    private static double PL(double d){
        return 135.41 + 12.49*Math.log10(F) - 4.99*Math.log10(Hbs) + (46.84 - 2.34*Math.log10(Hbs))*Math.log10(d);
    }

    public static double F;
    public static double Hbs;
    public static double Pt;
    public static double Gt;
    public static double A;

}
