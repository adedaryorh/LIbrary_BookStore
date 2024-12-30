package com.bookstore_library.book.service;

import com.bookstore_library.book.entity.Book;
import com.bookstore_library.book.entity.User;
import com.bookstore_library.book.repository.BookRepository;
import com.bookstore_library.book.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    public Book getBookById(long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplied id is wrong"));
    }
    public Book saveOrUpdateBook(Book book) {
        return bookRepository.save(book);
    }
    public void deleteBookById(long id) {
        bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplied id does not exit "));
        bookRepository.deleteById(id);
    }

    public boolean borrowBook(String userId, Long bookId) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null || book.isBorrowed()) {
            return false; // Book not found or already borrowed
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false; // User not found
        }
        book.setBorrowed(true);
        user.getBorrowedBooks().add(book);
        bookRepository.save(book);
        userRepository.save(user);
        return true;
    }

    public boolean returnBook(String userId, Long bookId) {
        // Find the book by ID
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null || !book.isBorrowed()) {
            return false;
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || !user.getBorrowedBooks().contains(book)) {
            return false;
        }
        book.setBorrowed(false);
        user.getBorrowedBooks().remove(book);

        // Save updated book and user
        bookRepository.save(book);
        userRepository.save(user);
        return true;
    }
}