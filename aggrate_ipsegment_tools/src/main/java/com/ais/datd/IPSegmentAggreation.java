package com.ais.datd;

import com.ais.datd.util.IpBean;
import com.ais.datd.util.IpCustomUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaoxx
 * @program: aggrate_ipsegment_tools
 * @description:根据地市信息将分散的IP段聚合
 * @date 2023-07-12 09:24:39
 */
public class IPSegmentAggreation {
    private static int row2num =0;
    public static void main(String[] args) {
        String filename = args[0];
        String resultfilename = filename.split("\\.")[0]+"_result"+"."+filename.split("\\.")[1];
        File file = new File(resultfilename);
        if(file.exists())
        {
            file.delete();
        }
        try(FileInputStream fileInputStream = new FileInputStream(filename)) {
            XSSFWorkbook outxssfWorkbook = new XSSFWorkbook();
            XSSFSheet outxssfSheet =outxssfWorkbook.createSheet("合并结果");
            XSSFSheet outxssfSheet2 =outxssfWorkbook.createSheet("合并结果最终值");
//            outxssfWorkbook
            FileOutputStream fileOutputStream = new FileOutputStream(resultfilename);

            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fileInputStream);
            XSSFSheet inputxssfSheet = xssfWorkbook.getSheetAt(0);
            Map<String, List<String>> cityIpSegmentMap = new HashMap<>();
            //无需第一行
            Set<String> ipsegmentSet = new HashSet<>();
            for(int i=1;i<inputxssfSheet.getLastRowNum()+1;i++)
            {
                XSSFRow row= inputxssfSheet.getRow(i);
                String city = row.getCell(0).getStringCellValue();
                String ipsegment = row.getCell(1).getStringCellValue();
                if(ipsegmentSet.contains(ipsegment))
                {
                    System.out.println("********"+ipsegment);
                }
//                ipsegmentSet.add(ipsegment)
                cityIpSegmentMap.computeIfAbsent(city,key->new ArrayList<>()).add(ipsegment);
            }

            XSSFRow xssfRow = outxssfSheet.createRow(0);
            XSSFCell xssfCell = xssfRow.createCell(0);
            XSSFCell xssfCell1 = xssfRow.createCell(1);
            XSSFCell xssfCell2 = xssfRow.createCell(2);
            xssfCell.setCellValue("地市");
            xssfCell1.setCellValue("IP段");
            xssfCell2.setCellValue("聚合后的IP段");
            int rownum = 1;
            int row2num=0;
            for(String city:cityIpSegmentMap.keySet())
            {
                List<String> original = cityIpSegmentMap.get(city);
                rownum = processSingleCity(city,original,outxssfSheet,rownum,outxssfSheet2);
            }
            outxssfWorkbook.write(fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            outxssfWorkbook.close();
        }catch (Exception e)
        {
            System.out.println("error："+e.getMessage());
        }
    }

    public static int processSingleCity(String city,List<String>originalIPList,XSSFSheet xssfSheet,int rownum,XSSFSheet xssfSheet2){
        String oneStr = "11111111";
        String zeroStr = "0000000000000000000000000000000000000000000";
        List<IPSegmentTmpEntity> firstIpEntities = originalIPList.stream().map(a->{
            String subnet = a.split("/")[0];
            String nestmask = a.split("/")[1];
            long tmpstartip = IpCustomUtil.getStartIp(subnet,Integer.parseInt(nestmask));
            String startIptworadixStr = Long.toString(tmpstartip&0x7fffffffffffffffl,2);
//            System.out.println("length:"+startIptworadixStr.length());
            long tmpendip = IpCustomUtil.getEndIp(subnet,Integer.parseInt(nestmask));
            IPSegmentTmpEntity ipSegmentTmpEntity = new IPSegmentTmpEntity(a,tmpstartip,tmpendip,startIptworadixStr.length()<32?(zeroStr.substring(0,32-startIptworadixStr.length())+startIptworadixStr):startIptworadixStr);
            if(ipSegmentTmpEntity.getStartIpTwoRadix().length()<32) {
                System.out.println("111111111111:"+ipSegmentTmpEntity.getStartIpTwoRadix().length());
            }
            return ipSegmentTmpEntity;
        }).collect(Collectors.toList());
        firstIpEntities = firstIpEntities.stream().sorted((a,b)->a.getStartip()<b.getStartip()?-1:0).collect(Collectors.toList());
        while(firstIpEntities.size()>0) {
//            System.out.println("kkkkkkkkkkkk"+city);
            Map<Long,IPSegmentTmpEntity> startiptmpset = new HashMap<>();
            for(int enti=0;enti<firstIpEntities.size();enti++)
            {
                if(startiptmpset.keySet().contains(firstIpEntities.get(enti).getStartip())){
                    System.out.println("相同地址，需去重:"+firstIpEntities.get(enti).getSegment());
                    firstIpEntities.remove(firstIpEntities.get(enti));
                    enti=enti-1;
                }
                else {
                    startiptmpset.put(firstIpEntities.get(enti).getStartip(),firstIpEntities.get(enti));
                }
            }
            Map<Long, IPSegmentTmpEntity> entityMap = firstIpEntities.stream().collect(Collectors.toMap(key -> key.getStartip(), value -> value));
//            System.out.println("tttttttttttttt"+city);
            IPSegmentTmpEntity ipSegmentTmpEntity = firstIpEntities.get(0);
            //长度为61，只用到后面的32位,前29位不用管
            String startIpTwoRadix = ipSegmentTmpEntity.getStartIpTwoRadix();
            String maskLength = ipSegmentTmpEntity.getSegment().split("/")[1];
//            String oneStr = "11111111";
//            String zeroStr = "00000000";
            int incre = 4;
            for (incre = 8; incre > 0; incre--) {
                if (incre==1)
                {
                    System.out.println("***********");
                }
                int expectmasklength = Integer.parseInt(maskLength) - incre;
                String start = startIpTwoRadix.substring(0, expectmasklength) + zeroStr.substring(0, incre) + startIpTwoRadix.substring(expectmasklength + incre);
                String end = startIpTwoRadix.substring(0, expectmasklength) + oneStr.substring(0, incre) + startIpTwoRadix.substring(expectmasklength + incre);
                long startvalue = Long.parseLong(start, 2);
                long endvalue = Long.parseLong(end, 2);
                boolean flag = true;
                List<IPSegmentTmpEntity> middleList = new ArrayList<>();
                for (long tmp1 = startvalue; tmp1 <= endvalue; tmp1 = (long) (tmp1 + Math.pow(2, 32 - Integer.parseInt(maskLength)))) {
                    if (!entityMap.keySet().contains(tmp1)) {
                        flag = false;
                        break;
                    }
                    {
                        middleList.add(entityMap.get(tmp1));
                    }
                }
                if (flag) {
                    String netsub = IpCustomUtil.getStartIpStr(IpBean.deparseIP(startvalue), expectmasklength);
                    int startrownum =rownum;
                    int endrownum = rownum;
                    for(IPSegmentTmpEntity ipSegmentTmpEntity1:middleList) {
                        endrownum = rownum++;
                        XSSFRow xssfRow = xssfSheet.createRow(endrownum);
                        XSSFCell xssfCell1 = xssfRow.createCell(0);
                        XSSFCell xssfCell2 = xssfRow.createCell(1);
                        XSSFCell xssfCell3 = xssfRow.createCell(2);
                        xssfCell1.setCellValue(city);
                        xssfCell2.setCellValue(ipSegmentTmpEntity1.getSegment());
                        xssfCell3.setCellValue(netsub + "/" + expectmasklength);
                    }
                    if(startrownum<endrownum) {
                        CellRangeAddress cellRangeAddress = new CellRangeAddress(startrownum, endrownum, 2, 2);
                        xssfSheet.addMergedRegion(cellRangeAddress);
                        CellRangeAddress cellRangeAddress2 = new CellRangeAddress(startrownum, endrownum, 0, 0);
                        xssfSheet.addMergedRegion(cellRangeAddress2);
                        XSSFRow xssfRow3 =xssfSheet2.createRow(row2num++);
                        XSSFCell xssfCell1 = xssfRow3.createCell(0);
                        XSSFCell xssfCell2 = xssfRow3.createCell(1);
                        xssfCell1.setCellValue(city);
                        xssfCell2.setCellValue(netsub + "/" + expectmasklength);
                    }
                    System.out.println(netsub + "/" + expectmasklength);
                    for (IPSegmentTmpEntity ipSegmentTmpEntity1 : middleList) {
                        firstIpEntities.remove(ipSegmentTmpEntity1);
                    }
                    break;
                }
            }
            if(incre==0)
            {
                XSSFRow xssfRow = xssfSheet.createRow(rownum++);
                XSSFCell xssfCell1 = xssfRow.createCell(0);
                XSSFCell xssfCell2 = xssfRow.createCell(1);
                XSSFCell xssfCell3 = xssfRow.createCell(2);
                xssfCell1.setCellValue(city);
                xssfCell2.setCellValue(ipSegmentTmpEntity.getSegment());
                xssfCell3.setCellValue(ipSegmentTmpEntity.getSegment());
                System.out.println("sdddd:::::::"+ipSegmentTmpEntity.getSegment());
                firstIpEntities.remove(ipSegmentTmpEntity);
                XSSFRow xssfRow3 =xssfSheet2.createRow(row2num++);
                XSSFCell xssfCell4 = xssfRow3.createCell(0);
                XSSFCell xssfCell5 = xssfRow3.createCell(1);
                xssfCell4.setCellValue(city);
                xssfCell5.setCellValue(ipSegmentTmpEntity.getSegment());

            }
        }
        return rownum;
    }
    }

