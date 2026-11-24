package org.firstinspires.ftc.teamcode.robot.systems;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.commands.Commands;
import com.pedropathing.ivy.groups.Groups;
import com.pedropathing.math.Pose;
import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.robot.RobotConstants.*;

public class Turret {
    private DcMotorEx motor;
    private final RevTouchSensor leftLimit;
    private final RevTouchSensor rightLimit;
    private final GoalTracker goalTracker;
    private final Follower follower;
    private final BallCountClass ballCounter;
    private final Telemetry telemetry;

    private int leftHomePosition;
    private int rightHomePosition;

    private boolean triggered; // used for both left and right switches, used to check for a change in state

    public Turret(HardwareMap hardwareMap, GoalTracker goalTracker, Follower follower, BallCountClass ballCounter, Telemetry telemetry){
        motor = hardwareMap.get(DcMotorEx.class, DeviceNames.turretMotor);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftLimit = hardwareMap.get(RevTouchSensor.class, DeviceNames.leftTurretLimitSwitch);
        rightLimit = hardwareMap.get(RevTouchSensor.class, DeviceNames.rightTurretLimitSwitch);
        this.goalTracker = goalTracker;
        this.follower = follower;
        // TODO: REPLACE FOLLOWER WITH TURRET LOCALIZER
        this.ballCounter = ballCounter;
        this.telemetry = telemetry;
    }

    private int turretDegreesToEncoderTicks(double angle) {
        return (int) (angle * Conversions.encoderTicksPerTurretDegree);
        // ticks are integers, so we just truncate after this conversion. we shouldn't have to worry too much about the rounding of half an encoder tick i hope
    }

    private double encoderTicksToTurretDegrees(int ticks) {
        return ticks / Conversions.encoderTicksPerTurretDegree;
    }

    private double getTargetAngle() {
        Pose goal = goalTracker.goal;
        Pose robot = follower.pose();
        double x = goal.x() - robot.x();
        double y = goal.y() - robot.y();
        double absoluteHeading = Math.atan2(y, x);
        return Math.toDegrees(absoluteHeading - robot.heading());
    }

    /* couldn't think of a better name for this function
     this takes the current turret position(ticks) and its target angle(degrees)
     returns the angle to run turret to
     currently has no logic for deciding whether to wrap or not, so just returns the target angle, converted from degrees to ticks
    */
    private int getTargetMovement(double targetAngle, int currentPosition) {
        // TODO: MAKE IT SO WE UTILIZE ALL OF THE TURRET'S RANGE OF MOVEMENT
        // this^ will probably require the usage of the follower and ball counter to check whether we're near the goal and are full

        // basic resetting to 0 if we don't have balls or if we're between the goals
        if (ballCounter.balls == 0) return 0;
        if (follower.pose().y() > 60 && follower.pose().y() < 96) return 0;

        int targetTicks = turretDegreesToEncoderTicks(targetAngle);
        return targetTicks;
    }

    private void runTurret(int ticks) {
        // TODO: STOP USING THE SDK RUN TO POSITION, REPLACE WITH A PROPER PID SYSTEM
        if (!SubsystemConstants.debug) {
            motor.setTargetPosition(ticks);
            motor.setVelocity(SubsystemConstants.turretRunSpeed);
        } else {
            telemetry.addData("turret target position", ticks);
            telemetry.addData("turret target angle", encoderTicksToTurretDegrees(ticks));
            telemetry.addData("turret current position", motor.getCurrentPosition());
            telemetry.addData("turret current angle", encoderTicksToTurretDegrees(motor.getCurrentPosition()));
        }
    }

    private Command getHomePoint(boolean left){
        return Command.build()
                .setStart(() -> {

                    if (left) motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); // reset encoder to 0, assume we are facing forwards (only if we're homing to the left, we don't do it again when homing right, as that could make the position we got on the left inaccurate)
                    motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    triggered = (left) ? leftLimit.isPressed() : rightLimit.isPressed();
                    motor.setPower(SubsystemConstants.turretHomePower * ((left) ? 1 : -1)); // swap the negative here if we move the wrong direction
                }).setDone(() -> (((left) ? leftLimit.isPressed() : rightLimit.isPressed()) != triggered))
                .setEnd(endCondition -> {
                    motor.setPower(0);
                    if (left) leftHomePosition = motor.getCurrentPosition(); else rightHomePosition = motor.getCurrentPosition();
                    motor.setTargetPosition(0);
                    motor.setPower(.1);
                    motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                });
    }

    private final Command finalizeHoming = Command.build()
            .setStart(() -> {
                int center = (leftHomePosition + rightHomePosition) / 2;
                motor.setTargetPositionTolerance(0);
                motor.setTargetPosition(center);
                motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                motor.setPower(SubsystemConstants.turretHomePower);
            })
            .setDone(() -> (motor.getCurrentPosition() == 0))
            .setEnd(endCondition -> {
                motor.setPower(0);
                motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            });

    public Command home = Groups.sequential(
            getHomePoint(true),
            Commands.waitUntil(() -> (motor.getCurrentPosition() == 0)),
            getHomePoint(false),
            Commands.instant(() -> motor.setPower(0)),
            finalizeHoming
    );

    public Command runTurret = Commands.instant(() -> {
        double targetAngle = getTargetAngle();
        int targetPosition = getTargetMovement(targetAngle, motor.getCurrentPosition());
        runTurret(targetPosition);
    });
}
