package com.practical.bookService.service;

import com.practical.bookService.dto.AuthorDTO;
import com.practical.bookService.dto.BookDTO;
import com.practical.bookService.dto.ResponseDTO;
import com.practical.bookService.model.Book;
import com.practical.bookService.repository.BookRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RestTemplate restTemplate;

    public ResponseDTO<BookDTO> createBook(BookDTO bookDTO, String jwtToken) {
        try {
            Book book = new Book();
            BeanUtils.copyProperties(bookDTO, book);
            Set<Long> validAuthorIds = new HashSet<>();

            bookDTO.getAuthorIds().forEach(authorId -> {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + jwtToken);

                HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
                ResponseEntity<AuthorDTO> responseEntity = restTemplate.exchange(
                        "http://AUTHOR-SERVICE/api/authors/" + authorId,
                        HttpMethod.GET,
                        requestEntity,
                        new ParameterizedTypeReference<AuthorDTO>() {
                        }
                );

                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    validAuthorIds.add(authorId);
                } else {
                    throw new RuntimeException("Author with ID " + authorId + " does not exist");
                }
            });

            book.setAuthorIds(validAuthorIds);
            book = bookRepository.save(book);
            BeanUtils.copyProperties(book, bookDTO);
            return new ResponseDTO<>("Book created successfully", bookDTO, true);

        } catch (Exception e) {
            return new ResponseDTO<>("Unexpected error occurred while creating book: " + e.getMessage(), null, false);
        }
    }

    public ResponseDTO<BookDTO> getBook(Long id) {
        try {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Book not found with ID: " + id));
            BookDTO bookDTO = new BookDTO();
            BeanUtils.copyProperties(book, bookDTO);
            return new ResponseDTO<>("Book fetched successfully", bookDTO, true);
        } catch (RuntimeException e) {
            return new ResponseDTO<>(e.getMessage(), null, false);
        } catch (Exception e) {
            return new ResponseDTO<>("Unexpected error occurred while fetching book: " + e.getMessage(), null, false);
        }
    }

    public ResponseDTO<Page<BookDTO>> getAllBooks(Pageable pageable) {
        try {
            Page<Book> books = bookRepository.findAll(pageable);
            Page<BookDTO> bookDTOPage = books.map(book -> {
                BookDTO dto = new BookDTO();
                BeanUtils.copyProperties(book, dto);
                return dto;
            });
            return new ResponseDTO<>("Books fetched successfully", bookDTOPage, true);
        } catch (Exception e) {
            return new ResponseDTO<>("Unexpected error occurred while fetching all books: " + e.getMessage(), null, false);
        }
    }

    public ResponseDTO<BookDTO> updateBook(Long id, BookDTO bookDTO) {
        try {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Book not found with ID: " + id));
            BeanUtils.copyProperties(bookDTO, book, "id");
            book = bookRepository.save(book);
            BeanUtils.copyProperties(book, bookDTO);
            return new ResponseDTO<>("Book updated successfully", bookDTO, true);
        } catch (RuntimeException e) {
            return new ResponseDTO<>(e.getMessage(), null, false);
        } catch (Exception e) {
            return new ResponseDTO<>("Unexpected error occurred while updating book: " + e.getMessage(), null, false);
        }
    }

    public ResponseDTO<String> deleteBook(Long id) {
        try {
            bookRepository.deleteById(id);
            return new ResponseDTO<>("Book deleted successfully", "Success", true);
        } catch (Exception e) {
            return new ResponseDTO<>("Unexpected error occurred while deleting book: " + e.getMessage(), null, false);
        }
    }

    public ResponseDTO<List<BookDTO>> searchBooks(String query) {
        try {
            List<Book> books = bookRepository.findByTitleContainingOrIsbn(query, query);
            List<BookDTO> bookDTOs = books.stream()
                    .map(book -> {
                        BookDTO dto = new BookDTO();
                        BeanUtils.copyProperties(book, dto);
                        return dto;
                    })
                    .collect(Collectors.toList());
            return new ResponseDTO<>("Books fetched successfully", bookDTOs, true);
        } catch (Exception e) {
            return new ResponseDTO<>("Unexpected error occurred while searching books: " + e.getMessage(), null, false);
        }
    }

    public ResponseDTO<String> assignAuthorsToBook(Long bookId, List<Long> authorIds, String jwtToken) {
        try {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("Book not found with ID: " + bookId));

            Set<Long> validAuthorIds = new HashSet<>();

            authorIds.forEach(authorId -> {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + jwtToken);

                HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
                ResponseEntity<AuthorDTO> responseEntity = restTemplate.exchange(
                        "http://AUTHOR-SERVICE/api/authors/" + authorId,
                        HttpMethod.GET,
                        requestEntity,
                        new ParameterizedTypeReference<AuthorDTO>() {
                        }
                );

                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    validAuthorIds.add(authorId);
                } else {
                    throw new RuntimeException("Author with ID " + authorId + " does not exist");
                }
            });

            book.setAuthorIds(validAuthorIds);
            bookRepository.save(book);
            return new ResponseDTO<>("Authors assigned successfully", "Success", true);

        } catch (RuntimeException e) {
            return new ResponseDTO<>(e.getMessage(), null, false);
        } catch (Exception e) {
            return new ResponseDTO<>("Unexpected error occurred while assigning authors: " + e.getMessage(), null, false);
        }
    }
}
