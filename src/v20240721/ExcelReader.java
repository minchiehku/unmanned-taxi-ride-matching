package v20240721;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {
	public static ArrayList<Node> withNodes(String filePath, String sheetName) {
			
	    	/**
	    	 * Process
	    	 * 1. 傳入讀取檔案位置與sheet名稱
	    	 * 2. 在Java中建立Excel物件
	    	 * 3. 在Java中建立Sheet物件
	    	 * 4. 透過row建立表格內容ㄝ, 依照for-each每一行row內容建立
	    	 * 5. 讀取row號碼, 節點編號為row號碼+1, row從0開始所以+1
	    	 * 6. 第一格儲存X座標
	    	 * 7. 第二格儲存Y座標
	    	 * 8. 第三格儲存是否能上車布林值
	    	 * 9. 建立node物件
	    	 */
			
		    ArrayList<Node> nodeList = new ArrayList<>();
		    try {
		        FileInputStream fileInputStream = new FileInputStream(filePath);
		        Workbook workbook = new XSSFWorkbook(fileInputStream); 
		        Sheet sheet = workbook.getSheet(sheetName); 
		        
		        //建立表格內容
		        for (Row row : sheet) {
		        	
		            int nodeNum = row.getRowNum() + 1;    
		            double nodeX = row.getCell(0).getNumericCellValue(); //X座標
		            double nodeY = row.getCell(1).getNumericCellValue(); //Y座標
		            boolean canPark = row.getCell(2).getBooleanCellValue();
	
		            Node node = new Node(nodeNum, nodeX, nodeY);
		            nodeList.add(node);
		            node.setCanPark(canPark);
		        }
		
		        workbook.close();
		        fileInputStream.close();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		
		    return nodeList; //回傳node集合
		}
	
	public static ArrayList<Taxi> withTaxis(String filePath, String sheetName, ArrayList<Node> nodeList) {
		ArrayList<Taxi> taxiList = new ArrayList<>();
	    try {
	        FileInputStream fileInputStream = new FileInputStream(filePath);
	        Workbook workbook = new XSSFWorkbook(fileInputStream);
	        Sheet sheet = workbook.getSheet(sheetName);
	
	        for (Row row : sheet) {	        	
	            int taxiID = row.getRowNum() + 1;   
	            int currentNodeNum = (int)row.getCell(0).getNumericCellValue();
	            Taxi taxi = new Taxi(taxiID, currentNodeNum, nodeList.get(currentNodeNum - 1).getNodeX(), nodeList.get(currentNodeNum - 1).getNodeY());
	            taxiList.add(taxi);
	        }
	
	        workbook.close();
	        fileInputStream.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	
	    return taxiList;
	}
}
