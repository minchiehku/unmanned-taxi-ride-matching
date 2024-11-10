package v20240721;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class OrderExport {
    public static void toExcel(ArrayList<Order> pastOrderList, String filePath, String sheetName) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(fis);

        // Create new sheet with unique name
        String newSheetName = getUniqueSheetName(workbook, sheetName);
        Sheet sheet = workbook.createSheet(newSheetName);
        createHeaderRow(sheet);       

        // Fill in data rows
        int rowNum = 1;
        for (Order order : pastOrderList) {
            Map<Integer, Double> estimatedOrderTimePoint = order.getEstimatedOrderTimePoint();
            Map<Integer, Double> orderTimePoint = order.getOrderTimePoint();
            Customer customer = order.getCreatorCustomer();      
            
            double waitingTime;
            double diff;
            double EntireServiceTime;
            
            if(!order.isReservation()) {
                double walkingTime = orderTimePoint.get(3) - orderTimePoint.get(2);
                waitingTime = (orderTimePoint.get(5) - orderTimePoint.get(1)) - walkingTime;
                diff = waitingTime - customer.getTolerableWaitingTime();
                EntireServiceTime = orderTimePoint.get(8) - orderTimePoint.get(1);
            }else {
                waitingTime = orderTimePoint.get(5) - order.getReservedPickUpTimePoint();
                diff = waitingTime - customer.getTolerableWaitingTime();
                EntireServiceTime = orderTimePoint.get(8) - orderTimePoint.get(1);
            }

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(order.getOrderID());
            row.createCell(1).setCellValue(customer.getCustomerID());
            row.createCell(2).setCellValue(order.getTolerableWaitingTime());
            row.createCell(3).setCellValue(order.isReservation());
            row.createCell(4).setCellValue(order.getReservedPickUpTimePoint());
            row.createCell(5).setCellValue(order.getRequestNodeID()); //叫車
            row.createCell(6).setCellValue(getSafeValue(orderTimePoint, 1)); //叫車點時間
            row.createCell(7).setCellValue(getSafeValue(orderTimePoint, 2)); //車輛匹配完成時間
            row.createCell(8).setCellValue(order.getPickUpNodeID()); //上車點                
            row.createCell(9).setCellValue(getSafeValue(orderTimePoint, 3)); //客戶到達上車點時間
            row.createCell(10).setCellValue(getSafeValue(orderTimePoint, 4)); //車到達上車點時間
            row.createCell(11).setCellValue(getSafeValue(orderTimePoint, 5)); //都到達上車點時間, 客戶開始上車
            row.createCell(12).setCellValue(getSafeValue(orderTimePoint, 6)); //發車時間點
            row.createCell(13).setCellValue(order.getDropOffNodeID()); //下車點   
            row.createCell(14).setCellValue(getSafeValue(orderTimePoint, 7)); //到達目的地時間點, 客戶開始下車
            row.createCell(15).setCellValue(getSafeValue(orderTimePoint, 8)); // 服務完成時間點
            row.createCell(16).setCellValue(waitingTime); //等車時間
            row.createCell(17).setCellValue(diff); // diff差值
            row.createCell(18).setCellValue(EntireServiceTime); // diff差值
            row.createCell(19).setCellValue(customer.getRequestTimes()); //叫車次數
            row.createCell(20).setCellValue(order.getTotalTravelDistance()); //總距離
            row.createCell(21).setCellValue(order.isReAsigned()); //有重新指派嗎

        }


        fis.close();

        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
        }

        workbook.close();
        System.out.println("excel寫入完成");
    }

    private static void createHeaderRow(Sheet sheet) {
        // Define headers
        String[] headers = {"orderID", "customerID", "tolerableWaitingTime", "isReserved", "reservedTimePoint",
        		"requestNodeID", "1", "2",
        		"pickUpNodeID", "3", "4", "5", "6",
        		"dropOffNodeID", "7", "8",
        		"waitingTime", "diff", "EntireServiceTime", "RequestTimes", "travelDistance", "ReAsign"
        		};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
    }

    private static String getUniqueSheetName(Workbook workbook, String baseSheetName) {
        String uniqueSheetName = baseSheetName;
        int suffix = 1;

        while (workbook.getSheet(uniqueSheetName) != null) {
            uniqueSheetName = baseSheetName + "_" + suffix;
            suffix++;
        }

        return uniqueSheetName;
    }
    
    private static double getSafeValue(Map<Integer, Double> map, int key) {
    	if (map.get(key) == null) {
    		return -1;
    	}else {
    		return map.get(key);
    	}
    }
}
