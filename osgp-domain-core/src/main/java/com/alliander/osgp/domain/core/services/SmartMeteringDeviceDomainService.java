/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.domain.core.entities.SmartMeteringDevice;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.repositories.SmartMeteringDeviceRepository;
import com.alliander.osgp.domain.core.validation.Identification;

@Service
@Validated
@Transactional(value = "transactionManager")
public class SmartMeteringDeviceDomainService {

    @Autowired
    private SmartMeteringDeviceRepository smartMeteringDeviceRepository;

    public SmartMeteringDevice searchSmartMeteringDevice(@Identification final String deviceIdentification)
            throws UnknownEntityException {

        final SmartMeteringDevice smartMeteringDevice = this.smartMeteringDeviceRepository.findByDeviceIdentification(deviceIdentification);

        if (smartMeteringDevice == null) {
            throw new UnknownEntityException(SmartMeteringDevice.class, deviceIdentification);
        }

        return smartMeteringDevice;
    }
}
