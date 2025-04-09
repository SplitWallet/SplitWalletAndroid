package com.example.splitwallet.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
@Root(name = "ValuteCursOnDate", strict = false)
public class ValuteCurs {

    @Getter
    @Setter
    @Element(name = "Vname", required = false)
    private String name;

    @Getter
    @Setter
    @Element(name = "Vnom", required = false)
    private double nominal;

    @Getter
    @Setter
    @Element(name = "Vcurs", required = false)
    private double rate;

    @Getter
    @Setter
    @Element(name = "VchCode", required = false)
    private String code;

    public double getConvertedRate() {
        return rate / nominal;
    }
}