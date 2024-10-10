package com.shoppingbackend.admin.setting.state;

import com.shopping.common.entity.Country;
import com.shopping.common.entity.State;
import com.shopping.common.entity.StateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/states")
public class StateRestController {

    @Autowired
    private StateServiceImpl stateService;

    @GetMapping("/list_by_country/{countryId}")
    public List<StateDTO> listStateByCountry(@PathVariable("countryId") Integer countryId) {
        List<StateDTO> stateDTOList = stateService.findAllStateByCountry(new Country(countryId));

        return stateDTOList;
    }

    @PostMapping("/save")
    public String saveState(@RequestBody State state) {
        State savedState = stateService.saveState(state);
        return String.valueOf(savedState.getId());
    }

    @DeleteMapping("/delete/{stateId}")
    public void deleteState(@PathVariable("stateId") Integer stateId) {
        stateService.deleteStateById(stateId);
    }
}
