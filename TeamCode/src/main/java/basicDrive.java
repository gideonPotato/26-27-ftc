import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.pedro.Constants;
// HEY YOU. AUSTIN PROBABLY. IF YOU'RE TRYING TO RUN THIS CODE AND IT'S NOT WORKING, CHECK THAT THE ROBOT IS TUNED AND THAT CONSTANTS.JAVA IS ACTUALLY SET UP RIGHT

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

        follower = Constants.create(hardwareMap);

        telemetry.addLine("if the code crashes or whatever when you try to run it you probably didn't set up constants.java yet");
        telemetry.addLine("or maybe i'm just stupid and wrote something wrong here, i dunno");
        telemetry.update();


        waitForStart();
        if (isStopRequested()) return;

        while (opModeIsActive()) {
            //loop code here
            follower.manual(
                    /* follower.manual() just drives according to standard robot centric driving.
                    manualdrive.fieldcentric() converts to be robot centric */
                    ManualDrive.fieldCentric(
                            -gamepad1.left_stick_y,
                            gamepad1.left_stick_x,
                            gamepad1.right_stick_x,
                            follower.pose().heading()
                    )
            );

            intake.setVelocity(1000*gamepad2.right_stick_y);
            // THIS IS GAMEPAD 2, NOT 1


            // ^this can obviously be changed, i'm still unsure what the ideal maximum value to feed to setvelocity is, but 1k is well below it
            // my head hurts (this is unrelated to the code, i probably need water)
            // the rev website or perhaps the ftc docs probably have the maximum encoder ticks for a bare motor, it shouldn't be hard to find i just don't feel like it

            follower.update();
        }
    }
}
