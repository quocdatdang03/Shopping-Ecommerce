package com.shoppingbackend.admin.setting.country;

import com.shopping.common.entity.Country;

import java.util.List;

public interface CountryService {
    public List<Country> findAllCountry();
    public Country saveCountry(Country country);
    public void deleteCountryById(Integer id);
}
