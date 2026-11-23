package org.firstinspires.ftc.teamcode.robot.systems;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.commands.Commands;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.pedro.Constants;

public class Drivetrain {
    public Follower follower;
    public boolean isFieldCentric = true;

    public Drivetrain(HardwareMap hardwareMap) {
        this.follower = Constants.create(hardwareMap);
    }

    public Command driveWithPowers(double forward, double lateral, double turn) {
        return Commands.instant(() -> {
            DrivePowers powers;
            if (isFieldCentric) {
                powers = ManualDrive.fieldCentric(forward, lateral, turn, follower.pose().heading());
            } else {
                powers = ManualDrive.fieldCentric(forward, lateral, turn, 0);
            }
            follower.manual(powers);
        });
    }

    public Command update = Commands.instant(() -> follower.update());
}
