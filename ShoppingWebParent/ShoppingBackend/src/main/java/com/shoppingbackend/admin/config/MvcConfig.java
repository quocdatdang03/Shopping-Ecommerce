package com.shoppingbackend.admin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    // Handle path for exposing user image
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // -------- Config Image Resource for User:
        String dirName = "user-images";

        // get path user-images directory:
        Path userImageDir = Paths.get(dirName);
        String userImageDirPath = userImageDir.toFile().getAbsolutePath();

        // Những request bắt đầu bằng /user-images/** -> thì đều có thể truy cập vào folder user-images
        registry.addResourceHandler("/"+dirName+"/**")
                .addResourceLocations("file:/"+userImageDirPath+"/");

        // -------- Config Image Resource for Category:
        String categoryImageDirName = "category-images";
        // get path category-images dir :
        Path categoryImageDir = Paths.get("../"+categoryImageDirName); // Vì ta lưu folder category-images ở ngoài ShoppingBackend nên phải ../
        String categoryImageDirPath = categoryImageDir.toFile().getAbsolutePath();

        // Những request bắt đầu bằng /category-images/** -> thì đều có thể truy cập vào folder category-images
        registry.addResourceHandler("/"+categoryImageDirName+"/**")
                .addResourceLocations("file:/"+categoryImageDirPath+"/");

    }
}
