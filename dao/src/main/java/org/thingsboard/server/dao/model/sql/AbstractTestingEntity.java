/**
 * Copyright © 2016-2021 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.dao.model.sql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.Testing;
import org.thingsboard.server.common.data.device.data.DeviceData;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.model.SearchTextEntity;
import org.thingsboard.server.dao.util.mapping.JacksonUtil;
import org.thingsboard.server.dao.util.mapping.JsonBinaryType;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
@MappedSuperclass
public abstract class AbstractTestingEntity<T extends Testing> extends BaseSqlEntity<T> implements SearchTextEntity<T> {

    @Column(name = "tenant_id", columnDefinition = "uuid")
    private UUID tenantId;

    @Column(name = "costumer_id", columnDefinition = "uuid")
    private UUID customerId;


    @Column(name = "name")
    private String name;

    @Column(name = "sensor_type")
    private String sensorType;

    @Column(name = "model")
    private String model;

    @Column(name = "protocol")
    private String protocol;

    @Type(type = "json")
    @Column(name = "additional_info")
    private JsonNode additionalInfo;



    public AbstractTestingEntity() {
        super();
        System.out.println("---------- Po ktu futesh te abstact testing entity");

    }

    public AbstractTestingEntity(Testing device) {
        if (device.getId() != null) {
            this.setUuid(device.getUuidId());
        }
        this.setCreatedTime(device.getCreatedTime());
        if (device.getTenantId() != null) {
            this.tenantId = device.getTenantId().getId();
        }
        if (device.getCustomerId() != null) {
            this.customerId = device.getCustomerId().getId();
        }

        this.name = device.getName();
        this.sensorType = device.getSensorType();
        this.model = device.getModel();
        this.protocol = device.getProtocol();
        this.additionalInfo = device.getAdditionalInfo();
    }

    public AbstractTestingEntity(TestingEntity deviceEntity) {
//        this.setId(deviceEntity.getId());
//        this.setCreatedTime(deviceEntity.getCreatedTime());
//        this.tenantId = deviceEntity.getTenantId();
//        this.customerId = deviceEntity.getCustomerId();
//        this.deviceProfileId = deviceEntity.getDeviceProfileId();
//        this.deviceData = deviceEntity.getDeviceData();
//        this.type = deviceEntity.getType();
//        this.name = deviceEntity.getName();
//
//        this.additionalInfo = deviceEntity.getAdditionalInfo();
    }

    @Override
    public String getSearchTextSource() {
        return name;
    }



    protected Testing toTesting() {
        Testing testing = new Testing(new TestingId(getUuid()));
        testing.setCreatedTime(createdTime);
        if (tenantId != null) {
            testing.setTenantId(new TenantId(tenantId));
        }
        if (customerId != null) {
            testing.setCustomerId(new CustomerId(customerId));
        }
        testing.setName(name);
        testing.setSensorType(sensorType);
        testing.setModel(model);
        testing.setProtocol(protocol);
        testing.setAdditionalInfo(additionalInfo);
        return testing;
    }

}
