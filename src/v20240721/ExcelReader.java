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
	    	 * 1. �ǤJŪ���ɮצ�m�Psheet�W��
	    	 * 2. �bJava���إ�Excel����
	    	 * 3. �bJava���إ�Sheet����
	    	 * 4. �z�Lrow�إߪ�椺�e��, �̷�for-each�C�@��row���e�إ�
	    	 * 5. Ū��row���X, �`�I�s����row���X+1, row�q0�}�l�ҥH+1
	    	 * 6. �Ĥ@���x�sX�y��
	    	 * 7. �ĤG���x�sY�y��
	    	 * 8. �ĤT���x�s�O�_��W�����L��
	    	 * 9. �إ�node����
	    	 */
			
		    ArrayList<Node> nodeList = new ArrayList<>();
		    try {
		        FileInputStream fileInputStream = new FileInputStream(filePath);
		        Workbook workbook = new XSSFWorkbook(fileInputStream); 
		        Sheet sheet = workbook.getSheet(sheetName); 
		        
		        //�إߪ�椺�e
		        for (Row row : sheet) {
		        	
		            int nodeNum = row.getRowNum() + 1;    
		            double nodeX = row.getCell(0).getNumericCellValue(); //X�y��
		            double nodeY = row.getCell(1).getNumericCellValue(); //Y�y��
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
		
		    return nodeList; //�^��node���X
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
