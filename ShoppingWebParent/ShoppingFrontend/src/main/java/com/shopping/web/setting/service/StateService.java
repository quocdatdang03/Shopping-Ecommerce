package com.shopping.web.setting.service;

import com.shopping.common.entity.Country;
import com.shopping.common.entity.State;
import com.shopping.common.entity.StateDTO;

import java.util.List;

public interface StateService {
    public List<StateDTO> findAllStateByCountry(Country country);
}
