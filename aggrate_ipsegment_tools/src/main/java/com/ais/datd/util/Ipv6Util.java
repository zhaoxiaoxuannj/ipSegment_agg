package com.ais.datd.util;

//import cn.hutool.core.util.StrUtil;
//import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @ClassName Ipv6Util
 * @Author zhangzq13
 **/
public class Ipv6Util {

    /**
     * 将字符串形式的ip地址转换为BigInteger
     *
     * @param ipInString
     *            字符串形式的ip地址
     * @return 整数形式的ip地址
     */
    public static BigInteger stringToBigInt(String ipInString) {
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
     * 补全ipv6
     *
     * @param strip
     * @return
     */
    public static String parseFullIPv6(String strip) {
        if(strip.equals("::"))
        {
            return "0000:0000:0000:0000:0000:0000:0000:0000";
        }
        strip = strip.trim();
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
                            strip = strip.replace("::", "0000:0000:0000:0000:0000:0000:0000:");
                        } else if (index == strlen - 2) {
                            strip = strip.replace("::", ":0000:0000:0000:0000:0000:0000:0000");
                        } else {
                            strip = strip.replace("::", ":0000:0000:0000:0000:0000:0000:");
                        }
                    case 3:
                        if (index == 0) {
                            strip = strip.replace("::", "0000:0000:0000:0000:0000:0000:");
                        } else if (index == strlen - 2) {
                            strip = strip.replace("::", ":0000:0000:0000:0000:0000:0000");
                        } else {
                            strip = strip.replace("::", ":0000:0000:0000:0000:0000:");
                        }

                        break;
                    case 4:
                        if (index == 0) {
                            strip = strip.replace("::", "0000:0000:0000:0000:0000:");
                        } else if (index == strlen - 2) {
                            strip = strip.replace("::", ":0000:0000:0000:0000:0000");
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
            if (i == ipArray.length - 1) {
                ipStr += tmp;
            } else {
                ipStr += tmp + ":";
            }

        }
        return ipStr;
    }
    /**
     * @description 小于16位，前面补0
     * @author lemon
     * @date 2010-9-17
     * @version 1.0.0
     * @history1:@author;@date;@description
     * @history2:@author;@date;@description
     * @param str
     * @return
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

    /**
     * @desc:  将一行的 ipv6 转换为 最全格式
     * 比如 a::a;1::1
     * 转换后  000a:0000:0000:0000:0000:0000:0000:000a;0001:0000:0000:0000:0000:0000:0000:0001
     * @MethodName: parseFullIpv6s
     * @param: ipv6s
     * @param: split 分割符号
     * @Return: java.lang.String
     * @Author: zhangzq13
     **/
    public static String parseFullIpv6s(String ipv6s, String split) {
        if (ipv6s==null||ipv6s.equals("")) {
            return "";
        }else {
            String[] splitIpv6 = ipv6s.split(split);
            List<String> ipv6List = new ArrayList<>();
            for (String ipv6 : splitIpv6) {
                ipv6List.add(parseFullIPv6(ipv6));
            }
            return ipv6List.stream().collect(Collectors.joining(split));
        }
    }
    /**
     * 格式化ipv6
     * 比如 aa::1
     * 格式化后： aa:0:0:0:0:0:0:1
     * @MethodName: parseIpv6Format
     * @Param: [ip]
     * @Return: java.lang.String
     * @Author: zhangzq13
     **/
    public static String parseIpv6Format(String ip){
        String ipv6Full = parseFullIPv6(ip);
        String[] split = ipv6Full.split(":");
        String[] ipArray = new String[8];
        for (int i = 0; i < split.length; i++) {
            String tempStr = split[i];
            if ("0000".equals(split[i])){
                tempStr = "0";
            }else{
                String[] singleIp = split[i].split("");
                String single0 = singleIp[0];
                String single1 = singleIp[1];
                String single2 = singleIp[2];
                if ("0".equals(single0) && "0".equals(single1) && "0".equals(single2)){
                    tempStr = singleIp[3];
                }else  if("0".equals(single0) && "0".equals(single1) && !"0".equals(single2)) {
                    tempStr = singleIp[2] + singleIp[3];
                }else if ("0".equals(single0) && !"0".equals(single1) ){
                    tempStr = singleIp[1] + singleIp[2] + singleIp[3];
                }

            }
            ipArray[i] = tempStr;
        }

        return Arrays.asList(ipArray).stream().peek(String::toLowerCase).collect(Collectors.joining(":"));
    }

    /**
     * 获取ipv6地址段起始地址
     *
     * @param prefix ipv6地址段
     * @return ipv6地址段起始地址
     */
    public static String getStartipv6ByPrefix(String prefix) {
        String prefixString = parseIPv6(prefix);
        String[] prefixArray = prefixString.split(":");
        String prefixTmp = "";
        for (int i = 0; i < prefixArray.length; i++) {
            prefixTmp += prefixArray[i];
        }
        BigInteger prefixTmp_bigInteger = new BigInteger(prefixTmp, 16);
        BigInteger startip_bigInteger = prefixTmp_bigInteger;
        String startipString = startip_bigInteger.toString(16);
        startipString = addZero(startipString, 32);
        StringBuffer sb = new StringBuffer();
        for (int index = 0; index <= startipString.length(); index++) {

            if (index != 0 && index % 4 == 0) {
                sb.append(startipString.substring(index - 4, index));
                if (index < 32)
                    sb.append(":");

            } else {

                continue;
            }

        }
        return sb.toString();

    }

    /**
     * 获取ipv6地址段终止地址
     *
     * @param prefix    ipv6地址段
     * @param prefixlen ipv6地址段前缀长度
     * @return ipv6地址段终止地址
     */
    public static String getStopipv6ByPrefix(String prefix, int prefixlen) {
        BigInteger two = new BigInteger("2");
        BigInteger one = new BigInteger("1");
        BigInteger absone = new BigInteger("-1");
        int exponent = 128 - prefixlen;
        BigInteger total = two.pow(exponent).add(one.multiply(absone));
        String prefixString = parseIPv6(prefix);
        String[] prefixArray = prefixString.split(":");
        String prefixTmp = "";
        for (int i = 0; i < prefixArray.length; i++) {
            prefixTmp += prefixArray[i];
        }
        BigInteger prefixTmp_bigInteger = new BigInteger(prefixTmp, 16);
        BigInteger startip_bigInteger = prefixTmp_bigInteger.add(total);
        String startipString = startip_bigInteger.toString(16);
        startipString = addZero(startipString, 32);
        StringBuffer sb = new StringBuffer();
        for (int index = 0; index <= startipString.length(); index++) {

            if (index != 0 && index % 4 == 0) {
                sb.append(startipString.substring(index - 4, index));
                if (index < 32)
                    sb.append(":");

            } else {

                continue;
            }

        }
        return sb.toString();
    }

    /**
     * 转换ipv6地址为全格式
     *
     * @param strip ipv6地址
     * @return ipv6地址全格式
     */
    public static String parseIPv6(String strip) {
        strip = strip.trim();
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
                            strip = strip.replace("::", "0000:0000:0000:0000:0000:0000:0000:");
                        } else if (index == strlen - 2) {
                            strip = strip.replace("::", ":0000:0000:0000:0000:0000:0000:0000");
                        } else {
                            strip = strip.replace("::", ":0000:0000:0000:0000:0000:0000:");
                        }
                    case 3:
                        if (index == 0) {
                            strip = strip.replace("::", "0000:0000:0000:0000:0000:0000:");
                        } else if (index == strlen - 2) {
                            strip = strip.replace("::", ":0000:0000:0000:0000:0000:0000");
                        } else {
                            strip = strip.replace("::", ":0000:0000:0000:0000:0000:");
                        }

                        break;
                    case 4:
                        if (index == 0) {
                            strip = strip.replace("::", "0000:0000:0000:0000:0000:");
                        } else if (index == strlen - 2) {
                            strip = strip.replace("::", ":0000:0000:0000:0000:0000");
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
    }

    /**
     * ipv6地址转换为简写，英文大写格式
     *
     * @param ipv6Str
     * @return 简写，英文大写的ipv6地址
     */
//    public static String parsePv6ToAbbreviation(String ipv6Str) {
//        String fullIpv6 = parseFullIPv6(ipv6Str);
//        String[] arr = fullIpv6.split(":");
//        for (int i = 0, len = arr.length; i < len; i++) {
//            arr[i] = arr[i].replaceAll("^0{1,3}", "");
//        }
//        String[] arr2 = arr.clone();
//        for (int i = 0, len = arr2.length; i < len; i++) {
//            if (!"0".equals(arr2[i])) {
//                arr2[i] = "-";
//            }
//        }
//        //找到最长的连续的0
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
//        //合并
//        if (maxStr.length() > 0) {
//            for (int i = start; i < end; i++) {
//                arr[i] = ":";
//            }
//        }
//        return StringUtils.join(arr, ":").replaceAll(":{2,}", "::").toUpperCase();
//    }

    /**
     * 检验是否为ipv6地址
     *
     * @param address ipv6地址
     * @return boolean
     */
    public static boolean isIPv6(String address) {
        boolean result = false;
        String regIPv6 = "^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$";
        result = address.matches(regIPv6);
        return result;
    }


    public static void main(String[] args) {

        System.out.println(Ipv6Util.stringToBigInt("40.64.1.1"));
        System.out.println(Ipv6Util.stringToBigInt("40.64.1.6"));
    }

}
