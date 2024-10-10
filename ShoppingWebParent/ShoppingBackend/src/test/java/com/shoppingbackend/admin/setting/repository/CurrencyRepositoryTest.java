package com.shoppingbackend.admin.setting.repository;

import com.shopping.common.entity.Currency;
import org.apache.xmlbeans.impl.store.Cur;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Arrays;
import java.util.List;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CurrencyRepositoryTest {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Test
    public void testCreateCurrencies() {
        List<Currency> currencies = Arrays.asList(
                new Currency("United States Dollar", "$", "USD"),
                new Currency("British Pound", "£", "GPB"),
                new Currency("Japanese Yen", "¥", "JPY"),
                new Currency("Euro", "€", "EUR"),
                new Currency("Russian Ruble", "₽", "RUB"),
                new Currency("South Korean Won", "₩", "KRW"),
                new Currency("Chinese Yuan", "¥", "CNY"),
                new Currency("Brazilian Real", "R$", "BRL"),
                new Currency("Australian Dollar", "$", "AUD"),
                new Currency("Canadian Dollar", "$", "CAD"),
                new Currency("Vietnamese đồng ", "₫", "VND"),
                new Currency("Indian Rupee", "₹", "INR")
        );

        Iterable<Currency> savedResult = currencyRepository.saveAll(currencies);

        Assertions.assertThat(savedResult).size().isGreaterThan(0);
    }

    @Test
    public void listAllCurrencyByOrderByNameAsc() {
        List<Currency> currencies = currencyRepository.findAllByOrderByNameAsc();

        currencies.forEach(item -> System.out.println(item));

        Assertions.assertThat(currencies.size()).isGreaterThan(0);

    }
}
