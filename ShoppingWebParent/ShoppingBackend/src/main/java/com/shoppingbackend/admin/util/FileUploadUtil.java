package com.shoppingbackend.admin.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtil {

    public static void uploadFileToLocalDirectory(String nameUploadDir, String fileName, MultipartFile multipartFile) throws IOException {

        // get Path of Upload Directory (path nơi chứa các file image được upload từ input file):
        Path uploadPath = Paths.get(nameUploadDir);

        // Nếu chứa có upload dir thì sẽ tạo mới:
        if(!Files.exists(uploadPath))
        {
            // Tạo mới upload dir
            Files.createDirectories(uploadPath);
        }

        try(InputStream inputStream = multipartFile.getInputStream())
        {
            // get Path của tệp đích
            Path filePath = uploadPath.resolve(fileName);

            // Sử dụng Files.copy để sao chép luồng dữ liệu từ InputStream vào đường dẫn đích (filePath). StandardCopyOption.REPLACE_EXISTING
            // cho phép ghi đè nếu tệp đã tồn tại.
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        catch(IOException e)
        {
            throw new IOException("Could not save file: "+fileName, e);
        }
    }

    public static void cleanDirectory(String dir) {
        Path dirPath = Paths.get(dir);
        try {
            // Xóa tất cả các file trong directory
            Files.list(dirPath).forEach(fileItem -> {
                // Nếu không phải là subdirectory (tức là file) thì xóa file
                if(!Files.isDirectory(fileItem))
                {
                    try {
                        Files.delete(fileItem);
                    } catch (IOException e) {
                        System.out.println("Could not delete file: "+fileItem);
                    }
                }
            });
        }
        catch(IOException e)
        {
            System.out.println("Could not delete directory: "+dirPath);
        }
    }
}
