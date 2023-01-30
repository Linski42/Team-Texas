package forgetthealamo;

import battlecode.common.*;

import forgetthealamo.utils.Build;
import forgetthealamo.utils.Utility;

public class Headquarters {
    private static void runHeadquarters(RobotController rc) throws GameActionException {
        final int ideal_launcher_number = Build.getIdealNumLaunchers(rc);
        final int ideal_amplifier_number = Build.getIdealNumLaunchers(rc)/2;
        final int ideal_carrier_number = Build.getIdealNumCarriers(rc);
        final MapLocation myLocation = rc.getLocation();
        final int currentCarriers = rc.readSharedArray(3);
        final int currentAmplifiers = rc.readSharedArray(4);
        final RobotInfo[] nearby = rc.senseNearbyRobots();
        final MapLocation mapCenter = new MapLocation(rc.getMapHeight()/2, rc.getMapWidth()/2);
        int nearbyEnemyCount = 0;
        RobotInfo[] nearbyEnemies = new RobotInfo[nearby.length];
        for (int i = 0; i < nearby.length; i++) {
          if(nearby[i].getTeam() != rc.getTeam()){nearbyEnemyCount++;}
        }
        if(nearbyEnemyCount > 0){
            Direction eDir = myLocation.directionTo(nearbyEnemies[0].getLocation());
            Build.buildLAUNCHER(rc, eDir);
        }
        boolean ri = false;
            final int[] s = {0, 1}; 
            final MapLocation leadPos = new MapLocation(s[0], s[1]);
        if(rc.canBuildAnchor(Anchor.ACCELERATING)) {
            rc.buildAnchor(Anchor.ACCELERATING);
        }else if(rc.canBuildAnchor(Anchor.STANDARD)) {
            rc.buildAnchor(Anchor.STANDARD);
        } else {
            rc.setIndicatorString("Trying to build a launcher");
            RobotInfo robotI = Build.buildLAUNCHER(rc, myLocation.directionTo(mapCenter));
        }
        if (ideal_carrier_number > currentCarriers) {
            rc.setIndicatorString("Trying to build a miner");
            rc.setIndicatorLine(myLocation, leadPos, 255, 0, 0);
            ri = tryBuild(rc, myLocation.directionTo(leadPos), RobotType.CARRIER);
            rc.writeSharedArray(3, currentCarriers + 1);

        }
        if(ideal_amplifier_number > currentAmplifiers){
            rc.setIndicatorString("Trying to build a miner");
            rc.setIndicatorLine(myLocation, leadPos, 255, 0, 0);
            ri = tryBuild(rc, myLocation.directionTo(leadPos), RobotType.CARRIER);
            rc.writeSharedArray(3, currentCarriers + 1);
        }
        RobotInfo lowest = nearby[0];
        for (int i = 0; i < nearby.length; i++) {
            if(nearby[i].health<lowest.health){
                lowest = nearby[i];
            }

        }

    }
    public static boolean tryBuild(RobotController rc, Direction dir, RobotType type) throws GameActionException{
        RobotInfo newRobot = null;
        Direction nD = dir;
        MapLocation adjLocation = rc.adjacentLocation(dir);
        while(rc.isLocationOccupied(adjLocation)){
            nD.rotateLeft();
            adjLocation = rc.adjacentLocation(nD);
        }

        if(rc.canBuildRobot(type, adjLocation)){
            rc.buildRobot(type, adjLocation);
            newRobot = rc.senseRobotAtLocation(adjLocation);
        } else{
            return false;
        }
        return true;
    }
    }