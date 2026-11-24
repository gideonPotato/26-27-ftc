package org.firstinspires.ftc.teamcode.robot;

import com.pedropathing.math.Pose;

public class RobotConstants {
    public static class DeviceNames {
        // Motors
        public static String intakeMotor = "intake";
        public static String transferMotor = "transfer";
        public static String turretMotor = "turret";
        public static String flywheelMotor = "flywheel";

        // Servos
        public static String leftHoodServo = "leftHood";
        public static String rightHoodServo = "rightHood";

        // Sensors
        public static String transferBeamBreak = "intakeBreak";
        public static String leftTurretLimitSwitch = "leftLimit";
        public static String rightTurretLimitSwitch = "rightLimit";
    }

    public static class SubsystemConstants {
        // motor powers/pid constants
        public static double intakeIn = .5;
        public static double intakeOut = -.5;
        public static double intakeHold = 0;
        public static double intakeIdle = -.25;

        public static double transferIn = .5;
        public static double transferHold = 0;
        public static double transferOut = -.25;

        public static double turretHomePower = 0.02;
        public static int turretRunSpeed = 3000;

        public static double flywheelSpeedDipTolerance = 100;


        public static double hoodServoOffset = 0; // i think this is needed, because 0 on the servos is not straight up

        // sensors
        public static double beamBreakDistance = 0; // this should be a value between what the distance sensor reads with no ball, and what it reads with a ball, in millimeters

        // flag that, if true, will disable all subsystem movement (except drivetrain?) and instead print values
        public static boolean debug = true;
    }

    public static class Conversions {
        public static double encoderTicksPerTurretDegree = -2.96;
        public static double servoRangesPerHoodDegree = 0; // i lost the value and don't feel like recalculating it :p      also see hoodServoOffset
        public static double encoderTicksPerFlywheelInch = 3.14606; // this isn't pi. but it's oddly close
    }

    public static class TrajectoryParams {
        // all physics calculations are in inches, seconds, and radians because that's what requires the least conversion
        private static final double finalSlopeDegrees = -55;
        private static final double finalSlopeRadians = Math.toRadians(finalSlopeDegrees);
        public static double finalSlope = Math.tan(finalSlopeRadians);

        public static double turretHeight = 10;
        public static double targetHeight = 59; // this is what i had on desmos, i don't really trust it

        public static double gravity = 385.8268; // in inches/sec^2

    }

    public static class Poses {
        public static Pose redAudience = new Pose(60, 50);
        public static Pose redScoring = new Pose(60, 94);
        public static Pose blueAudience = new Pose(89, 50);
        public static Pose blueScoring = new Pose(89, 94);
    }

    public enum Alliance {
        RED,
        BLUE
    }


}
