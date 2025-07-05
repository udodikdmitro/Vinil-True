package com.vinylshop.service;

import com.vinylshop.dto.GenreDto;
import com.vinylshop.dto.GenreRequest;
import com.vinylshop.entity.Genre;
import com.vinylshop.exception.ResourceAlreadyExistException;
import com.vinylshop.exception.ResourceNotFoundException;
import com.vinylshop.mapper.GenreMapper;
import com.vinylshop.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @Transactional
    public List<GenreDto> createFromDto(Collection<GenreRequest> dtos, Locale locale) {
        if (dtos == null || dtos.isEmpty()) {
            return List.of();
        }
        List<Genre> genres = new ArrayList<>(dtos.size());

        for(GenreRequest dto : dtos) {
            if (genreRepository.existsByNameEnAndNameUk(dto.nameEn(), dto.nameUk())) {
                String message = "Genre with names [nameEn= " + dto.nameEn() + "];[nameUk= " + dto.nameUk() + "] already exists";
                throw new ResourceAlreadyExistException(message, dto.nameEn() + '-' + dto.nameUk(), "Genre");
            }
            Genre genre = genreMapper.toEntity(dto);
            genres.add(genre);
        }

        List<Genre> created = genreRepository.saveAll(genres);
        return created.stream()
            .map(x -> genreMapper.toDto(x, locale))
            .toList();
    }

    @Transactional
    public Genre createGenre(Genre genre) {
        return genreRepository.save(genre);
    }

    @Transactional(readOnly = true)
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<GenreDto> getAllLocalizedGenres(Locale locale) {
        return genreRepository.findAll()
            .stream()
            .map(x -> genreMapper.toDto(x, locale))
            .toList();
    }

    @Transactional(readOnly = true)
    public Optional<Genre> getById(Long id) {
        return genreRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Genre> getByName(String name) {
        return genreRepository.findByNameEnOrNameUk(name, name);
    }

    @Transactional
    public GenreDto updateById(Long id, GenreRequest genreRequest, Locale locale) throws ResourceNotFoundException {
        Genre genre = new Genre(null, genreRequest.nameEn(), genreRequest.nameUk());
        Genre created = updateById(id, genre);
        return genreMapper.toDto(created, locale);
    }

    @Transactional
    public Genre updateById(Long id, Genre genre) throws ResourceNotFoundException {
        Genre found = getByIdOrThrow(id);
        if (genre.getNameEn() != null) {
            found.setNameEn(genre.getNameEn());
        }
        if (genre.getNameUk() != null) {
            found.setNameUk(genre.getNameUk());
        }
        return genreRepository.save(found);
    }

    @Transactional
    public void deleteById(Long id) throws ResourceNotFoundException {
        Genre found = getByIdOrThrow(id);
        genreRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Genre getByIdOrThrow(Long id) throws ResourceNotFoundException {
        return getById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Genre " + id + " not found", id, "Genre"));
    }

}
