package org.firstinspires.ftc.teamcode.robot.systems;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.commands.Commands;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.robot.RobotConstants.*;

public class Flywheel {
    private final DcMotorEx motor;
    private final BallCountClass ballCounter;
    private final Follower follower;
    private final GoalTracker goalTracker;
    private final Servo left;
    private final Servo right;

    private double targetFlywheelVelocity = 0;
    private double previousFlywheelVelocity = 0;
    private boolean speedDipAlreadyCounted = false;

    public Flywheel(HardwareMap hardwareMap, BallCountClass ballCounter, Follower follower, GoalTracker goalTracker) {
        motor = hardwareMap.get(DcMotorEx.class, DeviceNames.flywheelMotor);
        left = hardwareMap.get(Servo.class, DeviceNames.leftHoodServo);
        right = hardwareMap.get(Servo.class, DeviceNames.rightHoodServo);
        this.ballCounter = ballCounter;
        this.follower = follower;
        // TODO: REPLACE FOLLOWER WITH TURRET LOCALIZER
        this.goalTracker = goalTracker;
    }

    private double hoodAngleToServoPosition(double angle) {
        return angle * Conversions.servoRangesPerHoodDegree;
    }

    // speed is in inches/second because :(
    private int tangentialSpeedToFlywheelTicks(double speed) {
        return (int) (speed * Conversions.encoderTicksPerFlywheelInch);
    }

    private void runHood(double angle) {
        angle = hoodAngleToServoPosition(angle);
        angle += SubsystemConstants.hoodServoOffset; // added because 0 on the servos is not straight up
        left.setPosition(angle);
        right.setPosition(1-angle);
    }

    private void runFlywheel(double tangentialSpeed) {
        int ticks = tangentialSpeedToFlywheelTicks(tangentialSpeed);
        // TODO: IMPLEMENT PID CONTROLLER HERE
        motor.setVelocity(ticks);
    }

    private double getHorizontalTargetDistance(){
        // TODO: CORRECT FOR THE TURRET BEING OFFSET FROM THE CENTER OF THE ROBOT
        return Math.sqrt(
                Math.pow(
                        follower.pose().x() - goalTracker.goal.x(),
                        2
                ) + Math.pow(
                        follower.pose().y() - goalTracker.goal.y(),
                        2
                )
        );
    }

    private double getCoefficientA() {
        double m = TrajectoryParams.finalSlope;
        double x = getHorizontalTargetDistance();
        double y = TrajectoryParams.targetHeight - TrajectoryParams.turretHeight;
        return (m*x-y)/Math.pow(y,2);
    }

    private double getCoefficientB(double coefficientA) {
        double m = TrajectoryParams.finalSlope;
        double x = getHorizontalTargetDistance();
        return m - 2*coefficientA*x;
    }

    private double getVertexX(double a, double b) {
        return -b / 2*a;
    }

    private double getVertexY(double vertX, double a, double b) {
        return a*Math.pow(vertX, 2) + b*vertX;
    }

    private double getTargetVelocity(double vertX, double vertY){
        return vertY/vertX - .5*TrajectoryParams.gravity*vertX;
    }

    private double getHoodAngle(double b){
        return Math.toDegrees(
                Math.atan(b)
        );
    }

    public Command countBalls() {
        return Commands.infinite(() -> {
            double flywheelVelocity = motor.getVelocity();
            if (flywheelVelocity < previousFlywheelVelocity && flywheelVelocity < targetFlywheelVelocity - SubsystemConstants.flywheelSpeedDipTolerance) {
                if (!speedDipAlreadyCounted) {
                    if (ballCounter.balls != 0) ballCounter.balls--;
                    speedDipAlreadyCounted = true;
                }
            } else speedDipAlreadyCounted = false;
            previousFlywheelVelocity = flywheelVelocity;
        });
    }

    public Command runTurret = Commands.instant(() -> {
            double a = getCoefficientA();
            double b = getCoefficientB(a);
            double vertexX = getVertexX(a, b);
            double vertexY = getVertexY(vertexX, a, b);
            targetFlywheelVelocity = getTargetVelocity(vertexX, vertexY);
            runFlywheel(targetFlywheelVelocity);

            double hoodAngle = getHoodAngle(b);
            runHood(hoodAngle);
        }
    );
}
