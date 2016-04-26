/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bani hani
 */
public class UserCanvas implements Runnable{

    Graphics g;
    public static boolean exit = false;
    public static final int DELAY = 10;
    public boolean isPaint = false;
    public boolean finishedtesting = false;
    
    public UserCanvas(Graphics g) {
        this.g = g;
//        this.run();
    }



    public void run() {
        Long endTime = System.currentTimeMillis() + Area.testingTime + Area.trainingTime;
        while(System.currentTimeMillis() < endTime)
        {
            Long startComputeTime = System.currentTimeMillis();
             for(int i = 1; i <= Area.mobileNo; i++)
            {
//                 UserSimulation.users[i].changeAngleDir();
                 Area.getInstance().getMobileUserAt(i).move();
                 //Debug
//                 System.err.println(Area.getInstance().getMobileUserAt(i).toString());
                 paint(i);
                 
//                 long t = System.currentTimeMillis();
//           while(System.currentTimeMillis() - t < 10){}
             }
             
             long t = System.currentTimeMillis();
//             Thread.yield();
           while(System.currentTimeMillis() - t < DELAY){}
             
             //remove compute time
//             endTime += System.currentTimeMillis() - startComputeTime;
//             System.err.println("end time after   "+(endTime - System.currentTimeMillis()));
        }
        
        
//<editor-fold desc="//">
        System.err.println("");
        System.err.println("----------------------------------------");                                                                                                      
        System.err.println("My HO = "+Mobile.numMyHO);
        System.err.println("My Dropped = "+((Mobile.numMyDroped)));
        System.err.println("SNR HO = "+Mobile.numSNRHO);
        System.err.println("SNR Dropped = "+((Mobile.numSNRDroped )));
        System.err.println("----------------------------------------");
        //</editor-fold>
    }

    private void paint(int idx){

        if(!isPaint)
        {
            //Draw grid
            g.setColor(new Color(1.0F, 0.3F, 0.3F, 0.2F));
            int x = 0, y = 0;
            for(int i = 0; i <= Area.getColls(); i++)
            {
                x = Area.getCellWidth() * i + Area.X;
                g.drawLine(x, 0, x, Area.getInstance().getHeight());
            }

             for(int i = 0; i <= Area.getRows(); i++)
            {
                y = Area.getCellWidth() * i + Area.Y;
                g.drawLine(0, y, Area.getInstance().getWidth(), y);
            }
            isPaint = true;
        }

        g.setColor(Color.blue);
        for(int i = 1; i <= Area.BASE_STATION_NO; i++)
        {
            Area.getInstance().getBaseStationAt(i).paint(g);
        }

        g.setColor(Color.GREEN);
         for(int i = 1; i <= Area.ACCESS_POINT_NO; i++)
        {
            Area.getInstance().getAccessPointAt(i).paint(g);
        }

        g.setColor(Color.BLACK);
        g.drawString("S"+idx, Area.getInstance().getMobileUserAt(idx).init.x, Area.getInstance().getMobileUserAt(idx).init.y);
        g.drawOval(Area.getInstance().getMobileUserAt(idx).init.x - 1, Area.getInstance().getMobileUserAt(idx).init.y - 1, 2, 2);
        g.drawString("D"+idx, Area.getInstance().getMobileUserAt(idx).dest.x, Area.getInstance().getMobileUserAt(idx).dest.y);
        g.drawOval(Area.getInstance().getMobileUserAt(idx).dest.x, Area.getInstance().getMobileUserAt(idx).dest.y, 2, 2);
            switch(idx%10)
            {
                case 0:
                    g.setColor(Color.red);
                    break;
                case 1:
                    g.setColor(Color.BLUE);
                    break;
                case 2:
                     g.setColor(Color.CYAN);
                     break;
                case 3:
                     g.setColor(Color.GREEN);
                     break;
                case 4:
                     g.setColor(Color.MAGENTA);
                     break;
                case 5:
                     g.setColor(Color.ORANGE);
                     break;
                case 6:
                     g.setColor(Color.PINK);
                     break;
                case 7:
                     g.setColor(Color.YELLOW);
                     break;
                case 8:
                    g.setColor(Color.BLACK);
                    break;
                case 9:
                    g.setColor(Color.darkGray);
                    break;
            }
//            g.fillRect((int)Area.getInstance().getMobileUserAt(idx).getpoint().getX(), (int)Area.getInstance().getMobileUserAt(idx).getpoint().getY(), 2, 2);
            g.drawLine((int)Area.getInstance().getMobileUserAt(idx).prePoint.x, (int)Area.getInstance().getMobileUserAt(idx).prePoint.y, (int)Area.getInstance().getMobileUserAt(idx).getpoint().getX(), (int)Area.getInstance().getMobileUserAt(idx).getpoint().getY());
                                                                                                                                                                                                                                                                                                                                                               if (finishedtesting)    {
        System.err.println("");
        System.err.println("----------------------------------------");                                                                                                      
        System.err.println("My HO = "+Mobile.numMyHO);
        System.err.println("My Dropped = "+((int)(Mobile.numMyDroped)));
        System.err.println("SNR HO = "+Mobile.numSNRHO);
        System.err.println("SNR Dropped = "+((int)(Mobile.numSNRDroped )));
        System.err.println("----------------------------------------");
                                                                                                                                            }

    }



    private int getCircleRectXPoint(int x, int radius){
        return x - radius;
    }

    private int getCircleRectYPoint(int y, int radius){
        return y - radius;
    }

}
