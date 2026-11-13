package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

// helper opmode, just displays the position of a few things without running anything, so that we can find conversions

@TeleOp
public class configuration extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotorEx turret = hardwareMap.get(DcMotorEx.class, "turret");
        DcMotorEx flyweel = hardwareMap.get(DcMotorEx.class, "flywheel");

        waitForStart();
        if (isStopRequested()) return;
        while (opModeIsActive()) {
            telemetry.addData("turret pos", turret.getCurrentPosition());
            double numebrrrr = turret.getCurrentPosition();
            numebrrrr = numebrrrr / 360;
            telemetry.addData("value if you've moved it one full rotation", numebrrrr);
            telemetry.addData("flywheel pos", flyweel.getCurrentPosition());
            telemetry.update();

        }
    }
}
