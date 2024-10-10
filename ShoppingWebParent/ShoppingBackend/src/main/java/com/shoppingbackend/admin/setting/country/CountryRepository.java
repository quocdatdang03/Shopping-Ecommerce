package com.shoppingbackend.admin.setting.country;

import com.shopping.common.entity.Country;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends CrudRepository<Country, Integer> {
    // find all Country order by name in ascending
    public List<Country> findAllByOrderByNameAsc();
}
