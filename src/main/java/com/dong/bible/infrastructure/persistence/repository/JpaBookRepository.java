package com.dong.bible.infrastructure.persistence.repository;

import com.dong.bible.ENUM.Testament;
import com.dong.bible.domain.book.Book;
import com.dong.bible.domain.book.BookName;
import com.dong.bible.domain.book.BookRepository;
import com.dong.bible.infrastructure.persistence.jpa.KrvBookRepository;
import com.dong.bible.infrastructure.persistence.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * BookRepository의 JPA 구현체
 * 순수 DDD 원칙에 따라 Domain 인터페이스를 Infrastructure에서 구현합니다.
 * 기존 KrvBookRepository를 활용하여 Domain Repository를 구현합니다.
 */
@Repository
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {
    
    private final KrvBookRepository krvBookRepository;
    private final BookMapper bookMapper;
    
    @Override
    public Optional<Book> findByName(BookName bookName) {
        if (bookName == null) {
            return Optional.empty();
        }
        return findByName(bookName.getName());
    }
    
    @Override
    public Optional<Book> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }
        
        return krvBookRepository.findByName(name.trim())
                .map(bookMapper::toDomain);
    }
    
    @Override
    public List<Book> findByTestament(Testament testament) {
        if (testament == null) {
            return List.of();
        }
        
        return krvBookRepository.findByTestamentOrderByBookOrder(testament)
                .stream()
                .map(bookMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Book> findAll() {
        return krvBookRepository.findAllByOrderByBookOrder()
                .stream()
                .map(bookMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Book> findOldTestamentBooks() {
        return findByTestament(Testament.구약);
    }
    
    @Override
    public List<Book> findNewTestamentBooks() {
        return findByTestament(Testament.신약);
    }
    
    @Override
    public boolean existsByName(BookName bookName) {
        if (bookName == null) {
            return false;
        }
        
        return krvBookRepository.findByName(bookName.getName()).isPresent();
    }
    
    @Override
    public long count() {
        return krvBookRepository.count();
    }
}
