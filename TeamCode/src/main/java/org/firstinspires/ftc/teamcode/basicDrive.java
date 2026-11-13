package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.pedro.Constants;

// should just be a basic robot-centric and intake control yep

@TeleOp
public class basicDrive extends LinearOpMode {


    // pedro stuff
    private Follower follower;
    // don't need any pose stuff cause this is just direct driving, no automatic stuff, still need the follower for field-centric though


    @Override
    public void runOpMode() {
        // motors/sensors

        DcMotorEx intake = hardwareMap.get(DcMotorEx.class, "intake");
        DcMotorEx flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        DcMotorEx turret = hardwareMap.get(DcMotorEx.class, "turret");
        Servo leftHood = hardwareMap.get(Servo.class, "left");
        Servo rightHood = hardwareMap.get(Servo.class, "right");

        follower = Constants.create(hardwareMap);

        double flywheelSpeed = 4000;
        double servoHood = 0.5;

        waitForStart();
        if (isStopRequested()) return;

        while (opModeIsActive()) {
            // loop code here
            follower.manual(
                    /* follower.manual() just drives according to standard robot centric driving.
                    manualdrive.fieldcentric() converts to be robot centric */
                    ManualDrive.fieldCentric(
                            -gamepad1.left_stick_y,
                            -gamepad1.left_stick_x,
                            -gamepad1.right_stick_x,
                            follower.pose().heading()
                    )
            );

            if (gamepad1.left_bumper) {
                intake.setVelocity(-1000);
            } else if (gamepad1.left_trigger_pressed) {
                intake.setVelocity(2500);
            } else {
                intake.setVelocity(0);
            }
            flywheel.setVelocity(flywheelSpeed);

            if (gamepad1.dpadDownWasReleased()) {
                flywheelSpeed -= 50;
            }
            if (gamepad1.dpadUpWasReleased()) {
                flywheelSpeed += 50;
            }
            if (gamepad1.dpadLeftWasReleased()) {
                servoHood -= .05;
            }
            if (gamepad1.dpadRightWasReleased()) {
                servoHood += .05;
            }
            leftHood.setPosition(servoHood);
            rightHood.setPosition(1-servoHood);
            turret.setPower(gamepad2.left_stick_y);

            telemetry.addData("speed", flywheelSpeed);
            telemetry.addData("angle", servoHood);
            telemetry.update();
        }
    }
}
