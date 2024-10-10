package com.shoppingbackend.admin.setting.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.common.entity.Country;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest // Nó sẽ run toàn bộ Spring Boot Project để Testing:
@AutoConfigureMockMvc // Để sử dụng được MockMvc object:
public class CountryRestControllerTest {

    @Autowired
    private MockMvc mockMvc; // để test với controller, tức là test request đến Server :

    @Autowired
    private ObjectMapper objectMapper; // mapping JSON -> Java Object và ngược lại:

    @Test
    @WithMockUser(username = "dat03122003@gmail.com", password = "dat03122003", roles = "Admin") // Đây là annotation của Spring Security Test dùng cho việc giả user authenticaion cho việc testing
    public void listAllCountries() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/countries/list"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        // get json from response :
        String json = mvcResult.getResponse().getContentAsString();
        System.out.println(json);

        // convert json to java object (Country[] vì lúc này json đang return về 1 mảng các object)
        Country[] countries = objectMapper.readValue(json, Country[].class);
        for(Country country : countries) {
            System.out.println(country);
        }

        Assertions.assertThat(countries.length).isGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "dat03122003@gmail.com", password = "dat03122003", roles = "Admin") // Đây là annotation của Spring Security Test dùng cho việc giả user authenticaion cho việc testing
    public void testCreateNewCountry() throws Exception {
        String urlRequest = "/countries/save";
        Country country = new Country("ThaiLand", "TL");

        // test : client will send JSON data to server with method POST
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(urlRequest)
                                                .contentType("application/json")
                                                .content(objectMapper.writeValueAsString(country))
                                                .with(SecurityMockMvcRequestPostProcessors.csrf()) // request POST thì phải có csrf để khỏi lỗi 403 do Spring Security
                )

                .andDo(MockMvcResultHandlers.print()) // print details of request and response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        System.out.println(jsonResponse);

    }

    @Test
    @WithMockUser(username = "dat03122003@gmail.com", password = "dat03122003", roles = "Admin") // Đây là annotation của Spring Security Test dùng cho việc giả user authenticaion cho việc testing
    public void testUpdateCountry() throws Exception {
        String urlRequest = "/countries/save";
        Integer countryId = 3;
        Country country = new Country(countryId,"Thai Land", "TL");

        // test : client will send JSON data to server with method POST
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(urlRequest)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(country)) // write obj country to JSON string để làm data cho request để gửi đi cho server
                .with(SecurityMockMvcRequestPostProcessors.csrf()) // request POST thì phải có csrf để khỏi lỗi 403 do Spring Security
        )

                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        System.out.println(jsonResponse);

    }

    @Test
    @WithMockUser(username = "dat03122003@gmail.com", password = "dat03122003", roles = "Admin") // Đây là annotation của Spring Security Test dùng cho việc giả user authenticaion cho việc testing
    public void testDeleteCountry() throws Exception {
        Integer countryId = 4;
        String urlRequest = "/countries/delete/"+countryId;

        // test : client will send JSON data to server with method POST
        mockMvc.perform(MockMvcRequestBuilders.get(urlRequest))
                                    .andExpect(MockMvcResultMatchers.status().isOk());


    }

}
