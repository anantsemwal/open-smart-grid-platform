/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps;

import java.text.SimpleDateFormat;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceFirmware;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Firmware;
import com.alliander.osgp.domain.core.repositories.DeviceFirmwareRepository;
import com.alliander.osgp.domain.core.repositories.DeviceModelRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.platform.cucumber.support.DeviceId;

import cucumber.api.java.en.Given;

public class DeviceSteps {
    @Autowired
    private DeviceId deviceId;
    
    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private OrganisationRepository organizationRepository;

    @Autowired
    private DeviceModelRepository deviceModelRepository;
    
    @Autowired
    private DeviceFirmwareRepository deviceFirmwareRepository;

    @Given("^a device with DeviceID \"([^\"]*)\"$")
    public void aDeviceWithDeviceID(final String deviceId) throws Throwable {
        this.deviceId.setDeviceIdE(deviceId);
    }

    @Given("^a gas device with DeviceID \"([^\"]*)\"$")
    public void aGasDeviceWithDeviceID(final String deviceId) throws Throwable {
        this.deviceId.setDeviceIdG(deviceId);
    }
    
    /**
     * Generic method which adds a device using the settings.
     * 
     * @param settings The settings for the device to be used.
     * @throws Throwable
     */
    @Given("^a device$")
    public void aDevice(final Map<String, String> settings) throws Throwable {
    	
    	
    	// Set the required stuff
    	String deviceIdentification = settings.get("DeviceIdentification");
    	Device device = new Device(deviceIdentification);
    	
    	// Now set the optional stuff
    	if (settings.containsKey("DeviceId")) {
    		device.setId(Long.parseLong(settings.get("DeviceId")));
    	}
    	if (settings.containsKey("IsActivated")) {
    		device.setActivated(settings.get("IsActivated").toLowerCase().equals("true"));
    	}
    	if (settings.containsKey("TechnicalInstallationDate")) {
    		device.setTechnicalInstallationDate(new SimpleDateFormat("dd/MM/yyyy").parse(settings.get("TechnicalInstallationDate").toString()));
    	}
    	if (settings.containsKey("DeviceModelId")) {
        	DeviceModel deviceModel = deviceModelRepository.findOne(Long.parseLong(settings.get("DeviceModelId")));
        	device.setDeviceModel(deviceModel);
    	}
    	if (settings.containsKey("Version")) {
        	device.setVersion(Long.parseLong(settings.get("Version")));
    	}
    	if (settings.containsKey("Organization")) {
    		device.addOrganisation(settings.get("Organization"));
    	}
    	
    	// TODO: Add metadata if required
    	
    	deviceRepository.save(device);
    	
    	if (settings.containsKey("FirmwareVersion")) {
    		Firmware firmware = new Firmware();
    		firmware.setVersion(Long.parseLong(settings.get("FirmwareVersion")));
    		
    		DeviceFirmware deviceFirmware = new DeviceFirmware();
    		
    		deviceFirmware.setDevice(device);
    		deviceFirmware.setFirmware(firmware);
    		
    		deviceFirmwareRepository.save(deviceFirmware);
    	}
    }
}
