package com.shoppingbackend.admin.setting.country;

import com.shopping.common.entity.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/countries")
public class CountryRestController {

    @Autowired
    private CountryServiceImpl countryService;

    @GetMapping("/list")
    public List<Country> listAllCountries() {
        return countryService.findAllCountry();
    }

    @PostMapping("/save")
    public String save(@RequestBody  Country country) {
        // @RequestBody : convert JSON String is sent from client to Java object for Server
        Country savedCountry = countryService.saveCountry(country);

        // return id of saved country object:
        return String.valueOf(savedCountry.getId());
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCountry(@PathVariable("id") Integer id) {
        countryService.deleteCountryById(id);
    }


}
