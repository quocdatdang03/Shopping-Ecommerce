package com.shoppingbackend.admin.setting.state;

import com.shopping.common.entity.Country;
import com.shopping.common.entity.State;
import com.shopping.common.entity.StateDTO;

import java.util.List;

public interface StateService {
    public List<StateDTO> findAllStateByCountry(Country country);
    public State saveState(State state);
    public void deleteStateById(Integer id);
}
