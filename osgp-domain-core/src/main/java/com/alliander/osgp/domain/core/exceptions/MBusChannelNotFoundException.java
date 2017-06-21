/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.exceptions;

public class MBusChannelNotFoundException extends Exception {

    private static final long serialVersionUID = 7847147074103429934L;

    public MBusChannelNotFoundException(final String message) {
        super(message);
    }

}
