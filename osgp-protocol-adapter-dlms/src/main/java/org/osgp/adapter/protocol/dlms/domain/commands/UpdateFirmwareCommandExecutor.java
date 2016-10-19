package org.osgp.adapter.protocol.dlms.domain.commands;

import java.util.List;

import javax.annotation.PostConstruct;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DeviceConnector;
import org.osgp.adapter.protocol.dlms.domain.factories.FirwareImageFactory;
import org.osgp.adapter.protocol.dlms.exceptions.FirmwareImageFactoryException;
import org.osgp.adapter.protocol.dlms.exceptions.ImageTransferException;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.FirmwareVersionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.UpdateFirmwareRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.UpdateFirmwareResponseDto;

@Component
public class UpdateFirmwareCommandExecutor extends AbstractCommandExecutor<String, List<FirmwareVersionDto>> {

    private static final String EXCEPTION_MSG_UPDATE_FAILED = "Upgrade of firmware did not succeed.";

    private static final String EXCEPTION_MSG_INSTALLATION_FILE_NOT_AVAILABLE = "Installation file is not available.";

    @Autowired
    private FirwareImageFactory firmwareImageFactory;

    @Autowired
    GetFirmwareVersionsCommandExecutor getFirmwareVersionsCommandExecutor;

    @Value("${command.updatefirmware.activationstatuscheck.interval}")
    private int activationStatusCheckInterval;

    @Value("${command.updatefirmware.activationstatuscheck.timeout}")
    private int activationStatusCheckTimeout;

    @Value("${command.updatefirmware.verificationstatuscheck.interval}")
    private int verificationStatusCheckInterval;

    @Value("${command.updatefirmware.verificationstatuscheck.timeout}")
    private int verificationStatusCheckTimeout;

    private ImageTransfer.ImageTranferProperties imageTransferProperties;

    public UpdateFirmwareCommandExecutor() {
        super(UpdateFirmwareRequestDto.class);
    }

    @PostConstruct
    public void init() {
        this.imageTransferProperties = new ImageTransfer.ImageTranferProperties();
        this.imageTransferProperties.setActivationStatusCheckInterval(this.activationStatusCheckInterval);
        this.imageTransferProperties.setActivationStatusCheckTimeout(this.activationStatusCheckTimeout);
        this.imageTransferProperties.setVerificationStatusCheckInterval(this.verificationStatusCheckInterval);
        this.imageTransferProperties.setVerificationStatusCheckTimeout(this.verificationStatusCheckTimeout);

        super.init();
    }

    @Override
    public List<FirmwareVersionDto> execute(final DeviceConnector conn, final DlmsDevice device,
            final String firmwareIdentification) throws ProtocolAdapterException {

        final ImageTransfer transfer = new ImageTransfer(conn, imageTransferProperties, firmwareIdentification,
                this.getImageData(firmwareIdentification));

        try {
            if (!transfer.imageTransferEnabled()) {
                transfer.setImageTransferEnabled(true);
            }

            if (transfer.shouldInitiateTransfer()) {
                transfer.initiateImageTransfer();
            }

            if (transfer.shouldTransferImage()) {
                transfer.transferImageBlocks();
                transfer.transferMissingImageBlocks();
            }

            if (!transfer.imageIsVerified()) {
                transfer.verifyImage();
            }

            if (transfer.imageIsVerified() && transfer.imageToActivateOk()) {
                transfer.activateImage();
                return getFirmwareVersionsCommandExecutor.execute(conn, device, null);
            } else {
                // Image data is not correct.
                throw new ProtocolAdapterException("An unknown error occurred while updating firmware.");
            }
        } catch (ImageTransferException | ProtocolAdapterException e) {
            throw new ProtocolAdapterException(EXCEPTION_MSG_UPDATE_FAILED, e);
        } finally {
            transfer.setImageTransferEnabled(false);
        }
    }

    private byte[] getImageData(final String firmwareIdentification) throws ProtocolAdapterException {
        try {
            return this.firmwareImageFactory.getFirmwareImage(firmwareIdentification);
        } catch (FirmwareImageFactoryException e) {
            throw new ProtocolAdapterException(EXCEPTION_MSG_INSTALLATION_FILE_NOT_AVAILABLE, e);
        }
    }

    @Override
    public String fromBundleRequestInput(final ActionRequestDto bundleInput) throws ProtocolAdapterException {
        return ((UpdateFirmwareRequestDto) bundleInput).getFirmwareIdentification();
    }

    @Override
    public ActionResponseDto asBundleResponse(final List<FirmwareVersionDto> executionResult)
            throws ProtocolAdapterException {

        return new UpdateFirmwareResponseDto(executionResult);
    }
}
