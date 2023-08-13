package com.ais.datd;

/**
 * @author zhaoxx
 * @program: svn-mgr
 * @description:
 * @date 2023-07-11 17:17:08
 */
public class IPSegmentTmpEntity {
    private String segment;
    private long startip;
    private long endip;

    private String startIpTwoRadix;

    public IPSegmentTmpEntity(String segment, long startip, long endip, String startIpTwoRadix)
    {
        this.segment=segment;
        this.startip=startip;
        this.endip=endip;
        this.startIpTwoRadix = startIpTwoRadix;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public long getStartip() {
        return startip;
    }

    public void setStartip(long startip) {
        this.startip = startip;
    }

    public long getEndip() {
        return endip;
    }

    public void setEndip(long endip) {
        this.endip = endip;
    }

    public String getStartIpTwoRadix() {
        return startIpTwoRadix;
    }

    public void setStartIpTwoRadix(String startIpTwoRadix) {
        this.startIpTwoRadix = startIpTwoRadix;
    }
}
