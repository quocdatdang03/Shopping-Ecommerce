package com.shopping.web.setting.filter;

import com.shopping.common.entity.Setting;
import com.shopping.web.setting.service.SettingService;
import com.shopping.web.setting.service.SettingServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import java.util.List;

// interface Filter : dùng để intercept request From View to Controller và response từ Controller -> View:
// method doFilter() : sẽ được gọi khi request từ View -> Controller và response từ Controller -> View:
// Dùng filter như này nhẳm giảm thiểu code lặp lại ở Controller -> vì với mỗi thay đổi trên web ta lại phải viết code call db từ controller
// Filter sẽ được gọi mỗi lần send request luôn
@Component
public class SettingFilter implements Filter {

    @Autowired
    private SettingServiceImpl settingService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // method doFilter() : sẽ được gọi khi request từ View -> Controller và response từ Controller -> View:

        // get URL of request:
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        String requestURL = servletRequest.getRequestURL().toString();

        // Lưu ý: requestURL sẽ trả về cả main request và cả các request from Static resources (như css, js,...)
        // Ta chỉ muốn get và set value cho main request thôi nên các request from Static resources thì return không làm gì cả:
        if(requestURL.endsWith(".css") || requestURL.endsWith(".js") || requestURL.endsWith(".jpg") || requestURL.endsWith(".png"))
        {
            // delegate (ủy quyền) xử lý request và response cho filterChain:
            filterChain.doFilter(request, response);
            return;
        }

        List<Setting> generalSettings = settingService.getGeneralSettings();

        // Lúc này các request attribute sẽ gửi đến View:
        generalSettings.forEach(setting -> {
            System.out.println(setting);
            request.setAttribute(setting.getKey(), setting.getValue());
        });


        // delegate (ủy quyền) xử lý request và response cho filterChain:
        filterChain.doFilter(request, response);
    }
}
