package ru.sokolov.library.services;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sokolov.library.models.Book;
import ru.sokolov.library.models.Person;
import ru.sokolov.library.repositories.PeopleRepository;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PeopleService {
    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    @Transactional(readOnly = true)
    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    public Person findOne(int personId) {
        Optional<Person> foundPerson = peopleRepository.findById(personId);

        return foundPerson.orElse(null);
    }

    public List<Person> findByFullNameContainingIgnoreCase(String query) {
        return peopleRepository.findByFullNameContainingIgnoreCase(query);
    }

    public void save(Person person) {
        peopleRepository.save(person);
    }

    public void update(int id, Person updatedPerson) {
        updatedPerson.setId(id);
        peopleRepository.save(updatedPerson);
    }

    public void delete(int id) {
        peopleRepository.deleteById(id);
    }

    public Optional<Person> getPersonByFullName(String fullName) {
        return peopleRepository.findByFullName(fullName);
    }

    public List<Book> getBooksByPersonId(int personId) {
        Optional<Person> person = peopleRepository.findById(personId);

        if (person.isPresent()) {
            Hibernate.initialize(person.get().getBooks());
            // Мы внизу итерируемся по книгам, поэтому они точно будут загружены, но на всякий случай
            // не мешает всегда вызывать Hibernate.initialize()
            // (на случай, например, если код в дальнейшем поменяется и итерация по книгам удалится)

            // Проверка просроченности книг
            person.get().getBooks().forEach(book -> {
                long diffInMillies = Math.abs(book.getTakenAt().getTime() - new Date().getTime());

                if (diffInMillies > 864_000_000) { // = 10 суток
                    book.setExpired(true); // книга просрочена
                }
            });

            return person.get().getBooks();
        } else {
            return Collections.emptyList();
        }
    }
}