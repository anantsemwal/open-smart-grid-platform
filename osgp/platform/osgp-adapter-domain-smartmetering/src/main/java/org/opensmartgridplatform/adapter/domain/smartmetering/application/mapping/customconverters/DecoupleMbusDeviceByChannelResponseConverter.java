/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DecoupleMbusDeviceByChannelResponse;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DecoupleMbusDeviceResponseDto;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

@Component
public class DecoupleMbusDeviceByChannelResponseConverter
        extends CustomConverter<DecoupleMbusDeviceResponseDto, DecoupleMbusDeviceByChannelResponse> {

    @Override
    public DecoupleMbusDeviceByChannelResponse convert(final DecoupleMbusDeviceResponseDto source,
            final Type<? extends DecoupleMbusDeviceByChannelResponse> type, final MappingContext mappingContext) {

        if (source == null) {
            return null;
        }

        return new DecoupleMbusDeviceByChannelResponse(source.getMbusDeviceIdentification(),
                source.getChannelElementValues().getChannel());
    }
}
