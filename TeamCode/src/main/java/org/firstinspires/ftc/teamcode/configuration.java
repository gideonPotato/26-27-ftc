package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

// helper opmode, just displays the position of a few things without running anything, so that we can find conversions

@TeleOp
public class configuration extends LinearOpMode {

    static double min = .277;
    static double max = .722;
    static double change = .0001;
    double color = min;

    @Override
    public void runOpMode() {
        DcMotorEx turret = hardwareMap.get(DcMotorEx.class, "turret");
        DcMotorEx flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        DcMotorEx transfer = hardwareMap.get(DcMotorEx.class, "transfer");
        DcMotorEx intake = hardwareMap.get(DcMotorEx.class, "intake");
        Servo rave = hardwareMap.get(Servo.class, "lights");

        waitForStart();
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flywheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        transfer.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intake.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        if (isStopRequested()) return;
        while (opModeIsActive()) {
            telemetry.addData("intake pos", intake.getCurrentPosition());
            telemetry.addData("transfer pos", transfer.getCurrentPosition());
            telemetry.addData("turret pos", turret.getCurrentPosition());
            telemetry.addData("turret pos/360", turret.getCurrentPosition()/360);
            telemetry.addData("flywheel pos", flywheel.getCurrentPosition());
            telemetry.addData("flywheel pos/360", flywheel.getCurrentPosition()/360);
            telemetry.update();

            color += change;
            if (color > max || color < min) {
                change *= -1;
                color += change;
            }
            rave.setPosition(color);

        }
    }
}
