package net.debreczeni.remotedesktop.model.socket;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class RemoteDisplays implements Serializable {
    private final Map<Integer, RemoteImage> screenshotsByDisplay;

    public RemoteDisplays() {
        screenshotsByDisplay = new HashMap<>();
    }

    public void setImage(int nr, RemoteImage image) {
        screenshotsByDisplay.put(nr, image);
    }
}
