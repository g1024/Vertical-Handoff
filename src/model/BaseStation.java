/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.awt.Graphics;
import java.awt.Point;
import java.util.Random;

/**
 *
 * @author bani hani
 */
public class BaseStation {
private Point point;
private int radius;
private int id;
public int capacity = 40;
public int numOfConnectedUser = 0;
public int numOfSNRConnectedUser = 0;
private static final int INTERENCE = 60;

public BaseStation(int id, int radius){
    this.id = id;
    this.radius = radius;
    point = new Point();
    getPoint(id);
}
public int getId(){
    return id;
}

public int getradius(){
return radius;
}

public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
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
    
    
    

public boolean isContains(Point p){
    if(Math.abs(p.x - point.x) <= radius && Math.abs(p.y - point.y) <= radius){
        return true;
    }
    return false;
}

public void getPoint(int id){
    switch(id)
    {
        case 1://Center
            point.x = Area.getInstance().getHCenter();
            point.y = Area.getInstance().getVCenter();
            break;

        case 2://Top
            point.x = Area.getInstance().getHCenter();
            point.y = Area.getInstance().getVCenter() - radius*2 + INTERENCE;
            break;

        case 3://Top Right
            point.x = (Area.getInstance().getHCenter()) + (int)((2*radius - INTERENCE) * Math.cos(30 * Math.PI / 180));
            point.y = (Area.getInstance().getVCenter()) - (int)((2*radius - INTERENCE) * Math.sin(30 * Math.PI / 180));
            break;

        case 4://Bottom Right
            point.x = (Area.getInstance().getHCenter()) + (int)((2*radius - INTERENCE) * Math.cos(30 * Math.PI / 180));
            point.y = (Area.getInstance().getVCenter()) + (int)((2*radius - INTERENCE) * Math.sin(30 * Math.PI / 180));
            break;

        case 5://Bottom
            point.x = Area.getInstance().getHCenter();
            point.y = Area.getInstance().getVCenter() + radius*2 - INTERENCE;
            break;

        case 6://Bootom Left
            point.x = (Area.getInstance().getHCenter()) - (int)((2*radius - INTERENCE) * Math.cos(30 * Math.PI / 180));
            point.y = (Area.getInstance().getVCenter()) + (int)((2*radius - INTERENCE) * Math.sin(30 * Math.PI / 180));
            break;

        case 7://Top Left
            point.x = (Area.getInstance().getHCenter()) - (int)((2*radius - INTERENCE) * Math.cos(30 * Math.PI / 180));
            point.y = (Area.getInstance().getVCenter()) - (int)((2*radius - INTERENCE) * Math.sin(30 * Math.PI / 180));
            break;
            
        default:
            Random rand = new Random(System.nanoTime());
            
            point.x = rand.nextInt(Area.getInstance().getWidth() - Area.X) + Area.X;
            point.y = rand.nextInt(Area.getInstance().getHeight() - Area.Y) + Area.Y;
            break;
       
    }
    
}

public Point getPoint(){
    return point;
}

public void paint(Graphics g){
    g.drawOval(point.x - radius, point.y - radius, radius*2, radius*2);

}

 @Override
    public String toString() {
        return "BaseStation\tid["+id+"]\tPossition["+point.x+","+point.y+"]\tRadius["+radius+"]";
    }
}
