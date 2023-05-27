package ru.sokolov.library.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sokolov.library.models.Book;
import ru.sokolov.library.models.Person;
import ru.sokolov.library.repositories.BooksRepository;
import ru.sokolov.library.repositories.PeopleRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BooksService {
    private final BooksRepository booksRepository;

    @Autowired
    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public List<Book> findAll(boolean sortByYear) {
        if (sortByYear) {
            return booksRepository.findAll(Sort.by("year"));
        }  else {
            return booksRepository.findAll();
        }
    }

    public List<Book> findWithPagination(Integer page, Integer booksPerPage, boolean sortByYear) {
        if (sortByYear) {
            return booksRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("year"))).getContent();
        }  else {
            return booksRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
        }
    }

    public Book findOne(int bookId) {
        Optional<Book> foundBook = booksRepository.findById(bookId);

        return foundBook.orElse(null);
    }

    public List<Book> searchByTitle(String query) {
        return booksRepository.findByTitleStartingWith(query);
    }

    @Transactional
    public void save(Book book) {
        booksRepository.save(book);
    }

    @Transactional
    public void update(int bookId, Book updatedBook) {
        Book bookToBeUpdated = booksRepository.findById(bookId).get();

        // Добавляем по-сути новую книгу (которая не находится в Persistence Context), поэтому нужен save()
        updatedBook.setId(bookId);
        updatedBook.setOwner(bookToBeUpdated.getOwner()); // Чтобы не терялась связь при обновлении
        booksRepository.save(updatedBook);
    }

    @Transactional
    public void delete(int id) {
        booksRepository.deleteById(id);
    }

    // Возвращает null, если книгу никто брал
    public Person getBookOwner(int bookId) {
        // Здесь Hibernate.initialize() не нужен, т.к. владелец (сторона One) загружается не лениво
        return booksRepository.findById(bookId).map(Book::getOwner).orElse(null);
    }

    // Освобождает книгу (этот метод вызывается, когда человек возвращает книгу в библиотеку)
    @Transactional
    public void release(int bookId) {
        booksRepository.findById(bookId).ifPresent(
                book -> {
                    book.setOwner(null);
                    book.setTakenAt(null);
                }
        );
    }

    // Назначает книгу человеку (этот метод вызывается, когда человек забирает книгу из библиотеки)
    @Transactional
    public void assign(int bookId, Person selectedPerson) {
        booksRepository.findById(bookId).ifPresent(
                book -> {
                    book.setOwner(selectedPerson);
                    book.setTakenAt(new Date());
                }
        );
    }
}