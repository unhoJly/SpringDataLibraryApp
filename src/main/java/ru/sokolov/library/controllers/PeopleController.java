package ru.sokolov.library.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.sokolov.library.models.Person;
import ru.sokolov.library.services.PeopleService;
import ru.sokolov.library.util.PersonValidator;

import javax.validation.Valid;

@Controller
@RequestMapping("/people")
public class PeopleController {
    private final PeopleService peopleService;
//    private final PersonValidator personValidator;

    @Autowired
    public PeopleController(PeopleService peopleService/*, PersonValidator personValidator*/) {
        this.peopleService = peopleService;
//        this.personValidator = personValidator;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("people", peopleService.findAll());

        return "people/index";
    }

    @PostMapping()
    public String create(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "people/new";
        }

        peopleService.save(person);

        return "redirect:/people";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable int id, Model model) {
        model.addAttribute("person", peopleService.findOne(id));
        model.addAttribute("books", peopleService.getBooksByPersonId(id));

        return "people/show";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult,
                         @PathVariable int id) {
        if (bindingResult.hasErrors()) {
            return "people/edit";
        }

        peopleService.update(id, person);

        return "redirect:/people";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        peopleService.delete(id);

        return "redirect:/people";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable int id) {
        model.addAttribute("person", peopleService.findOne(id));

        return "people/edit";
    }

    @GetMapping("/new")
    public String newPerson(@ModelAttribute("person") Person person) {
        return "people/new";
    }
}