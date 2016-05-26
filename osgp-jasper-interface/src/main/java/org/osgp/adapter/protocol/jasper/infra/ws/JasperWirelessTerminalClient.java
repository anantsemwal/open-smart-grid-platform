/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.jasper.infra.ws;

import org.apache.ws.security.WSConstants;
import org.osgp.adapter.protocol.jasper.config.JasperWirelessAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.soap.security.wss4j.Wss4jSecurityInterceptor;

import com.jasperwireless.api.ws.service.GetSessionInfoRequest;
import com.jasperwireless.api.ws.service.GetSessionInfoResponse;
import com.jasperwireless.api.ws.service.ObjectFactory;

public class JasperWirelessTerminalClient {

	@Autowired
	WebServiceTemplate webServiceTemplate;

	private static final ObjectFactory WS_CLIENT_FACTORY = new ObjectFactory();

	@Autowired
	CorrelationIdProviderService correlationIdProviderService;

	@Autowired
	JasperWirelessAccess jasperWirelessTerminalAccess;

	public GetSessionInfoResponse getSession(final String iccid) {
		final GetSessionInfoRequest getSessionInfoRequest = WS_CLIENT_FACTORY
				.createGetSessionInfoRequest();

		getSessionInfoRequest.setLicenseKey(this.jasperWirelessTerminalAccess
				.getLicenseKey());
		getSessionInfoRequest.setMessageId(this.correlationIdProviderService
				.getCorrelationId("messageID", iccid));
		getSessionInfoRequest.setVersion(this.jasperWirelessTerminalAccess
				.getApiVersion());
		getSessionInfoRequest.getIccid().add(iccid);

		for (final ClientInterceptor interceptor : this.webServiceTemplate
				.getInterceptors()) {
			if (interceptor instanceof Wss4jSecurityInterceptor) {
				setUsernameToken((Wss4jSecurityInterceptor) interceptor,
						this.jasperWirelessTerminalAccess.getUsername(),
						this.jasperWirelessTerminalAccess.getPassword());
			}
		}

		// override default uri
		this.webServiceTemplate.setDefaultUri(this.jasperWirelessTerminalAccess
				.getUri());

		return (GetSessionInfoResponse) this.webServiceTemplate
				.marshalSendAndReceive(
						getSessionInfoRequest,
						new SoapActionCallback(
								"http://api.jasperwireless.com/ws/service/terminal/GetSessionInfo"));
	}

	private static void setUsernameToken(
			final Wss4jSecurityInterceptor interceptor, final String user,
			final String pass) {
		interceptor.setSecurementUsername(user);
		interceptor.setSecurementPassword(pass);
		interceptor.setSecurementPasswordType(WSConstants.PW_TEXT);
	}
}
