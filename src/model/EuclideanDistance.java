/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.awt.Point;

/**
 *
 * @author bani hani
 */
public class EuclideanDistance {

    public static double distance(Point p1, Point p2){
        double x1 = ((double) p1.x)/1000;
        double y1 = ((double) p1.y)/1000;
        double x2 = ((double) p2.x)/1000;
        double y2 = ((double) p2.y)/1000;

//        System.err.println("x1 = "+x1+" y1 = "+y1+" x2 = "+x2+" y2 = "+y2);

        return (Math.sqrt( Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2) ));
    }

    public static double distance2(Point p1, Point p2){
        double x1 = p1.x;
        double y1 = p1.y;
        double x2 = p2.x;
        double y2 = p2.y;

//        System.err.println("x1 = "+x1+" y1 = "+y1+" x2 = "+x2+" y2 = "+y2);

        return (Math.sqrt( Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2) ));
    }

}
