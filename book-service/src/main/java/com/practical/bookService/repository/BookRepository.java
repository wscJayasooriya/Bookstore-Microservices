package com.practical.bookService.repository;

import com.practical.bookService.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingOrIsbn(String title, String isbn);
}