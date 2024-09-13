package com.shoppingbackend.admin.user.service;

import com.shopping.common.entity.User;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class UserCsvExporter {
    public void export(List<User> listUser, HttpServletResponse response) throws IOException {
        // Tạo tên file export theo format sau:
        // users_YYYY-MM-DD_HH-MM-SS.csv , E.g. users_2024-09-13_08-30-12.csv

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        // convert dateFormat to String:
        String timestamp = dateFormat.format(new Date());
        // create file name csv in above format:
        String fileName = "users_"+timestamp+".csv";

        response.setContentType("text/csv");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename="+fileName;
        response.setHeader(headerKey, headerValue);

        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        // header value:
        String[] csvHeader = {"User ID", "E-mail", "First Name", "Last Name", "Roles", "Enabled"};
        // fields mapping with User object:
        String[] fieldsMapping = {"id", "email", "firstName", "lastName", "roles", "enabled"};

        // Write header name of listUser to Csv file:
        csvBeanWriter.writeHeader(csvHeader);

        // Write value listUser to Csv file:
        listUser.forEach(user -> {
            try {

                csvBeanWriter.write(user, fieldsMapping);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // after writing csv file, it must be closed
        csvBeanWriter.close();

    }
}
