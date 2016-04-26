/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.awt.Point;
import java.util.Vector;

/**
 *
 * @author bani hani
 */
public class Area {
    public static final int X = 0;
    public static final int Y = 0;
    private static int width;
    private static int height;
    private static int rows;
    private static int colls;
    public AccessPoint[] accessPoints = new AccessPoint[ACCESS_POINT_NO + 1];//index 0 is empty
    public BaseStation[] baseStations = new BaseStation[BASE_STATION_NO + 1];//index 0 is empty
    public Mobile[] mobileUsers = new Mobile[width];

    public static final int ACCESS_POINT_NO = 12;
    public static final int BASE_STATION_NO = 7;
    public static int mobileNo = 0;
    private static Area instance = null;

    public static long trainingTime;
    public static long testingTime;                                                                                                                                     public static double status = 1/7; public static double statu5 = 1/5;

    //Noise in dBm >> "from paper"
    public static int N = -90;

    public static Area getInstance(){
        if(instance == null){
            instance = new Area(width, height);
        }

        return instance;
    }

    private Area(int width, int height){
        this.width = width;
        this.height = height;
        mobileUsers = new Mobile[mobileNo+1];//index 0 is empty
    }

    public static void setWidth(int w){
        width = w;
    }

    public int getWidth(){
        return width;
    }

    public static void setHeight(int h){
        height = h;
    }

    public int getHeight(){
        return height;
    }


    public static void setRows(int rows) {
        Area.rows = rows;
    }

    public static void setColls(int colls) {
        Area.colls = colls;
    }

    public static int getRows() {
        return rows;
    }

    public static int getColls() {
        return colls;
    }

    public static int getCellWidth(){
        return width/colls;
    }
    
    public static int getCellHeight(){
        return height/rows;
    }
    
    public int getHCenter(){
        return ((width + X)/2);
    }

    public int getVCenter(){
        return ((height + Y)/2);
    }

    public void addBaseStationAt(int idx, BaseStation bs){
        baseStations[idx] = bs;
    }

    public BaseStation getBaseStationAt(int idx){
        if(idx >= baseStations.length){
            return null;
        }
        return baseStations[idx];
    }

    public void addAccessPointAt(int idx, AccessPoint ap){
        accessPoints[idx] = ap;
    }

    public AccessPoint getAccessPointAt(int idx){
        if(idx >= accessPoints.length){
            return null;
        }
        return accessPoints[idx];
    }

    public void AddUserMobileAt(int idx, Mobile m){
        mobileUsers[idx] = m;
    }

    public Mobile getMobileUserAt(int idx){
        if(idx >= mobileUsers.length){
            return null;
        }

        return mobileUsers[idx];
    }

    @Override
    public String toString() {
        return "Area";
    }




}
