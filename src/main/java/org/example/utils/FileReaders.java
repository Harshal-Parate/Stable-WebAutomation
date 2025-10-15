package org.example.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class FileReaders {

    private static FileInputStream fs;
    private static Properties p;
    private static ObjectMapper objectMapper;


    public static Object[][] excelReader(String path, String sheetName) throws IOException {
        fs = new FileInputStream(path);
        Workbook workbook = WorkbookFactory.create(fs);
        Sheet sheet = workbook.getSheet(sheetName);
        int row = sheet.getPhysicalNumberOfRows();
        int col = sheet.getRow(0).getLastCellNum();
        Object[][] data = new Object[row - 1][col];
        for (int i = 0; i < row; i++) {
            Row r = sheet.getRow(i);
            for (int j = 0; j < col; j++) {
                Cell c = r.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                data[i][j] = c.getStringCellValue();
            }
        }
        return data;
    }

    public static String propertiesReader(String path, String key) throws IOException {
        fs = new FileInputStream(path);
        p = new Properties();
        p.load(fs);
        return (String) p.get(key);
    }

    public static String readJSON(String path, String key) throws IOException {
        objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(new File(path));
        String[] parts = key.split("\\.");
        JsonNode current = rootNode;
        for (String k : parts) {
            if (current == null) return null;
            current = current.get(k);
        }
        return current != null ? current.asText() : null;
    }
}
