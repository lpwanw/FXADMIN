package com.csm.model;

public class ProcessModel {
    private Integer PID;
    private Integer PPID;
    private Integer Threads;
    private String cpu;
    private String cumulative;
    private Long VZS;
    private Long RSS;
    private String memory;
    private String processName;

    public Integer getPID() {
        return PID;
    }

    public void setPID(Integer PID) {
        this.PID = PID;
    }

    public Integer getPPID() {
        return PPID;
    }

    public void setPPID(Integer PPID) {
        this.PPID = PPID;
    }

    public Integer getThreads() {
        return Threads;
    }

    public void setThreads(Integer threads) {
        Threads = threads;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getCumulative() {
        return cumulative;
    }

    public void setCumulative(String cumulative) {
        this.cumulative = cumulative;
    }

    public Long getVZS() {
        return VZS;
    }

    public void setVZS(Long VZS) {
        this.VZS = VZS;
    }

    public Long getRSS() {
        return RSS;
    }

    public void setRSS(Long RSS) {
        this.RSS = RSS;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }
}
