package org.firstinspires.ftc.teamcode.robot.systems;



import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.commands.Commands;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.robot.RobotConstants.*;

public class Intake {
    private final DcMotorEx motor;
    private BallCountClass ballCounter;

    public Intake(HardwareMap hardwareMap, BallCountClass ballCounter) {
        motor = hardwareMap.get(DcMotorEx.class, DeviceNames.intakeMotor);
        this.ballCounter = ballCounter;
    }

    private void run(double power){
        motor.setPower(power);
    }

    private void runIn() {run(SubsystemConstants.intakeIn);}
    private void runOut() {run(SubsystemConstants.intakeOut);}
    private void runHold() {run(SubsystemConstants.intakeHold);}
    private void runIdle() {run(SubsystemConstants.intakeIdle);}

    // these are only public if you want to manually override something. might be worth doing that as a command that runs in parallel with/overwrites runIntake when you implement it
    public Command in() {
        return Commands.instant(this::runIn);
    }

    public Command out() {
        return Commands.instant(this::runOut);
    }

    public Command hold() {
        return Commands.instant(this::runHold);
    }

    public Command idle() {
        return Commands.instant(this::runIdle);
    }

    public Command runIntake = Commands.conditional(() -> (ballCounter.balls < 4), in(), idle());

}
