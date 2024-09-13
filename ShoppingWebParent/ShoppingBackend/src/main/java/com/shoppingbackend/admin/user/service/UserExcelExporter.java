package com.shoppingbackend.admin.user.service;

import com.shopping.common.entity.User;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class UserExcelExporter extends AbstractUserExporter{
    private XSSFWorkbook workBook;
    private XSSFSheet sheet;

    public UserExcelExporter()
    {
        workBook = new XSSFWorkbook();
        // Tạo sheet với name là Users cho excel:
        sheet = workBook.createSheet("Users");
    }

    // Tạo row header :
    private void createHeaderLine(){
        // Vì header là dòng thứ nhất -> có row index  = 0
        XSSFRow row = sheet.createRow(0);

        // Style cho column của header:
        XSSFCellStyle columnStyle = workBook.createCellStyle();
        // font style:
        XSSFFont font = workBook.createFont();
        font.setBold(true); // font bold
        font.setFontHeight(16); // font size
        columnStyle.setFont(font);

        // create column for row header :
        String[] headerColumns = {"User ID", "E-mail", "First Name", "Last Name", "Roles", "Enabled"};
        int index = 0;
        for(String headerValue : headerColumns) {
            createColumn(row, index, headerValue, columnStyle);
            index++;
        }

    }


    // Tạo row value:
    private void createValuesLine(List<User> userList) {
        // index của value line bắt đầu từ 1 vì ìndex = 0 là row của header
        int rowIndex = 1;

        // Style cho column của value:
        XSSFCellStyle columnStyle = workBook.createCellStyle();
        // font style:
        XSSFFont font = workBook.createFont();
        font.setFontHeight(14); // font size
        columnStyle.setFont(font);

        for(User user : userList) {
            // create row value:
            XSSFRow row = sheet.createRow(rowIndex);
            // create column for row value :
            createColumn(row, 0, user.getId(), columnStyle);
            createColumn(row, 1, user.getEmail(), columnStyle);
            createColumn(row, 2, user.getFirstName(), columnStyle);
            createColumn(row, 3, user.getLastName(), columnStyle);
            createColumn(row, 4, user.getRoles().toString(), columnStyle);
            createColumn(row, 5, user.isEnabled(), columnStyle);

            rowIndex++;
        }

    }

    // Tạo Column :
    private void createColumn(XSSFRow row, int columnIndex, Object columnValue, CellStyle columnStyle){

        // Tạo cell (column):
        XSSFCell column = row.createCell(columnIndex);

        // auto fit size column (Tự động căn chỉnh độ rộng của columnn theo content mà nó chứa)
        sheet.autoSizeColumn(columnIndex);

        if(columnValue instanceof Integer) {
            column.setCellValue((Integer) columnValue);
        }
        else if(columnValue instanceof Boolean) {
            column.setCellValue((Boolean) columnValue);
        }
        else {
            column.setCellValue((String) columnValue);
        }

        column.setCellStyle(columnStyle);

    }

    public void export(List<User> listUser, HttpServletResponse response) throws IOException {
        // Tạo tên file export theo format sau:
        // users_YYYY-MM-DD_HH-MM-SS.csv , E.g. users_2024-09-13_08-30-12.csv
        super.setContentTypeAndExtensionFile("application/octet-stream", ".xlsx", response);

        createHeaderLine();
        createValuesLine(listUser);

        ServletOutputStream outputStream = response.getOutputStream();
        workBook.write(outputStream);
        workBook.close();
        outputStream.close();


    }
}
