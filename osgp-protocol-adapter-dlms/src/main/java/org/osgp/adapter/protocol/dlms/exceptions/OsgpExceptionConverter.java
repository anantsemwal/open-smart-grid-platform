/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.exceptions;

import org.springframework.stereotype.Component;

import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

/**
 * OsgpExceptionConverter
 *
 * Converts given exception to a OsgpException type, and removes cause
 * exceptions from any other type. This is because other layers need to
 * deserialize the exception (and the cause within it) and the Exception class
 * must be known to these layers.
 *
 */
@Component
public class OsgpExceptionConverter {

    /**
     * If the Exception is a OsgpException, this exception is returned.
     *
     * If the Exception is not an OsgpException, only the exception message will
     * be wrapped in an TechnicalException (OsgpException subclass) and
     * returned. This also applies to the cause when it is an OsgpException.
     *
     * @param e
     *            The exception.
     * @return OsgpException the given exception or a new TechnicalException
     *         instance.
     */
    public OsgpException ensureOsgpOrTechnicalException(final Exception e) {
        if (e instanceof OsgpException) {
            return (OsgpException) e;
        }
        if (e instanceof ConnectionException) {
            return new FunctionalException(FunctionalExceptionType.CONNECTION_ERROR, ComponentType.PROTOCOL_DLMS, e);
        }

        return new TechnicalException(ComponentType.PROTOCOL_DLMS,
                "Unexpected exception while handling protocol request/response message",
                new OsgpException(ComponentType.PROTOCOL_DLMS, e.getMessage()));
    }
}
