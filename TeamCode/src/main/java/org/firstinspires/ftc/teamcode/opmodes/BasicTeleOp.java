package org.firstinspires.ftc.teamcode.opmodes;

import com.pedropathing.ivy.Scheduler;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.robot.Robot;
import org.firstinspires.ftc.teamcode.robot.RobotConstants;

@TeleOp
public class BasicTeleOp extends LinearOpMode {

    @Override
    public void runOpMode() {
        Robot robot = new Robot(hardwareMap, RobotConstants.Alliance.RED, gamepad1, gamepad2, telemetry);

        Scheduler.reset();

        waitForStart();
        if (isStopRequested()) return;

        Scheduler.schedule(robot.teleopRun());

        while (opModeIsActive()) {
            Scheduler.execute();
        }
    }
}
