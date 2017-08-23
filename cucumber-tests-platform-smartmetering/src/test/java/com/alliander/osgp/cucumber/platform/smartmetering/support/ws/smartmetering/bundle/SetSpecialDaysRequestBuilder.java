package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.SetSpecialDaysRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SpecialDay;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class SetSpecialDaysRequestBuilder {

    private static final int DEFAULT_SPECIAL_DAY_COUNT = 1;
    private static final int DEFAULT_SPECIAL_DAY_ID = 1;
    private static final byte[] DEFAULT_SPECIAL_DAY_DATE = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF };

    private List<SpecialDay> specialDays = new ArrayList<>();

    public SetSpecialDaysRequestBuilder withDefaults() {
        this.specialDays = new ArrayList<>();
        this.specialDays.add(this.getDefaultSpecialDay());
        return this;
    }

    public SetSpecialDaysRequestBuilder fromParameterMap(final Map<String, String> parameters) {
        this.specialDays = new ArrayList<>();
        final int specialDayCount = this.getSpecialDayCount(parameters);
        for (int i = 1; i <= specialDayCount; i++) {
            this.specialDays.add(this.getSpecialDay(parameters, i));
        }
        return this;
    }

    public SetSpecialDaysRequest build() {
        final SetSpecialDaysRequest request = new SetSpecialDaysRequest();
        request.getSpecialDays().addAll(this.specialDays);
        return request;
    }

    private int getSpecialDayCount(final Map<String, String> parameters) {
        if (parameters.containsKey(PlatformSmartmeteringKeys.SPECIAL_DAY_COUNT)) {
            return Integer.parseInt(parameters.get(PlatformSmartmeteringKeys.SPECIAL_DAY_COUNT));
        }
        return DEFAULT_SPECIAL_DAY_COUNT;

    }

    private SpecialDay getDefaultSpecialDay() {
        final SpecialDay specialDay = new SpecialDay();
        specialDay.setDayId(DEFAULT_SPECIAL_DAY_ID);
        specialDay.setSpecialDayDate(DEFAULT_SPECIAL_DAY_DATE);
        return specialDay;
    }

    private SpecialDay getSpecialDay(final Map<String, String> parameters, final int index) {
        final SpecialDay specialDay = new SpecialDay();
        specialDay.setDayId(this.getSpecialDayId(parameters, index));
        specialDay.setSpecialDayDate(this.getSpecialDayDate(parameters, index));
        return specialDay;
    }

    private int getSpecialDayId(final Map<String, String> parameters, final int index) {
        final String key = PlatformSmartmeteringKeys.SPECIAL_DAY_ID + "_" + index;
        if (parameters.containsKey(key)) {
            return Integer.parseInt(parameters.get(key));
        }
        return DEFAULT_SPECIAL_DAY_ID;
    }

    private byte[] getSpecialDayDate(final Map<String, String> parameters, final int index) {
        final String key = PlatformSmartmeteringKeys.SPECIAL_DAY_DATE + "_" + index;
        if (parameters.containsKey(key)) {
            return DatatypeConverter.parseHexBinary(parameters.get(key));
        }
        return DEFAULT_SPECIAL_DAY_DATE;

    }

}
