package com.practical.bookService.controller;

import com.practical.bookService.dto.BookDTO;
import com.practical.bookService.dto.ResponseDTO;
import com.practical.bookService.security.RoleAllowed;
import com.practical.bookService.service.BookService;
import com.practical.bookService.service.RabbitMQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    @Autowired
    private BookService bookService;
    @Autowired
    private RabbitMQSender rabbitMQSender;

    @PostMapping
    @RoleAllowed({"ADMIN"})
    public ResponseEntity<?> createBook(@RequestBody BookDTO bookDTO, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        ResponseDTO<BookDTO> response = bookService.createBook(bookDTO, jwtToken);

        if (response.isSuccess()) {
            String notificationMessage = "New book added: " + bookDTO.getTitle();
            rabbitMQSender.sendBookNotification(notificationMessage);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    @RoleAllowed({"USER", "ADMIN"})
    public ResponseEntity<?> getBook(@PathVariable Long id) {
        ResponseDTO<BookDTO> response = bookService.getBook(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    @RoleAllowed({"USER", "ADMIN"})
    public ResponseEntity<?> getAllBooks(Pageable pageable) {
        ResponseDTO<Page<BookDTO>> response = bookService.getAllBooks(pageable);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    @RoleAllowed({"ADMIN"})
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
        ResponseDTO<BookDTO> response = bookService.updateBook(id, bookDTO);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @RoleAllowed({"ADMIN"})
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        ResponseDTO<String> response = bookService.deleteBook(id);

        if (response.isSuccess()) {
            return ResponseEntity.noContent().build();
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    @RoleAllowed({"USER", "ADMIN"})
    public ResponseEntity<?> searchBooks(@RequestParam String query) {
        ResponseDTO<List<BookDTO>> response = bookService.searchBooks(query);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/authors")
    @RoleAllowed({"ADMIN"})
    public ResponseEntity<?> assignAuthorsToBook(
            @PathVariable Long id,
            @RequestBody List<Long> authorIds,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {

        String jwtToken = authorizationHeader.replace("Bearer ", "");
        ResponseDTO<String> response = bookService.assignAuthorsToBook(id, authorIds, jwtToken);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
