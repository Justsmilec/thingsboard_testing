package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.Testing;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonBinaryType;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
@Table(name = "testing", schema = "public")
public final class TestingEntity extends AbstractTestingEntity<Testing> {

    public TestingEntity() {
        super();
        System.out.println("---------- Po ktu futesh te testing entity");

    }

    public TestingEntity(Testing device) {
        super(device);
        System.out.println("---------- Po ktu futesh te testing entity");

    }

    @Override
    public Testing toData() {
        return super.toTesting();
    }

    @Override
    public void setSearchText(String searchText) {

    }
}
