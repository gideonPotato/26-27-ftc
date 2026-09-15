package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
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
        c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.frontRightDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backLeftDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backRightDirection.set(DcMotorSimple.Direction.REVERSE);
    });
    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(0.8358923093540462);
        c.yPodOffset.set(3.0050572072427104);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });
    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.12064564986636053);
                Controller secondaryTranslationalForward = Controller.proportional(0.044575343914483846);
                Controller primaryTranslationalLateral = Controller.proportional(0.16940354361301915);
                Controller secondaryTranslationalLateral = Controller.proportional(0.06259008281895866);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.018531987291017564));
                c.brake.set(Controller.proportionalFeedforward(0.01575218919736493));

                c.headingFeedback.set(Controller.proportional(4.228006905591377));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.05760316187573826, 0.00315816663384301));

                c.linearBrakeCoefficients.set(Matrix.diag(0.06124831379045534, 0.075867760106373));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.0012590636627971958, 9.488326812439392E-4));

                c.maxAchievableForwardVelocity.set(57.689801222263064);
                c.maxAchievableStrafeVelocity.set(47.905192939797224);
                c.naturalForwardDeceleration.set(54.6289872868334);
                c.naturalStrafeDeceleration.set(74.9972813475293);
            }
    );
    public static Follower create(HardwareMap h) {
        // return new Follower(Drivetrain, Localizer, Foresight);

        return null;
    }
}