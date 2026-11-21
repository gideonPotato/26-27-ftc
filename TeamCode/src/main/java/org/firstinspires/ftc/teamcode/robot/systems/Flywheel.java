package org.firstinspires.ftc.teamcode.robot.systems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.robot.BallCountClass;
import org.firstinspires.ftc.teamcode.robot.deviceNames;

public class Flywheel {
    private final DcMotorEx motor;
    public BallCountClass ballCount;

    public Flywheel(HardwareMap hardwareMap, BallCountClass ballCount){
        motor = hardwareMap.get(DcMotorEx.class, deviceNames.flywheel);
        this.ballCount = ballCount;
    }
}
