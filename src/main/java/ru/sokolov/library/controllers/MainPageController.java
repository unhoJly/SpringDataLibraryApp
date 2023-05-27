package ru.sokolov.library.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.sokolov.library.services.BooksService;
import ru.sokolov.library.services.PeopleService;

@Controller
@RequestMapping("/")
public class MainPageController {
    private final BooksService booksService;
    private final PeopleService peopleService;

    @Autowired
    public MainPageController(BooksService booksService, PeopleService peopleService) {
        this.booksService = booksService;
        this.peopleService = peopleService;
    }

    @GetMapping()
    public String mainPage() {
        return "index";
    }

    @PostMapping()
    public String bookSearch(Model model,
                             @RequestParam(value = "bookQuery", required = false) String bookQuery,
                             @RequestParam(value = "personQuery", required = false) String personQuery) {
        if (bookQuery != null) {
            model.addAttribute("books", booksService.searchByTitle(bookQuery));
        } else if (personQuery != null) {
            model.addAttribute("people", peopleService.findByFullNameContainingIgnoreCase(personQuery));
        }

        return "index";
    }
}