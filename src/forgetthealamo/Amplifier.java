package forgetthealamo;

import battlecode.common.*;

public class Amplifier {
    public static void runAmplifier(RobotController rc) throws GameActionException {
        RobotInfo[] ri = rc.senseNearbyRobots();
        RobotInfo closestFriend = ri[0];
        for (int i = 0; i < ri.length; i++) {
          if(ri[i].getTeam() == rc.getTeam() && (ri[i].getType().equals(RobotType.LAUNCHER) || ri[i].getType().equals(RobotType.CARRIER))){
            closestFriend = ri[i];
          }
        }
        Direction dir = rc.getLocation().directionTo(closestFriend.getLocation());
        if(rc.canMove(dir))
            rc.move(dir);
    }
}

