package com.ais.datd.util;


import com.googlecode.ipv6.IPv6Network;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author daimy
 * @date 2021/8/17 5:46 下午
 */

public class IpCustomUtil {
private static final String IPV6_MASK_LENGTH = "128";
    /**
     * 根据子网掩码获取子网开始地址
     *
     * @param subNet  网络号 例：192.168.1.0
     * @param netmask 子网掩码 例：255.255.255.0
     * @return 子网首地址 如上参数，则返回 192.168.1.0
     */
    public static String getStartIpStr(String subNet, String netmask) {
        return IpBean.deparseIP(getStartIp(subNet, netmask));
    }

    /**
     * 根据子网掩码获取子网开始地址
     *
     * @param subNet  网络号 例：192.168.1.0
     * @param netmask 子网掩码 例：255.255.255.0
     * @return 子网首地址 如上参数，则返回 192.168.1.0 的长整形表示
     */
    public static long getStartIp(String subNet, String netmask) {
        long maskLong = IpBean.parseIP(netmask);
        return getStartIp(subNet, maskLong);
    }

    /**
     * 根据网络前缀获取子网开始地址
     *
     * @param subNet 网络号 例：192.168.1.0
     * @param prefix 子网掩码 例：24
     * @return 子网首地址 如上参数，则返回 192.168.1.0
     */
    public static String getStartIpStr(String subNet, int prefix) {
        return IpBean.deparseIP(getStartIp(subNet, prefix));
    }

    /**
     * 根据网络前缀获取子网开始地址
     *
     * @param subNet 网络号 例：192.168.1.0
     * @param prefix 子网掩码 例：24
     * @return 子网首地址 如上参数，则返回 192.168.1.0 的长整形表示
     */
    public static long getStartIp(String subNet, int prefix) {
        return getStartIp(subNet, getNetmaskLong(prefix));
    }


    /**
     * @param subNet      子网号
     * @param netmaskLong 子网掩码长整形
     * @return 子网的开始地址
     */
    private static long getStartIp(String subNet, long netmaskLong) {
        return IpBean.parseIP(subNet) & netmaskLong;
    }

    /**
     * @param subNet      子网号
     * @param netmaskLong 子网掩码长整形
     * @return 子网的结束地址
     */
    private static long getEndIp(String subNet, long netmaskLong) {
        // 长整形取反，因为ipv4地址只覆盖了低32位，因此不能直接使用取反运算符，使用异或运算符对低32位取反
        return getStartIp(subNet, netmaskLong) + (netmaskLong ^ 0x00000000ffffffffL);
    }

    /**
     * 根据网络前缀获取子网掩码
     *
     * @param prefix 网络前缀 例：24
     * @return 子网掩码 如上参数返回 255.255.255.0
     */
    public static String getNetmask(int prefix) {
        return IpBean.deparseIP(getNetmaskLong(prefix));
    }

    /**
     * 根据网络前缀获取子网掩码长整形
     *
     * @param prefix 网络前缀 例：24
     * @return 子网掩码
     */
    public static long getNetmaskLong(int prefix) {
        // 由于长整形为64位，大于ipv4地址（32位），使用左移运算后对高32位 置0，即为真实大小
        return (0xffffffffL << (32 - prefix)) & 0x00000000ffffffffL;
    }

    /**
     * 根据子网掩码获取子网结束地址
     *
     * @param subNet  子网号 例：192.168.1.0
     * @param netmask 子网掩码 例： 255.255.255.0
     * @return 子网结束地址 如上参数返回 192.168.1.255 的长整形
     */
    public static long getEndIp(String subNet, String netmask) {
        return getEndIp(subNet, IpBean.parseIP(netmask));
    }

    /**
     * 根据子网掩码获取子网结束地址
     *
     * @param subNet  子网号 例：192.168.1.0
     * @param netmask 子网掩码 例： 255.255.255.0
     * @return 子网结束地址 如上参数返回 192.168.1.255
     */
    public static String getEndIpStr(String subNet, String netmask) {
        return IpBean.deparseIP(getEndIp(subNet, netmask));
    }

    /**
     * 根据网络前缀获取子网结束地址
     *
     * @param subNet 子网号 例：192.168.1.0
     * @param prefix 子网掩码 例： 24
     * @return 子网结束地址 如上参数返回 192.168.1.255 的长整形
     */
    public static long getEndIp(String subNet, int prefix) {
        return getEndIp(subNet, getNetmaskLong(prefix));
    }

    /**
     * 根据网络前缀获取子网结束地址
     *
     * @param subNet 子网号 例：192.168.1.0
     * @param prefix 子网掩码 例： 24
     * @return 子网结束地址 如上参数返回 192.168.1.255
     */
    public static String getEndIpStr(String subNet, int prefix) {
        return IpBean.deparseIP(getEndIp(subNet, prefix));
    }



    /**
     * ipv4校验子网号和子网掩码的长度是否匹配
     */
    public static boolean validateNetAndMask(String ipNo, Integer maskLength) {
        long ipnoLong = IpBean.parseIP(ipNo);
        long maskLong = 0xffffffffL << (32 - maskLength);
        long result = ipnoLong & (~maskLong);
        if (result != 0) {
            return false;
        } else {
            return true;
        }
    }
//    public static Set<BigInteger> getIncludeIpsBySegment(String ipSegement){
//        IpSegmentPojo ipSegmentPojo = getIpSegWithMask(ipSegement);
//        Set<BigInteger> resultSet = new HashSet<>();
////        String[] ipAndMask = ipSegement.split("/");
//        BigInteger startIpLong = ipSegmentPojo.getStartIpLong();
//        BigInteger endIpLong = ipSegmentPojo.getEndIpLong();
//        for(;startIpLong.compareTo(endIpLong)<=0;startIpLong = startIpLong.add(new BigInteger("1")))
//        {
//            resultSet.add(startIpLong);
//        }
//        return resultSet;
//    }
//    public static Set<String> getIncludeIpsBySegmentTest(String ipSegement){
//        IpSegmentPojo ipSegmentPojo = getIpSegWithMask(ipSegement);
//        Set<String> resultSet = new HashSet<>();
////        String[] ipAndMask = ipSegement.split("/");
//        BigInteger startIpLong = ipSegmentPojo.getStartIpLong();
//        BigInteger endIpLong = ipSegmentPojo.getEndIpLong();
//        for(;startIpLong.compareTo(endIpLong)<=0;startIpLong = startIpLong.add(new BigInteger("1")))
//        {
//            resultSet.add(startIpLong.toString());
//        }
//        return resultSet;
//    }

    public static boolean checkIPv6(String checkStr) {
        int count=0;
        for(int i =0 ;i<checkStr.length();i++){
            if(checkStr.charAt(i) ==':'){
                count++;
            }
        }
        if(count<2){
            return false;
        }
        String regexp="^\\s*((([0-9A-Fa-f]{1,4}:){7}(([0-9A-Fa-f]{1,4})|:))|(([0-9A-Fa-f]{1,4}:){6}(:|((25[0-5]|2[0-4]\\d|[01]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|[01]?\\d{1,2})){3})|(:[0-9A-Fa-f]{1,4})))|(([0-9A-Fa-f]{1,4}:){5}((:((25[0-5]|2[0-4]\\d|[01]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|[01]?\\d{1,2})){3})?)|((:[0-9A-Fa-f]{1,4}){1,2})))|(([0-9A-Fa-f]{1,4}:){4}(:[0-9A-Fa-f]{1,4}){0,1}((:((25[0-5]|2[0-4]\\d|[01]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|[01]?\\d{1,2})){3})?)|((:[0-9A-Fa-f]{1,4}){1,2})))|(([0-9A-Fa-f]{1,4}:){3}(:[0-9A-Fa-f]{1,4}){0,2}((:((25[0-5]|2[0-4]\\d|[01]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|[01]?\\d{1,2})){3})?)|((:[0-9A-Fa-f]{1,4}){1,2})))|(([0-9A-Fa-f]{1,4}:){2}(:[0-9A-Fa-f]{1,4}){0,3}((:((25[0-5]|2[0-4]\\d|[01]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|[01]?\\d{1,2})){3})?)|((:[0-9A-Fa-f]{1,4}){1,2})))|(([0-9A-Fa-f]{1,4}:)(:[0-9A-Fa-f]{1,4}){0,4}((:((25[0-5]|2[0-4]\\d|[01]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|[01]?\\d{1,2})){3})?)|((:[0-9A-Fa-f]{1,4}){1,2})))|(:(:[0-9A-Fa-f]{1,4}){0,5}((:((25[0-5]|2[0-4]\\d|[01]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|[01]?\\d{1,2})){3})?)|((:[0-9A-Fa-f]{1,4}){1,2}))))(%.+)?\\s*$";
        Pattern patt=Pattern.compile(regexp);
        return patt.matcher(checkStr).matches();
    }

    /**
     *
     * @param checkStr
     * @return
     */
    public static boolean checkIPv4(String checkStr) {
        if(!checkStr.matches("^(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)){3}$")){
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        String ip ="116.160.172.4";
        System.out.println(IpCustomUtil.stringToBigInt(ip));
//        int masklength = 30;
//        long startIP = getStartIp(ip,masklength);
//        long endIP = getEndIp(ip,masklength);
//        String startIPstr = getStartIpStr(ip,masklength);
//        String endIPStr = getEndIpStr(ip,masklength);
//        System.out.println("startip:"+startIP+",endip:"+endIP);
//        System.out.println("startip:"+startIPstr+",endip:"+endIPStr);
    }

    /**
     * 将字符串形式的ip地址转换为BigInteger
     *
     * @param ipInString
     *            字符串形式的ip地址
     * @return 整数形式的ip地址
     */
    public static BigInteger stringToBigInt(String ipInString) {
        if(ipInString==null||ipInString.equals(""))
        {
            return new BigInteger("0");
        }
        ipInString = ipInString.replace(" ", "");
        byte[] bytes;
        if (ipInString.contains(":")) {
            //补全ipv6
            ipInString=ipInString.startsWith("::")?"0"+ipInString:ipInString;
            ipInString=ipInString.endsWith("::")?ipInString+"0":ipInString;
            bytes = ipv6ToBytes(ipInString);
        } else {
            bytes = ipv4ToBytes(ipInString);
        }
        return new BigInteger(bytes);
    }
        public static String BigIntToString(BigInteger ipInBigInt) {
        byte[] bytes = ipInBigInt.toByteArray();
        byte[] unsignedBytes = Arrays.copyOfRange(bytes, bytes.length==5||bytes.length==17?1:0, bytes.length);
        // 去除符号位
        try {
            String ip = InetAddress.getByAddress(unsignedBytes).toString();
            return ip.substring(ip.indexOf('/') + 1).trim();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ipv6地址转有符号byte[17]
     */
    private static byte[] ipv6ToBytes(String ipv6) {
        byte[] ret = new byte[17];
        ret[0] = 0;
        int ib = 16;
        boolean comFlag = false;// ipv4混合模式标记
        if (ipv6.startsWith(":"))// 去掉开头的冒号
        {
            ipv6 = ipv6.substring(1);
        }
        String[] groups = ipv6.split(":");
        for (int ig = groups.length - 1; ig > -1; ig--) {// 反向扫描
            if (groups[ig].contains(".")) {
                // 出现ipv4混合模式
                byte[] temp = ipv4ToBytes(groups[ig]);
                ret[ib--] = temp[4];
                ret[ib--] = temp[3];
                ret[ib--] = temp[2];
                ret[ib--] = temp[1];
                comFlag = true;
            } else if ("".equals(groups[ig])) {
                // 出现零长度压缩,计算缺少的组数
                int zlg = 9 - (groups.length + (comFlag ? 1 : 0));
                while (zlg-- > 0) {// 将这些组置0
                    ret[ib--] = 0;
                    ret[ib--] = 0;
                }
            } else {
                int temp = Integer.parseInt(groups[ig], 16);
                ret[ib--] = (byte) temp;
                ret[ib--] = (byte) (temp >> 8);
            }
        }
        return ret;
    }
    /**
     * ipv4地址转有符号byte[5]
     */
    private static byte[] ipv4ToBytes(String ipv4) {
        byte[] ret = new byte[5];
        ret[0] = 0;
        // 先找到IP地址字符串中.的位置
        int position1 = ipv4.indexOf(".");
        int position2 = ipv4.indexOf(".", position1 + 1);
        int position3 = ipv4.indexOf(".", position2 + 1);
        // 将每个.之间的字符串转换成整型
        ret[1] = (byte) Integer.parseInt(ipv4.substring(0, position1));
        ret[2] = (byte) Integer.parseInt(ipv4.substring(position1 + 1,
                position2));
        ret[3] = (byte) Integer.parseInt(ipv4.substring(position2 + 1,
                position3));
        ret[4] = (byte) Integer.parseInt(ipv4.substring(position3 + 1));
        return ret;
    }
  /**
   * 将IP格式统一改为1.1.1.1/31这种格式
   * @param originalIPList
   * @return
   */
  public static List<String> transformIpToIpSegment(List<String>originalIPList) {
    List<String> ipList = originalIPList.stream().map(a->{
      String[] arr = a.split("/");
      if(arr.length==1)
      {
        if(IpCustomUtil.checkIPv4(a))
        {
          return a+"/32";
        }
        else if(IpCustomUtil.checkIPv6(a)){
          return a+"/128";
        }
        else{
          return a;
        }
      }
      else{
        return a;
      }
    }).collect(Collectors.toList());
    return ipList;
  }

}
