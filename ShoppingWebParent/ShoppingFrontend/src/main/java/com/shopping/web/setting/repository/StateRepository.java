package com.shopping.web.setting.repository;

import com.shopping.common.entity.Country;
import com.shopping.common.entity.State;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateRepository extends CrudRepository<State, Integer> {
    // find State by Country order by name in ascending
    public List<State> findAllByCountryOrderByNameAsc(Country country);
}
