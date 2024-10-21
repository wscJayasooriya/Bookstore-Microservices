package com.practical.authorService.controller;

import com.practical.authorService.dto.AuthorDTO;
import com.practical.authorService.dto.ResponseDTO;
import com.practical.authorService.security.RoleAllowed;
import com.practical.authorService.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/authors")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @PostMapping
    @RoleAllowed({"ADMIN"})
    public ResponseEntity<?> createAuthor(@RequestBody AuthorDTO authorDTO) {
        ResponseDTO<AuthorDTO> response = authorService.createAuthor(authorDTO);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    @RoleAllowed({"USER", "ADMIN"})
    public ResponseEntity<?> getAuthor(@PathVariable Long id) {
        ResponseDTO<AuthorDTO> response = authorService.getAuthor(id);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    @RoleAllowed({"USER", "ADMIN"})
    public ResponseEntity<?> getAllAuthors() {
        ResponseDTO<List<AuthorDTO>> response = authorService.getAllAuthors();
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    @RoleAllowed({"ADMIN"})
    public ResponseEntity<?> updateAuthor(@PathVariable Long id, @RequestBody AuthorDTO authorDTO) {
        ResponseDTO<AuthorDTO> response = authorService.updateAuthor(id, authorDTO);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @RoleAllowed({"ADMIN"})
    public ResponseEntity<?> deleteAuthor(@PathVariable Long id) {
        ResponseDTO<String> response = authorService.deleteAuthor(id);
        if (response.isSuccess()) {
            return ResponseEntity.noContent().build();
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
