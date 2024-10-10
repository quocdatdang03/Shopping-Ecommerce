package com.shoppingbackend.admin.setting.repository;

import com.shopping.common.entity.Country;
import com.shopping.common.entity.State;
import com.shoppingbackend.admin.setting.country.CountryRepository;
import com.shoppingbackend.admin.setting.state.StateRepository;
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
public class StateRepositoryTest {

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Test
    public void testCreateState() {
        Country country = countryRepository.findById(3).get();

        State state = new State("Shang Hai", country);

        stateRepository.save(state);

    }
}
