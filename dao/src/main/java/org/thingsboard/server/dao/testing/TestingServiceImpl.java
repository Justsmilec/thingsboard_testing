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
package org.thingsboard.server.dao.testing;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.thingsboard.server.common.data.*;

import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.relation.EntitySearchDirection;

import org.thingsboard.server.common.data.tenant.profile.DefaultTenantProfileConfiguration;
import org.thingsboard.server.dao.customer.CustomerDao;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.entityview.EntityViewService;
import org.thingsboard.server.dao.event.EventService;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.service.PaginatedRemover;
import org.thingsboard.server.dao.tenant.TbTenantProfileCache;
import org.thingsboard.server.dao.tenant.TenantDao;
import org.thingsboard.server.dao.util.mapping.JacksonUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.thingsboard.server.common.data.CacheConstants.DEVICE_CACHE;
import static org.thingsboard.server.dao.DaoUtil.toUUIDs;
import static org.thingsboard.server.dao.model.ModelConstants.NULL_UUID;
import static org.thingsboard.server.dao.service.Validator.validateId;
import static org.thingsboard.server.dao.service.Validator.validateIds;
import static org.thingsboard.server.dao.service.Validator.validatePageLink;
import static org.thingsboard.server.dao.service.Validator.validateString;

@Service
@Slf4j
public class TestingServiceImpl extends AbstractEntityService implements TestingService {

    public static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";
    public static final String INCORRECT_DEVICE_PROFILE_ID = "Incorrect testingProfileId ";
    public static final String INCORRECT_PAGE_LINK = "Incorrect page link ";
    public static final String INCORRECT_CUSTOMER_ID = "Incorrect customerId ";
    public static final String INCORRECT_DEVICE_ID = "Incorrect testingId ";

    @Autowired
    private TestingDao testingDao;

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private CustomerDao customerDao;


    @Autowired
    private EntityViewService entityViewService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private EventService eventService;

    @Autowired
    @Lazy
    private TbTenantProfileCache tenantProfileCache;

    @Override
    public TestingInfo findTestingInfoById(TenantId tenantId, TestingId testingId) {
        log.trace("Executing findTestingInfoById [{}]", testingId);
        validateId(testingId, INCORRECT_DEVICE_ID + testingId);
        return testingDao.findTestingInfoById(tenantId, testingId.getId());
    }

    @Override
    public ListenableFuture<TestingInfo> findTestingInfoByIdAsync(TenantId tenantId, TestingId testingId) {
        return null;
    }

    @Override
    public Testing findTestingById(TenantId tenantId, TestingId testingId) {
        log.trace("Executing findTestingById [{}]", testingId);
        validateId(testingId, INCORRECT_DEVICE_ID + testingId);
        if (TenantId.SYS_TENANT_ID.equals(tenantId)) {
            return testingDao.findById(tenantId, testingId.getId());
        } else {
            return testingDao.findTestingByTenantIdAndId(tenantId, testingId.getId());
        }
    }

    @Override
    public ListenableFuture<Testing> findTestingByIdAsync(TenantId tenantId, TestingId testingId) {
        log.trace("Executing findTestingById [{}]", testingId);
        validateId(testingId, INCORRECT_DEVICE_ID + testingId);
        if (TenantId.SYS_TENANT_ID.equals(tenantId)) {
            return testingDao.findByIdAsync(tenantId, testingId.getId());
        } else {
            return null; //testingDao.findTestingByTenantIdAndIdAsync(tenantId, testingId.getId())
        }
    }




    @CacheEvict(cacheNames = DEVICE_CACHE, key = "{#testing.tenantId, #testing.name}")
    @Override
    public Testing saveTesting(Testing testing) {
        return doSaveTesting(testing, null);
    }

    private Testing doSaveTesting(Testing testing, String accessToken) {
        log.trace("Executing saveTesting [{}]", testing);
        testingValidator.validate(testing, Testing::getTenantId);
        System.out.println("--------Do save testisng: " + testing);
        Testing savedTesting;
        try {
            System.out.println("--------  instide try Do save testisng: " + testing);
            System.out.println("--------  instide try Do save tenantId: " + testing.getTenantId());


            savedTesting = testingDao.save(testing.getTenantId(), testing);

            System.out.println("----::: ::: ::: "+ savedTesting);
        } catch (Exception t) {
            ConstraintViolationException e = extractConstraintViolationException(t).orElse(null);
            if (e != null && e.getConstraintName() != null && e.getConstraintName().equalsIgnoreCase("testing_name_unq_key")) {
                // remove testing from cache in case null value cached in the distributed redis.
                removeTestingFromCache(testing.getTenantId(), testing.getName());
                throw new DataValidationException("Testing with such name already exists!");
            } else {
                throw t;
            }
        }

        return savedTesting;
    }



    @Override
    public Testing assignTestingToCustomer(TenantId tenantId, TestingId testingId, CustomerId customerId) {
        Testing testing = findTestingById(tenantId, testingId);
        testing.setCustomerId(customerId);
        return saveTesting(testing);
    }

    @Override
    public Testing unassignTestingFromCustomer(TenantId tenantId, TestingId testingId, CustomerId customerId) {
        return null;
    }

//    @Override
//    public Testing unassignTestingFromCustomer(TenantId tenantId, TestingId testingId) {
//        Testing testing = findTestingById(tenantId, testingId);
//        testing.setCustomerId(null);
//        return saveTesting(testing);
//    }

    @Override
    public void deleteTesting(TenantId tenantId, TestingId testingId) {
        log.trace("Executing deleteTesting [{}]", testingId);
        validateId(testingId, INCORRECT_DEVICE_ID + testingId);

        Testing testing = testingDao.findById(tenantId, testingId.getId());
        try {
            List<EntityView> entityViews = entityViewService.findEntityViewsByTenantIdAndEntityIdAsync(testing.getTenantId(), testingId).get();
            if (entityViews != null && !entityViews.isEmpty()) {
                throw new DataValidationException("Can't delete testing that has entity views!");
            }
        } catch (ExecutionException | InterruptedException e) {
            log.error("Exception while finding entity views for testingId [{}]", testingId, e);
            throw new RuntimeException("Exception while finding entity views for testingId [" + testingId + "]", e);
        }

        deleteEntityRelations(tenantId, testingId);

        removeTestingFromCache(tenantId, testing.getName());

        testingDao.removeById(tenantId, testingId.getId());
    }

    @Override
    public PageData<TestingInfo> findTestingsByTenantId(TenantId tenantId, PageLink pageLink) {
        return null;
    }

    private void removeTestingFromCache(TenantId tenantId, String name) {
        List<Object> list = new ArrayList<>();
        list.add(tenantId);
        list.add(name);
        Cache cache = cacheManager.getCache(DEVICE_CACHE);
        cache.evict(list);
    }

//    @Override
//    public PageData<Testing> findTestingsByTenantId(TenantId tenantId, PageLink pageLink) {
//        log.trace("Executing findTestingsByTenantId, tenantId [{}], pageLink [{}]", tenantId, pageLink);
//        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
//        validatePageLink(pageLink);
//        return testingDao.findTestingsByTenantId(tenantId.getId(), pageLink);
//    }






    @Override
    public void deleteTestingsByTenantId(TenantId tenantId) {
        log.trace("Executing deleteTestingsByTenantId, tenantId [{}]", tenantId);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        tenantTestingsRemover.removeEntities(tenantId, tenantId);
    }

    @Override
    public PageData<TestingInfo> findTestingsByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, PageLink pageLink) {
        return null;
    }

//    @Override
//    public PageData<Testing> findTestingsByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, PageLink pageLink) {
//        log.trace("Executing findTestingsByTenantIdAndCustomerId, tenantId [{}], customerId [{}], pageLink [{}]", tenantId, customerId, pageLink);
//        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
//        validateId(customerId, INCORRECT_CUSTOMER_ID + customerId);
//        validatePageLink(pageLink);
//        return testingDao.findTestingsByTenantIdAndCustomerId(tenantId.getId(), customerId.getId(), pageLink);
//    }




    @Override
    public void unassignCustomerTestings(TenantId tenantId, CustomerId customerId) {
        log.trace("Executing unassignCustomerTestings, tenantId [{}], customerId [{}]", tenantId, customerId);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(customerId, INCORRECT_CUSTOMER_ID + customerId);
        customerTestingUnasigner.removeEntities(tenantId, customerId);
    }

    @Override
    public void updateCustomerTestings(TenantId tenantId, CustomerId customerId) {

    }

//    @Override
//    public ListenableFuture<List<Testing>> findTestingsByQuery(TenantId tenantId, TestingSearchQuery query) {
//        ListenableFuture<List<EntityRelation>> relations = relationService.findByQuery(tenantId, query.toEntitySearchQuery());
//        ListenableFuture<List<Testing>> testings = Futures.transformAsync(relations, r -> {
//            EntitySearchDirection direction = query.toEntitySearchQuery().getParameters().getDirection();
//            List<ListenableFuture<Testing>> futures = new ArrayList<>();
//            for (EntityRelation relation : r) {
//                EntityId entityId = direction == EntitySearchDirection.FROM ? relation.getTo() : relation.getFrom();
//                if (entityId.getEntityType() == EntityType.DEVICE) {
//                    futures.add(findTestingByIdAsync(tenantId, new TestingId(entityId.getId())));
//                }
//            }
//            return Futures.successfulAsList(futures);
//        }, MoreExecutors.directExecutor());
//
//        testings = Futures.transform(testings, new Function<List<Testing>, List<Testing>>() {
//            @Nullable
//            @Override
//            public List<Testing> apply(@Nullable List<Testing> testingList) {
//                return testingList == null ? Collections.emptyList() : testingList.stream().filter(testing -> query.getTestingTypes().contains(testing.getType())).collect(Collectors.toList());
//            }
//        }, MoreExecutors.directExecutor());
//
//        return testings;
//    }
//
//    @Override
//    public ListenableFuture<List<EntitySubtype>> findTestingTypesByTenantId(TenantId tenantId) {
//        log.trace("Executing findTestingTypesByTenantId, tenantId [{}]", tenantId);
//        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
//        ListenableFuture<List<EntitySubtype>> tenantTestingTypes = testingDao.findTenantTestingTypesAsync(tenantId.getId());
//        return Futures.transform(tenantTestingTypes,
//                testingTypes -> {
//                    testingTypes.sort(Comparator.comparing(EntitySubtype::getType));
//                    return testingTypes;
//                }, MoreExecutors.directExecutor());
//    }

//    @Transactional
//    @CacheEvict(cacheNames = DEVICE_CACHE, key = "{#testing.tenantId, #testing.name}")
//    @Override
//    public Testing assignTestingToTenant(TenantId tenantId, Testing testing) {
//        log.trace("Executing assignTestingToTenant [{}][{}]", tenantId, testing);
//
//        try {
//            List<EntityView> entityViews = entityViewService.findEntityViewsByTenantIdAndEntityIdAsync(testing.getTenantId(), testing.getId()).get();
//            if (!CollectionUtils.isEmpty(entityViews)) {
//                throw new DataValidationException("Can't assign testing that has entity views to another tenant!");
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            log.error("Exception while finding entity views for testingId [{}]", testing.getId(), e);
//            throw new RuntimeException("Exception while finding entity views for testingId [" + testing.getId() + "]", e);
//        }
//
//        eventService.removeEvents(testing.getTenantId(), testing.getId());
//
//        relationService.removeRelations(testing.getTenantId(), testing.getId());
//
//        testing.setTenantId(tenantId);
//        testing.setCustomerId(null);
//        return doSaveTesting(testing, null);
//    }
//
//    @Override
//    @CacheEvict(cacheNames = DEVICE_CACHE, key = "{#profile.tenantId, #provisionRequest.testingName}")
//    @Transactional
//    public Testing saveTesting(ProvisionRequest provisionRequest, TestingProfile profile) {
//        Testing testing = new Testing();
//        testing.setName(provisionRequest.getTestingName());
//        testing.setType(profile.getName());
//        testing.setTenantId(profile.getTenantId());
//        Testing savedTesting = saveTesting(testing);
//        if (!StringUtils.isEmpty(provisionRequest.getCredentialsData().getToken()) ||
//                !StringUtils.isEmpty(provisionRequest.getCredentialsData().getX509CertHash()) ||
//                !StringUtils.isEmpty(provisionRequest.getCredentialsData().getUsername()) ||
//                !StringUtils.isEmpty(provisionRequest.getCredentialsData().getPassword()) ||
//                !StringUtils.isEmpty(provisionRequest.getCredentialsData().getClientId())) {
//            TestingCredentials testingCredentials = testingCredentialsService.findTestingCredentialsByTestingId(savedTesting.getTenantId(), savedTesting.getId());
//            if (testingCredentials == null) {
//                testingCredentials = new TestingCredentials();
//            }
//            testingCredentials.setTestingId(savedTesting.getId());
//            testingCredentials.setCredentialsType(provisionRequest.getCredentialsType());
//            switch (provisionRequest.getCredentialsType()) {
//                case ACCESS_TOKEN:
//                    testingCredentials.setCredentialsId(provisionRequest.getCredentialsData().getToken());
//                    break;
//                case MQTT_BASIC:
//                    BasicMqttCredentials mqttCredentials = new BasicMqttCredentials();
//                    mqttCredentials.setClientId(provisionRequest.getCredentialsData().getClientId());
//                    mqttCredentials.setUserName(provisionRequest.getCredentialsData().getUsername());
//                    mqttCredentials.setPassword(provisionRequest.getCredentialsData().getPassword());
//                    testingCredentials.setCredentialsValue(JacksonUtil.toString(mqttCredentials));
//                    break;
//                case X509_CERTIFICATE:
//                    testingCredentials.setCredentialsValue(provisionRequest.getCredentialsData().getX509CertHash());
//                    break;
//            }
//            try {
//                testingCredentialsService.updateTestingCredentials(savedTesting.getTenantId(), testingCredentials);
//            } catch (Exception e) {
//                throw new ProvisionFailedException(ProvisionResponseStatus.FAILURE.name());
//            }
//        }
//        return savedTesting;
//    }


    @Override
    public ListenableFuture<List<Testing>> findTestingsByTenantIdAndIdsAsync(TenantId tenantId, List<TestingId> deviceIds) {
        log.trace("Executing findDevicesByTenantIdAndIdsAsync, tenantId [{}], deviceIds [{}]", tenantId, deviceIds);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateIds(deviceIds, "Incorrect deviceIds " + deviceIds);
        return testingDao.findTestingsByTenantIdAndIdsAsync(tenantId.getId(), toUUIDs(deviceIds));
    }

    @Override
    public ListenableFuture<List<Testing>> findTestingsByTenantIdCustomerIdAndIdsAsync(TenantId tenantId, CustomerId customerId, List<TestingId> deviceIds) {
        log.trace("Executing findDevicesByTenantIdCustomerIdAndIdsAsync, tenantId [{}], customerId [{}], deviceIds [{}]", tenantId, customerId, deviceIds);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(customerId, INCORRECT_CUSTOMER_ID + customerId);
        validateIds(deviceIds, "Incorrect deviceIds " + deviceIds);
        return testingDao.findTestingsByTenantIdCustomerIdAndIdsAsync(tenantId.getId(),
                customerId.getId(), toUUIDs(deviceIds));
    }

    private DataValidator<Testing> testingValidator =
            new DataValidator<Testing>() {

                @Override
                protected void validateCreate(TenantId tenantId, Testing testing) {
                    DefaultTenantProfileConfiguration profileConfiguration =
                            (DefaultTenantProfileConfiguration)tenantProfileCache.get(tenantId).getProfileData().getConfiguration();
                    //long maxTestings = profileConfiguration.getMaxTestings();
                    //validateNumberOfEntitiesPerTenant(tenantId, testingDao, maxTestings, EntityType.DEVICE);
                }

                @Override
                protected void validateUpdate(TenantId tenantId, Testing testing) {
                    Testing old = testingDao.findById(testing.getTenantId(), testing.getId().getId());
                    if (old == null) {
                        throw new DataValidationException("Can't update non existing testing!");
                    }
                }

                @Override
                protected void validateDataImpl(TenantId tenantId, Testing testing) {
                    if (StringUtils.isEmpty(testing.getName()) || testing.getName().trim().length() == 0) {
                        throw new DataValidationException("Testing name should be specified!");
                    }
                    if (testing.getTenantId() == null) {
                        throw new DataValidationException("Testing should be assigned to tenant!");
                    } else {
                        Tenant tenant = tenantDao.findById(testing.getTenantId(), testing.getTenantId().getId());
                        if (tenant == null) {
                            throw new DataValidationException("Testing is referencing to non-existent tenant!");
                        }
                    }
                    if (testing.getCustomerId() == null) {
                        testing.setCustomerId(new CustomerId(NULL_UUID));
                    } else if (!testing.getCustomerId().getId().equals(NULL_UUID)) {
                        Customer customer = customerDao.findById(testing.getTenantId(), testing.getCustomerId().getId());
                        if (customer == null) {
                            throw new DataValidationException("Can't assign testing to non-existent customer!");
                        }
                        if (!customer.getTenantId().getId().equals(testing.getTenantId().getId())) {
                            throw new DataValidationException("Can't assign testing to customer from different tenant!");
                        }
                    }
                }
            };

    private PaginatedRemover<TenantId, Testing> tenantTestingsRemover =
            new PaginatedRemover<TenantId, Testing>() {

                @Override
                protected PageData<Testing> findEntities(TenantId tenantId, TenantId id, PageLink pageLink) {
                    return testingDao.findTestingsByTenantId(id.getId(), pageLink);
                }

                @Override
                protected void removeEntity(TenantId tenantId, Testing entity) {
                    deleteTesting(tenantId, new TestingId(entity.getUuidId()));
                }
            };

    private PaginatedRemover<CustomerId, Testing> customerTestingUnasigner = new PaginatedRemover<CustomerId, Testing>() {

        @Override
        protected PageData<Testing> findEntities(TenantId tenantId, CustomerId id, PageLink pageLink) {
            return testingDao.findTestingsByTenantIdAndCustomerId(tenantId.getId(), id.getId(), pageLink);
        }

        @Override
        protected void removeEntity(TenantId tenantId, Testing entity) {
            //unassignTestingFromCustomer(tenantId, new TestingId(entity.getUuidId()));
        }
    };
}
