package com.shoppingbackend.admin.setting.repository;

import com.shopping.common.entity.Currency;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyRepository extends CrudRepository<Currency, Integer> {
    // find all currency and order by name in ascending:
    public List<Currency> findAllByOrderByNameAsc();
}

