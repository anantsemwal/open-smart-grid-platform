/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(value = "wsSmartMeteringInboundDomainResponsesMessageListener")
public class SmartMeteringResponseMessageListener implements MessageListener {

    @Autowired
    @Qualifier(value = "wsSmartMeteringInboundDomainResponsesMessageProcessorMap")
    private MessageProcessorMap domainResponseMessageProcessorMap;

    public SmartMeteringResponseMessageListener() {
        // empty constructor
    }

    @Override
    public void onMessage(final Message message) {
        try {
            log.info("Received message of type: {}", message.getJMSType());

            final ObjectMessage objectMessage = (ObjectMessage) message;
            final String correlationUid = objectMessage.getJMSCorrelationID();
            log.info("objectMessage CorrelationUID: {}", correlationUid);

            final MessageProcessor processor = this.domainResponseMessageProcessorMap.getMessageProcessor(
                    objectMessage);

            processor.processMessage(objectMessage);

        } catch (final JMSException ex) {
            log.error("Exception: {} ", ex.getMessage(), ex);
        }
    }
}
