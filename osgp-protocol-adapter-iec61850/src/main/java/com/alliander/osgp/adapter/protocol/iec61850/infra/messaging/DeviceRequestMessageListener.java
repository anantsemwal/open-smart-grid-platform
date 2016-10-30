/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.messaging;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Component;

import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.NotSupportedException;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.MessageProcessor;
import com.alliander.osgp.shared.infra.jms.MessageProcessorMap;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Component(value = "iec61850RequestsMessageListener")
public class DeviceRequestMessageListener implements SessionAwareMessageListener<Message> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRequestMessageListener.class);

    @Autowired
    @Qualifier("iec61850DeviceRequestMessageProcessorMap")
    private MessageProcessorMap iec61850RequestMessageProcessorMap;

    @Autowired
    private DeviceResponseMessageSender deviceResponseMessageSender;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.jms.listener.SessionAwareMessageListener#onMessage
     * (javax.jms.Message, javax.jms.Session)
     */
    @Override
    public void onMessage(final Message message, final Session session) throws JMSException {
        final ObjectMessage objectMessage = (ObjectMessage) message;
        String messageType = null;
        MessageProcessor processor = null;
        try {
            messageType = message.getJMSType();
            LOGGER.info("Received message of type: {}", messageType);
            processor = this.iec61850RequestMessageProcessorMap.getMessageProcessor(objectMessage);
        } catch (final IllegalArgumentException | JMSException e) {
            LOGGER.error("Unexpected IllegalArgumentException | JMSExceptionduring during onMessage(Message)", e);
            this.createAndSendException(objectMessage, messageType);
            return;
        }
        processor.processMessage(objectMessage);
    }

    private void createAndSendException(final ObjectMessage objectMessage, final String messageType) {
        this.sendException(objectMessage, new NotSupportedException(ComponentType.PROTOCOL_IEC61850, messageType),
                "Unsupported device function: " + messageType);
    }

    private void sendException(final ObjectMessage objectMessage, final Exception exception, final String errorMessage) {
        try {
            final String domain = objectMessage.getStringProperty(Constants.DOMAIN);
            final String domainVersion = objectMessage.getStringProperty(Constants.DOMAIN_VERSION);
            final ResponseMessageResultType result = ResponseMessageResultType.NOT_OK;
            final FunctionalException osgpException = new FunctionalException(
                    FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION, ComponentType.PROTOCOL_IEC61850, exception);
            final Serializable dataObject = objectMessage.getObject();

            final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(objectMessage);
            final ProtocolResponseMessage protocolResponseMessage = new ProtocolResponseMessage.Builder()
            .deviceMessageMetadata(deviceMessageMetadata).domain(domain).domainVersion(domainVersion)
            .result(result).osgpException(osgpException).dataObject(dataObject).scheduled(false).build();

            this.deviceResponseMessageSender.send(protocolResponseMessage);
        } catch (final Exception e) {
            LOGGER.error("Unexpected error during sendException(ObjectMessage, Exception)", e);
        }
    }
}
