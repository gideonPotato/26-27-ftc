package org.firstinspires.ftc.teamcode.robot.systems;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.commands.Commands;
import com.pedropathing.math.Pose;

import org.firstinspires.ftc.teamcode.robot.RobotConstants.*;

public class GoalTracker {
    private final Follower follower;
    private final Alliance alliance;
    public Pose goal;

    public GoalTracker(Follower follower, Alliance alliance) {
        this.follower = follower;
        this.alliance = alliance;
    }

    private void updateGoal() {
        if (follower.pose().y() < 72) {
            if (alliance == Alliance.BLUE) goal = Poses.blueAudience; else goal = Poses.redAudience;
        } else if (alliance == Alliance.BLUE) goal = Poses.blueScoring; else goal = Poses.redScoring;
    }
    public Command update = Commands.instant(this::updateGoal);
}
