package org.firstinspires.ftc.teamcode.robot.systems;

import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.commands.Commands;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.List;

public class LynxHandler {

    List<LynxModule> hubs;

    public LynxHandler(HardwareMap hardwareMap) {
        hubs = hardwareMap.getAll(LynxModule.class);

        for (LynxModule hub : hubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }
    }

    // this command clears the cache of sensor values (not including i2c) for both expansion and control hubs. it should be run every loop to keep sensor values up to date
    public Command update = Commands.instant(() -> {
        for (LynxModule hub : hubs) {
            hub.clearBulkCache();
        }
    });
}
