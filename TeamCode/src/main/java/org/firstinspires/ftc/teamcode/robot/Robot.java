package org.firstinspires.ftc.teamcode.robot;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.groups.Groups;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.robot.RobotConstants.*;
import org.firstinspires.ftc.teamcode.robot.systems.*;

public class Robot {

    private final Drivetrain drivetrain;
    private final Intake intake;
    private final Transfer transfer;
    private final Turret turret;
    private final Flywheel flywheel;
    private final LynxHandler lynxHandler;
    private final BallCountClass ballCounter;
    private final GoalTracker goalTracker;
    private final Follower follower;
    private final InputHandler inputs;

    public Robot(HardwareMap hardwareMap, Alliance alliance, Gamepad gamepad1, Gamepad gamepad2) {
        this.ballCounter = new BallCountClass();
        this.drivetrain = new Drivetrain(hardwareMap);
        this.follower = drivetrain.follower;
        this.goalTracker = new GoalTracker(follower, alliance);
        this.lynxHandler = new LynxHandler(hardwareMap);

        this.inputs = new InputHandler(gamepad1, gamepad2);

        this.intake = new Intake(hardwareMap, ballCounter);
        this.transfer = new Transfer(hardwareMap,ballCounter);
        this.turret = new Turret(hardwareMap, goalTracker, follower, ballCounter);
        this.flywheel = new Flywheel(hardwareMap, ballCounter, follower, goalTracker);

        // TEMPORARY
        follower.setPose(new Pose(0,0,0));
        follower.update();
    }



    public Command teleopInstants() {
        return Groups.parallel(
                drivetrain.update,
                transfer.countBalls(),
                flywheel.countBalls(),

                goalTracker.update,
                lynxHandler.update,

                drivetrain.driveWithPowers(inputs.driveForward(), inputs.driveLateral(), inputs.driveAngular()),
                intake.runIntake,
                transfer.runTransfer,
                turret.runTurret,
                flywheel.runTurret
        );
    }

    public Command teleopRun() {
        return Groups.loop(teleopInstants());
    }

    public Command homeTurret() {
        return turret.home;
    }

}
