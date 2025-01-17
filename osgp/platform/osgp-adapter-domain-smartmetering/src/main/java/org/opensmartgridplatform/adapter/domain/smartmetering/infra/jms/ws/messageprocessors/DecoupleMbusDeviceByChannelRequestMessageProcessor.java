/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.InstallationService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.BaseRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DecoupleMbusDeviceByChannelRequestData;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DecoupleMbusDeviceByChannelRequestMessageProcessor
    extends BaseRequestMessageProcessor {

  private InstallationService installationService;

  protected DecoupleMbusDeviceByChannelRequestMessageProcessor(
      @Qualifier("domainSmartMeteringInboundWebServiceRequestsMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap,
      @Qualifier("domainSmartMeteringInstallationService")
          final InstallationService installationService) {
    super(messageProcessorMap, MessageType.DECOUPLE_MBUS_DEVICE_BY_CHANNEL);
    this.installationService = installationService;
  }

  @Override
  protected void handleMessage(
      final DeviceMessageMetadata deviceMessageMetadata, final Object dataObject)
      throws FunctionalException {

    final DecoupleMbusDeviceByChannelRequestData decoupleMbusDeviceByChannelRequest =
        (DecoupleMbusDeviceByChannelRequestData) dataObject;

    this.installationService.decoupleMbusDeviceByChannel(
        deviceMessageMetadata, decoupleMbusDeviceByChannelRequest);
  }
}
