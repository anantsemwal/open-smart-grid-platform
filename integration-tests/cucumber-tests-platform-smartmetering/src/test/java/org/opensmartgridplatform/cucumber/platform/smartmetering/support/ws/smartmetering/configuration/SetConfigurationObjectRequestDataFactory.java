/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.Map;

public class SetConfigurationObjectRequestDataFactory {

    private SetConfigurationObjectRequestDataFactory() {
        // Private constructor for utility class
    }

    public static SetConfigurationObjectRequestData fromParameterMap(final Map<String, String> requestParameters) {
        final SetConfigurationObjectRequestData setConfigurationObjectRequestData = new SetConfigurationObjectRequestData();
        setConfigurationObjectRequestData
                .setConfigurationObject(ConfigurationObjectFactory.fromParameterMap(requestParameters));
        return setConfigurationObjectRequestData;
    }
}
