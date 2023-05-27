package ru.sokolov.library.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.sokolov.library.models.Book;
import ru.sokolov.library.models.Person;
import ru.sokolov.library.services.BooksService;
import ru.sokolov.library.services.PeopleService;

import javax.validation.Valid;

@Controller
@RequestMapping("/books")
public class BooksController {
    private final BooksService booksService;
    private final PeopleService peopleService;

    @Autowired
    public BooksController(BooksService booksService, PeopleService peopleService) {
        this.booksService = booksService;
        this.peopleService = peopleService;
    }

    @GetMapping()
    public String index(Model model, @RequestParam(value = "page", required = false) Integer page,
                        @RequestParam(value = "books_per_page", required = false) Integer booksPerPage,
                        @RequestParam(value = "sort_by_year", required = false) boolean sortByYear) {
        if (page == null || booksPerPage == null) {
            model.addAttribute("books", booksService.findAll(sortByYear)); // выдача всех книг
        } else {
            model.addAttribute("books", booksService.findWithPagination(page, booksPerPage, sortByYear));
        }

        return "books/index";
    }

    @PostMapping()
    public String create(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "books/new";
        }

        booksService.save(book);

        return "redirect:/books";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable int id, Model model, @ModelAttribute("person") Person person) {
        model.addAttribute("book", booksService.findOne(id));
        Person bookOwner = booksService.getBookOwner(id);

        if (bookOwner != null) {
            model.addAttribute("owner", bookOwner);
        } else {
            model.addAttribute("people", peopleService.findAll());
        }

        return "books/show";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult,
                         @PathVariable int id) {
        if (bindingResult.hasErrors()) {
            return "books/edit";
        }

        booksService.update(id, book);

        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable int id) {
        booksService.delete(id);

        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable int id) {
        model.addAttribute("book", booksService.findOne(id));

        return "books/edit";
    }

    @PatchMapping("/{id}/assign")
    public String assign(@PathVariable int id, @ModelAttribute("person") Person selectedPerson) {
        // у selectedPerson назначено только поле id, остальные поля - null
        booksService.assign(id, selectedPerson);

        return "redirect:/books/{id}";
    }

    @PatchMapping("/{id}/release")
    public String release(@PathVariable int id) {
        booksService.release(id);

        return "redirect:/books/{id}";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book) {
        return "books/new";
    }
}