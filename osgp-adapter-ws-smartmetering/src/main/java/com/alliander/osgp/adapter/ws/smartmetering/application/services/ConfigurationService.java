/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessage;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageSender;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageType;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotifications;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetAdministration;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetAdministration;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequest;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

@Service(value = "wsSmartMeteringConfigurationService")
@Validated
public class ConfigurationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private SmartMeteringRequestMessageSender smartMeteringRequestMessageSender;

    /**
     * @param organisationIdentification
     * @param requestData
     * @throws FunctionalException
     */
    public String requestGetAdministration(final String organisationIdentification, final GetAdministration requestData)
            throws FunctionalException {
        return this.enqueueGetAdministration(organisationIdentification, requestData.getDeviceIdentification(),
                requestData);
    }

    public String enqueueGetAdministration(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, @Identification final GetAdministration requestData)
            throws FunctionalException {

        LOGGER.info("enqueueDaysRequest called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage(
                SmartMeteringRequestMessageType.GET_ADMINISTRATION, correlationUid, organisationIdentification,
                requestData.getDeviceIdentification(), requestData);

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    /**
     * @param organisationIdentification
     * @param requestData
     * @throws FunctionalException
     */
    public String requestSetAdministration(final String organisationIdentification, final SetAdministration requestData)
            throws FunctionalException {
        return this.enqueueSetAdministration(organisationIdentification, requestData.getDeviceIdentification(),
                requestData);
    }

    public String enqueueSetAdministration(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, @Identification final SetAdministration requestData)
            throws FunctionalException {

        LOGGER.debug("enqueueDaysRequest called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage(
                SmartMeteringRequestMessageType.SET_ADMINISTRATION, correlationUid, organisationIdentification,
                deviceIdentification, requestData);

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueSpecialDaysRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, @Identification final SpecialDaysRequest requestData)
            throws FunctionalException {

        LOGGER.debug("enqueueSpecialDaysRequest called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage(
                SmartMeteringRequestMessageType.REQUEST_SPECIAL_DAYS, correlationUid, organisationIdentification,
                deviceIdentification, requestData);

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueSetConfigurationObjectRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            @Identification final SetConfigurationObjectRequest requestData) throws FunctionalException {

        LOGGER.debug("enqueueSetConfigurationObjectRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage(
                SmartMeteringRequestMessageType.SET_CONFIGURATION_OBJECT, correlationUid, organisationIdentification,
                deviceIdentification, requestData);

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    /**
     * @param organisationIdentification
     * @param requestData
     * @throws FunctionalException
     */
    public String requestSpecialDaysData(final String organisationIdentification, final SpecialDaysRequest requestData)
            throws FunctionalException {
        return this.enqueueSpecialDaysRequest(organisationIdentification, requestData.getDeviceIdentification(),
                requestData);
    }

    /**
     * @param organisationIdentification
     * @param requestData
     * @throws FunctionalException
     */
    public String setConfigurationObject(final String organisationIdentification,
            final SetConfigurationObjectRequest requestData) throws FunctionalException {
        return this.enqueueSetConfigurationObjectRequest(organisationIdentification,
                requestData.getDeviceIdentification(), requestData);
    }

    public String enqueueSetAlarmNotificationsRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final AlarmNotifications alarmSwitches)
            throws FunctionalException {

        LOGGER.debug("enqueueSetAlarmNotificationsRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage(
                SmartMeteringRequestMessageType.SET_ALARM_NOTIFICATIONS, correlationUid, organisationIdentification,
                deviceIdentification, alarmSwitches);

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    /**
     * @param organisationIdentification
     * @param deviceIdentification
     * @param alarmSwitches
     * @throws FunctionalException
     */
    public String setAlarmNotifications(final String organisationIdentification, final String deviceIdentification,
            final AlarmNotifications alarmSwitches) throws FunctionalException {
        return this
                .enqueueSetAlarmNotificationsRequest(organisationIdentification, deviceIdentification, alarmSwitches);
    }
}
