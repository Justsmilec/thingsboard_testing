package org.thingsboard.server.dao.testing;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.device.DeviceSearchQuery;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.device.provision.ProvisionRequest;

import java.util.List;

public interface TestingService {


    Testing findTestingById(TenantId tenantId, TestingId testingId);

    ListenableFuture<Testing> findTestingByIdAsync(TenantId tenantId, TestingId testingId);

    TestingInfo findTestingInfoById(TenantId tenantId, TestingId testingId);

    ListenableFuture<TestingInfo> findTestingInfoByIdAsync(TenantId tenantId, TestingId testingId);

    Testing saveTesting(Testing testing);

    Testing assignTestingToCustomer(TenantId tenantId, TestingId testingId, CustomerId customerId);

    Testing unassignTestingFromCustomer(TenantId tenantId, TestingId testingId, CustomerId customerId);

    void deleteTesting(TenantId tenantId, TestingId testingId);

    PageData<TestingInfo> findTestingsByTenantId(TenantId tenantId, PageLink pageLink);

    void deleteTestingsByTenantId(TenantId tenantId);

    PageData<TestingInfo> findTestingsByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, PageLink pageLink);

    void unassignCustomerTestings(TenantId tenantId, CustomerId customerId);

    void updateCustomerTestings(TenantId tenantId, CustomerId customerId);

    ListenableFuture<List<Testing>> findTestingsByTenantIdAndIdsAsync(TenantId tenantId, List<TestingId> deviceIds);
    ListenableFuture<List<Testing>> findTestingsByTenantIdCustomerIdAndIdsAsync(TenantId tenantId, CustomerId customerId, List<TestingId> deviceIds);

}
