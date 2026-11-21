package org.firstinspires.ftc.teamcode.robot.systems;


import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.commands.Commands;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.robot.BallCountClass;
import org.firstinspires.ftc.teamcode.robot.deviceNames;

public class Intake {
    private DcMotorEx motor;
    static double inwards = 1;
    static double eject = -1;
    static double hold = 0;
    static double idle = -.5;
    private BallCountClass ballCount;

    public void map(HardwareMap hardwareMap, BallCountClass ballCount) {
        motor = hardwareMap.get(DcMotorEx.class, deviceNames.intake);
        this.ballCount=ballCount;
    }

    public void run(double power){
        motor.setPower(power);
    }

    public void runIn() {run(inwards);}
    public void runOut() {run(eject);}
    public void runHold() {run(hold);}
    public void runIdle() {run(idle);}

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

    public Command runIntake = Commands.conditional(() -> (ballCount.balls < 4), in(), out());

}
