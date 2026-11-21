package org.firstinspires.ftc.teamcode.robot.systems;

import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.commands.Commands;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.robot.BallCountClass;
import org.firstinspires.ftc.teamcode.robot.deviceNames;

public class Transfer {
    private final DcMotorEx motor;
    private final Rev2mDistanceSensor beam;
    public BallCountClass ballCount;
    private boolean triggered = false;
    private final double distance = 0; // this should be a bit less than the distance the beam break reads with no ball in the spot

    static double in = 1;
    static double hold = 0;
    static double out = -.5;

    public Transfer(HardwareMap hardwareMap, BallCountClass ballCount) {
        motor = hardwareMap.get(DcMotorEx.class, deviceNames.transfer);
        beam = hardwareMap.get(Rev2mDistanceSensor.class, deviceNames.beamBreak);
        this.ballCount = ballCount;
    }

    private void runIn(){motor.setPower(in);}
    private void runOut(){motor.setPower(out);}
    private void runHold(){motor.setPower(hold);}

    public Command in() {
        return Commands.instant(this::runIn);
    }
    public Command out() {
        return Commands.instant(this::runOut);
    }
    public Command hold() {
        return Commands.instant(this::runHold);
    }

    private Command countBalls(){
        return Commands.infinite(() -> {
            if (beam.getDistance(DistanceUnit.MM) < distance) {
                if (!triggered) {
                    ballCount.balls++;
                    triggered = true;
                }
            } else {
                triggered = false;
            }
        });
    }

    public Command runTransfer = Commands.conditional(
            () -> (ballCount.balls < 4),
            in(),
            hold()
    ).with(
            countBalls()
    );
}
