package org.firstinspires.ftc.teamcode.opmodes;

import com.pedropathing.ivy.Scheduler;
import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.robot.Robot;
import org.firstinspires.ftc.teamcode.robot.RobotConstants;

@TeleOp
public class HomeTurret extends LinearOpMode {

    @Override
    public void runOpMode() {
        Robot robot = new Robot(hardwareMap, RobotConstants.Alliance.BLUE, gamepad1, gamepad2, telemetry);
        Scheduler.reset();

        RevTouchSensor leftSwitch = hardwareMap.get(RevTouchSensor.class, RobotConstants.DeviceNames.leftTurretLimitSwitch);
        RevTouchSensor rightSwitch = hardwareMap.get(RevTouchSensor.class, RobotConstants.DeviceNames.rightTurretLimitSwitch);

        telemetry.addLine("This opmode homes the turret. Only run it if the turret is not already homed, due to a power loss, unplugged wire, rapid unplanned disassembly, etc.");
        telemetry.addLine("As a check to make sure the limit switches are wired correctly, please press the left limit switch as viewed from the rear of the robot");
        telemetry.update();

        while (!rightSwitch.isPressed() && opModeIsActive()) {
            if (leftSwitch.isPressed()) {
                telemetry.addLine("That's the wrong switch.");
                telemetry.addLine("Please press the left limit switch as viewed from the rear of the robot, or, if necessary, reconfigure so that the one you pressed is called" + RobotConstants.DeviceNames.leftTurretLimitSwitch);
                telemetry.update();
            }
        }
        telemetry.addLine("As a check to make sure the limit switches are wired correctly, please press the right limit switch as viewed from the rear of the robot");

        while (!leftSwitch.isPressed() && opModeIsActive()) {
            if (rightSwitch.isPressed()) {
                telemetry.addLine("That's the wrong switch.");
                telemetry.addLine("Please press the right limit switch as viewed from the rear of the robot, or, if necessary, reconfigure so that the one you pressed is called" + RobotConstants.DeviceNames.rightTurretLimitSwitch);
                telemetry.update();
            }
        }

        waitForStart();
        if (isStopRequested()) return;
        while (opModeIsActive() && Scheduler.isScheduled(robot.homeTurret())) {
            Scheduler.schedule(robot.homeTurret());
            Scheduler.execute();
        }
    }
}
