package org.thingsboard.server.common.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.device.data.DeviceData;
import org.thingsboard.server.common.data.id.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@EqualsAndHashCode(callSuper = true)
@Slf4j
public class Testing extends SearchTextBasedWithAdditionalInfo<TestingId> implements HasName, HasTenantId, HasCustomerId{

    private static final long serialVersionUID = 2807343040519543363L;

    private TenantId tenantId;
    private CustomerId customerId;
    private String name;
    private String sensorType;
    private String model;
    private String protocol;

    @JsonIgnore
    private byte[] testingDataBytes;

    public Testing() {
        super();
    }

    public Testing(TestingId id) {
        super(id);
    }

    public Testing(Testing device) {
        super(device);
        this.tenantId = device.getTenantId();
        this.customerId = device.getCustomerId();
        this.name = device.getName();
        this.sensorType = device.getSensorType();
        this.model = device.getModel();
        this.protocol = device.getProtocol();
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public void setCustomerId(CustomerId customerId) {
        this.customerId = customerId;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public String getSearchText() {
        return getName();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Device [tenantId=");
        builder.append(tenantId);
        builder.append(", customerId=");
        builder.append(customerId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", additionalInfo=");
        builder.append(getAdditionalInfo());
        builder.append(", createdTime=");
        builder.append(createdTime);
        builder.append(", id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }

}
