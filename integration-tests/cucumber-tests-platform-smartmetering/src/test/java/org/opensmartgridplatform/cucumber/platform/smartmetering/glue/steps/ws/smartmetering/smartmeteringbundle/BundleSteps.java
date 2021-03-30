/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.HashMap;
import java.util.Map;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.ScenarioContextHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.SmartMeteringBundleClient;
import org.springframework.beans.factory.annotation.Autowired;

public class BundleSteps extends BaseBundleSteps {

    private static final Map<Class<?>, Class<?>> REQUEST_RESPONSE_MAP = new HashMap<>();

    static {
        REQUEST_RESPONSE_MAP.put(FindEventsRequest.class, FindEventsResponse.class);
        REQUEST_RESPONSE_MAP.put(GetActualMeterReadsRequest.class, ActualMeterReadsResponse.class);
        REQUEST_RESPONSE_MAP.put(GetAdministrativeStatusRequest.class, AdministrativeStatusResponse.class);
        REQUEST_RESPONSE_MAP.put(GetAllAttributeValuesRequest.class, ActionResponse.class);
        REQUEST_RESPONSE_MAP.put(GetAssociationLnObjectsRequest.class, AssociationLnObjectsResponse.class);
        REQUEST_RESPONSE_MAP.put(GetConfigurationObjectRequest.class, GetConfigurationObjectResponse.class);
        REQUEST_RESPONSE_MAP.put(GetPowerQualityProfileRequest.class, GetPowerQualityProfileRequest.class);
        REQUEST_RESPONSE_MAP.put(GetSpecificAttributeValueRequest.class, ActionResponse.class);
        REQUEST_RESPONSE_MAP.put(ReadAlarmRegisterRequest.class, ReadAlarmRegisterResponse.class);
        REQUEST_RESPONSE_MAP.put(SetActivityCalendarRequest.class, ActionResponse.class);
        REQUEST_RESPONSE_MAP.put(SetAdministrativeStatusRequest.class, ActionResponse.class);
        REQUEST_RESPONSE_MAP.put(SetAlarmNotificationsRequest.class, ActionResponse.class);
        REQUEST_RESPONSE_MAP.put(SetClockConfigurationRequest.class, ActionResponse.class);
        REQUEST_RESPONSE_MAP.put(SetConfigurationObjectRequest.class, ActionResponse.class);
        REQUEST_RESPONSE_MAP.put(SetPushSetupAlarmRequest.class, ActionResponse.class);
        REQUEST_RESPONSE_MAP.put(SetSpecialDaysRequest.class, ActionResponse.class);
        REQUEST_RESPONSE_MAP.put(SynchronizeTimeRequest.class, ActionResponse.class);
        REQUEST_RESPONSE_MAP.put(GetMbusEncryptionKeyStatusRequest.class, GetMbusEncryptionKeyStatusResponse.class);
        REQUEST_RESPONSE_MAP.put(ScanMbusChannelsRequest.class, ScanMbusChannelsResponse.class);
    }

    @Autowired
    private SmartMeteringBundleClient client;

    @Given("^a bundle request$")
    public void aABundleRequest(final Map<String, String> settings) throws Throwable {
        final BundleRequest request = new BundleRequest();
        request.setDeviceIdentification(getString(settings, PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION,
                PlatformSmartmeteringDefaults.DEVICE_IDENTIFICATION));

        final Actions actions = new Actions();
        request.setActions(actions);

        ScenarioContext.current().put(PlatformSmartmeteringKeys.BUNDLE_REQUEST, request);
    }

    @When("^the bundle request is received$")
    public void theBundleRequestIsReceived() throws Throwable {
        final BundleRequest request = (BundleRequest) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.BUNDLE_REQUEST);

        final BundleAsyncResponse asyncResponse = this.client.sendBundleRequest(request);

        assertThat(asyncResponse).isNotNull();
        ScenarioContextHelper.saveAsyncResponse(asyncResponse);
    }

    @Then("^the number of responses in the bundle response should match the number of actions in the bundle request$")
    public void theNumberOfResponsesInTheBundleResponseShouldMatchTheNumberOfActionsInTheBundleRequest()
            throws Throwable {

        this.ensureBundleResponse();

        final BundleRequest request = (BundleRequest) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.BUNDLE_REQUEST);
        final BundleResponse response = (BundleResponse) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.BUNDLE_RESPONSE);

        this.assertSameSize(request, response);
    }

    @Then("^the order of the responses in the bundle response should match the order of actions in the bundle request$")
    public void theOrderOfTheResponsesInTheBundleResponseShouldMatchTheOrderOfActionsInTheBundleRequest()
            throws Throwable {

        this.ensureBundleResponse();

        final BundleRequest request = (BundleRequest) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.BUNDLE_REQUEST);
        final BundleResponse response = (BundleResponse) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.BUNDLE_RESPONSE);

        this.assertSameSize(request, response);
        this.assertSameOrder(request, response);
    }

    private void assertSameSize(final BundleRequest bundleRequest, final BundleResponse bundleResponse) {

        final int actionsSize = bundleRequest.getActions().getActionList().size();
        final int responsesSize = bundleResponse.getAllResponses().getResponseList().size();

        assertThat(responsesSize)
                .as("The number of responses in the bundle responses should match the number of actions in the bundle "
                        + "request")
                .isEqualTo(actionsSize);
    }

    private void assertSameOrder(final BundleRequest bundleRequest, final BundleResponse bundleResponse) {

        final int actionsSize = bundleRequest.getActions().getActionList().size();

        for (int i = 0; i < actionsSize; i++) {
            final Action action = bundleRequest.getActions().getActionList().get(i);
            final Response response = bundleResponse.getAllResponses().getResponseList().get(i);

            assertThat(response.getClass()).isEqualTo(REQUEST_RESPONSE_MAP.get(action.getClass()));
        }
    }
}
