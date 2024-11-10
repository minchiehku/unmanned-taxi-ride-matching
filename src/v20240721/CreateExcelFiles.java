package v20240721;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class CreateExcelFiles {
	public static void createExcelFiles(String prefix, int start, int end, String folderPath) {
        for (int i = start; i <= end; i += 120) {
            String fileName = String.format("%s.%d.xlsx", prefix, i);
            File file = Paths.get(folderPath, fileName).toFile();
            try (Workbook workbook = new XSSFWorkbook();
                 FileOutputStream fos = new FileOutputStream(file)) {
                workbook.createSheet();
                workbook.write(fos);
                System.out.println("Created: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error creating file: " + fileName);
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        // 設置參數
        String prefix = "4.6";
        int start = 120;
        int end = 720;
        String folderPath = "G:\\我的雲端硬碟\\NTPU\\KuWorkSpace\\UnmannedTaxi\\output\\case4"; // 指定目標資料夾路徑

        // 確保資料夾存在
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        createExcelFiles(prefix, start, end, folderPath);
    }
}
