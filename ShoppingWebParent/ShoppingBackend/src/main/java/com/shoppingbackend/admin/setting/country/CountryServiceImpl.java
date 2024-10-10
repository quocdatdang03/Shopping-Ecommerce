package com.shoppingbackend.admin.setting.country;

import com.shopping.common.entity.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryServiceImpl implements CountryService{

    @Autowired
    private CountryRepository countryRepository;

    @Override
    public List<Country> findAllCountry() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    @Override
    public Country saveCountry(Country country) {
        return countryRepository.save(country);
    }

    @Override
    public void deleteCountryById(Integer id) {
        countryRepository.deleteById(id);
    }
}
