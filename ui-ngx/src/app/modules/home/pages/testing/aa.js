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
 package org.thingsboard.server.dao.sql.testing;

 import com.google.common.util.concurrent.ListenableFuture;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.data.repository.CrudRepository;
 import org.springframework.stereotype.Component;
 import org.springframework.util.StringUtils;
 import org.thingsboard.server.common.data.Testing;
 import org.thingsboard.server.common.data.TestingInfo;
 import org.thingsboard.server.common.data.EntitySubtype;
 import org.thingsboard.server.common.data.EntityType;
 import org.thingsboard.server.common.data.id.TenantId;
 import org.thingsboard.server.common.data.page.PageData;
 import org.thingsboard.server.common.data.page.PageLink;
 import org.thingsboard.server.dao.DaoUtil;
 import org.thingsboard.server.dao.testing.TestingDao;
 import org.thingsboard.server.dao.model.sql.TestingEntity;
 import org.thingsboard.server.dao.model.sql.TestingInfoEntity;
 import org.thingsboard.server.dao.sql.JpaAbstractSearchTextDao;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 import java.util.Objects;
 import java.util.Optional;
 import java.util.UUID;
 
 /**
  * Created by Valerii Sosliuk on 5/6/2017.
  */
 @Component
 public class JpaTestingDao extends JpaAbstractSearchTextDao<TestingEntity, Testing> implements TestingDao {
 
     @Autowired
     private TestingRepository testingRepository;
 
     @Override
     protected Class<TestingEntity> getEntityClass() {
         return TestingEntity.class;
     }
 
     @Override
     protected CrudRepository<TestingEntity, UUID> getCrudRepository() {
         return testingRepository;
     }
 
     @Override
     public TestingInfo findTestingInfoById(TenantId tenantId, UUID testingId) {
         return DaoUtil.getData(testingRepository.findTestingInfoById(testingId));
     }
 
     @Override
     public PageData<Testing> findTestingsByTenantId(UUID tenantId, PageLink pageLink) {
         if (StringUtils.isEmpty(pageLink.getTextSearch())) {
             return DaoUtil.toPageData(
                     testingRepository.findByTenantId(
                             tenantId,
                             DaoUtil.toPageable(pageLink)));
         } else {
             return DaoUtil.toPageData(
                     testingRepository.findByTenantId(
                             tenantId,
                             Objects.toString(pageLink.getTextSearch(), ""),
                             DaoUtil.toPageable(pageLink)));
         }
     }
 
     @Override
     public PageData<TestingInfo> findTestingInfosByTenantId(UUID tenantId, PageLink pageLink) {
         return DaoUtil.toPageData(
                 testingRepository.findTestingInfosByTenantId(
                         tenantId,
                         Objects.toString(pageLink.getTextSearch(), ""),
                         DaoUtil.toPageable(pageLink, TestingInfoEntity.testingInfoColumnMap)));
     }
 
     @Override
     public ListenableFuture<List<Testing>> findTestingsByTenantIdAndIdsAsync(UUID tenantId, List<UUID> testingIds) {
         return service.submit(() -> DaoUtil.convertDataList(testingRepository.findTestingsByTenantIdAndIdIn(tenantId, testingIds)));
     }
 
     @Override
     public PageData<Testing> findTestingsByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink) {
         return DaoUtil.toPageData(
                 testingRepository.findByTenantIdAndCustomerId(
                         tenantId,
                         customerId,
                         Objects.toString(pageLink.getTextSearch(), ""),
                         DaoUtil.toPageable(pageLink)));
     }
 
     @Override
     public PageData<Testing> findTestingsByTenantIdAndProfileId(UUID tenantId, UUID profileId, PageLink pageLink) {
         return DaoUtil.toPageData(
                 testingRepository.findByTenantIdAndProfileId(
                         tenantId,
                         profileId,
                         Objects.toString(pageLink.getTextSearch(), ""),
                         DaoUtil.toPageable(pageLink)));
     }
 
     @Override
     public PageData<TestingInfo> findTestingInfosByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink) {
         return DaoUtil.toPageData(
                 testingRepository.findTestingInfosByTenantIdAndCustomerId(
                         tenantId,
                         customerId,
                         Objects.toString(pageLink.getTextSearch(), ""),
                         DaoUtil.toPageable(pageLink, TestingInfoEntity.testingInfoColumnMap)));
     }
 
     @Override
     public ListenableFuture<List<Testing>> findTestingsByTenantIdCustomerIdAndIdsAsync(UUID tenantId, UUID customerId, List<UUID> testingIds) {
         return service.submit(() -> DaoUtil.convertDataList(
                 testingRepository.findTestingsByTenantIdAndCustomerIdAndIdIn(tenantId, customerId, testingIds)));
     }
 
     @Override
     public Optional<Testing> findTestingByTenantIdAndName(UUID tenantId, String name) {
         Testing testing = DaoUtil.getData(testingRepository.findByTenantIdAndName(tenantId, name));
         return Optional.ofNullable(testing);
     }
 
     @Override
     public PageData<Testing> findTestingsByTenantIdAndType(UUID tenantId, String type, PageLink pageLink) {
         return DaoUtil.toPageData(
                 testingRepository.findByTenantIdAndType(
                         tenantId,
                         type,
                         Objects.toString(pageLink.getTextSearch(), ""),
                         DaoUtil.toPageable(pageLink)));
     }
 
     @Override
     public PageData<TestingInfo> findTestingInfosByTenantIdAndType(UUID tenantId, String type, PageLink pageLink) {
         return DaoUtil.toPageData(
                 testingRepository.findTestingInfosByTenantIdAndType(
                         tenantId,
                         type,
                         Objects.toString(pageLink.getTextSearch(), ""),
                         DaoUtil.toPageable(pageLink, TestingInfoEntity.testingInfoColumnMap)));
     }
 
     @Override
     public PageData<TestingInfo> findTestingInfosByTenantIdAndTestingProfileId(UUID tenantId, UUID testingProfileId, PageLink pageLink) {
         return DaoUtil.toPageData(
                 testingRepository.findTestingInfosByTenantIdAndTestingProfileId(
                         tenantId,
                         testingProfileId,
                         Objects.toString(pageLink.getTextSearch(), ""),
                         DaoUtil.toPageable(pageLink, TestingInfoEntity.testingInfoColumnMap)));
     }
 
     @Override
     public PageData<Testing> findTestingsByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink) {
         return DaoUtil.toPageData(
                 testingRepository.findByTenantIdAndCustomerIdAndType(
                         tenantId,
                         customerId,
                         type,
                         Objects.toString(pageLink.getTextSearch(), ""),
                         DaoUtil.toPageable(pageLink)));
     }
 
     @Override
     public PageData<TestingInfo> findTestingInfosByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink) {
         return DaoUtil.toPageData(
                 testingRepository.findTestingInfosByTenantIdAndCustomerIdAndType(
                         tenantId,
                         customerId,
                         type,
                         Objects.toString(pageLink.getTextSearch(), ""),
                         DaoUtil.toPageable(pageLink, TestingInfoEntity.testingInfoColumnMap)));
     }
 
     @Override
     public PageData<TestingInfo> findTestingInfosByTenantIdAndCustomerIdAndTestingProfileId(UUID tenantId, UUID customerId, UUID testingProfileId, PageLink pageLink) {
         return DaoUtil.toPageData(
                 testingRepository.findTestingInfosByTenantIdAndCustomerIdAndTestingProfileId(
                         tenantId,
                         customerId,
                         testingProfileId,
                         Objects.toString(pageLink.getTextSearch(), ""),
                         DaoUtil.toPageable(pageLink, TestingInfoEntity.testingInfoColumnMap)));
     }
 
     @Override
     public ListenableFuture<List<EntitySubtype>> findTenantTestingTypesAsync(UUID tenantId) {
         return service.submit(() -> convertTenantTestingTypesToDto(tenantId, testingRepository.findTenantTestingTypes(tenantId)));
     }
 
     @Override
     public Testing findTestingByTenantIdAndId(TenantId tenantId, UUID id) {
         return DaoUtil.getData(testingRepository.findByTenantIdAndId(tenantId.getId(), id));
     }
 
     @Override
     public ListenableFuture<Testing> findTestingByTenantIdAndIdAsync(TenantId tenantId, UUID id) {
         return service.submit(() -> DaoUtil.getData(testingRepository.findByTenantIdAndId(tenantId.getId(), id)));
     }
 
     @Override
     public Long countTestingsByTestingProfileId(TenantId tenantId, UUID testingProfileId) {
         return testingRepository.countByTestingProfileId(testingProfileId);
     }
 
     @Override
     public Long countByTenantId(TenantId tenantId) {
         return testingRepository.countByTenantId(tenantId.getId());
     }
 
     private List<EntitySubtype> convertTenantTestingTypesToDto(UUID tenantId, List<String> types) {
         List<EntitySubtype> list = Collections.emptyList();
         if (types != null && !types.isEmpty()) {
             list = new ArrayList<>();
             for (String type : types) {
                 list.add(new EntitySubtype(new TenantId(tenantId), EntityType.DEVICE, type));
             }
         }
         return list;
     }
 }
 