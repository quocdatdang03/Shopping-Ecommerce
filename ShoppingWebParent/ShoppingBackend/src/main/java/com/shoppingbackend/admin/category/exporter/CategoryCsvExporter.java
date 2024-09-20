package com.shoppingbackend.admin.category.exporter;

import com.shopping.common.entity.Category;
import com.shopping.common.entity.User;
import com.shoppingbackend.admin.common.AbstractExporter;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@Service
public class CategoryCsvExporter extends AbstractExporter {
    public void export(List<Category> listCategory, HttpServletResponse response) throws IOException {
        // Tạo tên file export theo format sau:
        // users_YYYY-MM-DD_HH-MM-SS.csv , E.g. users_2024-09-13_08-30-12.csv
        super.setContentTypeAndExtensionFile("categories","text/csv", ".csv", response);

        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        // header value:
        // we'll export all fields of category exclude : image
        String[] csvHeader = {"Category ID", "Name", "Alias", "Enabled"};
        // fields mapping with User object:
        String[] fieldsMapping = {"id", "name", "alias", "enabled"};

        // Write header name of listCategory to Csv file:
        csvBeanWriter.writeHeader(csvHeader);


        // Write value listCategory to Csv file:
        listCategory.forEach(category -> {
            try {
                // Thay dấu -- thành 2 dấu space :
                String categoryName = category.getName();
                category.setName(categoryName.replace("--", "  "));
                csvBeanWriter.write(category, fieldsMapping);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // after writing csv file, it must be closed
        csvBeanWriter.close();

    }
}
