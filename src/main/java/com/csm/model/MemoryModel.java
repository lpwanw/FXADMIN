package com.csm.model;

public class MemoryModel {
    private String physicalMemoryUsed;
    private String physicalMemoryAvailable;
    private String virtualMemoryUsed;
    private String virtualMemoryAvailable;
    private String physicalMemoryInfo;

    public String getPhysicalMemoryUsed() {
        return physicalMemoryUsed;
    }

    public void setPhysicalMemoryUsed(String physicalMemoryUsed) {
        this.physicalMemoryUsed = physicalMemoryUsed;
    }

    public String getPhysicalMemoryAvailable() {
        return physicalMemoryAvailable;
    }

    public void setPhysicalMemoryAvailable(String physicalMemoryAvailable) {
        this.physicalMemoryAvailable = physicalMemoryAvailable;
    }

    public String getVirtualMemoryUsed() {
        return virtualMemoryUsed;
    }

    public void setVirtualMemoryUsed(String virtualMemoryUsed) {
        this.virtualMemoryUsed = virtualMemoryUsed;
    }

    public String getVirtualMemoryAvailable() {
        return virtualMemoryAvailable;
    }

    public void setVirtualMemoryAvailable(String virtualMemoryAvailable) {
        this.virtualMemoryAvailable = virtualMemoryAvailable;
    }

    public String getPhysicalMemoryInfo() {
        return physicalMemoryInfo;
    }

    public void setPhysicalMemoryInfo(String physicalMemoryInfo) {
        this.physicalMemoryInfo = physicalMemoryInfo;
    }
}
