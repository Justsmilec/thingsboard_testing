package org.thingsboard.server.common.data.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.thingsboard.server.common.data.EntityType;

import java.util.UUID;

public class TestingId extends UUIDBased implements EntityId{

    private static final long serialVersionUID = 1L;

    @JsonCreator
    public TestingId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static TestingId fromString(String testingId) {
        return new TestingId(UUID.fromString(testingId));
    }

    @JsonIgnore
    @Override
    public EntityType getEntityType() {
        return EntityType.TESTING;
    }

}
