package org.firstinspires.ftc.teamcode.robot.systems;

import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.commands.Commands;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.robot.RobotConstants.*;

public class Transfer {
    private final DcMotorEx motor;
    private final Rev2mDistanceSensor beam;
    private BallCountClass ballCounter;
    private boolean triggered = false;

    public Transfer(HardwareMap hardwareMap, BallCountClass ballCounter) {
        motor = hardwareMap.get(DcMotorEx.class, DeviceNames.transferMotor);
        beam = hardwareMap.get(Rev2mDistanceSensor.class, DeviceNames.transferBeamBreak);
        this.ballCounter = ballCounter;
    }

    private void run(double power){
        motor.setPower(power);
    }

    private void runIn(){run(SubsystemConstants.transferIn);}
    private void runOut(){run(SubsystemConstants.transferOut);}
    private void runHold(){run(SubsystemConstants.transferHold);}

    public Command in() {
        return Commands.instant(this::runIn);
    }
    public Command out() {
        return Commands.instant(this::runOut);
    }
    public Command hold() {
        return Commands.instant(this::runHold);
    }

    public Command countBalls(){
        return Commands.infinite(() -> {
            if (beam.getDistance(DistanceUnit.MM) < SubsystemConstants.beamBreakDistance) {
                if (!triggered) {
                    ballCounter.balls++;
                    triggered = true;
                }
            } else {
                triggered = false;
            }
        });
    }

    public Command runTransfer = Commands.conditional(
            () -> (ballCounter.balls < 4),
            in(),
            hold()
    );
}
