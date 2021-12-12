package com.csm.model;

public class InternetModel {

    private String name;
    private Integer index;
    private Long speed;
    private String IPV4;
    private String IPV6;
    private String MACAdress;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Long getSpeed() {
        return speed;
    }

    public void setSpeed(Long speed) {
        this.speed = speed;
    }

    public String getIPV4() {
        return IPV4;
    }

    public void setIPV4(String IPV4) {
        this.IPV4 = IPV4;
    }

    public String getIPV6() {
        return IPV6;
    }

    public void setIPV6(String IPV6) {
        this.IPV6 = IPV6;
    }

    public String getMACAdress() {
        return MACAdress;
    }

    public void setMACAdress(String MACAdress) {
        this.MACAdress = MACAdress;
    }
}
