/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsCaptureObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsClock;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsExtendedRegister;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsProfile;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.Medium;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.ProfileCaptureTime;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.RegisterUnit;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadGasResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsGasResponseItemDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;

@RunWith(MockitoJUnitRunner.class)
public class GetPeriodicMeterReadsGasCommandExecutorTest {

    @InjectMocks
    private GetPeriodicMeterReadsGasCommandExecutor executor;

    @Mock
    private DlmsMessageListener dlmsMessageListener;

    @Mock
    private DlmsHelper dlmsHelper;

    @Mock
    private DlmsObjectConfigService dlmsObjectConfigService;

    @Mock
    private DlmsConnectionManager connectionManager;

    private final DlmsDevice device = this.createDevice(Protocol.DSMR_4_2_2);
    private final long from = 1111111L;
    private final long to = 2222222L;
    private final DateTime fromDateTime = new DateTime(this.from);
    private final DateTime toDateTime = new DateTime(this.to);

    @Before
    public void setUp() {
        when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    }

    @Test
    public void testExecuteNullRequest() throws ProtocolAdapterException {
        try {
            this.executor.execute(this.connectionManager, this.device, null);
            fail("Calling execute with null query should fail");
        } catch (final IllegalArgumentException e) {
            // expected exception
        }
    }

    @Test
    public void testExecuteObjectNotFound() {
        //SETUP
        final PeriodicMeterReadsRequestDto request = new PeriodicMeterReadsRequestDto(PeriodTypeDto.DAILY,
                this.fromDateTime.toDate(), this.toDateTime.toDate(), ChannelDto.ONE);
        when(this.dlmsObjectConfigService.findAttributeAddress(any(), any(), any(), any(), any(), any(),
                any())).thenReturn(Optional.empty());

        // CALL
        try {
            this.executor.execute(this.connectionManager, this.device, request);
            fail("When no matching object is found, then execute should fail");
        } catch (final ProtocolAdapterException e) {
            assertThat(e.getMessage()).isEqualTo("No address found for " + DlmsObjectType.DAILY_LOAD_PROFILE);
        }
    }

    @Test
    public void testHappy() throws Exception {

        // SETUP - request
        final PeriodTypeDto periodType = PeriodTypeDto.DAILY;
        final ChannelDto channel = ChannelDto.ONE;
        final PeriodicMeterReadsRequestDto request = new PeriodicMeterReadsRequestDto(periodType,
                this.fromDateTime.toDate(), this.toDateTime.toDate(), channel);

        // SETUP - dlms objects
        final DlmsObject dlmsClock = new DlmsClock(DlmsObjectType.CLOCK, "0.0.1.0.0.255");
        final DlmsCaptureObject captureObject1 = new DlmsCaptureObject(dlmsClock, 2);

        final DlmsObject dlmsExtendedRegister = new DlmsExtendedRegister(DlmsObjectType.MBUS_MASTER_VALUE,
                "0.0.24.0.0.255", 0, RegisterUnit.M3, Medium.GAS);
        final DlmsCaptureObject captureObject2 = new DlmsCaptureObject(dlmsExtendedRegister, 2);
        final DlmsCaptureObject captureObject3 = new DlmsCaptureObject(dlmsExtendedRegister, 5);

        final List<DlmsCaptureObject> captureObjects = Arrays.asList(captureObject1, captureObject2, captureObject3);

        final DlmsProfile dlmsProfile = new DlmsProfile(DlmsObjectType.DAILY_LOAD_PROFILE, "1.2.3.4.5.6",
                captureObjects, ProfileCaptureTime.DAY, Medium.COMBINED);

        // SETUP - mock dlms object config to return attribute addresses
        final AttributeAddress attributeAddress = this.createAttributeAddress(dlmsProfile);
        final AttributeAddress attributeAddressScalerUnit = this.createAttributeAddress(dlmsExtendedRegister);

        doAnswer(new Answer() {
            @Override
            public Object answer(final InvocationOnMock invocation) {
                final Object[] args = invocation.getArguments();
                ((List<DlmsCaptureObject>) args[6]).addAll(captureObjects);
                return Optional.of(attributeAddress);
            }
        }).when(this.dlmsObjectConfigService)
                .findAttributeAddress(eq(this.device), eq(DlmsObjectType.DAILY_LOAD_PROFILE),
                        eq(channel.getChannelNumber()), eq(this.fromDateTime), eq(this.toDateTime), eq(Medium.GAS),
                        eq(Collections.emptyList()));

        when(this.dlmsObjectConfigService.getAttributeAddressesForScalerUnit(eq(captureObjects),
                eq(channel.getChannelNumber()))).thenReturn(Collections.singletonList(attributeAddressScalerUnit));

        // SETUP - mock dlms helper to return data objects on request
        final DataObject data0 = mock(DataObject.class);
        final DataObject data1 = mock(DataObject.class);
        final DataObject data2 = mock(DataObject.class);
        final DataObject bufferedObject1 = mock(DataObject.class);
        when(bufferedObject1.getValue()).thenReturn(asList(data0, data1, data2));

        final DataObject data3 = mock(DataObject.class);
        final DataObject data4 = mock(DataObject.class);
        final DataObject data5 = mock(DataObject.class);
        final DataObject bufferedObject2 = mock(DataObject.class);
        when(bufferedObject2.getValue()).thenReturn(asList(data3, data4, data5));

        final DataObject resultData = mock(DataObject.class);
        when(resultData.getValue()).thenReturn(asList(bufferedObject1, bufferedObject2));

        final String expectedDescription = "retrieve periodic meter reads for " + periodType + ", channel " + channel;
        final GetResult getResult = mock(GetResult.class);
        when(this.dlmsHelper.getAndCheck(this.connectionManager, this.device, expectedDescription,
                attributeAddress)).thenReturn(Collections.singletonList(getResult));
        when(this.dlmsHelper.getAndCheck(this.connectionManager, this.device, expectedDescription,
                attributeAddressScalerUnit)).thenReturn(Collections.singletonList(getResult));

        when(this.dlmsHelper.readDataObject(eq(getResult), any(String.class))).thenReturn(resultData);

        // SETUP - mock dlms helper to handle converting the data objects
        final String expectedDateTimeDescriptionLogTime = String.format("Clock from %s buffer gas", periodType);
        final String expectedDateTimeDescriptionCaptureTime = "Clock from mbus interval extended register";
        final CosemDateTimeDto cosemDateTime = new CosemDateTimeDto(this.fromDateTime);
        when(this.dlmsHelper.readDateTime(data0, expectedDateTimeDescriptionLogTime)).thenReturn(cosemDateTime);
        when(this.dlmsHelper.readDateTime(data3, expectedDateTimeDescriptionLogTime)).thenReturn(cosemDateTime);
        when(this.dlmsHelper.readDateTime(data2, expectedDateTimeDescriptionCaptureTime)).thenReturn(cosemDateTime);
        when(this.dlmsHelper.readDateTime(data5, expectedDateTimeDescriptionCaptureTime)).thenReturn(cosemDateTime);

        final DlmsMeterValueDto meterValue1 = mock(DlmsMeterValueDto.class);
        final DlmsMeterValueDto meterValue2 = mock(DlmsMeterValueDto.class);
        when(this.dlmsHelper.getScaledMeterValue(data1, null, "gasValue")).thenReturn(meterValue1);
        when(this.dlmsHelper.getScaledMeterValue(data4, null, "gasValue")).thenReturn(meterValue2);

        // CALL
        final PeriodicMeterReadGasResponseDto result = this.executor.execute(this.connectionManager, this.device,
                request);

        // VERIFY - the right functions should be called
        verify(this.dlmsMessageListener).setDescription(String.format(
                "GetPeriodicMeterReadsGas for channel ONE, DAILY from %s until %s, retrieve attribute: {%s,%s,%s}",
                new DateTime(this.from), new DateTime(this.to), dlmsProfile.getClassId(), dlmsProfile.getObisCode(),
                dlmsProfile.getDefaultAttributeId()));

        verify(this.dlmsHelper, times(2)).validateBufferedDateTime(any(DateTime.class), any(CosemDateTimeDto.class),
                argThat(new DateTimeMatcher(this.from)), argThat(new DateTimeMatcher(this.to)));

        // VERIFY - the result should contain 2 values
        final List<PeriodicMeterReadsGasResponseItemDto> periodicMeterReads = result.getPeriodicMeterReadsGas();

        assertThat(periodicMeterReads.size()).isEqualTo(2);
        assertThat(periodicMeterReads.stream().anyMatch(r -> r.getConsumption() == meterValue1)).isEqualTo(true);
        assertThat(periodicMeterReads.stream().anyMatch(r -> r.getConsumption() == meterValue2)).isEqualTo(true);
        assertThat(
                periodicMeterReads.stream().allMatch(r -> this.areDatesEqual(r.getLogTime(), cosemDateTime))).isEqualTo(
                true);
        assertThat(periodicMeterReads.stream()
                .allMatch(r -> this.areDatesEqual(r.getCaptureTime(), cosemDateTime))).isEqualTo(true);
    }

    private AttributeAddress createAttributeAddress(final DlmsObject dlmsObject) {
        return new AttributeAddress(dlmsObject.getClassId(), new ObisCode(dlmsObject.getObisCode()),
                dlmsObject.getDefaultAttributeId());
    }

    private DlmsDevice createDevice(final Protocol protocol) {
        final DlmsDevice device = new DlmsDevice();
        device.setProtocol(protocol.getName(), protocol.getVersion());
        return device;
    }

    // Compares date with cosemDateTime. Note: cosemDateTime uses hundreths and not milliseconds
    private boolean areDatesEqual(final Date date, final CosemDateTimeDto cosemDateTime) {
        final DateTime dateTime = new DateTime(date);
        final CosemDateDto cosemDate = cosemDateTime.getDate();
        final CosemTimeDto cosemTime = cosemDateTime.getTime();

        return (dateTime.getYear() == cosemDate.getYear() && dateTime.getMonthOfYear() == cosemDate.getMonth()
                && dateTime.getDayOfMonth() == cosemDate.getDayOfMonth()
                && dateTime.getHourOfDay() == cosemTime.getHour() && dateTime.getMinuteOfHour() == cosemTime.getMinute()
                && dateTime.getSecondOfMinute() == cosemTime.getSecond()
                && dateTime.getMillisOfSecond() == cosemTime.getHundredths() * 10);
    }
}

