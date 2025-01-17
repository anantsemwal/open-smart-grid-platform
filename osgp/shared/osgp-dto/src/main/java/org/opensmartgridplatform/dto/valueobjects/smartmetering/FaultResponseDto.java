/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.Objects;

public class FaultResponseDto extends ActionResponseDto {

  private static final long serialVersionUID = -2599283959144295334L;

  private final Integer code;
  private final String message;
  private final String component;
  private final String innerException;
  private final String innerMessage;
  private final FaultResponseParametersDto faultResponseParameters;

  private FaultResponseDto(final Builder builder) {
    super(OsgpResultTypeDto.NOT_OK, null, null);

    Objects.requireNonNull("Message is not allowed to be null", builder.message);

    this.code = builder.code;
    this.message = builder.message;
    this.component = builder.component;
    this.innerException = builder.innerException;
    this.innerMessage = builder.innerMessage;
    this.faultResponseParameters = builder.faultResponseParameters;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("FaultResponseDto[");
    if (this.hasCode()) {
      sb.append("code=").append(this.code).append(", ");
    }
    sb.append("message=").append(this.message);
    if (this.hasComponent()) {
      sb.append(", component=").append(this.component);
    }
    if (this.hasInnerException()) {
      sb.append(", innerException=").append(this.innerException);
    }
    if (this.hasInnerMessage()) {
      sb.append(", innerMessage=").append(this.innerMessage);
    }
    if (this.hasFaultResponseParameters()) {
      sb.append(", ").append(this.faultResponseParameters);
    }
    return sb.append(']').toString();
  }

  public boolean hasCode() {
    return this.code != null;
  }

  public Integer getCode() {
    return this.code;
  }

  public String getMessage() {
    return this.message;
  }

  public boolean hasComponent() {
    return this.component != null;
  }

  public String getComponent() {
    return this.component;
  }

  public boolean hasInnerException() {
    return this.innerException != null;
  }

  public String getInnerException() {
    return this.innerException;
  }

  public boolean hasInnerMessage() {
    return this.innerMessage != null;
  }

  public String getInnerMessage() {
    return this.innerMessage;
  }

  public boolean hasFaultResponseParameters() {
    return this.faultResponseParameters != null;
  }

  public FaultResponseParametersDto getFaultResponseParameters() {
    return this.faultResponseParameters;
  }

  public static class Builder {

    private Integer code;
    private String message;
    private String component;
    private String innerException;
    private String innerMessage;
    private FaultResponseParametersDto faultResponseParameters;

    public Builder withCode(final Integer code) {
      this.code = code;
      return this;
    }

    public Builder withMessage(final String message) {
      this.message = message;
      return this;
    }

    public Builder withComponent(final String component) {
      this.component = component;
      return this;
    }

    public Builder withInnerException(final String innerException) {
      this.innerException = innerException;
      return this;
    }

    public Builder withInnerMessage(final String innerMessage) {
      this.innerMessage = innerMessage;
      return this;
    }

    public Builder withFaultResponseParameters(
        final FaultResponseParametersDto faultResponseParameters) {
      this.faultResponseParameters = faultResponseParameters;
      return this;
    }

    public FaultResponseDto build() {
      return new FaultResponseDto(this);
    }
  }
}
