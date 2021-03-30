/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

import lombok.Data;

@Data
public class SetSubscriptionInformationResponseData implements Serializable {

    private static final long serialVersionUID = 333099329546171974L;

    String meId;

    String esn;

    String uimId;

    String eqId;

    String ipAddress;

    String mdn;

    Integer btsId;

    Integer cellId;

    String status;

    String custCode;

    String supplierReferenceId;

}
