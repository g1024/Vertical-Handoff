/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.awt.Point;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

/**
 *
 * @author bani hani
 */
// 10*log10(P2/P1)
public class Mobile {

    private int velocity;
    private Point point;
    public Point prePoint;
    private int radius;
    private double sinr;
    private static final double SINR_BS_THRESHOLD = -80;
    private static final double SINR_AP_THRESHOLD = -110;
//    private int oldAngle;
    private double angle;
    private int angleSum = 0;//compute sum angles changes
    private int angleChangeCounter = 0;//to compute avgAngle
    private static final int ANGLE_RANGE = 60;
    private int changeProbability;
    private Object connectTO;
    private Object snrConnectTo;
    public static int numMyHO = 0;
    public static int numSNRHO = 0;
    public static int numMyDroped = 0;
    public static int numSNRDroped = 0;
    private Vector history = new Vector(40);//slice window
    public static final int destProb = 25;
    Vector destination = new Vector();
    private int destIdx = 1;
    private static final int DEST_SIZE = 40;
    public Point init = null;
    public Point src = null;
    public Point dest = null;
    long idleTime;
    public static final long IDLE_TIME = 3000L;
    private int state;
    public static final int STATE_IDLE = 0;
    public static final int STATE_MOVE = 1;
    private int sysMode;
    private static final int STATE_LEARNING = 0;
    private static final int STATE_TESTING = 1;
    public int curentDiriction = 0;
    public static final int NORTH = 0;
    public static final int EAST = 1;
    public static final int SOUTH = 2;
    public static final int WEST = 3;
    private boolean mustChangeDiriction = false;
    private Hashtable sidTable;
    private int[] path = new int[]{-1, -1, -1, -1};
    private boolean ft = false;//Fault Tolerance Scheme
    private Long testingTime;

    public Mobile(int velocity, Point point, int radius, int changeProbability) {
//        System.err.println("sin(angle) = 0.5  >> angle = "+Math.toDegrees(Math.asin(0.5)));
        this.velocity = velocity;
//        this.point = point;
        this.radius = radius;
        this.changeProbability = changeProbability;
        sysMode = STATE_LEARNING;
        
        initSIDTable();

        Random rand = new Random(System.currentTimeMillis());
        src = /*point;*/ new Point(rand.nextInt(Area.getInstance().getWidth() - Area.X) + Area.X, rand.nextInt(Area.getInstance().getHeight() - Area.Y) + Area.Y);
        src = Util.getCellCenter(Util.getSidAtCords(src));
        init = (Point) src.clone();
        this.point = (Point) src.clone();
        prePoint = (Point) point.clone();
        waits(1L);
        for (int i = 0; i < DEST_SIZE - 3; i++) {
            if (i == 0) {
                destination.add(init);
            } else {
                destination.add(Util.getCellCenter(Util.getSidAtCords(new Point(rand.nextInt(Area.getInstance().getWidth() - Area.X) + Area.X, rand.nextInt(Area.getInstance().getHeight() - Area.Y) + Area.Y))));
            }                                                                                                                                                                                                                                                            destination.add(Util.getCellCenter(Util.getSidAtCords(new Point(150, 200))));
                                                                                                                                                                                                                                                                          destination.add(Util.getCellCenter(Util.getSidAtCords(new Point(400, 250))));
                                                                                                                                                                                                                                                                          destination.add(Util.getCellCenter(Util.getSidAtCords(new Point(200, 400))));

        }

        history.setSize(40);
//        dest = new Point(rand.nextInt(Area.getInstance().getWidth() - Area.X) + Area.X, rand.nextInt(Area.getInstance().getHeight() - Area.Y) + Area.Y);
        getDest(destProb);
        changeHistory(dest);
        state = STATE_MOVE;

        double maxRSS = Integer.MIN_VALUE;
        for (int i = 1; i <= Area.BASE_STATION_NO; i++) {
            if (Area.getInstance().baseStations[i].isContains(src)) {
                double rss = RSSCN.RSS(EuclideanDistance.distance(src, Area.getInstance().baseStations[i].getPoint()));
                if (rss > maxRSS) {
                    maxRSS = rss;
                    connectTO = Area.getInstance().baseStations[i];
                    snrConnectTo = Area.getInstance().baseStations[i];
                }
            }
        }
        for (int i = 1; i <= Area.ACCESS_POINT_NO; i++) {
            if (Area.getInstance().accessPoints[i].isContains(src)) {
                double rss = RSSWL.RSS(EuclideanDistance.distance(src, Area.getInstance().accessPoints[i].getPoint()));
                if (rss > maxRSS) {
                    maxRSS = rss;
                    connectTO = Area.getInstance().accessPoints[i];
                    snrConnectTo = Area.getInstance().accessPoints[i];

                }
            }
        }

        //Debug
//        connectTO = Area.getInstance().accessPoints[1];

//        System.err.println("Init Connect to >> "+connectTO.toString());

        getsinr(connectTO, connectTO);

        System.err.println("STATE_LEARNING :: Starting ...");
        testingTime = System.currentTimeMillis() + Area.trainingTime;

    }

    public int getvelocity() {
        return velocity;
    }

    public Point getpoint() {
        return point;
    }

    public int getradius() {
        return radius;
    }

    public double getsinr(Object obj, Object connectTo) {
        double rss1 = 0, rss2 = 0;
//        System.err.println("Connect to >> "+connectTO.toString());
        if (obj instanceof BaseStation) {
            rss1 = RSSCN.RSS(EuclideanDistance.distance(((BaseStation) obj).getPoint(), point));
//            System.err.println("RSSCN = "+rss1);
            rss2 -= rss1;
        } else if (obj instanceof AccessPoint) {
            rss1 = RSSWL.RSS(EuclideanDistance.distance(((AccessPoint) obj).getPoint(), point));
//            System.err.println("RSSWL = "+rss1);
            rss2 -= rss1;
        }
        if (connectTo instanceof BaseStation) {
            for (int i = 1; i <= Area.BASE_STATION_NO; i++) {
//                if(Area.getInstance().baseStations[i].isContains(point))
//                {
                rss2 += RSSCN.RSS(EuclideanDistance.distance(point, Area.getInstance().baseStations[i].getPoint()));

//                }
            }
        } else if (connectTo instanceof AccessPoint) {
            for (int i = 1; i <= Area.ACCESS_POINT_NO; i++) {
//                if(Area.getInstance().accessPoints[i].isContains(point))
//                {
                rss2 += RSSWL.RSS(EuclideanDistance.distance(point, Area.getInstance().accessPoints[i].getPoint()));

//                }
            }
        }

        //Debug
//        System.err.println("RSS1 = "+rss1+"   RSS2 = "+rss2);
        //10*log10(P2/P1)
//        System.err.println("before rss2"+rss2);
        rss1 = Math.pow((rss1 / 10), 10);
        rss2 = Math.pow((rss2 / 10), 10);
//        System.err.println("rss1 = "+rss1);
//        System.err.println("rss2 = "+rss2);
//        sinr = ((rss1)/(Math.pow(10,(Area.N/10)) + rss2));
        sinr = ((rss1) / (Math.pow((Area.N / 10), 10) + rss2));
        sinr = 10 * Math.log10(sinr);
//        System.err.println("sinr = "+sinr+"");
        return sinr;
    }

    public double getAvgSinrAtSID(Object obj, Object connectTo, int sid) {
        double rss1 = 0, rss2 = 0;
        Point point = new Point(Util.sidToColls(sid) * Area.getCellWidth() + Area.getCellWidth() / 2, Util.sidToRows(sid) * Area.getCellHeight() + Area.getCellHeight() / 2);
//        System.err.println("Connect to >> "+connectTO.toString());
        if (obj instanceof BaseStation) {
            rss1 = RSSCN.RSS(EuclideanDistance.distance(((BaseStation) obj).getPoint(), point));
//            System.err.println("RSSCN = "+rss1);
            rss2 -= rss1;
        } else if (obj instanceof AccessPoint) {
            rss1 = RSSWL.RSS(EuclideanDistance.distance(((AccessPoint) obj).getPoint(), point));
//            System.err.println("RSSWL = "+rss1);
            rss2 -= rss1;
        }
        if (connectTo instanceof BaseStation) {
            for (int i = 1; i <= Area.BASE_STATION_NO; i++) {
//                if(Area.getInstance().baseStations[i].isContains(point))
//                {
                rss2 += RSSCN.RSS(EuclideanDistance.distance(point, Area.getInstance().baseStations[i].getPoint()));

//                }
            }
        } else if (connectTo instanceof AccessPoint) {
            for (int i = 1; i <= Area.ACCESS_POINT_NO; i++) {
//                if(Area.getInstance().accessPoints[i].isContains(point))
//                {
                rss2 += RSSWL.RSS(EuclideanDistance.distance(point, Area.getInstance().accessPoints[i].getPoint()));

//                }
            }
        }

        //Debug
//        System.err.println("RSS1 = "+rss1+"   RSS2 = "+rss2);
        //10*log10(P2/P1)
//        System.err.println("before rss2"+rss2);
        rss1 = Math.pow((rss1 / 10), 10);
        rss2 = Math.pow((rss2 / 10), 10);
//        System.err.println("rss1 = "+rss1);
//        System.err.println("rss2 = "+rss2);
//        sinr = ((rss1)/(Math.pow(10,(Area.N/10)) + rss2));
        sinr = ((rss1) / (Math.pow((Area.N / 10), 10) + rss2));
        sinr = 10 * Math.log10(sinr);
//        System.err.println("sinr = "+sinr+"");
        return sinr;
    }

    public void setConnectTo(Object obj) {
        if(connectTO instanceof BaseStation){
                     BaseStation bs = (BaseStation)connectTO;
                     bs.setNumOfConnectedUser(bs.getNumOfConnectedUser() - 1);
                     
                 }else{
                     AccessPoint as = (AccessPoint)connectTO;
                     as.setNumOfConnectedUser(as.getNumOfConnectedUser() - 1);
                     
                 }
        
        if(obj instanceof BaseStation){
                     BaseStation bs = (BaseStation)obj;
                     bs.setNumOfConnectedUser(bs.getNumOfConnectedUser() + 1);
                     if(bs.getNumOfConnectedUser() > bs.getCapacity()){
                         numMyDroped++;
                     }
                 }else{
                     AccessPoint as = (AccessPoint)obj;
                     as.setNumOfConnectedUser(as.getNumOfConnectedUser() + 1);
                     if(as.getNumOfConnectedUser() > as.getCapacity()){
                         numMyDroped++;
                     }
                 }
        connectTO = obj;
    }

    public void setSNRConnectTo(Object obj) {
        
        if(snrConnectTo instanceof BaseStation){
                     BaseStation bs = (BaseStation)snrConnectTo;
                     bs.setNumOfSNRConnectedUser(bs.getNumOfSNRConnectedUser() - 1);
                     
                 }else{
                     AccessPoint as = (AccessPoint)snrConnectTo;
                     as.setNumOfSNRConnectedUser(as.getNumOfSNRConnectedUser() - 1);
                     
                 }
        
        if(obj instanceof BaseStation){
                     BaseStation bs = (BaseStation)obj;
                     bs.setNumOfSNRConnectedUser(bs.getNumOfSNRConnectedUser() + 1);
                     if(bs.getNumOfSNRConnectedUser() > bs.getCapacity()){
                         numSNRDroped++;
                     }
                 }else{
                     AccessPoint as = (AccessPoint)obj;
                     as.setNumOfSNRConnectedUser(as.getNumOfSNRConnectedUser() + 1);
                     if(as.getNumOfSNRConnectedUser() > as.getCapacity()){
                         numSNRDroped++;
                     }
                 }
        
        
        snrConnectTo = obj;
    }

    public Object getConnectTo() {
        return connectTO;
    }

    public Object getSNRConnectTo() {
        return snrConnectTo;
    }

    public void changeHistory(Point dest) {
        if (history.contains(dest)) {
            for (int i = 0; i < history.size(); i++) {
                if (history.elementAt(i).equals(dest)) {
                    history.removeElementAt(i);
                    history.insertElementAt(dest, 0);
                    break;
                }
            }
        } else {
            history.removeElementAt(history.size() - 1);
            history.insertElementAt(dest, 0);
        }
//      Debug
//       System.err.println("history size = "+history.size());
//       for(int i = 0; i < history.size(); i++)
//       {
//           System.err.println("["+i+"] "+((Point)history.elementAt(i)));
//       }
    }

//public Vector getPatternOf(int patternLength){
//        Vector pattern = new Vector();
//        for(int i = history.size() - patternLength; i < history.size(); i++)
//        {
//            pattern.add(history.elementAt(i));
//        }
//        return pattern;
//   }
    private boolean changeDirection() {
        Random rand = new Random(System.currentTimeMillis());
        if (rand.nextInt(100) >= changeProbability) {
            return false;
        }

        return true;
    }

    public int getState() {
        return state;
    }

    //prob from 0-100 probability take destination from destination vector
    public Point getDest(int prob) {
        Random rand = new Random(System.currentTimeMillis());
        Point preDest = null;
        if (dest != null) {
            preDest = (Point) dest.clone();
        }

        if (rand.nextInt(100) <= 100/*prob*/) {
//           dest = (Point)destination.elementAt(rand.nextInt(destination.size()));
            dest = (Point) destination.elementAt((destIdx++) % destination.size());
        } else {
            dest = new Point(Util.getCellCenter(Util.getSidAtCords(rand.nextInt(Area.getInstance().getWidth() - Area.X) + Area.X, rand.nextInt(Area.getInstance().getHeight() - Area.Y) + Area.Y)));
        }

        if (preDest != null && Util.getSidAtCords(preDest) == Util.getSidAtCords(dest)) {
            return getDest(prob);
        }

        changeHistory(dest);
        angleSum = 0;
        angleChangeCounter = 0;
        return dest;
    }

    private void pathWayModel() {

//        System.err.println("point  = " + point.toString());
//        System.err.println("dest = " + dest.toString());
        if (curentDiriction == 0) {
            if (dest.x > point.x) {
                curentDiriction = EAST;
            } else if (dest.x < point.x) {
                curentDiriction = WEST;
            } else if (dest.y > point.y) {
                curentDiriction = SOUTH;
            } else if (dest.y < point.y) {
                curentDiriction = NORTH;
            } else {
                return;
            }
        } else {
            if (!changeDirection()) {
                switch (curentDiriction) {
                    case NORTH:
                        if (dest.y >= point.y) {
                            mustChangeDiriction = true;
                        }
                        break;

                    case EAST:
                        if (dest.x <= point.x) {
                            mustChangeDiriction = true;
                        }
                        break;

                    case SOUTH:
                        if (dest.y <= point.y) {
                            mustChangeDiriction = true;
                        }
                        break;

                    case WEST:
                        if (dest.x >= point.x) {
                            mustChangeDiriction = true;
                        }
                        break;
                }
            }

            if (changeDirection() || mustChangeDiriction) {
                int right = (curentDiriction + 1) % 4;
                int left = (curentDiriction + 3) % 4;

                switch (left + right) {
                    case WEST + EAST:
                        if (dest.x > point.x) {
                            curentDiriction = EAST;
                        } else if (dest.x < point.x) {
                            curentDiriction = WEST;
                        }
                        break;

                    case SOUTH + NORTH:
                        if (dest.y > point.y) {
                            curentDiriction = SOUTH;
                        } else if (dest.y < point.y) {
                            curentDiriction = NORTH;
                        }
                        break;
                }

                mustChangeDiriction = false;
            }

        }

        prePoint = (Point) point.clone();


        switch (curentDiriction) {
            case NORTH:
                point.y -= Area.getCellHeight();
                break;

            case SOUTH:
                point.y += Area.getCellHeight();
                break;

            case EAST:
                point.x += Area.getCellWidth();
                break;

            case WEST:
                point.x -= Area.getCellWidth();
                break;
        }

//        switch (curentDiriction) {
//            case NORTH:
//                System.err.println("NORTH");
//                break;
//
//            case SOUTH:
//                System.err.println("SOUTH");
//                break;
//
//            case EAST:
//                System.err.println("EAST");
//                break;
//
//            case WEST:
//                System.err.println("WEST");
//                break;
//        }
        updatePath(prePoint);
        if (path[1] != -1) {
//            printMArray(path[1]);
        }

    }

    private void RandomWayPoint() {
        if (changeDirection()) {

            int square = 0;
            int x = dest.x - point.x;
            int y = point.y - dest.y;

            if (x > 0 && y > 0) {
                square = 1;
            } else if (x < 0 && y > 0) {
                square = 2;
            } else if (x < 0 && y < 0) {
                square = 3;
            } else {
                square = 4;
            }

            Random rand = new Random(System.currentTimeMillis());
            int index = rand.nextInt(4);//0=>+30 , 1=>+60 , 2=>-30 , 3=>-60
            if (index % 4 == 0) {
                index = 3;
            } else if (index % 3 == 0) {
                index = 2;
            } else if (index % 2 == 0) {
                index = 1;
            } else {
                index = 0;
            }

            switch (index) {
                case 2://+30
                    angle = 30 + ((square - 1) * 90);
                    break;

                case 0://+60
                    angle = 60 + ((square - 1) * 90);
                    break;

                case 3://-30
                    angle = 90 + ((square - 1) * 90);
                    break;

                case 1://-60
                    angle = 0 + ((square - 1) * 90);
                    break;

            }//end switch

        }//end if

        angleChangeCounter++;
        angleSum += angle;

        //Debug
//       int avgAngle = (angleChangeCounter == 0) ? angleSum : (angleSum / angleChangeCounter);
//       System.err.println("Angle = "+angle);
//       System.err.println("avgAngle = "+avgAngle);
    }

    private void myRandomWayPoint() {


        int square = 0;
        int x = dest.x - point.x;
        int y = point.y - dest.y;

        if (x >= 0 && y >= 0) {
            square = 1;
            angle = 0 + Math.toDegrees(Math.acos(Math.abs(x) / ((EuclideanDistance.distance2(dest, point)))));
        } else if (x <= 0 && y >= 0) {
            square = 2;
            angle = 180 - Math.toDegrees(Math.acos(Math.abs(x) / ((EuclideanDistance.distance2(dest, point)))));
        } else if (x <= 0 && y <= 0) {
            square = 3;
            angle = 180 + Math.toDegrees(Math.acos(Math.abs(x) / ((EuclideanDistance.distance2(dest, point)))));
        } else {
            square = 4;
            angle = 360 - Math.toDegrees(Math.acos(Math.abs(x) / ((EuclideanDistance.distance2(dest, point)))));
        }

        System.err.println("##################### Q = " + square);
        System.err.println("@@@@@@@@@@@@@@@@@@@ src = " + src.toString());
        System.err.println("@@@@@@@@@@@@@@@@@@@ dist = " + dest.toString());
        System.err.println("xxxxxxxxxxxxxxxxxxx mobile point " + point.toString());
//           angle = 90 - Math.toDegrees(Math.acos(Math.abs(x) / ((EuclideanDistance.distance(dest, src))*1000)));
        System.err.println("----------------- angle " + angle);
//           angle += ((square - 1) * 90);
//           System.err.println("+++++++++++++++++ angle "+angle);


        angleChangeCounter++;
        angleSum += angle;

        //Debug
//       int avgAngle = (angleChangeCounter == 0) ? angleSum : (angleSum / angleChangeCounter);
//       System.err.println("Angle = "+angle);
//       System.err.println("avgAngle = "+avgAngle);

        point.x += 4 * (velocity * Math.cos(angle * Math.PI / 180));
        point.y -= 4 * (velocity * Math.sin(angle * Math.PI / 180));
    }

    //<editor-fold desc=" Comment ">
   /*
    public void changeAngleDir(){
    if(changeDirection()){
    Random rand = new Random(System.currentTimeMillis());
    int index = rand.nextInt(4);//0=>+30 , 1=>+60 , 2=>-30 , 3=>-60
    if(index % 4 == 0){
    index = 3;
    }else if(index % 3 == 0){
    index = 2;
    }else if(index % 2 == 0){
    index = 1;
    }else{
    index = 0;
    }
    oldAngle = angle;
    switch(index)
    {
    case 2://+30
    angle = (angle + 30) % 360;
    break;

    case 0://+60
    angle = (angle + 60) % 360;
    break;

    case 3://-30
    angle = (angle - 30 + 360) % 360;
    break;

    case 1://-60
    angle = (angle - 60 + 360) % 360;
    break;

    }
    }
    }
     */
    //</editor-fold>
    public void move() {
       
        if (System.currentTimeMillis() >= testingTime && sysMode != STATE_TESTING) {
            sysMode = STATE_TESTING;
            System.err.println("STATE_TESTING :: Starting ...");
        }

        if (Util.getSidAtCords(point) == Util.getSidAtCords(dest)) {
//           Random rand = new Random(System.currentTimeMillis());
//            src = point;//new Point(rand.nextInt(500), rand.nextInt(500));
//            dist = new Point(rand.nextInt(500), rand.nextInt(500));
//            long time = System.currentTimeMillis();
//            while(System.currentTimeMillis() - time <= 5000)
//            {}
            if (state == STATE_MOVE) {
                state = STATE_IDLE;
                Random rand = new Random(System.currentTimeMillis() * System.nanoTime());

                src = new Point(point);//new Point(rand.nextInt(500), rand.nextInt(500));
//               dest = new Point(rand.nextInt(Area.getInstance().getWidth() - Area.X) + Area.X, rand.nextInt(Area.getInstance().getHeight() - Area.Y) + Area.Y);
                getDest(destProb);
                idleTime = System.currentTimeMillis();
            } else {
                curentDiriction = 0;
                state = STATE_MOVE;
            }
//            System.err.println("=================================================");
        }

        if (state == STATE_IDLE && System.currentTimeMillis() - idleTime < IDLE_TIME) {
            return;
        }
//       changeAngleDir();

//       RandomWayPoint();
//      myRandomWayPoint();
        pathWayModel();
//       System.err.println("-----------------------------------------------------");
//       System.err.println("X = "+point.x+"  Y = "+point.y + "  angle = "+oldAngle);

        //       for(int i = 0; i < 4; i++){
//           if(Math.abs(point.x - dist.x) > Math.abs((4*(velocity * Math.cos(angle*Math.PI/180)))- dist.x)){
//               angle = ((angle+90)%360);
//           }
//
//           if(Math.abs(point.y - dist.y) > Math.abs((4*(velocity * Math.cos(angle*Math.PI/180)))- dist.y)){
//               angle = ((angle+90)%360);
//           }
//       }



        getsinr(connectTO, connectTO);

        if (sysMode == STATE_TESTING) {
            if (AIHODecision()) {
                //           waits(5000);
                //           System.err.println("HO :: Connect to >> "+connectTO.toString());
                numMyHO++;
                System.err.println("Hand Off ...");
            }

            if (HOSNRDecision()) {
                //           waits(5000);
                //           System.err.println("HO SNR :: Connect to >> "+connectTO.toString());
                numSNRHO++;
                System.err.println("SNR Hand Off ...");
            }
        }
        
//        System.err.println("getsinr("+snrConnectTo+", "+snrConnectTo+") = "+getsinr(snrConnectTo, snrConnectTo));
//       System.err.println("X = "+point.x+"  Y = "+point.y + "  angle = "+angle);

    }

    public boolean HODecision() {
//       System.err.println("sinr = "+sinr);
        double threshold = 0;
        if (connectTO instanceof BaseStation) {
            threshold = SINR_BS_THRESHOLD;
        } else if (connectTO instanceof AccessPoint) {
            threshold = SINR_AP_THRESHOLD;
        } else {
            System.err.println("Error :: Connect To = NULL");
        }
        if (getsinr(connectTO, connectTO) > threshold) {
            System.err.println("Hand Off ...");
            int avgAngle = (angleChangeCounter == 1) ? angleSum : (angleSum / angleChangeCounter);
//           System.err.println("avgAngle = "+avgAngle);

            Vector availableSNROptions = getAvailableSNROptions(threshold);
//           System.err.println("\navailableSNROptions:");
//           printVector(availableSNROptions);

            Vector availableRangeOptions = getAvailableRangeOptions(avgAngle, ANGLE_RANGE);
//           System.err.println("\navailableRangeOptions:");
//           printVector(availableRangeOptions);

            Vector concatenationRangeAndSNR = getConcatenatRangeAndSNR(availableRangeOptions, availableSNROptions);
//           System.err.println("\nconcatenationRangeAndSNR:");
//           printVector(concatenationRangeAndSNR);

            Point bestDest = getBestDest(availableRangeOptions);
            //check BS or AP that neer to distination
            int minDest = Integer.MAX_VALUE;
            int maxSNR = Integer.MIN_VALUE;
            int idx = -1;

            Vector option;
            if (concatenationRangeAndSNR.size() == 0) {
                option = availableSNROptions;
            } else {
                option = concatenationRangeAndSNR;
            }

            for (int i = 0; i < option.size(); i++) {

                if (option.elementAt(i) instanceof BaseStation) {
                    if (bestDest == null)//Decision upon SINR
                    {
                        if (getsinr((BaseStation) option.elementAt(i), connectTO) > maxSNR) {
                            idx = i;
                        }
                    } else {
                        if (EuclideanDistance.distance(((BaseStation) option.elementAt(i)).getPoint(), point) < minDest) {
                            idx = i;
                        }
                    }

                } else if (option.elementAt(i) instanceof AccessPoint) {
                    if (bestDest == null)//Decision upon SINR
                    {
                        if (getsinr((AccessPoint) option.elementAt(i), connectTO) > maxSNR) {
                            idx = i;
                        }
                    } else {
                        if (EuclideanDistance.distance(((AccessPoint) option.elementAt(i)).getPoint(), point) < minDest) {
                            idx = i;
                        }
                    }
                }
            }//end for loop

            if (idx != -1) {
                setConnectTo(option.elementAt(idx));
                return true;
            } else {
//               System.err.println("ERROR :: OPTION = NULL");
                return false;
            }

        }

        return false;
    }

    public boolean AIHODecision() {

        double threshold = 0;
        if (connectTO instanceof BaseStation) {
            threshold = SINR_BS_THRESHOLD;
        } else if (connectTO instanceof AccessPoint) {
            threshold = SINR_AP_THRESHOLD;
        } else {
            System.err.println("AIHODecision() >> Error :: Connect To = NULL");
        }

        if (getsinr(connectTO, connectTO) >= threshold) {
            

            int predictionArray[][] = predict(new int[]{path[2], path[3]});
            int fp1 = predictionArray[0][0];//first predicted location 1
            int fp2 = predictionArray[0][1];//first  predicted location 2
            int sp1 = predictionArray[1][0];//second  predicted location 1
            int sp2 = predictionArray[1][1];//second  predicted location 2


//            System.err.println("fp1 = "+fp1);
//            System.err.println("fp2 = "+fp2);
            Vector coverage;
            if (ft) {
                coverage = getCoverageBSAndAPAtAll(new int[]{fp1, fp2, sp1, sp2});
                if (coverage == null || isContainAP(coverage) == -1) {
                    coverage = getCoverageBSAndAPAtAll(new int[]{fp1, fp2, sp1});
                    if (coverage == null || isContainAP(coverage) == -1) {
                        coverage = getCoverageBSAndAPAtAll(new int[]{fp1, sp1});
                        if (coverage == null || isContainAP(coverage) == -1) {
                            coverage = getCoverageBSAndAPAtAll(new int[]{fp1});
                            if (coverage == null || isContainAP(coverage) == -1) {
                                coverage = getCoverageBSAndAPAtAll(new int[]{fp1, fp2, sp1, sp2});
                                if (coverage == null) {
                                    coverage = getCoverageBSAndAPAtAll(new int[]{fp1, fp2, sp1});
                                    if (coverage == null) {
                                        coverage = getCoverageBSAndAPAtAll(new int[]{fp1, sp1});
                                        if (coverage == null) {
                                            coverage = getCoverageBSAndAPAtAll(new int[]{fp1});
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                } else {
                    coverage = getCoverageBSAndAPAtAll(new int[]{fp1, fp2});
                    if (coverage == null || isContainAP(coverage) == -1) {
                        coverage = getCoverageBSAndAPAtAll(new int[]{fp1});
                        if (coverage == null || isContainAP(coverage) == -1) {
                            coverage = getCoverageBSAndAPAtAll(new int[]{fp1, fp2});
                            if (coverage == null) {
                                coverage = getCoverageBSAndAPAtAll(new int[]{fp1});
                            }
                        }
                    }
                }//end if-else

            if(coverage == null || coverage.size() == 0){
//                System.err.println("fp1 = "+fp1);
//                System.err.println("fp2 = "+fp2);
            }

            int idx = isContainAP(coverage);
            if(idx != -1){//connect to AP
                if(connectTO.equals(coverage.elementAt(idx))){
                    return false;
                }
                setConnectTo(coverage.elementAt(idx));
                
                 
                
                
                return true;
            }else{//connect to BS
                 if(connectTO.equals(coverage.elementAt(0))){
                    return false;
                }
           
                 
                setConnectTo(coverage.elementAt(0));
                return true;
            }
            



        }

        return false;
    }

   
    public boolean HOSNRDecision() {
//       System.err.println("sinr = "+sinr);
        double threshold = 0;
        if (snrConnectTo instanceof BaseStation) {
            threshold = SINR_BS_THRESHOLD;
        } else if (snrConnectTo instanceof AccessPoint) {
            threshold = SINR_AP_THRESHOLD;
        } else {
            System.err.println("HOSNRDecision() >> Error :: Connect To = NULL");
        }
        if (getsinr(snrConnectTo, snrConnectTo) >= threshold) {
            

            Vector availableSNROptions = getAvailableSNROptions(threshold);
//          
            int bestSNR = Integer.MAX_VALUE;
            int idx = -1;

            Vector option;

            option = availableSNROptions;
            
            if(option.size() == 0){
                System.err.println("availableSNROptions = NULL");
            }


            for (int i = 0; i < option.size(); i++) {

                if (option.elementAt(i) instanceof BaseStation) {

                    if (getsinr((BaseStation) option.elementAt(i), snrConnectTo) < bestSNR) {
                        idx = i;
                    }


                } else if (option.elementAt(i) instanceof AccessPoint) {

                    if (getsinr((AccessPoint) option.elementAt(i), snrConnectTo) < bestSNR) {
                        idx = i;
                    }
                }
            }//end for loop

            if (idx != -1) {
//                if(option.elementAt(idx).equals(snrConnectTo)){return false;}
                setSNRConnectTo(option.elementAt(idx));
                
                 
                
                return true;
            } else {
//               System.err.println("ERROR :: OPTION = NULL");
                return true;
            }

        }

        return false;
    }

    private Vector getAvailableSNROptions(double SINR) {
        Vector ret = new Vector();
        //check BS
        for (int i = 1; i <= Area.BASE_STATION_NO; i++) {
//           System.err.print("BS "+i+" ");
            if (getsinr(Area.getInstance().baseStations[i], connectTO) < SINR_BS_THRESHOLD) {
                ret.add(Area.getInstance().baseStations[i]);
            }
        }

        //check AP
        for (int i = 1; i <= Area.ACCESS_POINT_NO; i++) {
//           System.err.print("AP "+i+" ");
            if (getsinr(Area.getInstance().accessPoints[i], connectTO) < SINR_AP_THRESHOLD) {
                ret.add(Area.getInstance().accessPoints[i]);
            }
        }
//       System.err.println("AvailableSNROptions = "+ret.size());
        return ret;

    }

    private Vector getAvailableRangeOptions(int angle, int range) {
        range = range / 2;
        Vector ret = new Vector();
        //check BS
        for (int i = 1; i <= Area.BASE_STATION_NO; i++) {
            //chech center
//           System.err.println("behaind   "+(Math.abs(Area.getInstance().baseStations[i].getPoint().x - point.x)));
//           System.err.println("r = "+EuclideanDistance.distance(Area.getInstance().baseStations[i].getPoint(), point));
//           System.err.println(":(   "+(Math.abs(Area.getInstance().baseStations[i].getPoint().x - point.x) / (EuclideanDistance.distance(Area.getInstance().baseStations[i].getPoint(), point))));
            double BSAngle = Math.toDegrees(Math.acos(Math.abs(Area.getInstance().baseStations[i].getPoint().x - point.x) / (EuclideanDistance.distance(Area.getInstance().baseStations[i].getPoint(), point)) * 1000));
            if (BSAngle >= angle - range && BSAngle <= angle + range) {
                ret.addElement(Area.getInstance().baseStations[i]);
                continue;
            }

            //check top
            BSAngle = Math.toDegrees(Math.asin(Math.abs(Area.getInstance().baseStations[i].getPoint().y + Area.getInstance().baseStations[i].getradius() - point.y) / (EuclideanDistance.distance(new Point(Area.getInstance().baseStations[i].getPoint().x, Area.getInstance().baseStations[i].getPoint().y + Area.getInstance().baseStations[i].getradius()), point) * 1000)));
            if (BSAngle >= angle - range && BSAngle <= angle + range) {
                ret.addElement(Area.getInstance().baseStations[i]);
                continue;
            }

            //check bottom
            BSAngle = Math.toDegrees(Math.asin(Math.abs(Area.getInstance().baseStations[i].getPoint().y - Area.getInstance().baseStations[i].getradius() - point.y) / (EuclideanDistance.distance(new Point(Area.getInstance().baseStations[i].getPoint().x, Area.getInstance().baseStations[i].getPoint().y - Area.getInstance().baseStations[i].getradius()), point) * 1000)));
            if (BSAngle >= angle - range && BSAngle <= angle + range) {
                ret.addElement(Area.getInstance().baseStations[i]);
                continue;
            }

            //check left
            BSAngle = Math.toDegrees(Math.acos(Math.abs(Area.getInstance().baseStations[i].getPoint().x + Area.getInstance().baseStations[i].getradius() - point.x) / (EuclideanDistance.distance(new Point(Area.getInstance().baseStations[i].getPoint().x + Area.getInstance().baseStations[i].getradius(), Area.getInstance().baseStations[i].getPoint().y), point) * 1000)));
            if (BSAngle >= angle - range && BSAngle <= angle + range) {
                ret.addElement(Area.getInstance().baseStations[i]);
                continue;
            }

            //check right
            BSAngle = Math.toDegrees(Math.acos(Math.abs(Area.getInstance().baseStations[i].getPoint().x - Area.getInstance().baseStations[i].getradius() - point.x) / (EuclideanDistance.distance(new Point(Area.getInstance().baseStations[i].getPoint().x - Area.getInstance().baseStations[i].getradius(), Area.getInstance().baseStations[i].getPoint().y), point) * 1000)));
            if (BSAngle >= angle - range && BSAngle <= angle + range) {
                ret.addElement(Area.getInstance().baseStations[i]);
                continue;
            }
//           System.err.println("Angle BS = "+BSAngle);

        }
        //check AP
        for (int i = 1; i <= Area.ACCESS_POINT_NO; i++) {
            //chech center
            double APAngle = Math.toDegrees(Math.acos(Math.abs(Area.getInstance().accessPoints[i].getPoint().x - point.x) / (EuclideanDistance.distance(Area.getInstance().accessPoints[i].getPoint(), point) * 1000)));
            if (APAngle >= angle - range && APAngle <= angle + range) {
                ret.addElement(Area.getInstance().accessPoints[i]);
                continue;
            }

            //check top
            APAngle = Math.toDegrees(Math.abs(Math.asin(Area.getInstance().accessPoints[i].getPoint().y + Area.getInstance().accessPoints[i].getradius() - point.y) / (EuclideanDistance.distance(new Point(Area.getInstance().accessPoints[i].getPoint().x, Area.getInstance().accessPoints[i].getPoint().y + Area.getInstance().accessPoints[i].getradius()), point) * 1000)));
            if (APAngle >= angle - range && APAngle <= angle + range) {
                ret.addElement(Area.getInstance().accessPoints[i]);
                continue;
            }

            //check bottom
            APAngle = Math.toDegrees(Math.abs(Math.asin(Area.getInstance().accessPoints[i].getPoint().y - Area.getInstance().accessPoints[i].getradius() - point.y) / (EuclideanDistance.distance(new Point(Area.getInstance().accessPoints[i].getPoint().x, Area.getInstance().accessPoints[i].getPoint().y - Area.getInstance().accessPoints[i].getradius()), point) * 1000)));
            if (APAngle >= angle - range && APAngle <= angle + range) {
                ret.addElement(Area.getInstance().accessPoints[i]);
                continue;
            }

            //check left
            APAngle = Math.toDegrees(Math.abs(Math.acos(Area.getInstance().accessPoints[i].getPoint().x + Area.getInstance().accessPoints[i].getradius() - point.x) / (EuclideanDistance.distance(new Point(Area.getInstance().accessPoints[i].getPoint().x + Area.getInstance().accessPoints[i].getradius(), Area.getInstance().accessPoints[i].getPoint().y), point) * 1000)));
            if (APAngle >= angle - range && APAngle <= angle + range) {
                ret.addElement(Area.getInstance().accessPoints[i]);
                continue;
            }

            //check right
            APAngle = Math.toDegrees(Math.abs(Math.acos(Area.getInstance().accessPoints[i].getPoint().x - Area.getInstance().accessPoints[i].getradius() - point.x) / (EuclideanDistance.distance(new Point(Area.getInstance().accessPoints[i].getPoint().x - Area.getInstance().accessPoints[i].getradius(), Area.getInstance().accessPoints[i].getPoint().y), point) * 1000)));
            if (APAngle >= angle - range && APAngle <= angle + range) {
                ret.addElement(Area.getInstance().accessPoints[i]);
                continue;
            }
        }

        //check destination
        for (int i = 0; i < history.size(); i++) {
            if (history.elementAt(i) != null) {
                Point p = (Point) history.elementAt(i);
                //check destination Angles
                double destAngle = Math.toDegrees(Math.acos(p.x - point.x / EuclideanDistance.distance(p, point)));
                if (destAngle >= angle - range && destAngle <= angle + range) {
                    ret.addElement(history.elementAt(i));
                    continue;
                }
            }
        }

        return ret;
    }

    private Vector getConcatenatRangeAndSNR(Vector range, Vector SNR) {
        Vector ret = new Vector();
        for (int i = 0; i < SNR.size(); i++) {
            for (int j = 0; j < range.size(); j++) {
                if (SNR.elementAt(i).equals(range.elementAt(j))) {
                    ret.addElement(SNR.elementAt(i));
                }
            }
        }

        return ret;
    }

    private Point getBestDest(Vector option) {
        int minDist = Integer.MAX_VALUE;
        int idx = -1;
        for (int i = 0; i < option.size(); i++) {
            if (option.elementAt(i) instanceof Point) {
                if (EuclideanDistance.distance(point, ((Point) option.elementAt(i))) < minDist) {
                    idx = i;
                }
            }
        }

        if (idx != -1) {
            return ((Point) option.elementAt(idx));
        }
        return null;
    }

    public Vector getCoverageAtSID(int sid) {
        Vector ret = new Vector();

        int x = Util.sidToColls(sid) * Area.getCellWidth();
        int y = Util.sidToRows(sid) * Area.getCellHeight();
        Vector edge1 = new Vector();
        inserVector(getBSCoveringAt(x, y), edge1);
        inserVector(getAPCoveringAt(x, y), edge1);


        x += 5;
        Vector edge2 = new Vector();
        inserVector(getBSCoveringAt(x, y), edge2);
        inserVector(getAPCoveringAt(x, y), edge2);

        y += 5;
        Vector edge3 = new Vector();
        inserVector(getBSCoveringAt(x, y), edge3);
        inserVector(getAPCoveringAt(x, y), edge3);

        x -= 5;
        Vector edge4 = new Vector();
        inserVector(getBSCoveringAt(x, y), edge4);
        inserVector(getAPCoveringAt(x, y), edge4);



        ret = commonConnectionsbtw(commonConnectionsbtw(edge1, edge2), commonConnectionsbtw(edge3, edge4));
        //intersect
        return ret;
    }

    public Vector getBSCoveringAt(int x, int y) {
        Vector ret = new Vector();
        Point p = new Point(x, y);
        for (int i = 0; i < Area.getInstance().BASE_STATION_NO; i++) {
            if (Area.getInstance().getBaseStationAt(i).isContains(p)) {
                ret.add(Area.getInstance().getBaseStationAt(i));
            }
        }
        return ret;
    }

    public Vector getAPCoveringAt(int x, int y) {
        Vector ret = new Vector();
        Point p = new Point(x, y);
        for (int i = 1; i <= Area.getInstance().ACCESS_POINT_NO; i++) {
            if (Area.getInstance().getAccessPointAt(i).isContains(p)) {
                ret.add(Area.getInstance().getAccessPointAt(i));
            }
        }
        return ret;
    }

    public void inserVector(Vector src, Vector dist) {
        for (int i = 0; i < src.size(); i++) {
            dist.add(src.elementAt(i));
        }
    }

    public Vector commonConnectionsbtw(Vector v1, Vector v2) {
        Vector ret = new Vector();
        for (int i = 0; i < v1.size(); i++) {
            if (v2.contains(v1.elementAt(i))) {
                ret.add(v1.elementAt(i));
            }
        }
        return ret;
    }

    public Vector getCoverageBSAndAPAtAll(int[] sid) {
        Vector ret = new Vector();

        for (int i = 1; i <= Area.ACCESS_POINT_NO; i++) {
            boolean isContained = false;
            for (int j = 0; j < sid.length; j++) {
                if (Area.getInstance().accessPoints[i].isContains(Util.getCellCenter(sid[j]))) {
                    isContained = true;
                } else {
                    isContained = false;
                    break;
                }
            }//end internal loop

            if (isContained) {
                ret.add(Area.getInstance().accessPoints[i]);
            }
        }//end external loop

        for (int i = 1; i <= Area.BASE_STATION_NO; i++) {
            boolean isContained = false;
            for (int j = 0; j < sid.length; j++) {
                if (Area.getInstance().baseStations[i].isContains(Util.getCellCenter(sid[j]))) {
                    isContained = true;
                } else {
                    isContained = false;
                    break;
                }
            }//end internal loop

            if (isContained) {
                ret.add(Area.getInstance().baseStations[i]);
            }
        }//end external loop

        if(ret.size() == 0){return null;}

        return ret;
    }

    public int isContainAP(Vector v) {
        int found = -1;
        if (v == null) {
            return found;
        }

        for (int i = 0; i < v.size(); i++) {
            if (v.elementAt(i) instanceof AccessPoint) {
                return i;
            }
        }
        return found;
    }

    public void initSIDTable() {
        sidTable = new Hashtable(Area.getRows() * Area.getColls());
        for (int i = 0; i < (Area.getRows() * Area.getColls()); i++) {
            int[][] m_Array = new int[7][28];
            sidTable.put(new Integer(i), m_Array);
            fill_Prediction(i);
            fill_history(i);
        }
    }

    public int[][] getMArrayAt(int sid) {
        return (int[][]) (sidTable.get(Integer.valueOf(sid)));
    }

    public void fill_Prediction(int sid) {

        int col = 2;
        int[] array_p1 = new int[5];
        int[] array_p2 = new int[5];
        array_p1 = next_move(sid);

        for (int i = 0; i < 5; i++) {
            array_p2 = next_move(array_p1[i]);
            for (int k = 0; k < 5; k++) {
                getMArrayAt(sid)[0][col] = array_p2[k];
                getMArrayAt(sid)[1][col] = array_p1[i];
                col++;
            } // end of for k

        } // end for for i

        return;
    } // end of function

    public void fill_history(int sid) {
        int ro = 2;
        int h0 = sid;
        int[] array_h1 = new int[5];

        array_h1 = next_move(sid);

        for (int i = 0; i < 5; i++) {
            getMArrayAt(sid)[ro + i][1] = h0;
            getMArrayAt(sid)[ro + i][0] = array_h1[i];
        } //end of for i
        return;
    } // end of function

    public int[] next_move(int sid) {
        int[] temp_array = new int[5];
        int si = Util.sidToRows(sid);
        int sj = Util.sidToColls(sid);

        for (int i = 0; i < 5; i++) {
            if (i == 0) {
                temp_array[i] = ( (Util.getSid(si - 1, sj) < 0) || (Util.getSid(si - 1, sj) > (Area.getRows()*Area.getColls())-1)) ? sid : Util.getSid(si - 1, sj);
            } // up
            else if (i == 1) {
                temp_array[i] = ( (Util.getSid(si + 1, sj) < 0) || (Util.getSid(si + 1, sj) > (Area.getRows()*Area.getColls())-1)) ? sid : Util.getSid(si + 1, sj);
            } // down
            else if (i == 2) {
                temp_array[i] = ( (Util.getSid(si, sj - 1) < 0) || (Util.getSid(si, sj - 1) > (Area.getRows()*Area.getColls())-1)) ? sid : Util.getSid(si, sj - 1);
            } // left
            else if (i == 3) {
                temp_array[i] = ( (Util.getSid(si, sj + 1) < 0) || (Util.getSid(si, sj + 1) > (Area.getRows()*Area.getColls())-1)) ? sid : Util.getSid(si, sj + 1);
            } // right
            else {
                temp_array[i] = Util.getSid(si, sj);
            }	// stay
        } // end of for i

        return temp_array;
    } //end of function

    public int[][] predict(int h2p[]) {

        int f_max, s_max, temp, ro = 0;
        int[][] p_array = new int[2][4];

        //To Do
        // #get the m_array that is related to square h2p[1]  ( current  position)



        for (int i = 0; i < 5; i++) {
            if (getMArrayAt(h2p[1])[i][0] == h2p[0]) { //this row is the related history of h2p 0 (h1)
                ro = i; // row in interest is saved now
            } // end of if
        } // end of for i

        f_max = s_max = 2; // initialization to the first value
// now get max1 and max2

        for (int j = 3; j < 26; j++) {
            if (getMArrayAt(h2p[1])[ro][j] == getMArrayAt(h2p[1])[ro][f_max]) { //this value = f_max
                s_max = j;
            } else if (getMArrayAt(h2p[1])[ro][j] > getMArrayAt(h2p[1])[ro][f_max]) {
                temp = f_max;
                f_max = j;
                s_max = temp;
            } else if (getMArrayAt(h2p[1])[ro][j] < getMArrayAt(h2p[1])[ro][f_max]) {
                if (getMArrayAt(h2p[1])[ro][j] > getMArrayAt(h2p[1])[ro][s_max]) {
                    s_max = j;
                }
            }
        } // end of for


        p_array[0][0] = getMArrayAt(h2p[1])[1][f_max];
        p_array[0][1] = getMArrayAt(h2p[1])[0][f_max];
        p_array[1][0] = getMArrayAt(h2p[1])[1][s_max];
        p_array[1][1] = getMArrayAt(h2p[1])[0][s_max];

        if (f_max == s_max) {
            // both predictions are the same one
            p_array[0][2] = 0;
        } else {
            p_array[0][2] = 1;
        }

        if (getMArrayAt(h2p[1])[ro][f_max] > getMArrayAt(h2p[1])[ro][s_max]) {
            p_array[1][2] = 0;
        } else {
            p_array[1][2] = 1;
        } // both predictions have the same power

        p_array[0][3] = getMArrayAt(h2p[1])[ro][27]; // get denumerator of this row ""how many times this node used this path""
        p_array[1][3] = 0; // just a value

        return p_array;
    } // end of function

    public void learn(int learn_array[]) {
//        System.err.print("[");
//        for (int i = 0; i < learn_array.length; i++) {
//            System.err.print(learn_array[i] + ", ");
//        }
//        System.err.println("]");
        int ro = 0, col = 0;

        //To Do
        // #get search array of interest of l_array[1]

// find row

        for (int i = 2; i < 7; i++) {
            if (getMArrayAt(learn_array[1])[i][0] == learn_array[0]) {
                ro = i;
                break;
            } // ro = the related row

            if (ro == 0) {
//                System.err.println("Warning :: Row = 0");
            }
        }

        int j;
        for (j = 2; j < 27; j++) {
            if (getMArrayAt(learn_array[1])[1][j] == learn_array[2]) {  // p1 = p1 now p2
//                    System.err.println("getMArrayAt(learn_array[2])[1]["+j+"] = "+getMArrayAt(learn_array[1])[1][j]);
                break;
            } // end of if
        } // end of for jj (i++ to keep j pointer on the same column of jj)
//        System.err.println("j = " + j);
        for (int jj = j; jj < j + 5; jj++) {
            if (getMArrayAt(learn_array[1])[0][jj] == learn_array[3]) { //column of p1 & p2 is found
                col = jj;
                getMArrayAt(learn_array[1])[ro][27]++;
                getMArrayAt(learn_array[1])[ro][col]++;
                return;
            } // end of if
//                        j++;


// if p1 isn't found in this j, then the next j elements are the same
// so j should make jumps of 5s
//                j = j + 4;
        } // end of for j "j++"

//update denumerator


//update prediction value


//        }getMArrayAt(learn_array[2])[1][j] // end of function learn
        return;
    }

    public void updatePath(Point curPoint) {
        int idx = -1;
        for (int i = 0; i < path.length; i++) {
            if (path[i] == -1) {
                idx = i;
                break;
            }
        }

        if (idx != -1) {
            path[idx] = Util.getSidAtCords(curPoint);
        } else {
            for (int i = 1; i < path.length; i++) {
                path[i - 1] = path[i];
            }
            path[path.length - 1] = Util.getSidAtCords(curPoint);
            learn(path);
        }
    }

    public void printMArray(int sid) {
        System.err.println("getMArrayAt(" + sid + ") = " + getMArrayAt(sid));
        for (int i = 0; i < 7; i++) {
            for (int k = 0; k < 28; k++) {

                System.err.print("" + getMArrayAt(sid)[i][k] + " , ");
            }
            System.err.println("");
        }
    }

    public void printVector(Vector v) {
        if (v != null) {
            for (int i = 0; i < v.size(); i++) {
                if (v.elementAt(i) instanceof BaseStation) {
                    System.err.println(((BaseStation) v.elementAt(i)).toString());
                } else if (v.elementAt(i) instanceof AccessPoint) {
                    System.err.println(((AccessPoint) v.elementAt(i)).toString());
                } else if (v.elementAt(i) instanceof Point) {
                    System.err.println("X = " + ((Point) v.elementAt(i)).x + ", Y = " + ((Point) v.elementAt(i)).x);
                }
            }
        }

    }

    @Override
    public String toString() {
        return "\nMobile\nPosition[" + point.x + "," + point.y + "]\tRadius[" + radius + "]"
                + "\tVelocity[" + velocity + "]\nSINR[" + sinr + "]"
                + "\nConnectTo" + connectTO;
    }

    public void waits(long waitTime) {
        long wait = System.currentTimeMillis();
        while (System.currentTimeMillis() - wait < waitTime) {
        }
    }
}

