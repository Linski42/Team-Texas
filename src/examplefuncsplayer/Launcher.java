package examplefuncsplayer;

import battlecode.common.*;
import examplefuncsplayer.Utils.Utility;
import examplefuncsplayer.Utils.Zone;

public class Launcher {
       private static void runLauncher(RobotController rc, Dijkstra dijik) throws GameActionException {
        int ind = 0;
        //init{ //we only want this to be the case on first run
        final int[] idIndexN = Utility.getSageIndex(rc); //gives an index for robot to reference //TODO: find some way to cache this
        final int idIndex = idIndexN[0];
        final int unit = idIndexN[1];
        if(idIndex == -1){
           RobotInfo[] rInfo = rc.senseNearbyRobots();
           for (int i = 0; i < rInfo.length; i++) {
                if(rInfo[i].getTeam() != rc.getTeam() && rc.canAttack(rInfo[i].getLocation())){
                    rc.attack(rInfo[i].getLocation());
                }
           }
        }

        final int unitCount = rc.readSharedArray(15 + (unit * 15)); 
        final MapLocation thisLoc = rc.getLocation();
        int[] eLS = new int[2];  //2 layers of deserialization
        MapLocation targetLoc = Utility.getMapCenter(rc);
        RobotType targetType = RobotType.LAUNCHER;
        rc.setIndicatorString(String.valueOf(++ind));//1
        WellInfo[] LOOT = rc.senseNearbyWells();
        if(LOOT.length > 0){
            rc.writeSharedArray(8, Utility.serializeMapLocation(LOOT[0].getMapLocation(), 0));
        }
        try{
            eLS = Utility.deserializeRobotLocation(rc.readSharedArray(16+(unit*15)));
            targetLoc = new MapLocation(eLS[0], eLS[1]);
        }catch(GameActionException e){
            rc.setIndicatorString(e.getMessage());
        }
        
        rc.setIndicatorString(String.valueOf(++ind));//2
        int targetID = 0;
        try{
            targetID = rc.readSharedArray(17+(unit*15));
        }catch(GameActionException e){

        }

        rc.setIndicatorString(String.valueOf(++ind));
        int[] centerDe = new int[]{rc.getMapWidth()/2, rc.getMapHeight()/2};
        MapLocation centerLoc = Utility.getMapCenter(rc); //location of center for Zone creation
        boolean hasCenter = true;
        try{
            centerDe = Utility.deserializeMapLocation(rc.readSharedArray(18+(unit * 15)));
            centerLoc = new MapLocation(centerDe[0], centerDe[1]); //location of center for Zone creation
        }catch(GameActionException e){
            hasCenter = false;
         }
        rc.setIndicatorString(String.valueOf(++ind));
        final int targetVision = 6;
        Zone zone = new Zone(rc, targetLoc, centerLoc, targetVision, unitCount);
        final int zoneNumber = zone.getZone(thisLoc);
        MapLocation desiredPos = centerLoc;
        rc.setIndicatorString(String.valueOf(++ind));
        if(hasCenter){
            rc.setIndicatorString(String.valueOf(++ind));
            rc.setIndicatorString(String.valueOf(++ind));
        if (rc.canSenseRobot(targetID)) {
                RobotInfo targetInfo = rc.senseRobot(targetID);
                rc.attack(targetInfo.getLocation()); //TODO: Research, can this do more than one action
                rc.setIndicatorString("ind is"  + String.valueOf(++ind));
            if (zoneNumber == 1) { //in attack zone
                if (!rc.canSenseRobot(targetID)) { //if target down 
                    RobotInfo[] rInfo = rc.senseNearbyRobots(); //TODO: Possible Optimization
                    int minHP = 500;
                    RobotInfo ri = null;
                    for(int i = 0; i < rInfo.length; i++){
                        if(rInfo[i].getHealth() < minHP){
                            minHP = rInfo[i].getHealth();
                            ri = rInfo[i];
                        }
                    }
                    int sPos = 15 + (15*unit); //TODO: Make sure all of this works
                    rc.writeSharedArray(sPos+1, Utility.serializeRobotLocation(ri));
                    rc.writeSharedArray(sPos+2, ri.getID());
                    MapLocation newCent = Zone.calculateCenter(thisLoc, rInfo, targetInfo);
                    boolean rub = false;
                    if(rc.canSenseLocation(newCent)){
                        rub = rc.sensePassability(newCent);   
                    }
                        rc.writeSharedArray(sPos+3, Utility.serializeMapLocation(newCent, 0));
                    }
                //TODO: Implement Sage Casting
               desiredPos = zone.getLocationInZone(2);
            }else { //recreate zones
                final RobotInfo ti = rc.senseRobot(targetID);
                rc.attack(ti.getLocation()); 
                RobotInfo[] rInfo = rc.senseNearbyRobots();
                int minHP = 500;
                RobotInfo info = null;
                zone = new Zone(rc, ti.getLocation(), thisLoc, 6, unitCount);
                for(int i = 0; i < rInfo.length; i++){
                    if(rInfo[i].getHealth() < minHP){
                        minHP = rInfo[i].getHealth();
                        info = rInfo[i];
                    }
                }
                MapLocation newCent = Zone.calculateCenter(thisLoc, rInfo, ti);
                rc.writeSharedArray(18+(15*unit),Utility.serializeMapLocation(newCent, 0));
                    //run away
                    //TODO: I want to play with this to see if it's too fidgety as is        
            }
        }else {
            switch(zoneNumber){ //if I can't find an enemy then either push up or rotate to the next zone
                case 1: desiredPos = targetLoc; break; //TODO: this needs to be a lot more complex, kiting is what wins games
                case 2: desiredPos = zone.getLocationInZone(3); break;
                case 3: desiredPos = zone.getLocationInZone(1); break;//This could be optimized
                default: desiredPos = centerLoc;
            }

        }
            if(rc.canMove(thisLoc.directionTo(desiredPos))){
                rc.move(thisLoc.directionTo(desiredPos));
            }else{
                rc.move(dijik.getBestDirection(desiredPos, thisLoc.directionTo(desiredPos)));
            }
        }else{
            if(rc.canMove(thisLoc.directionTo(centerLoc))){
                rc.move(thisLoc.directionTo(centerLoc));
            }else{
                rc.move(dijik.getBestDirection(centerLoc, thisLoc.directionTo(centerLoc)));
            }
        }
    } 
}
