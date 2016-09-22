/**
 * Copyright 2016 Smart Society Services B.V.
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.database.device;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getBoolean;

import java.util.Map;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.platform.cucumber.steps.database.DeviceSteps;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;

/**
 * DLMS device specific steps.
 */
public class DlmsDeviceSteps {

    private static final String DEFAULT_COMMUNICATION_METHOD = "GPRS";
	private static final Boolean DEFAULT_IP_ADDRESS_IS_STATIC = true;

	@Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;
    
    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepository;

    @Autowired
    private DeviceSteps deviceSteps;

    @Given("^a dlms device$")
    public void a_dlms_device(final Map<String, String> settings) throws Throwable {

        // First create the device itself
        this.deviceSteps.aDevice(settings);

        // Now create the DLMS device in the DLMS database
        final String deviceIdentification = getString(settings, "DeviceIdentification",
                DeviceSteps.DEFAULT_DEVICE_IDENTIFICATION);
        final DlmsDevice dlmsDevice = new DlmsDevice(deviceIdentification);
        dlmsDevice.setCommunicationMethod(
        		getString(settings, "CommunicationMethod", DEFAULT_COMMUNICATION_METHOD));
        dlmsDevice.setIpAddressIsStatic(
        		getBoolean(settings, "IpAddressIsStatic", DEFAULT_IP_ADDRESS_IS_STATIC));
        dlmsDevice.setPort(4059L);
        // TODO: Set dlms specific device settings
        this.dlmsDeviceRepository.save(dlmsDevice);
        
        // Now add additional data in the core device.
        Device device = deviceRepository.findByDeviceIdentification(deviceIdentification);
        // TODO: Set the correct gateway
        //device.updateGatewayDevice(gatewayDevice);
        deviceRepository.save(device);
    }

    /**
     * check that the given dlms device is inserted
     *
     * @param deviceId
     * @return
     */
    @And("^the device with id \"([^\"]*)\" should be added in the dlms database$")
    public void theDeviceShouldBeAddedInTheDlmsDatabase(final String deviceId) throws Throwable {
        final DlmsDevice dlmsDevice = this.dlmsDeviceRepository.findByDeviceIdentification(deviceId);
        
        Assert.notNull(dlmsDevice);
        Assert.isTrue(dlmsDevice.getSecurityKeys().size() > 0);
    }
}
