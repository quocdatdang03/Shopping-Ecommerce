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

        // Những request bắt đầu bằng /user-images/** -> thì đều có thể truy cập vào folder user-images
        registerImageResources(dirName, userImageDir, registry);

        // -------- Config Image Resource for Category:
        String categoryImageDirName = "category-images";
        // get path category-images dir :
        Path categoryImageDir = Paths.get("../"+categoryImageDirName); // Vì ta lưu folder category-images ở ngoài ShoppingBackend nên phải ../

        // Những request bắt đầu bằng /category-images/** -> thì đều có thể truy cập vào folder category-images
        registerImageResources(categoryImageDirName, categoryImageDir, registry);


        // -------- Config Image Resource for Brand:
        String brandImageDirName = "brand-images";
        // get path category-images dir :
        Path brandImageDir = Paths.get("../"+brandImageDirName); // Vì ta lưu folder brand-images ở ngoài ShoppingBackend nên phải ../

        // Những request bắt đầu bằng /brand-images/** -> thì đều có thể truy cập vào folder brand-images
        registerImageResources(brandImageDirName, brandImageDir, registry);
    }

    private void registerImageResources(String imageDirName, Path imageDir, ResourceHandlerRegistry registry) {

        // get image path:
        String imageDirPath = imageDir.toFile().getAbsolutePath();

        // Những request bắt đầu bằng /<imageDirName>/** -> thì đều có thể truy cập vào folder <imageDirName>
        registry.addResourceHandler("/"+imageDirName+"/**")
                .addResourceLocations("file:/"+imageDirPath+"/");
    }
}
