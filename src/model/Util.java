/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.awt.Point;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author Bright.Tech
 */
public class Util {

    public static int getSid(int r, int c) {
        int tsid = r * Area.getColls() + c;
        return tsid;
    }

    public static int sidToRows(int sid) {
        int ti = sid / Area.getRows();
        return ti;
    }

    public static int sidToColls(int sid) {
        int tj = ((sid) % Area.getColls());
        return tj;
    }

    public static Point getCellCenter(int sid){
        return getCellCenter(sidToRows(sid), sidToColls(sid));
    }

    public static Point getCellCenter(int row, int coll){
        int x = coll * Area.getCellWidth() + Area.getCellWidth()/2;
        int y = row * Area.getCellHeight() + Area.getCellHeight()/2;
        return new Point(x, y);
    }

    public static int getSidAtCords(int x, int y){
        return getSid((int)(x/Area.getCellWidth()), (int)(y/Area.getCellHeight()));
    }

    public static int getSidAtCords(Point point){
        return getSidAtCords(point.x, point.y);
    }

}
