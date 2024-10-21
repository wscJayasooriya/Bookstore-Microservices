package com.practical.authorService.service;

import com.practical.authorService.dto.AuthorDTO;
import com.practical.authorService.dto.ResponseDTO;
import com.practical.authorService.model.Author;
import com.practical.authorService.repository.AuthorRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public ResponseDTO<AuthorDTO> createAuthor(AuthorDTO authorDTO) {
        try {
            Author author = new Author();
            BeanUtils.copyProperties(authorDTO, author);
            author = authorRepository.save(author);
            BeanUtils.copyProperties(author, authorDTO);
            return new ResponseDTO<>("Author created successfully", authorDTO, true);
        } catch (IllegalArgumentException e) {
            return new ResponseDTO<>("Invalid argument for creating author: " + e.getMessage(), null, false);
        } catch (DataAccessException e) {
            return new ResponseDTO<>("Database error while creating author: " + e.getMessage(), null, false);
        } catch (Exception e) {
            return new ResponseDTO<>("Unexpected error while creating author: " + e.getMessage(), null, false);
        }
    }

    public ResponseDTO<AuthorDTO> getAuthor(Long id) {
        try {
            Optional<Author> author = authorRepository.findById(id);
            if (author.isPresent()) {
                AuthorDTO authorDTO = new AuthorDTO();
                BeanUtils.copyProperties(author.get(), authorDTO);
                return new ResponseDTO<>("Author fetched successfully", authorDTO, true);
            } else {
                return new ResponseDTO<>("Author not found with ID: " + id, null, false);
            }
        } catch (DataAccessException e) {
            return new ResponseDTO<>("Database error while fetching author: " + e.getMessage(), null, false);
        } catch (Exception e) {
            return new ResponseDTO<>("Unexpected error while fetching author: " + e.getMessage(), null, false);
        }
    }

    public ResponseDTO<List<AuthorDTO>> getAllAuthors() {
        try {
            List<Author> authors = authorRepository.findAll();
            List<AuthorDTO> authorDTOs = authors.stream()
                    .map(author -> {
                        AuthorDTO dto = new AuthorDTO();
                        BeanUtils.copyProperties(author, dto);
                        return dto;
                    })
                    .collect(Collectors.toList());
            return new ResponseDTO<>("Authors fetched successfully", authorDTOs, true);
        } catch (DataAccessException e) {
            return new ResponseDTO<>("Database error while fetching authors: " + e.getMessage(), null, false);
        } catch (Exception e) {
            return new ResponseDTO<>("Unexpected error while fetching authors: " + e.getMessage(), null, false);
        }
    }

    public ResponseDTO<AuthorDTO> updateAuthor(Long id, AuthorDTO authorDTO) {
        try {
            Optional<Author> authorOptional = authorRepository.findById(id);
            if (authorOptional.isPresent()) {
                Author author = authorOptional.get();
                BeanUtils.copyProperties(authorDTO, author, "id");
                author = authorRepository.save(author);
                BeanUtils.copyProperties(author, authorDTO);
                return new ResponseDTO<>("Author updated successfully", authorDTO, true);
            } else {
                return new ResponseDTO<>("Author not found with ID: " + id, null, false);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseDTO<>("Invalid argument for updating author: " + e.getMessage(), null, false);
        } catch (DataAccessException e) {
            return new ResponseDTO<>("Database error while updating author: " + e.getMessage(), null, false);
        } catch (Exception e) {
            return new ResponseDTO<>("Unexpected error while updating author: " + e.getMessage(), null, false);
        }
    }

    public ResponseDTO<String> deleteAuthor(Long id) {
        try {
            if (authorRepository.existsById(id)) {
                authorRepository.deleteById(id);
                return new ResponseDTO<>("Author deleted successfully", "Success", true);
            } else {
                return new ResponseDTO<>("Author not found with ID: " + id, null, false);
            }
        } catch (DataAccessException e) {
            return new ResponseDTO<>("Database error while deleting author: " + e.getMessage(), null, false);
        } catch (Exception e) {
            return new ResponseDTO<>("Unexpected error while deleting author: " + e.getMessage(), null, false);
        }
    }
}
