package com.bspl.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.ArrayList;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
public class RestAdapterCommonDao {
    public static Connection getConnection(String UnitCode) throws SQLException {
        ArrayList<String> excelData = (ArrayList) excelFileDetails(UnitCode);
        Object[] excelObj = excelData.toArray();
        String dbname = excelObj[0].toString();
        String pwd = excelObj[1].toString();
        String ipAddress = excelObj[2].toString();
        String url = "jdbc:postgresql://" + ipAddress + "/" + dbname + "";
        System.out.println("url--->" + url);
        String user = "postgres";
        String password = pwd;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void closeDataSourceConnection(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static ArrayList<String> excelFileDetails(String unitcd) {
        System.out.println("unitcd--->" + unitcd);
        FileInputStream fis = null;
        HSSFWorkbook wb = null;
        ArrayList content = new ArrayList<ArrayList<String>>();
        ArrayList line = new ArrayList<String>();
        ArrayList<String> excelArray = new ArrayList<String>();
        try {
            String filePath="/home/lenovo/jdeveloper/jar/dbExcel.xls";
            //String filePath="/root/Documents/dbExcel.xls";
            fis =new FileInputStream(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            wb = new HSSFWorkbook(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HSSFSheet sheet = wb.getSheetAt(0);
        FormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
        for (Row row : sheet) {
            line = new ArrayList<String>();
            for (Cell cell : row) {
                switch (formulaEvaluator.evaluateInCell(cell).getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    String value = String.valueOf(cell.getNumericCellValue());
                    if (value.contains(".")) {
                        String result = value.substring(0, value.indexOf("."));
                        line.add(result);
                    }
                    line.add(value);
                    System.out.print(cell.getNumericCellValue() + "\t\t");
                    break;
                case Cell.CELL_TYPE_STRING:
                    line.add(cell.getStringCellValue());
                    System.out.print(cell.getStringCellValue() + "\t\t");
                    break;
                }
            }
            content.add(line.clone());
            System.out.println();
        }
        ArrayList<String> d = content;
        Object[] obj = d.toArray();
        int i = 1;
        while (i < obj.length) {
            System.out.println("value---" + obj[i]);
            if (obj[i].toString().contains(unitcd)) {
                ArrayList arry = (ArrayList) obj[i];
                Object[] cv2 = arry.toArray();
                int j = 0;
                while (j < cv2.length) {
                    System.out.println(cv2[j]);
                    excelArray.add(cv2[j].toString());
                    j++;
                }
            }
            i++;
        }

        return excelArray;
    }
}
