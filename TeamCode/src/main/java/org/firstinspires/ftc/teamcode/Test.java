package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcontroller.external.samples.SensorGoBildaPinpoint;

@TeleOp
public class Test extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        SensorGoBildaPinpoint pinpoint = hardwareMap.get(SensorGoBildaPinpoint.class, "pinpoint");
//        DcMotorEx flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
//        Servo left = hardwareMap.servo.get("left");
//        Servo right = hardwareMap.servo.get("right");

//        left.setDirection(Servo.Direction.REVERSE);

//        double servoPosition = 0;

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
//            if (gamepad1.dpad_down) {
//                servoPosition = -1;
//            } else if (gamepad1.dpad_up) {
//                servoPosition = 1;
//            }
//
//            if (gamepad1.a) {
//                flywheel.setPower(1);
//            } else {
//                flywheel.setPower(0);
//            }
//
//            left.setPosition(servoPosition);
//            right.setPosition(servoPosition);
//            telemetry.addData("AAAAA", servoPosition);
//            pinpoint.;
//            telemetry.addData();
            telemetry.update();
        }
    }
}
