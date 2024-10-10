package com.shoppingbackend.admin.setting.state;

import com.shopping.common.entity.Country;
import com.shopping.common.entity.State;
import com.shopping.common.entity.StateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StateServiceImpl implements StateService {

    @Autowired
    private StateRepository stateRepository;

    @Override
    public List<StateDTO> findAllStateByCountry(Country country) {
        List<State> stateList = stateRepository.findAllByCountryOrderByNameAsc(country);
        List<StateDTO> stateDTOList = new ArrayList<StateDTO>();

        for(State state : stateList)
        {
            stateDTOList.add(new StateDTO(state.getId(), state.getName()));
        }
        return stateDTOList;
    }

    @Override
    public State saveState(State state) {
        return stateRepository.save(state);
    }

    @Override
    public void deleteStateById(Integer id) {
        stateRepository.deleteById(id);
    }
}
