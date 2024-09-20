package com.shoppingbackend.admin.common;

import com.shopping.common.entity.User;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

// Tạo class này để chứa nội dung chung cho các class exporter :
public class AbstractExporter {

    // set Content type and extensionFile
    public void setContentTypeAndExtensionFile(String prefix, String contentType, String extensionFile, HttpServletResponse response) {
        // Tạo tên file export theo format sau:
        // users_YYYY-MM-DD_HH-MM-SS.csv , E.g. users_2024-09-13_08-30-12.csv

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        // convert dateFormat to String:
        String timestamp = dateFormat.format(new Date());
        // create file name csv in above format:
        String fileName = prefix+"_"+timestamp+extensionFile;

        response.setContentType(contentType);
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename="+fileName;
        response.setHeader(headerKey, headerValue);

    }
}
