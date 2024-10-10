package com.shoppingbackend.admin.setting.controller;

import com.shopping.common.entity.Currency;
import com.shopping.common.entity.Setting;
import com.shoppingbackend.admin.setting.GeneralSettingUtils;
import com.shoppingbackend.admin.setting.repository.CurrencyRepository;
import com.shoppingbackend.admin.setting.service.SettingServiceImpl;
import com.shoppingbackend.admin.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class SettingController {

    @Autowired
    private SettingServiceImpl settingService;

    @Autowired
    private CurrencyRepository currencyRepository;

    @GetMapping("/settings")
    public String showSettings(Model model) {
        List<Setting> settingList = settingService.listAllSettings();
        List<Currency> currencyList = settingService.listAllCurrency();

        model.addAttribute("currencyList", currencyList);
        for(Setting setting : settingList) {
            model.addAttribute(setting.getKey(), setting.getValue());
        }

        model.addAttribute("pageTitle", "Setting - Shopping Admin");

        return "setting/settings";
    }

    @PostMapping("/settings/saveGeneralSetting")
    public String saveGeneralSetting(
            @RequestParam(name="inputFileImage") MultipartFile multipartFile,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) throws IOException {

        // get General Settings :
        GeneralSettingUtils generalSettingUtils = settingService.getGeneralSettingUtils();

        setSiteLogo(multipartFile, generalSettingUtils);
        setSymbol(request, generalSettingUtils);
        updateSettingValuesFromForm(request, generalSettingUtils.list());


        redirectAttributes.addFlashAttribute("message", "General Settings have been saved successfully!");
        return "redirect:/settings";
    }

    private void setSiteLogo(MultipartFile multipartFile, GeneralSettingUtils generalSettingUtils) throws IOException {
        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            String uploadDir = "../site-logo";
            String value = "/site-logo/"+fileName;

            // update value for field SITE_LOGO :
            generalSettingUtils.updateSiteLogo(value);

            // before save -> clean Dir :
            FileUploadUtil.cleanDirectory(uploadDir);

            // save new File image to upload Dir:
            FileUploadUtil.uploadFileToLocalDirectory(uploadDir,fileName,multipartFile);
        }
    }

    private void setSymbol(HttpServletRequest request ,GeneralSettingUtils generalSettingUtils) {
        Integer currencyId = Integer.valueOf(request.getParameter("CURRENCY_ID"));
        Optional<Currency> currencyOptional = currencyRepository.findById(currencyId);
        if(currencyOptional.isPresent())
        {
            Currency currency = currencyOptional.get();
            generalSettingUtils.updateCurrencySymbol(currency.getSymbol());
        }
    }

    private void updateSettingValuesFromForm(HttpServletRequest request,  List<Setting> generalSettings) {
        for(Setting generalSetting : generalSettings) {
            // set rest value from Form:
            String value = request.getParameter(generalSetting.getKey());
            if(value!=null)
                generalSetting.setValue(value);
        }
        // save all changes to Database :
        settingService.saveAll(generalSettings);
    }

}
