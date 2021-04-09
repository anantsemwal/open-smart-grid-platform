/**
 * Copyright 2021 Alliander N.V.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import lombok.Data;

import java.io.Serializable;

@Data
public class SetCommunicationNetworkInformationResponseData implements Serializable {

    private static final long serialVersionUID = 333099329546171974L;

    private String ipAddress;

    private Integer btsId;

    private Integer cellId;
}
