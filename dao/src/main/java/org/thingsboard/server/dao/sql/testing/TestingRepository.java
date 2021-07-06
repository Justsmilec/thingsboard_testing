package org.thingsboard.server.dao.sql.testing;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.dao.model.sql.DeviceEntity;
import org.thingsboard.server.dao.model.sql.DeviceInfoEntity;
import org.thingsboard.server.dao.model.sql.TestingEntity;

import java.util.List;
import java.util.UUID;

/**
 * Created by Valerii Sosliuk on 5/6/2017.
 */
public interface TestingRepository extends PagingAndSortingRepository<TestingEntity, UUID> {





    @Query("SELECT d FROM TestingEntity d WHERE d.tenantId = :tenantId")
    Page<TestingEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                      Pageable pageable);

    TestingEntity findByTenantIdAndName(UUID tenantId, String name);

    List<TestingEntity> findTestingsByTenantIdAndCustomerIdAndIdIn(UUID tenantId, UUID customerId, List<UUID> deviceIds);

    List<TestingEntity> findDevicesByTenantIdAndIdIn(UUID tenantId, List<UUID> deviceIds);

    TestingEntity findByTenantIdAndId(UUID tenantId, UUID id);

    Long countByTenantId(UUID tenantId);

    List<TestingEntity> findTestingsByTenantIdAndIdIn(UUID tenantId, List<UUID> deviceIds);
}
