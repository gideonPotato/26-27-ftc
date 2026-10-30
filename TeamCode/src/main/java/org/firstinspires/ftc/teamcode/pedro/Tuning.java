package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.pedropathing.tuning.autotune.Procedure;
import com.pedropathing.tuning.autotune.Tuner;
import com.sun.tools.javac.code.Attribute;

import org.firstinspires.ftc.robotcore.external.Const;
import org.firstinspires.ftc.teamcode.pedro.procedures.ForesightTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.MecanumTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.PinpointTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.Tests;

public class Tuning {
    @Tuner

//    public static Procedure mecanumTuner() {
//        return new MecanumTuner();
//    }
//
//    public static Procedure pinpointTuner() {
//        return new PinpointTuner();
//    }
//    public static Procedure foresightTuner() {
//        return new ForesightTuner((hardwareMap) -> new PinpointLocalizer(hardwareMap, Constants.localizerConfig), (hardwareMap) -> new Mecanum(hardwareMap, Constants.drivetrainConfig));
//    }

    public static Procedure tests() {
        return new Tests(
                hardwareMap -> new Mecanum(hardwareMap, Constants.drivetrainConfig),
                (hardwareMap -> new PinpointLocalizer(hardwareMap, Constants.localizerConfig)),
                () -> new Foresight(Constants.acrylicChassisForesight)
        );
    }
}
