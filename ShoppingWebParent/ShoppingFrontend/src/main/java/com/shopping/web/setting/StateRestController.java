package com.shopping.web.setting;

import com.shopping.common.entity.Country;
import com.shopping.common.entity.StateDTO;
import com.shopping.web.setting.service.StateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/settings")
public class StateRestController {

    @Autowired
    private StateServiceImpl stateService;

    @GetMapping("/list_states_by_country/{countryId}")
    public List<StateDTO> listStateByCountry(@PathVariable("countryId") Integer countryId) {
        List<StateDTO> stateDTOList = stateService.findAllStateByCountry(new Country(countryId));

        return stateDTOList;
    }
}
