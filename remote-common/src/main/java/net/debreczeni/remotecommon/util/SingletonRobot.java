package net.debreczeni.remotecommon.util;

import lombok.Singular;
import lombok.SneakyThrows;

import java.awt.*;

public final class SingletonRobot {
    private static Robot single_instance = null;

    private SingletonRobot() {
    }

    @SneakyThrows
    public static Robot getInstance() {
        if (single_instance == null)
            single_instance = new Robot();

        return single_instance;
    }
}
