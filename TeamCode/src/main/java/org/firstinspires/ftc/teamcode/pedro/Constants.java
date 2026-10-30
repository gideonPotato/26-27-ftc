package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set("frontLeft");
        c.frontRightName.set("frontRight");
        c.backLeftName.set("backLeft");
        c.backRightName.set("backRight");
        c.frontLeftDirection.set(DcMotorSimple.Direction.FORWARD);
        c.frontRightDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backLeftDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backRightDirection.set(DcMotorSimple.Direction.REVERSE);
    });
    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(-4.744404469888042);
        c.yPodOffset.set(1.8993804210752954);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });
    public static ForesightConfig acrylicChassisForesight = new ForesightConfig(
            // making this name specific to the chassis/rest of the bot, so that we can do things like quickly swap to the acrylic panels if the polycarb breaks or something. probably fine to delete as we add more permanent things on though.
            // in short, don't delete this when you reconfigure, make a new foresightconfig, and name it according to the robot you're tuning it for
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.20209188794684144);
                Controller secondaryTranslationalForward = Controller.proportional(0.07466755260165885);
                Controller primaryTranslationalLateral = Controller.proportional(0.30582425640434585);
                Controller secondaryTranslationalLateral = Controller.proportional(0.11299389096677295);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.013827601909211769));
                c.brake.set(Controller.proportionalFeedforward(0.011753461622830003));

                c.headingFeedback.set(Controller.proportional(3.0465447728745176));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.03117027220958883, 0.0078958638915876));

                c.linearBrakeCoefficients.set(Matrix.diag(0.031024229126925745, 0.022403198522584624));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.001961232816509029, 0.0021068267650300116));

                c.maxAchievableForwardVelocity.set(72.80987298831698);
                c.maxAchievableStrafeVelocity.set(63.68403713194634);
                c.naturalForwardDeceleration.set(61.723391453690326);
                c.naturalStrafeDeceleration.set(82.90260329801517);
            }
    );

    public static Follower create(HardwareMap h) {
        return new Follower(
                new PinpointLocalizer(h, localizerConfig),
                new Mecanum(h, drivetrainConfig),
                new Foresight(acrylicChassisForesight)
        );
    }
}