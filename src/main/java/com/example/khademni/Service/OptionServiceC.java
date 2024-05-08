package com.example.khademni.Service;

import com.example.khademni.Repository.OptionRepository;
import com.example.khademni.entity.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
@CrossOrigin(origins = "http://localhost:4200")
@Service
public class OptionServiceC implements OptionService{

    private final OptionRepository optionRepository;

    @Autowired
    public OptionServiceC(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    @Override
    public List<Option> getAllOptions() {
        return (List<Option>) optionRepository.findAll();
    }

    @Override
    public Option getOptionById(Long option_id) {
        return optionRepository.findById(option_id).get();
    }

    @Override
    public Option createOption(Option option) {
        return optionRepository.save(option);
    }

    @Override
    public Option updateOption(Option option) {
        return optionRepository.save(option);
    }

    @Override
    public void deleteOption(Long option_id) {
        optionRepository.deleteById(option_id);
    }
}
