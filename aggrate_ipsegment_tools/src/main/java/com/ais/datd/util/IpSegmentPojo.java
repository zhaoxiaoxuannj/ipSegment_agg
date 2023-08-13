package com.ais.datd.util;

import java.math.BigInteger;

/**
 * @author zhaoxx
 * @program: aggrate_ipsegment_tools
 * @description:
 * @date 2023-07-12 09:43:59
 */
public class IpSegmentPojo {
    //地址段
    private String ipSegment;
    //组id
    private Integer ipGroupId;
    //类型
    private Integer typeId;
    //起始IP
    private String startIp;
    //终止IP
    private String endIp;
    //起始IP
    private BigInteger startIpLong;
    //终止IP
    private BigInteger endIpLong;
    //是否合法
    private boolean isIllegal;
    //非法原因
    private String reason;
    //行数
    private Integer lineNum;

    public IpSegmentPojo(){}

    public String getIpSegment() {
        return ipSegment;
    }

    public void setIpSegment(String ipSegment) {
        this.ipSegment = ipSegment;
    }

    public Integer getIpGroupId() {
        return ipGroupId;
    }

    public void setIpGroupId(Integer ipGroupId) {
        this.ipGroupId = ipGroupId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getStartIp() {
        return startIp;
    }

    public void setStartIp(String startIp) {
        this.startIp = startIp;
    }

    public String getEndIp() {
        return endIp;
    }

    public void setEndIp(String endIp) {
        this.endIp = endIp;
    }

    public BigInteger getStartIpLong() {
        return startIpLong;
    }

    public void setStartIpLong(BigInteger startIpLong) {
        this.startIpLong = startIpLong;
    }

    public BigInteger getEndIpLong() {
        return endIpLong;
    }

    public void setEndIpLong(BigInteger endIpLong) {
        this.endIpLong = endIpLong;
    }

    public boolean isIllegal() {
        return isIllegal;
    }

    public void setIllegal(boolean illegal) {
        isIllegal = illegal;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getLineNum() {
        return lineNum;
    }

    public void setLineNum(Integer lineNum) {
        this.lineNum = lineNum;
    }
}
