package org.firstinspires.ftc.teamcode;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@TeleOp
public class TestTeleop extends OpMode {

    private Follower follower;

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
    }

    @Override
    public void start() {

    }

    @Override
    public void loop() {
        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;
        double rx = gamepad1.right_stick_x;
        double mult = gamepad1.right_trigger/2+0.5;
        rx *= mult;
        x *= mult;
        y *= mult;
        DrivePowers powers = ManualDrive.fieldCentric(
                y, x, rx,
                follower.pose().heading()
        );

        follower.manual(powers);
        follower.update();
        Pose robotPose = follower.pose();

        telemetry.addData("x", robotPose.x());
        telemetry.addData("y", robotPose.y());
        telemetry.addData("heading", Math.toDegrees(robotPose.heading()));

    }
}
