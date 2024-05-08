package tn.esprit.pi.controllers;

import tn.esprit.pi.repositories.QuestionRepository;
import tn.esprit.pi.services.OptionServiceC;
import tn.esprit.pi.entities.Option;
import tn.esprit.pi.entities.Question;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@AllArgsConstructor
@RequestMapping("/options")
public class OptionRestController {

    OptionServiceC optionService;
    QuestionRepository questionRepository ;


    @GetMapping("/retrieve-all-options")
    public List<Option> getOptions() {
        List<Option> listOptions = optionService.getAllOptions();
        return listOptions;
    }


    @GetMapping("/retrieve-option/{option-id}")
    public Option retrieveOption(@PathVariable("option-id") Long OptionId) {
        return optionService.getOptionById(OptionId);
    }



    @PostMapping("/add-option")
    public Option addOption(@RequestBody Option option) {
        Option option1 = optionService.createOption(option);
        return option1;
    }


    @PostMapping("/{question_id}/options")
    public Option createOptionForQuestion(@PathVariable Long question_id, @RequestBody Option option) {
        Question question = questionRepository.findById(question_id)
                .orElseThrow(() -> new NoSuchElementException("Question not found"));
        option.setQuestion(question);
        Option option1 = optionService.createOption(option);
        return option1;
    }



    @DeleteMapping("/remove-option/{option-id}")
    public void removeOption(@PathVariable("option-id") Long optionId) {
        optionService.deleteOption(optionId);
    }

    @PutMapping("/update-option")
    public Option updateOption(@RequestBody Option option) {
        Option option1 = optionService.updateOption(option);
        return option1;
    }






















}
