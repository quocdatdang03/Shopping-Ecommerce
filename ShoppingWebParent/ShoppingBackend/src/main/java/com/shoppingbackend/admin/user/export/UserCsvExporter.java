package com.shoppingbackend.admin.user.export;

import com.shopping.common.entity.User;
import com.shoppingbackend.admin.user.service.AbstractUserExporter;
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
public class UserCsvExporter extends AbstractUserExporter {
    public void export(List<User> listUser, HttpServletResponse response) throws IOException {
        // Tạo tên file export theo format sau:
        // users_YYYY-MM-DD_HH-MM-SS.csv , E.g. users_2024-09-13_08-30-12.csv
        super.setContentTypeAndExtensionFile("text/csv", ".csv", response);

        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        // header value:
        // we'll export all fields of user exclude : password, photos
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
