package com.shoppingbackend.admin.setting.repository;

import com.shopping.common.entity.Country;
import com.shoppingbackend.admin.setting.country.CountryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Arrays;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CountryRepositoryTest {

    @Autowired
    private CountryRepository countryRepository;

    @Test
    public void testCreateCountries() {
        List<Country> countryList = Arrays.asList(
                new Country("Viet Nam", "VN"),
                new Country("United Kingdom", "UK"),
                new Country("China", "CN")
        );

        countryRepository.saveAll(countryList);
    }
}
