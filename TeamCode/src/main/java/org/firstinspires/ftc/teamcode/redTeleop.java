package org.firstinspires.ftc.teamcode;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import static com.pedropathing.ivy.commands.Commands.infinite;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.pedro.Constants;

import java.util.EnumMap;

// this is just me trying to see how much code i can get done because my brain isn't letting me do anything else

/* going to just put random notes up here because uhh

the constant definition in the beginning of the class feels very messy, but i'm not sure how to go about cleaning it up
*/

@TeleOp
public class redTeleop extends LinearOpMode {

    // hardware
    DcMotorEx turretMotor; // positive should run clockwise
    DcMotorEx intakeMotor; // positive intakes, negative is backwards
    DcMotorEx transferMotor; // same as intake
    DcMotorEx flywheelMotor; // positive runs out, negative should probably never be used
    Servo leftHoodServo;
    Servo rightHoodServo;
    Servo rampServo;
    Servo transferBlockerServo;
    Rev2mDistanceSensor transferBreak1;
    Rev2mDistanceSensor transferBreak2; // these should be identical, we have several for redundancy
    RevTouchSensor turretLimitLeft; // left as viewed from the back of the robot
    RevTouchSensor turretLimitRight;

    // wow, look at me, i'm doing stuff right for once!!  amazing

    enum Alliance {
        red,
        blue
    }

    enum BotStates {
        intaking,
        full,
        launching
    }

    {
        EnumMap<BotStates, Command> states = new EnumMap<>(BotStates.class);
        states.put(BotStates.intaking, runIntake); // what what what what what i can't reference the nonstatic runintake from inside the static thing, and i can't make the runintake static because it needs to depend on things like the number of balls oh dear everything is melting :(
    }

    // variables

    int ballCount = 0; // will need to carry from auto
    boolean beamWasBroken = false; // might need to carry from auto?
    double targetFlywheelSpeed = 0;
    double hoodAngle = 0; // not in degrees, in Servo Measurements [tm]
    double oldWheelSpeed = 0; // used for detecting rpm drops

    // constants
    double transferClosedAngle = 1;
    double transferOpenAngle = -1;

    double emptyTransferWidth = 6; // this is in centimeters
    int rpmDropThreshold = 200; // how much the rpm of the flywheel needs to drop to register
    boolean rpmDropThrottle = false; // is true until the rpm is above the threshold, drop only registers if this is false

    // interrupt conditions
    private boolean intakeInterrupt() {return gamepad2.a;}
    private boolean idleInterrupt() {return gamepad2.b;}
    private boolean launchInterrupt() {return gamepad2.x;}

    Pose robotPose; // this should be updated every loop
    Pose goalPose; // this should also be updated every loop


    private final Alliance currentAlliance = Alliance.red;
    // TODO: make some sort of way to select, or perhaps just split into two programs by alliance idk
    // oh crap can't i just like. do the @Override thing on this class to get the other alliance? i should check that

    private BotStates currentState = BotStates.intaking;
    // TODO: make this (and several other variables) carry over from auto probably

    // pedro stuff
    private Follower follower;
    private final PoseFactory poseFactory = PoseFactory.radians();

    private final Pose startPose = poseFactory.of(36, 9, 0); // <- this is at the point on the wall closest to the up hive i think
    // this should also carry over from auto, pedropathing docs has instructions on how to do that

    // goal poses
    // TODO: find the actual positions of basically all the poses
    // (all headings are currently 0 cause they're not robot poses, they're just positions on the field for reference) (this is probably bad practice but the tools are here so i will be using them)
    private final Pose redGoalAudience = poseFactory.of(60, 50, 0);
    private final Pose redGoalScoring = poseFactory.of(60, 94, 0);
    private final Pose blueGoalAudience = poseFactory.of(84, 50, 0);
    private final Pose blueGoalScoring = poseFactory.of(84, 94, 0);



    Command runIntake = Command.build()
            .setStart(() -> {
                intakeMotor.setPower(1);
                transferMotor.setPower(1);
                transferBlockerServo.setPosition(transferClosedAngle);
            })
            .setExecute(() -> {
                boolean beamBroken = (transferBreak1.getDistance(DistanceUnit.CM) < emptyTransferWidth) || (transferBreak2.getDistance(DistanceUnit.CM) < emptyTransferWidth);
                if (beamBroken && !beamWasBroken) {
                    ballCount ++;
                }
                beamWasBroken = beamBroken;
                // this system should usually work, but if it needs reinforcement you'll probably have to start averaging the last few ticks or something :(
            })
            .setDone(() -> (ballCount == 4 || intakeInterrupt()))
            .setEnd(endCondition -> currentState = BotStates.full);

    Command idleFull = Command.build()
            .setStart(() -> {
                transferMotor.setPower(.1);
                intakeMotor.setPower(-.1);
                transferBlockerServo.setPosition(transferClosedAngle);
            })
            .setDone(() -> (canFire() || idleInterrupt())) // switch to firing if it can fire at the goal from its current position
            .setEnd(endCondition -> currentState = BotStates.launching);

    Command launchBalls = Command.build()
            .setStart(() -> {
                transferBlockerServo.setPosition(transferOpenAngle);
                transferMotor.setPower(1);
                intakeMotor.setPower(1);
                flywheelMotor.setVelocity(targetFlywheelSpeed);
                setHoodAngle(hoodAngle);
            })
            .setExecute(() -> {
                double currentSpeed = flywheelMotor.getVelocity();
                double wheelSpeedError = targetFlywheelSpeed - currentSpeed;
                if (wheelSpeedError > rpmDropThreshold && oldWheelSpeed > currentSpeed) { // if the speed drops since the last frame, and is below a threshold, count that as a ball being launched
                    if (!rpmDropThrottle) { // only do this if the flywheel's reached full speed or accelerated since we last counted a ball
                        ballCount--;
                        rpmDropThrottle = true;
                    }
                }
                // if rpm is high or if it's rising, allow the next drop to count as a ball
                if (oldWheelSpeed < currentSpeed || wheelSpeedError < rpmDropThreshold) {
                    rpmDropThrottle = false;
                }
                oldWheelSpeed = flywheelMotor.getVelocity();
            })
            .setDone(() -> (ballCount == 0 || launchInterrupt()))
            .setEnd(endCondition -> currentState = BotStates.intaking);

    private Boolean canFire() {
        if (goalPose.y()<72) {
            return goalPose.y() > robotPose.y();
        } else {
            return goalPose.y() < robotPose.y();
        }
    }
    private Pose getCurrentTarget() {
        double y = robotPose.y();
        if (currentAlliance != Alliance.blue) {
            if (y > 72) {
                return redGoalScoring;
            } else {
                return redGoalAudience;
            }
        } else {
            if (y < 72) {
                return blueGoalScoring;
            } else {
                return blueGoalAudience;
            }
        }

        // TODO: make this work for red and blue alliances, not just red
    }

    private double getTurretHeading() {
        // i hope this is correct (if not just negate these values)
        double x = goalPose.x() - robotPose.x();
        double y = goalPose.y() - robotPose.y();
        double absoluteHeading = Math.tan(y/x);
        return absoluteHeading - robotPose.heading();
    }

    private void setTurretTrajectory() {
        // TODO: make this set both the velocity and the angle of launch
    }

    private void setTurretTarget(double heading) {
        double turretRatio = 2.75;
        double encoderOutputPPR = 500;
        double ticksPerTurretRev = turretRatio * encoderOutputPPR;
        double ticksPerRadian = ticksPerTurretRev / (2 * Math.PI);
        double position = ticksPerRadian * heading;
        turretMotor.setTargetPosition((int) position);
        // ^rounded because encoder ticks are integers
    }

    Command runTurretAim = infinite(() -> {
                double relativeHeading = getTurretHeading();
                setTurretTarget(relativeHeading);
                turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                double error = relativeHeading - turretMotor.getCurrentPosition();
                turretMotor.setVelocity(error * 4); // error might need to be Math.abs(error), i'm unsure how running to a position works and can't be bothered to check the docs cause this probably will be changed later
                // turret speed set to several times the error, might need to be higher
                // TODO: replace with a proper pid system
            });

    Command updatePoses = infinite(() -> {
        robotPose = follower.pose();
        goalPose = getCurrentTarget();
    });

    Command mainLoop = Command.build();


    private void setHoodAngle(double angle) {
        leftHoodServo.setPosition(angle);
        rightHoodServo.setPosition(angle);
    }

    public void homeTurret() {
        turretMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        turretMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        double slowPower = .1;

        // left switch
        turretMotor.setPower(slowPower);
        if (turretLimitLeft.isPressed()) {
            while(turretLimitLeft.isPressed()) {
                telemetry.addLine("finding Left switch");
                telemetry.update();
            }
        } else {
            while (!turretLimitLeft.isPressed()) {
                telemetry.addLine("finding Left switch");
                telemetry.update();
            }
        }
        turretMotor.setPower(0);
        int leftSwitchPosition = turretMotor.getCurrentPosition();

        // reset turret to starting position
        turretMotor.setTargetPosition(0);
        turretMotor.setTargetPositionTolerance(0);
        turretMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turretMotor.setPower(-slowPower);
        while (0 != turretMotor.getCurrentPosition()) {
            telemetry.addData("current position (not homed)", turretMotor.getCurrentPosition());
            telemetry.addData("target position", 0);
            telemetry.update();
        }

        // right switch
        turretMotor.setPower(-slowPower);
        if (turretLimitRight.isPressed()) {
            while(turretLimitRight.isPressed()) {
                telemetry.addLine("finding Right switch");
                telemetry.update();
            }
        } else {
            while (!turretLimitRight.isPressed()) {
                telemetry.addLine("finding Right switch");
                telemetry.update();
            }
        }
        turretMotor.setPower(0);
        int rightSwitchPosition = turretMotor.getCurrentPosition();

        // clear the telemetry
        telemetry.update();

        // get point between, then move there and reset
        int zeroPosition = (leftSwitchPosition + rightSwitchPosition)/2;
        turretMotor.setTargetPosition(zeroPosition);
        turretMotor.setTargetPositionTolerance(0); // this value will need to be tuned, cause we probably can't perfectly get that position
        turretMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turretMotor.setPower(slowPower);
        while (zeroPosition != turretMotor.getCurrentPosition()) {
            telemetry.addData("current position", turretMotor.getCurrentPosition());
            telemetry.addData("target position", zeroPosition);
            telemetry.update();
        }
        turretMotor.setPower(0);
        turretMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        telemetry.addLine("turret homed");
        telemetry.update();

    }

    @Override
    public void runOpMode() {
        // assign motor/sensor variables to actual devices

        turretMotor = hardwareMap.get(DcMotorEx.class, "turret");
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intake");
        flywheelMotor = hardwareMap.get(DcMotorEx.class, "flywheel");
        transferMotor = hardwareMap.get(DcMotorEx.class, "transfer");

        leftHoodServo = hardwareMap.get(Servo.class, "leftHood");
        rightHoodServo = hardwareMap.get(Servo.class, "rightHood");

        rampServo = hardwareMap.get(Servo.class, "ramp");
        transferBlockerServo = hardwareMap.get(Servo.class, "blocker");

        turretLimitLeft = hardwareMap.get(RevTouchSensor.class, "leftLimit");
        turretLimitRight = hardwareMap.get(RevTouchSensor.class, "rightLimit");

        transferBreak1 = hardwareMap.get(Rev2mDistanceSensor.class, "break1");
        transferBreak2 = hardwareMap.get(Rev2mDistanceSensor.class, "break2");

        rightHoodServo.setDirection(Servo.Direction.REVERSE);


        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);
        // sets a starting position for the robot, we can change this to be where the auto ends later
        follower.update();



        waitForStart();
        if (isStopRequested()) return;

        while (opModeIsActive()) {
            //loop code here
            follower.update();
            Scheduler.execute();
        }
    }
}
