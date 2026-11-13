package org.firstinspires.ftc.teamcode;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.pedropathing.follower.Follower;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@TeleOp
public class manualControl extends LinearOpMode {

    // variables(?)
    Pose robotPose;
    Pose goalPose;

    Follower follower;

    DcMotorEx intakeMotor;
    DcMotorEx transferMotor;
    DcMotorEx turretMotor;
    DcMotorEx flywheelMotor;

    Servo leftHood;
    Servo rightHood;

    Servo blockerServo;

    enum Alliance {
        red,
        blue
    }

    private final PoseFactory poseFactory = PoseFactory.radians();


    // constants
    double blockerClosed = 0.45;
    double blockerOpen = 0.55; //both of these set to smaller values to hopefully not break things

    double servoRangesPerHoodDegree = 0; // currently set to 0 so it won't move until we get an actually correct value set
    double encoderTicksPerTurretDegree = 2.96; // same as above
    double encoderTicksPerFlywheelCircumferenceInch = 3.14606;

    Alliance currentAlliance = Alliance.red;

    private final Pose redGoalAudience = poseFactory.of(60, 50, 0);
    private final Pose redGoalScoring = poseFactory.of(60, 94, 0);
    private final Pose blueGoalAudience = poseFactory.of(84, 50, 0);
    private final Pose blueGoalScoring = poseFactory.of(84, 94, 0);

    private final Pose startPose = poseFactory.of(6.25, 5, Math.toRadians(90)); // start the bot in this position (red tape corner, forward facing along red alliance area)


    // binds
    boolean shouldIntake() {
        return gamepad1.a;
    }
    boolean shouldTransfer() {
        return gamepad1.b;
    }
    boolean shouldLaunch() {
        return gamepad1.left_trigger_pressed;
    }

    // functions
    private void moveHood(double angle) {
        if (angle >= 5 && angle <= 20) { // check that this is within bounds (this is degrees forward from vertical)
            double servoPos = angle * servoRangesPerHoodDegree;
            leftHood.setPosition(servoPos);
            rightHood.setPosition(-servoPos);
        }
    }

    private void moveTurret(double angle) {
        double target = angle * encoderTicksPerTurretDegree;
        double currentTicks = turretMotor.getCurrentPosition();
        double currentAngle = currentTicks / encoderTicksPerTurretDegree;
        telemetry.addData("current ticks", currentTicks);
        telemetry.addData("current relative turret angle", currentAngle);
//        double p = 1; // also it needs tuned
//        double d = -1; // THIS SHOULD BE MOVED TO BE CONSTANTS, AND NOT CREATED EVERY LOOP IF DOING THIS WORKS
//        double currentVelocity = turretMotor.getVelocity(AngleUnit.DEGREES);
//        double error = target - currentAngle;
//        double max = 360;
//        double whatDoICallThis = p * error + d * currentVelocity; // this works right? we go with a power proportional to the error, then subtract a power proportional to the speed? i think?
//        double power = whatDoICallThis/max;
//        turretMotor.setPower(power);
        turretMotor.setTargetPosition((int) target);
        turretMotor.setVelocity(-200);

    }

    private Pose getCurrentTarget() { // copied from what was at the time redTeleop.java, which may or may not exist by the time you read this
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
    }

    private double getTurretHeading() {
        double x = goalPose.x() - robotPose.x();
        double y = goalPose.y() - robotPose.y();
        double absoluteHeading = Math.atan2(y, x);
        return Math.toDegrees(absoluteHeading - robotPose.heading());
    }

    private void setTrajectory() {
        double m = -55; // final slope
        double x = Math.sqrt(Math.pow(goalPose.x() - robotPose.x(), 2) + Math.pow(goalPose.y() - robotPose.y(), 2)); // direct horizontal distance from center of robot to goal (if this is inaccurate, that's probably because the turret isn't centered on the chassis)
        double y = 60 - 12; // general estimate on the vertical distance from the center of the goal (60) to the turret (12). both are approximate, and in inches
        double a = m * (x-y)/Math.pow(x, 2); // quadratic formula reference
        double b = m - 2 * a * x; // this will be hell to debug if it's not functional. i am just copying this down from the desmos graph though, so we should be fine
        double vertexX = -b/(2 * a); // me when determinant
        double vertexY = a * Math.pow(vertexX, 2) + b * vertexX; // no constant term because our parabola lies on the origin (because our origin is the turret)
        double g = 385.8268; // this is 9.8 m/s^2 in inches per second^2 because we're in inches for some reason
        double exitVelocity = vertexY/vertexX - .5 * g * vertexX;
        int flywheelVelocity = (int) (exitVelocity * encoderTicksPerFlywheelCircumferenceInch); // in/s * ticks/in = ticks/s which i think is what we want
        flywheelMotor.setVelocity(flywheelVelocity);

        // ax^2+bx is our parabola, so 2ax+b should be its derivative
        // at the origin (also where we launch from) the derivative is 2a(0)+b -> b
        // so the angle should be arctan(b)
        double hoodAngle = 90 - Math.atan(b);
        moveHood(hoodAngle);
    }

    private void runTurret() {
        double relativeHeading = getTurretHeading();
        telemetry.addData("heading on the robot of the turret (target)", relativeHeading);
        moveTurret(relativeHeading);
    }

    private void drive() {
        DrivePowers powers = ManualDrive.fieldCentric(
                -gamepad1.left_stick_y,
                -gamepad1.left_stick_x,
                -gamepad1.right_stick_x,
                follower.pose().heading()
        );
        follower.manual(powers);
        follower.update();
        robotPose = follower.pose();
        goalPose = getCurrentTarget();
    }

    private void randomStuff() {
        if (shouldIntake()) {
            intakeMotor.setPower(1);
        } else intakeMotor.setPower(0);

        if (shouldTransfer()) {
            transferMotor.setPower(.75);
        } else transferMotor.setPower(0);

        blockerServo.setPosition((shouldLaunch()) ? blockerOpen : blockerClosed); // (bool) ? thing1 : thing2 is equivalent to if (bool) { thing1 } else { thing2 }
    }



    @Override
    public void runOpMode() {

        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);
        follower.update();

        intakeMotor = hardwareMap.get(DcMotorEx.class, "intake");
        transferMotor = hardwareMap.get(DcMotorEx.class, "transfer");
        turretMotor = hardwareMap.get(DcMotorEx.class, "turret");
        flywheelMotor = hardwareMap.get(DcMotorEx.class, "flywheel");

        intakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        transferMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        turretMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        flywheelMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        turretMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turretMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftHood = hardwareMap.get(Servo.class, "left");
        rightHood = hardwareMap.get(Servo.class, "right");

        blockerServo = hardwareMap.get(Servo.class, "blocker");

        telemetry.addLine("YOU'D BETTER HAVE THE TURRET UNPLUGGED AND FACING FORWARDS");
        telemetry.addLine("INSERT A LOT OF TEXT TO MAKE IT REALLY OBVIOUS THAT THERE'S SOMETHING HERE");
        telemetry.addLine("      #####  ");
        telemetry.addLine("     #&@@@&# ");
        telemetry.addLine("    #@@@@@@&#");
        telemetry.addLine("   #&@@@@&@@#");
        telemetry.addLine("   #@@@@@&&@#");
        telemetry.addLine("   #@@&,,,,##");
        telemetry.addLine("   #@&,....##");
        telemetry.addLine("  ##===,,,#&#");
        telemetry.addLine(" #==**--==## ");
        telemetry.addLine(" #***-*|--*# ");
        telemetry.addLine(" #**||,.||,,#");
        telemetry.addLine(" #/*=|,.||,,#");
        telemetry.addLine("#/%*==*****# ");
        telemetry.addLine("#//= ======# ");
        telemetry.addLine(" ###=**##**# ");
        telemetry.addLine("   #**# #**# ");
        telemetry.addLine("   #*,# #*,# ");
        telemetry.addLine("madeline stretcheleste");
        telemetry.addLine("i put way too much effort into making this");
        telemetry.addLine("this is going to look really stupid if the font's not monospace, but it should still work");
        telemetry.update();

        waitForStart();
        if (isStopRequested()) return;

        while (opModeIsActive()) {

            drive();
            randomStuff();
            runTurret();
            setTrajectory();

            telemetry.addData("x", robotPose.x());
            telemetry.addData("y", robotPose.y());
            telemetry.addData("heading", robotPose.heading());

            telemetry.update();
        }
    }
}
