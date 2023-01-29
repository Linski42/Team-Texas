import battlecode.common.*;

import dijkstra.Dijkstra;

public class Headquarters {
    private static void Headquarters(RobotController rc, Dijkstra dijik) throws GameActionException {
        final int ideal_miner_number = Build.getIdealNumMiners(rc);
        final int ideal_builder_number = Build.getIdealNumBuilders(rc);
        final int ideal_soldier_number = Build.getIdealNumSoldiers(rc);
        final MapLocation myLocation = rc.getLocation();
        final int currentMinerNumber = rc.readSharedArray(3);
        final int currentBuilderNumber = rc.readSharedArray(4);
        final RobotInfo[] nearby = rc.senseNearbyRobots();
        final MapLocation mapCenter = Utility.getMapCenter(rc);

        int nearbyEnemyCount = 0;
        RobotInfo[] nearbyEnemies = new RobotInfo[nearby.length];
        for (int i = 0; i < nearby.length; i++) {
          if(nearby[i].getTeam() != rc.getTeam()){nearbyEnemyCount++;}
        }
        if(nearbyEnemyCount > 0){
            Direction eDir = myLocation.directionTo(nearbyEnemies[0].getLocation());
            build.tryBuild(rc, eDir, RobotType.SOLDIER);
            if(rc.canMove(eDir.opposite()))
                rc.move(eDir.opposite());
        }
        RobotInfo ri = null;
            final int[] s = Utility.deserializeMapLocation(rc.readSharedArray(8)); 
            final MapLocation leadPos = new MapLocation(s[0], s[1]);
        if (ideal_miner_number > currentMinerNumber) {
            rc.setIndicatorString("Trying to build a miner");
            rc.setIndicatorLine(myLocation, leadPos, 255, 0, 0);
            ri = build.tryBuild(rc, myLocation.directionTo(leadPos), RobotType.MINER);
            rc.writeSharedArray(3, currentMinerNumber + 1);

        }else if(currentBuilderNumber < ideal_builder_number){
            rc.setIndicatorString("Trying to build a builder");
            ri = build.tryBuild(rc, myLocation.directionTo(mapCenter).opposite(), RobotType.BUILDER);
            if(ri != null){
                rc.writeSharedArray(4, currentBuilderNumber+ 1);
            } }else if(rc.getRoundNum() % 10 == 0){
            ri = build.tryBuild(rc, myLocation.directionTo(mapCenter), RobotType.SOLDIER);
        } else {
            rc.setIndicatorString("Trying to build a sage");
            ri = build.buildSage(rc, myLocation.directionTo(mapCenter));
            //buildSage
        }

        RobotInfo lowest = nearby[0];
        for (int i = 0; i < nearby.length; i++) {
            if(nearby[i].health<lowest.health){
                lowest = nearby[i];
            }

        }
        if(rc.canRepair(lowest.getLocation())){
            rc.repair(lowest.getLocation());
        }

    }
    }
}