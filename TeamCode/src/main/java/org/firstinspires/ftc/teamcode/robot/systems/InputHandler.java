package org.firstinspires.ftc.teamcode.robot.systems;

import com.qualcomm.robotcore.hardware.Gamepad;

public class InputHandler {
    Gamepad gamepad1, gamepad2;

    public InputHandler(Gamepad gamepad1, Gamepad gamepad2) {
        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;
    }

    public double driveForward() {return -gamepad1.left_stick_y;}
    public double driveLateral() {return gamepad1.left_stick_x;}
    public double driveAngular() {return gamepad1.right_stick_x;}
}
