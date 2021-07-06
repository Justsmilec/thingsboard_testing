package org.thingsboard.server.dao.testing;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.Testing;
import org.thingsboard.server.common.data.TestingInfo;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.TenantEntityDao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TestingDao extends Dao<Testing>, TenantEntityDao {

    /**
     * Find testing info by id.
     *
     * @param tenantId the tenant id
     * @param testingId the testing id
     * @return the testing info object
     */
    TestingInfo findTestingInfoById(TenantId tenantId, UUID testingId);

    /**
     * Save or update testing object
     *
     * @param testing the testing object
     * @return saved testing object
     */
    Testing save(TenantId tenantId, Testing testing);

    /**
     * Find testings by tenantId and page link.
     *
     * @param tenantId the tenantId
     * @param pageLink the page link
     * @return the list of testing objects
     */
    PageData<Testing> findTestingsByTenantId(UUID tenantId, PageLink pageLink);

    /**
     * Find testing infos by tenantId and page link.
     *
     * @param tenantId the tenantId
     * @param pageLink the page link
     * @return the list of testing info objects
     */
    PageData<TestingInfo> findTestingInfosByTenantId(UUID tenantId, PageLink pageLink);

    /**
     * Find testings by tenantId, type and page link.
     *
     * @param tenantId the tenantId
     * @param type the type
     * @param pageLink the page link
     * @return the list of testing objects
     */
    PageData<Testing> findTestingsByTenantIdAndType(UUID tenantId, String type, PageLink pageLink);

    /**
     * Find testing infos by tenantId, type and page link.
     *
     * @param tenantId the tenantId
     * @param type the type
     * @param pageLink the page link
     * @return the list of testing info objects
     */
    PageData<TestingInfo> findTestingInfosByTenantIdAndType(UUID tenantId, String type, PageLink pageLink);


    /**
     * Find testings by tenantId and testings Ids.
     *
     * @param tenantId the tenantId
     * @param testingIds the testing Ids
     * @return the list of testing objects
     */
    ListenableFuture<List<Testing>> findTestingsByTenantIdAndIdsAsync(UUID tenantId, List<UUID> testingIds);

    /**
     * Find testings by tenantId, customerId and page link.
     *
     * @param tenantId the tenantId
     * @param customerId the customerId
     * @param pageLink the page link
     * @return the list of testing objects
     */
    PageData<Testing> findTestingsByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink);

    /**
     * Find testing infos by tenantId, customerId and page link.
     *
     * @param tenantId the tenantId
     * @param customerId the customerId
     * @param pageLink the page link
     * @return the list of testing info objects
     */
    PageData<TestingInfo> findTestingInfosByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink);

    /**
     * Find testings by tenantId, customerId, type and page link.
     *
     * @param tenantId the tenantId
     * @param customerId the customerId
     * @param type the type
     * @param pageLink the page link
     * @return the list of testing objects
     */
    PageData<Testing> findTestingsByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink);

    /**
     * Find testing infos by tenantId, customerId, type and page link.
     *
     * @param tenantId the tenantId
     * @param customerId the customerId
     * @param type the type
     * @param pageLink the page link
     * @return the list of testing info objects
     */
    PageData<TestingInfo> findTestingInfosByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink);

    /**
     * Find testing infos by tenantId, customerId, testingProfileId and page link.
     *
     * @param tenantId the tenantId
     * @param customerId the customerId
     * @param testingProfileId the testingProfileId
     * @param pageLink the page link
     * @return the list of testing info objects
     */
    PageData<TestingInfo> findTestingInfosByTenantIdAndCustomerIdAndTestingProfileId(UUID tenantId, UUID customerId, UUID testingProfileId, PageLink pageLink);



    /**
     * Find testings by tenantId and testing name.
     *
     * @param tenantId the tenantId
     * @param name the testing name
     * @return the optional testing object
     */
    Optional<Testing> findTestingByTenantIdAndName(UUID tenantId, String name);



    /**
     * Find testings by tenantId and testing id.
     * @param tenantId the tenant Id
     * @param id the testing Id
     * @return the testing object
     */
    Testing findTestingByTenantIdAndId(TenantId tenantId, UUID id);



    /**
     * Find testings by tenantId, profileId and page link.
     *
     * @param tenantId the tenantId
     * @param profileId the profileId
     * @param pageLink the page link
     * @return the list of testing objects
     */
    PageData<Testing> findTestingsByTenantIdAndProfileId(UUID tenantId, UUID profileId, PageLink pageLink);


    ListenableFuture<List<Testing>> findTestingsByTenantIdCustomerIdAndIdsAsync(UUID id, UUID id1, List<UUID> toUUIDs);
}