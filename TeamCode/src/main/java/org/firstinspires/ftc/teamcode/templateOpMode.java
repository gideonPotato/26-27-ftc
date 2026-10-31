package org.firstinspires.ftc.teamcode;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.parallel;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import org.firstinspires.ftc.teamcode.pedro.Constants;

// hopefully this will work as just a template we can base future opmodes? this will probably be updated as time goes on but yeah


@TeleOp
public class templateOpMode extends LinearOpMode {


    DcMotorEx turretMotor;
    // pedro stuff
    private Follower follower;
    private final PoseFactory poseFactory = PoseFactory.degrees();
    // only pose factory, used to make all the poses (unless you want a separate one to make radian ones with? idk

    private final Pose examplepose = poseFactory.of(12, 12, 90);

    private Path examplePath() {
        return line(examplepose, examplepose).linear(examplepose,examplepose);
        // pose start, pose end, pose heading start/end
    }


    // ivy stuff

    private Command exampleCommand2 = Command.build()
            .setStart(() -> {
                turretMotor.setPower(1);
            })
            .setDone(() ->
                turretMotor.getVelocity() > 1000
                    //this is the condition for it to end yep once the velocity is 1000 it stops
            )
            .setEnd(endCondition -> {
                turretMotor.setPower(0);
            });
    // ohhhh this is a bit scrungly i don't like how i did that, but it's fine
    private Command exampleCommand() {
        return parallel(
                follow(follower, examplePath()),
                follow(follower, examplePath())
                // this can take more arguments, will run in parallel
        );
    }

    @Override
    public void runOpMode() {
        // motors/sensors

        turretMotor = hardwareMap.get(DcMotorEx.class, "turret");

        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        follower.setPose(examplepose);
        // sets a starting position
        follower.update();


        schedule(exampleCommand());

        waitForStart();
        if (isStopRequested()) return;

        while (opModeIsActive()) {
            //loop code here
            follower.update();
            Scheduler.execute();
        }
    }
}
