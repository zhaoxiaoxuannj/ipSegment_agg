package com.ais.datd.util;

//package com.ais.cs.ddi.web.utils;

//import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpBean {

    /**
     * 检查起始地址、终止地址是否有交叉
     *
     * @param subnetCode
     * @param startip
     * @param stopip
     * @return
     */
    public static boolean isIpOverlap(String startaddr1, String stopaddr1,
                                      String startaddr2, String stopaddr2) {
        try {
            long inputStartIp = parseIP(startaddr1);
            long inputStopIp = parseIP(stopaddr1);
            long subnetStartIp = parseIP(startaddr2);
            long subnetStopIp = parseIP(stopaddr2);
            if (inputStartIp <= subnetStopIp && inputStartIp >= subnetStartIp) {
                return true;
            }
            if (inputStopIp <= subnetStopIp && inputStopIp >= subnetStartIp) {
                return true;
            }
            if (subnetStartIp <= inputStopIp && subnetStartIp >= inputStartIp) {
                return true;
            }
            if (subnetStopIp <= inputStopIp && subnetStopIp >= inputStartIp) {
                return true;
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }

//    /**
//     * 检查起始地址、终止地址是否在父起止地址内
//     *
//     * @param subnetCode
//     * @param startip
//     * @param stopip
//     * @return
//     */
    public static boolean isIpInclude(String fstartaddr, String fstopaddr,
                                      String startaddr, String stopaddr) {
        try {
            long fstartaddrL = parseIP(fstartaddr);
            long fstopaddrL = parseIP(fstopaddr);
            long startaddrL = parseIP(startaddr);
            long stopaddrL = parseIP(stopaddr);
            if (fstartaddrL > startaddrL) {
                return false;
            }
            if (fstopaddrL < stopaddrL) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查IP地址是否在父起止地址内
     *
     * @param subnetCode
     * @param startip
     * @param stopip
     * @return
     */
    public static boolean isIpInclude(String fstartaddr, String fstopaddr,
                                      String ipaddr) {
        try {
            long fstartaddrL = parseIP(fstartaddr);
            long fstopaddrL = parseIP(fstopaddr);
            long ipaddrL = parseIP(ipaddr);
			/*System.out.println(fstartaddrL + "   " + fstopaddrL + "   "
					+ ipaddrL);*/
            if (fstartaddrL > ipaddrL) {
                return false;
            }
            if (fstopaddrL < ipaddrL) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查起始地址、终止地址是否在子网内
     *
     * @param subnetCode
     * @param startip
     * @param stopip
     * @return
     */
    public static boolean checkIpRange(String subnetCode, String netmask,
                                       String startip, String stopip) {
        try {
            String start = IpBean.getStartip(subnetCode, netmask);
            String stop = IpBean.getStopip(subnetCode, netmask);
            long inputStartIp = parseIP(startip);
            long inputStopIp = parseIP(stopip);
            long subnetStartIp = parseIP(start);
            long subnetStopIp = parseIP(stop);
            if (inputStopIp < inputStartIp) {
                return false;
            }
            if (inputStartIp < subnetStartIp) {
                return false;
            }
            if (inputStopIp > subnetStopIp) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 获取子网内起始地址
     *
     * @param subnetCode
     * @param netmask
     * @return
     */
    public static String getStartip(String subnetCode, String netmask) {
        long subnetL = parseIP(subnetCode);
        long startip = subnetL + 1;
        return deparseIP(startip);
    }

    /**
     * 获取子网内终止地址
     *
     * @param subnetCode
     * @param netmask
     * @return
     */
    //1.1.0.0 255.255.255.0 return 1.1.0.254
    public static String getStopip(String subnetCode, String netmask) {
        long subnetL = parseIP(subnetCode);
        long netmaskL = parseIP(netmask);

        long stopipL = subnetL + (~netmaskL) - 1;
        return deparseIP(stopipL);
    }

    //1.1.0.0 255.255.255.0 return 1.1.0.255
    public static String getLastip(String subnetCode, String netmask) {
        long subnetL = parseIP(subnetCode);
        long netmaskL = parseIP(netmask);

        long stopipL = subnetL + (~netmaskL);
        return deparseIP(stopipL);
    }

    /**
     * 获取子网的广播地址
     *
     * @param subnetCode
     * @param netmask
     * @return
     */
    public static String getBroadcastip(String subnetCode, String netmask) {
        long subnetL = parseIP(subnetCode);
        long netmaskL = parseIP(netmask);

        long broadcastipL = subnetL + (~netmaskL);
        return deparseIP(broadcastipL);
    }

    public static long parseIP(String strip) {
        strip = strip.trim();
        long[] ip = new long[4];
        int position1 = strip.indexOf(".");
        int position2 = strip.indexOf(".", position1 + 1);
        int position3 = strip.indexOf(".", position2 + 1);
        ip[0] = Long.parseLong(strip.substring(0, position1));
        ip[1] = Long.parseLong(strip.substring(position1 + 1, position2));
        ip[2] = Long.parseLong(strip.substring(position2 + 1, position3));
        ip[3] = Long.parseLong(strip.substring(position3 + 1));
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }

    public static String deparseIP(long ip) {
        long[] tmpIp = new long[4];
        tmpIp[3] = ip % 256;
        tmpIp[2] = (ip >> 8) % 256;
        tmpIp[1] = (ip >> 16) % 256;
        tmpIp[0] = (ip >> 24) % 256;

        for (int i = 0; i < 4; i++) {
            if (tmpIp[i] < 0) {
                tmpIp[i] = tmpIp[i] + 256;
            }
        }
        return tmpIp[0] + "." + tmpIp[1] + "." + tmpIp[2] + "." + tmpIp[3];
    }

    /**
     * 检查起始地址、终止地址包含地址个数
     *
     * @param subnetCode
     * @param startip
     * @param stopip
     * @return
     */
    public static long getIpSegmentSize(String startaddr, String stopaddr) {
        try {
            long startaddrL = parseIP(startaddr);
            long stopaddrL = parseIP(stopaddr);
            long size = stopaddrL - startaddrL;
            if (size < 0)
                return 0;
            else
                return size;
        } catch (Exception e) {
            return 0;
        }
    }

    //判断是否是ip地址
    public static boolean checkIp(String ip) {
        if (!ip.matches("^(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)){3}$")) {
            return false;
        }
        return true;
    }

    public static String parseIPv6(String strip) {
        strip = strip.trim();
        if (!strip.contains(".")) {
            int strlen = strip.length();
            int count = 0;
            for (int i = 0; i < strip.length(); i++) {
                if (strip.charAt(i) == ':') {
                    count++;
                }
            }
            if (strip.indexOf("::") != -1) {
                int index = strip.indexOf("::");
                if (count > 1) {
                    switch (count) {
                        case 2:
                            if (index == 0) {
                                strip = strip.replace("::",
                                        "0000:0000:0000:0000:0000:0000:0000:");
                            } else if (index == strlen - 2) {
                                strip = strip.replace("::",
                                        ":0000:0000:0000:0000:0000:0000:0000");
                            } else {
                                strip = strip.replace("::",
                                        ":0000:0000:0000:0000:0000:0000:");
                            }
                        case 3:
                            if (index == 0) {
                                strip = strip.replace("::",
                                        "0000:0000:0000:0000:0000:0000:");
                            } else if (index == strlen - 2) {
                                strip = strip.replace("::",
                                        ":0000:0000:0000:0000:0000:0000");
                            } else {
                                strip = strip.replace("::",
                                        ":0000:0000:0000:0000:0000:");
                            }

                            break;
                        case 4:
                            if (index == 0) {
                                strip = strip.replace("::",
                                        "0000:0000:0000:0000:0000:");
                            } else if (index == strlen - 2) {
                                strip = strip.replace("::",
                                        ":0000:0000:0000:0000:0000");
                            } else {
                                strip = strip.replace("::", ":0000:0000:0000:0000:");
                            }
                            break;
                        case 5:
                            if (index == 0) {
                                strip = strip.replace("::", "0000:0000:0000:0000:");
                            } else if (index == strlen - 2) {
                                strip = strip.replace("::", ":0000:0000:0000:0000");
                            } else {
                                strip = strip.replace("::", ":0000:0000:0000:");
                            }
                            break;
                        case 6:
                            if (index == 0) {
                                strip = strip.replace("::", "0000:0000:0000:");
                            } else if (index == strlen - 2) {
                                strip = strip.replace("::", ":0000:0000:0000");
                            } else {
                                strip = strip.replace("::", ":0000:0000:");
                            }
                            break;
                        case 7:
                            if (index == 0) {
                                strip = strip.replace("::", "0000:0000:");
                            } else if (index == strlen - 2) {
                                strip = strip.replace("::", ":0000:0000");
                            } else {
                                strip = strip.replace("::", ":0000:");
                            }
                            break;
                        case 8:
                            if (index == 0) {
                                strip = strip.replace("::", "0000:");
                            } else if (index == strlen - 2) {
                                strip = strip.replace("::", ":0000");
                            } else {
                                break;
                            }
                            break;
                        default:
                            break;
                    }
                }

            }
            String[] ipArray = strip.split(":");
            String ipStr = "";
            for (int i = 0; i < ipArray.length; i++) {
                String tmp = addZero(ipArray[i], 4);
                if (i == ipArray.length - 1)
                    ipStr += tmp;
                else
                    ipStr += tmp + ":";

            }
            return ipStr;
        } else {//获取ipv4,转换诚ipv6
            Map<String, String> map = new HashMap<String, String>();
            String ipstr = "";
            if ("::".equals(strip.substring(strip.length() - 2, strip.length()))) {
                strip = strip + "0";
            }
            for (int i = 0; i < strip.split(":").length; i++) {
                if (null != strip.split(":")[i] && !"".equals(strip.split(":")[i])) {
                    if (checkIp(strip.split(":")[i])) {
                        String ip = strip.split(":")[i];
                        map.put("ipv4", ip);
                        String[] ipv4 = ip.split("\\.");
                        String ipv4_1 = Long.toHexString((Long.parseLong(ipv4[0]) << 8) + Long.parseLong(ipv4[1]));
                        String v4 = "0000";
                        ipv4_1 = (v4 + ipv4_1).substring(ipv4_1.length(), (v4 + ipv4_1).length());
                        String ipv4_2 = Long.toHexString((Long.parseLong(ipv4[2]) << 8) + Long.parseLong(ipv4[3]));
                        ipv4_2 = (v4 + ipv4_2).substring(ipv4_2.length(), (v4 + ipv4_2).length());
                        map.put("" + i, ipv4_1 + ":" + ipv4_2);
                        map.put("ipv6", ipv4_1 + ":" + ipv4_2);
                        if (i == strip.split(":").length - 1) {
                            ipstr = ipstr + (ipv4_1 + ":" + ipv4_2);
                        } else {
                            ipstr = ipstr + (ipv4_1 + ":" + ipv4_2) + ":";
                        }
                    } else {
                        map.put(i + "", strip.split(":")[i]);
                        if (i == strip.split(":").length - 1) {
                            ipstr = ipstr + strip.split(":")[i];
                        } else {
                            ipstr = ipstr + strip.split(":")[i] + ":";
                        }
                    }
                } else {
                    map.put(i + "", "");
                    ipstr = ipstr + ":" + strip.split(":")[i];
                }
            }

            strip = ipstr;
            int strlen = strip.length();
            int count = 0;
            for (int i = 0; i < strip.length(); i++) {
                if (strip.charAt(i) == ':') {
                    count++;
                }
            }
            if (strip.indexOf("::") != -1) {
                int index = strip.indexOf("::");
                if (count > 1) {
                    switch (count) {
                        case 2:
                            if (index == 0) {
                                strip = strip.replace("::",
                                        "0000:0000:0000:0000:0000:0000:0000:");
                            } else if (index == strlen - 2) {
                                strip = strip.replace("::",
                                        ":0000:0000:0000:0000:0000:0000:0000");
                            } else {
                                strip = strip.replace("::",
                                        ":0000:0000:0000:0000:0000:0000:");
                            }
                        case 3:
                            if (index == 0) {
                                strip = strip.replace("::",
                                        "0000:0000:0000:0000:0000:0000:");
                            } else if (index == strlen - 2) {
                                strip = strip.replace("::",
                                        ":0000:0000:0000:0000:0000:0000");
                            } else {
                                strip = strip.replace("::",
                                        ":0000:0000:0000:0000:0000:");
                            }

                            break;
                        case 4:
                            if (index == 0) {
                                strip = strip.replace("::",
                                        "0000:0000:0000:0000:0000:");
                            } else if (index == strlen - 2) {
                                strip = strip.replace("::",
                                        ":0000:0000:0000:0000:0000");
                            } else {
                                strip = strip.replace("::", ":0000:0000:0000:0000:");
                            }
                            break;
                        case 5:
                            if (index == 0) {
                                strip = strip.replace("::", "0000:0000:0000:0000:");
                            } else if (index == strlen - 2) {
                                strip = strip.replace("::", ":0000:0000:0000:0000");
                            } else {
                                strip = strip.replace("::", ":0000:0000:0000:");
                            }
                            break;
                        case 6:
                            if (index == 0) {
                                strip = strip.replace("::", "0000:0000:0000:");
                            } else if (index == strlen - 2) {
                                strip = strip.replace("::", ":0000:0000:0000");
                            } else {
                                strip = strip.replace("::", ":0000:0000:");
                            }
                            break;
                        case 7:
                            if (index == 0) {
                                strip = strip.replace("::", "0000:0000:");
                            } else if (index == strlen - 2) {
                                strip = strip.replace("::", ":0000:0000");
                            } else {
                                strip = strip.replace("::", ":0000:");
                            }
                            break;
                        case 8:
                            if (index == 0) {
                                strip = strip.replace("::", "0000:");
                            } else if (index == strlen - 2) {
                                strip = strip.replace("::", ":0000");
                            } else {
                                break;
                            }
                            break;
                        default:
                            break;
                    }
                }

            }
            String[] ipArray = strip.split(":");
            String ipStr = "";
            for (int i = 0; i < ipArray.length; i++) {
                String tmp = addZero(ipArray[i], 4);
                if (i == ipArray.length - 1)
                    ipStr += tmp;
                else
                    ipStr += tmp + ":";

            }
            //ipStr=ipStr.replace(map.get("ipv6"),map.get("ipv4"));
            return ipStr;
        }
    }

    /**
     * @param str
     * @return
     * @description 小于16位，前面补0
     * @author lemon
     * @date 2010-9-17
     * @version 1.0.0
     * @history1:@author;@date;@description
     * @history2:@author;@date;@description
     */

    public static String addZero(String str, int total) {
        int bitcount = str.length();
        String zeroString = "";
        if (bitcount < total) {
            for (int i = 0; i < total - bitcount; i++) {
                zeroString += "0";
            }
        }
        return str = zeroString + str;

    }

    public static String parsePrefixToBinary(String prefix) {
        String[] prefixStrs = prefix.split(":");
        String str = "";
        String result = "";
        for (int i = 0; i < prefixStrs.length; i++) {
            long l = Long.parseLong(prefixStrs[i], 16);
            str = Long.toBinaryString(l);
            result += addZero(str, 16);

        }
        return result;
    }

    public static String compareIpv6(String string1, String string2) {
        String[] stringarray1 = string1.split(":");
        String[] stringarray2 = string2.split(":");
        boolean flag = false;
        for (int i = 0; i < stringarray1.length && i < stringarray2.length; i++) {
            String tempString1 = stringarray1[i];
            String tempString2 = stringarray2[i];
            long l1 = Long.valueOf(tempString1, 16);
            long l2 = Long.valueOf(tempString2, 16);
            if (l2 > l1) {
                return "-1";
            } else if (l2 < l1) {
                return "1";
            } else {
                continue;
            }
        }
        return "0";
    }

    public static String getStartipv6ByPrefix(String prefix) {
        String result = "";
        String prefixString = parseIPv6(prefix);
        String[] prefixArray = prefixString.split(":");
        String prefixTmp = "";
        for (int i = 0; i < prefixArray.length; i++) {
            prefixTmp += prefixArray[i];
        }
        BigInteger prefixTmp_bigInteger = new BigInteger(prefixTmp, 16);
        BigInteger startip_bigInteger = prefixTmp_bigInteger.add(new BigInteger("1"));
        String startipString = startip_bigInteger.toString(16);
        startipString = IpBean.addZero(startipString, 32);
        StringBuffer sb = new StringBuffer();
        for (int index = 0; index <= startipString.length(); index++) {

            if (index != 0 && index % 4 == 0) {
                int tmp = index;
                sb.append(startipString.substring(index - 4, index));
                if (index < 32)
                    sb.append(":");

            } else {

                continue;
            }


        }
        return sb.toString();

    }

    public static String getStopipv6ByPrefix(String prefix, int prefixlen) {

        double sss = Math.pow(2, 128 - prefixlen);


        BigInteger two = new BigInteger("2");
        BigInteger one = new BigInteger("1");
        BigInteger absone = new BigInteger("-1");
        int exponent = 128 - prefixlen;
        BigInteger total = two.pow(exponent).add(one.multiply(absone));
        String result = "";
        String prefixString = parseIPv6(prefix);
        String[] prefixArray = prefixString.split(":");
        String prefixTmp = "";
        for (int i = 0; i < prefixArray.length; i++) {
            prefixTmp += prefixArray[i];
        }
        BigInteger prefixTmp_bigInteger = new BigInteger(prefixTmp, 16);
        BigInteger startip_bigInteger = prefixTmp_bigInteger.add(total);
        String startipString = startip_bigInteger.toString(16);
        startipString = IpBean.addZero(startipString, 32);
        StringBuffer sb = new StringBuffer();
        for (int index = 0; index <= startipString.length(); index++) {

            if (index != 0 && index % 4 == 0) {
                int tmp = index;
                sb.append(startipString.substring(index - 4, index));
                if (index < 32)
                    sb.append(":");

            } else {

                continue;
            }


        }
        return sb.toString();
    }

//    /**
//     * 检查起始地址、终止地址是否在父起止地址内
//     *
//     * @param subnetCode
//     * @param startip
//     * @param stopip
//     * @return
//     */
    public static boolean isIpv6Include(String fstartaddr, String fstopaddr,
                                        String startaddr, String stopaddr) {
        try {
            if (compareIpv6(startaddr, fstartaddr).equals("-1")) {
                return false;
            }
            if (compareIpv6(stopaddr, fstopaddr).equals("1")) {
                return false;
            }
            if (compareIpv6(stopaddr, fstartaddr).equals("-1")) {
                return false;
            }
            if (compareIpv6(startaddr, fstopaddr).equals("1")) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isIpv6Include(String fstartaddr, String fstopaddr,
                                        String ipaddr) {
        try {
            if (compareIpv6(ipaddr, fstartaddr).equals("-1")) {
                return false;
            }
            if (compareIpv6(ipaddr, fstopaddr).equals("1")) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param startaddr1
     * @param stopaddr1
     * @param startaddr2
     * @param stopaddr2
     * @return
     * @description 校验ipv6的范围是否已经有覆盖
     * @author lemon
     * @date 2010-9-17
     * @version 1.0.0
     * @history1:@author;@date;@description
     * @history2:@author;@date;@description
     */
    public static boolean isIpv6Overlap(String startaddr1, String stopaddr1,
                                        String startaddr2, String stopaddr2) {
        try {
            String string1 = compareIpv6(startaddr1, stopaddr2);
            String string2 = compareIpv6(startaddr1, startaddr2);
            String string3 = compareIpv6(stopaddr1, stopaddr2);
            String string4 = compareIpv6(stopaddr1, startaddr2);
            String string5 = compareIpv6(startaddr2, stopaddr1);
            String string6 = compareIpv6(startaddr2, startaddr1);
            String string7 = compareIpv6(stopaddr2, stopaddr1);
            String string8 = compareIpv6(stopaddr2, startaddr1);
            if ((string1.equals("0") || string1.equals("-1"))
                    && (string2.equals("0") || string2.equals("1"))) {
                return true;
            }
            if ((string3.equals("0") || string3.equals("-1"))
                    && (string4.equals("0") || string4.equals("1"))) {
                return true;
            }
            if ((string5.equals("0") || string5.equals("-1"))
                    && (string6.equals("0") || string6.equals("1"))) {
                return true;
            }
            if ((string7.equals("0") || string7.equals("-1"))
                    && (string8.equals("0") || string8.equals("1"))) {
                return true;
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    /**
     * 将 全写的IPv6 转换成 缩写的IPv6
     *
//     * @param fullIPv6 全写的IPv6
     * @return 缩写的IPv6
     */
//    public static String parseFullIPv6ToAbbreviation(String fullIPv6) {
//        //空校验
//        if (StringUtils.isEmpty(fullIPv6)) {
//            return fullIPv6;
//        }
//
//        fullIPv6 = fullIPv6.trim();
//
//        //不符合 IPv6全写规则直接返回原值
//        String reg_fullIPv6 = "^([\\da-fA-F]{4}:){7}[\\da-fA-F]{4}$";
//        if (!Pattern.matches(reg_fullIPv6, fullIPv6)) {
//            return fullIPv6;
//        }
//
//        String abbreviation = "";
//
//        // 2,去掉每一位前面的0
//        String[] arr = fullIPv6.split(":");
//
//        for (int i = 0; i < arr.length; i++) {
//            arr[i] = arr[i].replaceAll("^0{1,3}", "");
//        }
//
//        // 3,找到最长的连续的0
//        String[] arr2 = arr.clone();
//        for (int i = 0; i < arr2.length; i++) {
//            if (!"0".equals(arr2[i])) {
//                arr2[i] = "-";
//            }
//        }
//
//        Pattern pattern = Pattern.compile("0{2,}");
//        Matcher matcher = pattern.matcher(StringUtils.join(arr2, ""));
//        String maxStr = "";
//        int start = -1;
//        int end = -1;
//        while (matcher.find()) {
//            if (maxStr.length() < matcher.group().length()) {
//                maxStr = matcher.group();
//                start = matcher.start();
//                end = matcher.end();
//            }
//        }
//        // 4,合并
//        if (maxStr.length() > 0) {
//            for (int i = start; i < end; i++) {
//                arr[i] = ":";
//            }
//        }
//        abbreviation = StringUtils.join(arr, ":");
//        abbreviation = abbreviation.replaceAll(":{2,}", "::");
//        return abbreviation;
//    }


    public static void main(String[] args) {

    }
}
