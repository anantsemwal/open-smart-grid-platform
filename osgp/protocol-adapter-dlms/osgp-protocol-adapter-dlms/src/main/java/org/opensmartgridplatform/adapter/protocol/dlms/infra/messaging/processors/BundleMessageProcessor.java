/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.BundleService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.BundleMessagesRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Class for processing find events request messages */
@Component
public class BundleMessageProcessor extends DeviceRequestMessageProcessor {

  @Autowired private BundleService bundleService;

  public BundleMessageProcessor() {
    super(MessageType.HANDLE_BUNDLED_ACTIONS);
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn, final DlmsDevice device, final Serializable requestObject)
      throws ProtocolAdapterException {

    this.assertRequestObjectType(BundleMessagesRequestDto.class, requestObject);
    final BundleMessagesRequestDto bundleMessagesRequest = (BundleMessagesRequestDto) requestObject;

    return this.bundleService.callExecutors(conn, device, bundleMessagesRequest);
  }
}
