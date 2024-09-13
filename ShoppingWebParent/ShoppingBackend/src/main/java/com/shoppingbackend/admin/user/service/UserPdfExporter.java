package com.shoppingbackend.admin.user.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.shopping.common.entity.User;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.List;

@Service
public class UserPdfExporter extends AbstractUserExporter{
    public void export(List<User> listUser, HttpServletResponse response) throws IOException {
        // Tạo tên file export theo format sau:
        // users_YYYY-MM-DD_HH-MM-SS.csv , E.g. users_2024-09-13_08-30-12.csv
        super.setContentTypeAndExtensionFile("application/pdf", ".pdf", response);

        // we'll export all fields of user exclude : password, photos

        // create PDF document:
        Document document = new Document(PageSize.A4);

        // PdfWriter : dùng để write content của document vào output stream của HttpServletResponse
        // Browser sẽ download response như PDF document (vì ta đã chỉ định content type cho nó là application/pdf)
        // Nội dung của response (output stream) chính là document Pdf
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        // style :
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        font.setColor(Color.ORANGE);

        // ------- Create Title
        // set title (paragraph) and add font style for it
        Paragraph paragraph = new Paragraph("List of Users", font);
        // Căn giữa title
        paragraph.setAlignment(Element.ALIGN_CENTER);

        // add title vào document pdf :
        document.add(paragraph);


        // ------- Create Table:
        // tạo table với 6 column
        PdfPTable table = new PdfPTable(6);
        // set width cho table (chiếm 100% page)
        table.setWidthPercentage(100f);
        // thêm margin top cho table:
        table.setSpacingBefore(10);
        // Căn giữa text của table
        table.setHorizontalAlignment(Element.ALIGN_CENTER);

        // ------- Create Table Header
        createTableHeader(table);

        // ------- Create Table Datas
        createTableDatas(listUser, table);


        // add table vào document pdf:
        document.add(table);
        document.close();
    }

    private void createTableHeader(PdfPTable table)
    {
        // style :
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE);
        font.setColor(Color.WHITE);

        String[] headerFields = {"User ID", "E-mail", "First Name", "Last Name", "Roles", "Enabled"};

        // Tạo column cho table header :
        PdfPCell column = new PdfPCell();
        column.setBackgroundColor(Color.GREEN);
        column.setPadding(5);

        for(String columnName : headerFields)
        {
            // set text (đã style) cho column của header
            column.setPhrase(new Phrase(columnName, font));

            // add column vào table
            table.addCell(column);
        }
    }

    private void createTableDatas(List<User> userList, PdfPTable table) {

        for(User user : userList)
        {
            table.addCell(String.valueOf(user.getId()));
            table.addCell(user.getEmail());
            table.addCell(user.getFirstName());
            table.addCell(user.getLastName());
            table.addCell(user.getRoles().toString());
            table.addCell(String.valueOf(user.isEnabled()));
        }
    }
}
