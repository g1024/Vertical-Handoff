/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.awt.Graphics;
import java.awt.Point;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bani hani
 */
public class AccessPoint {

    private Point point;
    private int radius;
    private int id;
    public int numOfConnectedUser = 0;
    public int numOfSNRConnectedUser = 0;
    public int capacity = 15;

    public AccessPoint(int id, int radius) {
        this.id = id;
        this.radius = radius;
        point = new Point();
        setCenterPoint(id);
    }

    public int getId() {
        return id;
    }

    public int getradius() {
        return radius;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setNumOfConnectedUser(int numOfConnectedUser) {
        this.numOfConnectedUser = numOfConnectedUser;
    }

    public int getNumOfConnectedUser() {
        return numOfConnectedUser;
    }

    public void setNumOfSNRConnectedUser(int numOfSNRConnectedUser) {
        this.numOfSNRConnectedUser = numOfSNRConnectedUser;
    }

    public int getNumOfSNRConnectedUser() {
        return numOfSNRConnectedUser;
    }
    
    

    public int getCapacity() {
        return capacity;
    }

    public void setCenterPoint(int id) {
        switch (id) {
            case 1:
                point.x = Area.getInstance().getBaseStationAt(1).getPoint().x;
                point.y = Area.getInstance().getBaseStationAt(1).getPoint().y - Math.abs(Area.getInstance().getBaseStationAt(1).getPoint().y - Area.getInstance().getBaseStationAt(2).getPoint().y) / 2;
                break;

            case 2:
                point.x = Area.getInstance().getBaseStationAt(1).getPoint().x + Math.abs(Area.getInstance().getBaseStationAt(1).getPoint().x - Area.getInstance().getBaseStationAt(3).getPoint().x) / 2;
                point.y = Area.getInstance().getBaseStationAt(1).getPoint().y - Math.abs(Area.getInstance().getBaseStationAt(1).getPoint().y - Area.getInstance().getBaseStationAt(3).getPoint().y) / 2;
                break;

            case 3:
                point.x = Area.getInstance().getBaseStationAt(1).getPoint().x + Math.abs(Area.getInstance().getBaseStationAt(1).getPoint().x - Area.getInstance().getBaseStationAt(4).getPoint().x) / 2;
                point.y = Area.getInstance().getBaseStationAt(1).getPoint().y + Math.abs(Area.getInstance().getBaseStationAt(1).getPoint().y - Area.getInstance().getBaseStationAt(4).getPoint().y) / 2;
                break;

            case 4:
                point.x = Area.getInstance().getBaseStationAt(1).getPoint().x;
                point.y = Area.getInstance().getBaseStationAt(1).getPoint().y + Math.abs(Area.getInstance().getBaseStationAt(1).getPoint().y - Area.getInstance().getBaseStationAt(5).getPoint().y) / 2;
                break;

            case 5:
                point.x = Area.getInstance().getBaseStationAt(1).getPoint().x - Math.abs(Area.getInstance().getBaseStationAt(1).getPoint().x - Area.getInstance().getBaseStationAt(4).getPoint().x) / 2;
                point.y = Area.getInstance().getBaseStationAt(1).getPoint().y + Math.abs(Area.getInstance().getBaseStationAt(1).getPoint().y - Area.getInstance().getBaseStationAt(4).getPoint().y) / 2;
                break;

            case 6:
                point.x = Area.getInstance().getBaseStationAt(1).getPoint().x - Math.abs(Area.getInstance().getBaseStationAt(1).getPoint().x - Area.getInstance().getBaseStationAt(3).getPoint().x) / 2;
                point.y = Area.getInstance().getBaseStationAt(1).getPoint().y - Math.abs(Area.getInstance().getBaseStationAt(1).getPoint().y - Area.getInstance().getBaseStationAt(3).getPoint().y) / 2;
                break;

            case 7:
                point.x = Area.getInstance().getBaseStationAt(2).getPoint().x + Math.abs(Area.getInstance().getBaseStationAt(2).getPoint().x - Area.getInstance().getBaseStationAt(3).getPoint().x) / 2;
                point.y = Area.getInstance().getBaseStationAt(2).getPoint().y + Math.abs(Area.getInstance().getBaseStationAt(2).getPoint().y - Area.getInstance().getBaseStationAt(3).getPoint().y) / 2;
                break;

            case 8:
                point.x = Area.getInstance().getBaseStationAt(3).getPoint().x + Math.abs(Area.getInstance().getBaseStationAt(3).getPoint().x - Area.getInstance().getBaseStationAt(4).getPoint().x) / 2;
                point.y = Area.getInstance().getBaseStationAt(3).getPoint().y + Math.abs(Area.getInstance().getBaseStationAt(3).getPoint().y - Area.getInstance().getBaseStationAt(4).getPoint().y) / 2;
                break;

            case 9:
                point.x = Area.getInstance().getBaseStationAt(4).getPoint().x - Math.abs(Area.getInstance().getBaseStationAt(4).getPoint().x - Area.getInstance().getBaseStationAt(5).getPoint().x) / 2;
                point.y = Area.getInstance().getBaseStationAt(4).getPoint().y + Math.abs(Area.getInstance().getBaseStationAt(4).getPoint().y - Area.getInstance().getBaseStationAt(5).getPoint().y) / 2;
                break;

            case 10:
                point.x = Area.getInstance().getBaseStationAt(5).getPoint().x - Math.abs(Area.getInstance().getBaseStationAt(5).getPoint().x - Area.getInstance().getBaseStationAt(6).getPoint().x) / 2;
                point.y = Area.getInstance().getBaseStationAt(5).getPoint().y - Math.abs(Area.getInstance().getBaseStationAt(5).getPoint().y - Area.getInstance().getBaseStationAt(6).getPoint().y) / 2;
                break;

            case 11:
                point.x = Area.getInstance().getBaseStationAt(6).getPoint().x + Math.abs(Area.getInstance().getBaseStationAt(6).getPoint().x - Area.getInstance().getBaseStationAt(7).getPoint().x) / 2;
                point.y = Area.getInstance().getBaseStationAt(6).getPoint().y - Math.abs(Area.getInstance().getBaseStationAt(6).getPoint().y - Area.getInstance().getBaseStationAt(7).getPoint().y) / 2;
                break;

            case 12:
                point.x = Area.getInstance().getBaseStationAt(7).getPoint().x + Math.abs(Area.getInstance().getBaseStationAt(7).getPoint().x - Area.getInstance().getBaseStationAt(2).getPoint().x) / 2;
                point.y = Area.getInstance().getBaseStationAt(7).getPoint().y - Math.abs(Area.getInstance().getBaseStationAt(7).getPoint().y - Area.getInstance().getBaseStationAt(2).getPoint().y) / 2;
                break;

            default:
                Random rand = new Random(System.currentTimeMillis());
        
                System.nanoTime();
                point.x = rand.nextInt(Area.getInstance().getWidth() - Area.X) + Area.X;
                point.y = rand.nextInt(Area.getInstance().getHeight() - Area.Y) + Area.Y;
                break;
        }

    }

    public Point getPoint() {
        return point;
    }

    public void paint(Graphics g) {
        g.drawOval(point.x - radius, point.y - radius, radius * 2, radius * 2);
    }

    public boolean isContains(Point p) {
        if (Math.abs(p.x - point.x) <= radius && Math.abs(p.y - point.y) <= radius) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "AccessPoint\tid[" + id + "]\tPossition[" + point.x + "," + point.y + "]\tRadius[" + radius + "]";
    }
}
