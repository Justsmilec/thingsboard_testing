package org.thingsboard.server.common.data;

import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TestingId;

public class TestingInfo extends Testing{
    private String customerTitle;
    private boolean customerIsPublic;

    public TestingInfo() {
        super();
    }

    public TestingInfo(TestingId deviceId) {
        super(deviceId);
    }

    public TestingInfo(Testing device, String customerTitle, boolean customerIsPublic) {
        super(device);
        this.customerTitle = customerTitle;
        this.customerIsPublic = customerIsPublic;
    }
}
