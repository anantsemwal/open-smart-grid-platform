/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.management;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.management.FindMessageLogsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.FindMessageLogsRequest;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class FindMessageLogsRequestFactory {
    private FindMessageLogsRequestFactory() {
        // Private constructor for utility class
    }

    public static FindMessageLogsRequest fromParameterMap(final Map<String, String> requestParameters) {
        final FindMessageLogsRequest request = new FindMessageLogsRequest();
        request.setDeviceIdentification(requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
        return request;
    }

    public static FindMessageLogsAsyncRequest fromScenarioContext() {
        final FindMessageLogsAsyncRequest asyncRequest = new FindMessageLogsAsyncRequest();
        asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
        asyncRequest.setDeviceIdentification(RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
        return asyncRequest;
    }
}
