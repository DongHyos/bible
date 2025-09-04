# Code Templates for Bible API

## 📋 Overview
This document contains reusable code templates for common patterns in the Bible API project.

## 🏗️ Repository Implementation Template

### JPA Repository Interface Template
```java
package com.dong.bible.infrastructure.persistence.repository;

import com.dong.bible.infrastructure.persistence.entity.{{EntityName}}Entity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface {{EntityName}}JpaRepository extends JpaRepository<{{EntityName}}Entity, Long> {
    
    // Basic queries
    List<{{EntityName}}Entity> findBy{{FieldName}}({{FieldType}} {{fieldName}});
    Page<{{EntityName}}Entity> findBy{{FieldName}}({{FieldType}} {{fieldName}}, Pageable pageable);
    
    // Text search
    List<{{EntityName}}Entity> findBy{{FieldName}}ContainingIgnoreCase(String {{fieldName}});
    Page<{{EntityName}}Entity> findBy{{FieldName}}ContainingIgnoreCase(String {{fieldName}}, Pageable pageable);
    
    // Date range queries
    List<{{EntityName}}Entity> findBy{{DateField}}Between(LocalDate startDate, LocalDate endDate);
    Page<{{EntityName}}Entity> findBy{{DateField}}Between(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    // Count queries
    long countBy{{FieldName}}({{FieldType}} {{fieldName}});
    long countBy{{FieldName}}Containing(String {{fieldName}});
    
    // Existence checks
    boolean existsBy{{FieldName}}And{{AnotherField}}({{FieldType}} {{fieldName}}, {{AnotherFieldType}} {{anotherField}});
    
    // Custom queries
    @Query("SELECT e FROM {{EntityName}}Entity e WHERE LOWER(e.{{fieldName}}) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY e.{{sortField}} DESC")
    List<{{EntityName}}Entity> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT e FROM {{EntityName}}Entity e WHERE LOWER(e.{{fieldName}}) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY e.{{sortField}} DESC")
    Page<{{EntityName}}Entity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // Update queries
    @Modifying
    @Query("UPDATE {{EntityName}}Entity e SET e.{{fieldName}} = e.{{fieldName}} + 1 WHERE e.id = :id")
    void increment{{FieldName}}(@Param("id") Long id);
    
    @Modifying
    @Query("UPDATE {{EntityName}}Entity e SET e.{{fieldName}} = :value WHERE e.id = :id")
    void update{{FieldName}}(@Param("id") Long id, @Param("value") {{FieldType}} value);
    
    // Complex queries
    @Query("""
        SELECT e FROM {{EntityName}}Entity e
        WHERE (:{{param1}} IS NULL OR e.{{field1}} = :{{param1}})
        AND (:{{param2}} IS NULL OR e.{{field2}} = :{{param2}})
        ORDER BY e.{{sortField}} DESC
        """)
    Page<{{EntityName}}Entity> findByMultipleConditions(
        @Param("{{param1}}") {{Field1Type}} {{param1}},
        @Param("{{param2}}") {{Field2Type}} {{param2}},
        Pageable pageable
    );
}
```

### Domain Repository Implementation Template
```java
package com.dong.bible.infrastructure.persistence.repository;

import com.dong.bible.domain.{{domain}}.{{DomainName}};
import com.dong.bible.domain.{{domain}}.{{DomainName}}Repository;
import com.dong.bible.infrastructure.persistence.entity.{{EntityName}}Entity;
import com.dong.bible.infrastructure.persistence.mapper.{{DomainName}}Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class {{DomainName}}RepositoryImpl implements {{DomainName}}Repository {
    
    private final {{EntityName}}JpaRepository jpaRepository;
    private final {{DomainName}}Mapper mapper;
    
    @Override
    public Optional<{{DomainName}}> findById(Long id) {
        log.debug("Finding {{domainName}} by id: {}", id);
        
        if (id == null) {
            return Optional.empty();
        }
        
        return jpaRepository.findById(id)
            .map(mapper::toDomain);
    }
    
    @Override
    public List<{{DomainName}}> findAll() {
        log.debug("Finding all {{domainName}}s");
        
        List<{{EntityName}}Entity> entities = jpaRepository.findAll();
        return mapper.toDomainList(entities);
    }
    
    @Override
    public List<{{DomainName}}> findBy{{FieldName}}({{FieldType}} {{fieldName}}) {
        log.debug("Finding {{domainName}}s by {{fieldName}}: {}", {{fieldName}});
        
        if ({{fieldName}} == null) {
            return new ArrayList<>();
        }
        
        List<{{EntityName}}Entity> entities = jpaRepository.findBy{{FieldName}}({{fieldName}});
        return mapper.toDomainList(entities);
    }
    
    @Override
    public Page<{{DomainName}}> findBy{{FieldName}}({{FieldType}} {{fieldName}}, Pageable pageable) {
        log.debug("Finding {{domainName}}s by {{fieldName}} with paging: {}", {{fieldName}});
        
        if ({{fieldName}} == null) {
            return Page.empty(pageable);
        }
        
        Page<{{EntityName}}Entity> entityPage = jpaRepository.findBy{{FieldName}}({{fieldName}}, pageable);
        List<{{DomainName}}> domains = mapper.toDomainList(entityPage.getContent());
        return new PageImpl<>(domains, pageable, entityPage.getTotalElements());
    }
    
    @Override
    public List<{{DomainName}}> findBy{{TextFieldName}}Containing(String {{textFieldName}}) {
        log.debug("Finding {{domainName}}s by {{textFieldName}} containing: {}", {{textFieldName}});
        
        if ({{textFieldName}} == null || {{textFieldName}}.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<{{EntityName}}Entity> entities = jpaRepository
            .findBy{{TextFieldName}}ContainingIgnoreCase({{textFieldName}}.trim());
        return mapper.toDomainList(entities);
    }
    
    @Override
    public List<{{DomainName}}> findBy{{DateField}}Between(LocalDate startDate, LocalDate endDate) {
        log.debug("Finding {{domainName}}s by date range: {} ~ {}", startDate, endDate);
        
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date must not be null");
        }
        
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
        
        List<{{EntityName}}Entity> entities = jpaRepository
            .findBy{{DateField}}Between(startDate, endDate);
        return mapper.toDomainList(entities);
    }
    
    @Override
    public long countBy{{FieldName}}({{FieldType}} {{fieldName}}) {
        log.debug("Counting {{domainName}}s by {{fieldName}}: {}", {{fieldName}});
        
        if ({{fieldName}} == null) {
            return 0L;
        }
        
        return jpaRepository.countBy{{FieldName}}({{fieldName}});
    }
    
    @Override
    public boolean exists{{DomainName}}({{KeyFieldType}} {{keyField}}) {
        log.debug("Checking if {{domainName}} exists: {}", {{keyField}});
        
        if ({{keyField}} == null) {
            return false;
        }
        
        return jpaRepository.existsBy{{KeyField}}({{keyField}});
    }
    
    @Override
    @Transactional
    public {{DomainName}} store({{DomainName}} {{domainName}}) {
        log.debug("Storing {{domainName}}: {}", {{domainName}});
        
        if ({{domainName}} == null) {
            throw new IllegalArgumentException("{{DomainName}} must not be null");
        }
        
        {{EntityName}}Entity entity = mapper.toEntity({{domainName}});
        {{EntityName}}Entity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    @Transactional
    public void remove({{DomainName}} {{domainName}}) {
        log.debug("Removing {{domainName}}: {}", {{domainName}});
        
        if ({{domainName}} == null || {{domainName}}.getId() == null) {
            throw new IllegalArgumentException("{{DomainName}} and its ID must not be null");
        }
        
        jpaRepository.deleteById({{domainName}}.getId());
    }
    
    @Override
    @Transactional
    public void removeById(Long id) {
        log.debug("Removing {{domainName}} by id: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        
        jpaRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    public void increment{{CounterField}}(Long id) {
        log.debug("Incrementing {{counterField}} for {{domainName}}: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        
        jpaRepository.increment{{CounterField}}(id);
    }
}
```

## 🎯 Application Service Template

### Query Service Template
```java
package com.dong.bible.application.service;

import com.dong.bible.application.dto.query.{{DomainName}}QueryDto;
import com.dong.bible.domain.{{domain}}.{{DomainName}};
import com.dong.bible.domain.{{domain}}.{{DomainName}}Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class {{DomainName}}ApplicationService {
    
    private final {{DomainName}}Repository {{domainName}}Repository;
    
    /**
     * {{DomainName}} 단일 조회
     */
    public {{DomainName}}QueryDto get{{DomainName}}(Long id) {
        log.info("Getting {{domainName}}: id={}", id);
        
        {{DomainName}} {{domainName}} = {{domainName}}Repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("{{DomainName}} not found: id=" + id));
        
        return {{DomainName}}QueryDto.from({{domainName}});
    }
    
    /**
     * {{DomainName}} 목록 조회
     */
    public List<{{DomainName}}QueryDto> getAll{{DomainName}}s() {
        log.info("Getting all {{domainName}}s");
        
        List<{{DomainName}}> {{domainName}}s = {{domainName}}Repository.findAll();
        
        return {{domainName}}s.stream()
            .map({{DomainName}}QueryDto::from)
            .collect(Collectors.toList());
    }
    
    /**
     * {{DomainName}} 목록 조회 (페이징)
     */
    public Page<{{DomainName}}QueryDto> getAll{{DomainName}}s(Pageable pageable) {
        log.info("Getting {{domainName}}s with paging: page={}, size={}", 
            pageable.getPageNumber(), pageable.getPageSize());
        
        Page<{{DomainName}}> {{domainName}}Page = {{domainName}}Repository.findAll(pageable);
        
        List<{{DomainName}}QueryDto> dtos = {{domainName}}Page.getContent().stream()
            .map({{DomainName}}QueryDto::from)
            .collect(Collectors.toList());
        
        return new PageImpl<>(dtos, pageable, {{domainName}}Page.getTotalElements());
    }
    
    /**
     * 조건별 {{DomainName}} 조회
     */
    public List<{{DomainName}}QueryDto> get{{DomainName}}sBy{{FieldName}}({{FieldType}} {{fieldName}}) {
        log.info("Getting {{domainName}}s by {{fieldName}}: {}", {{fieldName}});
        
        if ({{fieldName}} == null) {
            throw new IllegalArgumentException("{{FieldName}} must not be null");
        }
        
        List<{{DomainName}}> {{domainName}}s = {{domainName}}Repository.findBy{{FieldName}}({{fieldName}});
        
        return {{domainName}}s.stream()
            .map({{DomainName}}QueryDto::from)
            .collect(Collectors.toList());
    }
    
    /**
     * 검색 기능
     */
    public Page<{{DomainName}}QueryDto> search{{DomainName}}s(String keyword, Pageable pageable) {
        log.info("Searching {{domainName}}s: keyword={}", keyword);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll{{DomainName}}s(pageable);
        }
        
        Page<{{DomainName}}> {{domainName}}Page = {{domainName}}Repository
            .searchByKeyword(keyword.trim(), pageable);
        
        List<{{DomainName}}QueryDto> dtos = {{domainName}}Page.getContent().stream()
            .map({{DomainName}}QueryDto::from)
            .collect(Collectors.toList());
        
        return new PageImpl<>(dtos, pageable, {{domainName}}Page.getTotalElements());
    }
    
    /**
     * 날짜 범위별 조회
     */
    public List<{{DomainName}}QueryDto> get{{DomainName}}sByDateRange(
            LocalDate startDate, LocalDate endDate) {
        log.info("Getting {{domainName}}s by date range: {} ~ {}", startDate, endDate);
        
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date must not be null");
        }
        
        List<{{DomainName}}> {{domainName}}s = {{domainName}}Repository
            .findBy{{DateField}}Between(startDate, endDate);
        
        return {{domainName}}s.stream()
            .map({{DomainName}}QueryDto::from)
            .collect(Collectors.toList());
    }
    
    /**
     * 통계 정보
     */
    public long count{{DomainName}}sBy{{FieldName}}({{FieldType}} {{fieldName}}) {
        log.info("Counting {{domainName}}s by {{fieldName}}: {}", {{fieldName}});
        
        return {{domainName}}Repository.countBy{{FieldName}}({{fieldName}});
    }
}
```

### Command Service Template
```java
package com.dong.bible.application.service;

import com.dong.bible.application.dto.command.{{DomainName}}CommandDto;
import com.dong.bible.domain.{{domain}}.{{DomainName}};
import com.dong.bible.domain.{{domain}}.{{DomainName}}Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class {{DomainName}}CommandService {
    
    private final {{DomainName}}Repository {{domainName}}Repository;
    
    /**
     * {{DomainName}} 생성
     */
    public Long create{{DomainName}}({{DomainName}}CommandDto command) {
        log.info("Creating {{domainName}}: {}", command);
        
        if (command == null) {
            throw new IllegalArgumentException("Command must not be null");
        }
        
        // 비즈니스 규칙 검증
        validateCreate{{DomainName}}(command);
        
        // 도메인 객체 생성
        {{DomainName}} {{domainName}} = {{DomainName}}.builder()
            .{{field1}}(command.get{{Field1}}())
            .{{field2}}(command.get{{Field2}}())
            .{{field3}}(command.get{{Field3}}())
            .build();
        
        // 저장
        {{DomainName}} saved = {{domainName}}Repository.store({{domainName}});
        
        log.info("Created {{domainName}} with id: {}", saved.getId());
        return saved.getId();
    }
    
    /**
     * {{DomainName}} 수정
     */
    public void update{{DomainName}}(Long id, {{DomainName}}CommandDto command) {
        log.info("Updating {{domainName}}: id={}, command={}", id, command);
        
        if (id == null || command == null) {
            throw new IllegalArgumentException("ID and command must not be null");
        }
        
        // 기존 엔티티 조회
        {{DomainName}} existing = {{domainName}}Repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("{{DomainName}} not found: id=" + id));
        
        // 비즈니스 규칙 검증
        validateUpdate{{DomainName}}(existing, command);
        
        // 도메인 객체 업데이트
        {{DomainName}} updated = {{DomainName}}.builder()
            .id(existing.getId())
            .{{field1}}(command.get{{Field1}}())
            .{{field2}}(command.get{{Field2}}())
            .{{field3}}(command.get{{Field3}}())
            .createdAt(existing.getCreatedAt())
            .build();
        
        // 저장
        {{domainName}}Repository.store(updated);
        
        log.info("Updated {{domainName}} with id: {}", id);
    }
    
    /**
     * {{DomainName}} 삭제
     */
    public void delete{{DomainName}}(Long id) {
        log.info("Deleting {{domainName}}: id={}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        
        // 존재 여부 확인
        {{DomainName}} existing = {{domainName}}Repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("{{DomainName}} not found: id=" + id));
        
        // 삭제 가능 여부 검증
        validateDelete{{DomainName}}(existing);
        
        // 삭제
        {{domainName}}Repository.removeById(id);
        
        log.info("Deleted {{domainName}} with id: {}", id);
    }
    
    /**
     * {{CounterField}} 증가
     */
    public void increment{{CounterField}}(Long id) {
        log.info("Incrementing {{counterField}} for {{domainName}}: id={}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        
        // 존재 여부 확인
        {{domainName}}Repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("{{DomainName}} not found: id=" + id));
        
        // 카운터 증가
        {{domainName}}Repository.increment{{CounterField}}(id);
        
        log.info("Incremented {{counterField}} for {{domainName}} with id: {}", id);
    }
    
    private void validateCreate{{DomainName}}({{DomainName}}CommandDto command) {
        // 생성 시 비즈니스 규칙 검증
        if (command.get{{RequiredField}}() == null || command.get{{RequiredField}}().trim().isEmpty()) {
            throw new IllegalArgumentException("{{RequiredField}} must not be empty");
        }
        
        // 중복 검사 예시
        if ({{domainName}}Repository.exists{{DomainName}}(command.get{{UniqueField}}())) {
            throw new IllegalArgumentException("{{DomainName}} already exists: " + command.get{{UniqueField}}());
        }
    }
    
    private void validateUpdate{{DomainName}}({{DomainName}} existing, {{DomainName}}CommandDto command) {
        // 수정 시 비즈니스 규칙 검증
        if (command.get{{RequiredField}}() == null || command.get{{RequiredField}}().trim().isEmpty()) {
            throw new IllegalArgumentException("{{RequiredField}} must not be empty");
        }
        
        // 변경 불가 필드 검증 예시
        if (!existing.get{{ImmutableField}}().equals(command.get{{ImmutableField}}())) {
            throw new IllegalArgumentException("{{ImmutableField}} cannot be changed");
        }
    }
    
    private void validateDelete{{DomainName}}({{DomainName}} {{domainName}}) {
        // 삭제 가능 여부 검증
        // 예: 연관된 데이터가 있는지 확인
        // 예: 특정 상태인지 확인
    }
}
```

## 🌐 Controller Template

### REST Controller Template
```java
package com.dong.bible.web.controller;

import com.dong.bible.application.dto.command.{{DomainName}}CommandDto;
import com.dong.bible.application.dto.query.{{DomainName}}QueryDto;
import com.dong.bible.application.service.{{DomainName}}ApplicationService;
import com.dong.bible.application.service.{{DomainName}}CommandService;
import com.dong.bible.common.response.AppResponse;
import com.dong.bible.web.dto.request.{{DomainName}}Request;
import com.dong.bible.web.dto.response.{{DomainName}}Response;
import com.dong.bible.web.mapper.{{DomainName}}RequestMapper;
import com.dong.bible.web.mapper.{{DomainName}}ResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/{{api-path}}")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "{{DomainName}}", description = "{{DomainName}} API")
public class {{DomainName}}Controller {
    
    private final {{DomainName}}ApplicationService applicationService;
    private final {{DomainName}}CommandService commandService;
    private final {{DomainName}}RequestMapper requestMapper;
    private final {{DomainName}}ResponseMapper responseMapper;
    
    /**
     * {{DomainName}} 단일 조회
     */
    @GetMapping("/{id}")
    @Operation(summary = "{{DomainName}} 단일 조회", description = "ID로 {{DomainName}}를 조회합니다.")
    public AppResponse<{{DomainName}}Response> get{{DomainName}}(
            @Parameter(description = "{{DomainName}} ID", required = true)
            @PathVariable @Positive Long id) {
        
        log.info("Get {{domainName}} request: id={}", id);
        
        {{DomainName}}QueryDto dto = applicationService.get{{DomainName}}(id);
        {{DomainName}}Response response = responseMapper.toResponse(dto);
        
        return AppResponse.success(response);
    }
    
    /**
     * {{DomainName}} 목록 조회
     */
    @GetMapping
    @Operation(summary = "{{DomainName}} 목록 조회", description = "{{DomainName}} 목록을 페이징으로 조회합니다.")
    public AppResponse<Page<{{DomainName}}Response>> get{{DomainName}}s(
            @Parameter(description = "페이징 정보")
            @PageableDefault(size = 20, sort = "{{defaultSortField}}", direction = Sort.Direction.DESC) 
            Pageable pageable) {
        
        log.info("Get {{domainName}}s request: page={}, size={}", 
            pageable.getPageNumber(), pageable.getPageSize());
        
        Page<{{DomainName}}QueryDto> dtoPage = applicationService.getAll{{DomainName}}s(pageable);
        Page<{{DomainName}}Response> responsePage = dtoPage.map(responseMapper::toResponse);
        
        return AppResponse.success(responsePage);
    }
    
    /**
     * {{DomainName}} 검색
     */
    @GetMapping("/search")
    @Operation(summary = "{{DomainName}} 검색", description = "키워드로 {{DomainName}}를 검색합니다.")
    public AppResponse<Page<{{DomainName}}Response>> search{{DomainName}}s(
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword,
            @Parameter(description = "페이징 정보")
            @PageableDefault(size = 20, sort = "{{defaultSortField}}", direction = Sort.Direction.DESC)
            Pageable pageable) {
        
        log.info("Search {{domainName}}s request: keyword={}", keyword);
        
        Page<{{DomainName}}QueryDto> dtoPage = applicationService.search{{DomainName}}s(keyword, pageable);
        Page<{{DomainName}}Response> responsePage = dtoPage.map(responseMapper::toResponse);
        
        return AppResponse.success(responsePage);
    }
    
    /**
     * 조건별 {{DomainName}} 조회
     */
    @GetMapping("/by-{{field-name}}")
    @Operation(summary = "조건별 {{DomainName}} 조회", description = "{{fieldName}}로 {{DomainName}}를 조회합니다.")
    public AppResponse<List<{{DomainName}}Response>> get{{DomainName}}sBy{{FieldName}}(
            @Parameter(description = "{{FieldName}}", required = true)
            @RequestParam {{FieldType}} {{fieldName}}) {
        
        log.info("Get {{domainName}}s by {{fieldName}} request: {}", {{fieldName}});
        
        List<{{DomainName}}QueryDto> dtos = applicationService.get{{DomainName}}sBy{{FieldName}}({{fieldName}});
        List<{{DomainName}}Response> responses = dtos.stream()
            .map(responseMapper::toResponse)
            .collect(Collectors.toList());
        
        return AppResponse.success(responses);
    }
    
    /**
     * 날짜 범위별 {{DomainName}} 조회
     */
    @GetMapping("/by-date-range")
    @Operation(summary = "날짜 범위별 {{DomainName}} 조회", description = "날짜 범위로 {{DomainName}}를 조회합니다.")
    public AppResponse<List<{{DomainName}}Response>> get{{DomainName}}sByDateRange(
            @Parameter(description = "시작 날짜", required = true)
            @RequestParam LocalDate startDate,
            @Parameter(description = "종료 날짜", required = true)
            @RequestParam LocalDate endDate) {
        
        log.info("Get {{domainName}}s by date range request: {} ~ {}", startDate, endDate);
        
        List<{{DomainName}}QueryDto> dtos = applicationService
            .get{{DomainName}}sByDateRange(startDate, endDate);
        List<{{DomainName}}Response> responses = dtos.stream()
            .map(responseMapper::toResponse)
            .collect(Collectors.toList());
        
        return AppResponse.success(responses);
    }
    
    /**
     * {{DomainName}} 생성
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "{{DomainName}} 생성", description = "새로운 {{DomainName}}를 생성합니다.")
    public AppResponse<Long> create{{DomainName}}(
            @Parameter(description = "{{DomainName}} 생성 요청", required = true)
            @RequestBody @Valid {{DomainName}}Request request) {
        
        log.info("Create {{domainName}} request: {}", request);
        
        {{DomainName}}CommandDto command = requestMapper.toCommand(request);
        Long id = commandService.create{{DomainName}}(command);
        
        return AppResponse.success(id);
    }
    
    /**
     * {{DomainName}} 수정
     */
    @PutMapping("/{id}")
    @Operation(summary = "{{DomainName}} 수정", description = "기존 {{DomainName}}를 수정합니다.")
    public AppResponse<Void> update{{DomainName}}(
            @Parameter(description = "{{DomainName}} ID", required = true)
            @PathVariable @Positive Long id,
            @Parameter(description = "{{DomainName}} 수정 요청", required = true)
            @RequestBody @Valid {{DomainName}}Request request) {
        
        log.info("Update {{domainName}} request: id={}, request={}", id, request);
        
        {{DomainName}}CommandDto command = requestMapper.toCommand(request);
        commandService.update{{DomainName}}(id, command);
        
        return AppResponse.success();
    }
    
    /**
     * {{DomainName}} 삭제
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "{{DomainName}} 삭제", description = "{{DomainName}}를 삭제합니다.")
    public AppResponse<Void> delete{{DomainName}}(
            @Parameter(description = "{{DomainName}} ID", required = true)
            @PathVariable @Positive Long id) {
        
        log.info("Delete {{domainName}} request: id={}", id);
        
        commandService.delete{{DomainName}}(id);
        
        return AppResponse.success();
    }
    
    /**
     * {{CounterField}} 증가
     */
    @PostMapping("/{id}/{{counter-endpoint}}")
    @Operation(summary = "{{CounterField}} 증가", description = "{{DomainName}}의 {{counterField}}를 증가시킵니다.")
    public AppResponse<Void> increment{{CounterField}}(
            @Parameter(description = "{{DomainName}} ID", required = true)
            @PathVariable @Positive Long id) {
        
        log.info("Increment {{counterField}} request: id={}", id);
        
        commandService.increment{{CounterField}}(id);
        
        return AppResponse.success();
    }
    
    /**
     * 통계 정보 조회
     */
    @GetMapping("/statistics/count-by-{{field-name}}")
    @Operation(summary = "{{FieldName}}별 개수", description = "{{FieldName}}별 {{DomainName}} 개수를 조회합니다.")
    public AppResponse<Long> count{{DomainName}}sBy{{FieldName}}(
            @Parameter(description = "{{FieldName}}", required = true)
            @RequestParam {{FieldType}} {{fieldName}}) {
        
        log.info("Count {{domainName}}s by {{fieldName}} request: {}", {{fieldName}});
        
        long count = applicationService.count{{DomainName}}sBy{{FieldName}}({{fieldName}});
        
        return AppResponse.success(count);
    }
}
```

## 🗂️ DTO Templates

### Query DTO Template
```java
package com.dong.bible.application.dto.query;

import com.dong.bible.domain.{{domain}}.{{DomainName}};
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * {{DomainName}} 조회용 DTO
 */
@Getter
@Builder
@ToString
public class {{DomainName}}QueryDto {
    
    private final Long id;
    private final String {{field1}};
    private final {{Field2Type}} {{field2}};
    private final {{Field3Type}} {{field3}};
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    /**
     * Domain 객체에서 DTO 생성
     */
    public static {{DomainName}}QueryDto from({{DomainName}} {{domainName}}) {
        if ({{domainName}} == null) {
            return null;
        }
        
        return {{DomainName}}QueryDto.builder()
            .id({{domainName}}.getId())
            .{{field1}}({{domainName}}.get{{Field1}}())
            .{{field2}}({{domainName}}.get{{Field2}}())
            .{{field3}}({{domainName}}.get{{Field3}}())
            .createdAt({{domainName}}.getCreatedAt())
            .updatedAt({{domainName}}.getUpdatedAt())
            .build();
    }
}
```

### Command DTO Template
```java
package com.dong.bible.application.dto.command;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * {{DomainName}} 명령용 DTO
 */
@Getter
@Builder
@ToString
public class {{DomainName}}CommandDto {
    
    private final String {{field1}};
    private final {{Field2Type}} {{field2}};
    private final {{Field3Type}} {{field3}};
    
    /**
     * Request 객체에서 Command 생성
     */
    public static {{DomainName}}CommandDto from({{RequestType}} request) {
        if (request == null) {
            return null;
        }
        
        return {{DomainName}}CommandDto.builder()
            .{{field1}}(request.get{{Field1}}())
            .{{field2}}(request.get{{Field2}}())
            .{{field3}}(request.get{{Field3}}())
            .build();
    }
}
```

## 🌐 Request/Response Templates

### Request DTO Template
```java
package com.dong.bible.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.*;

/**
 * {{DomainName}} 요청 DTO
 */
@Getter
@Setter
@ToString
@Schema(description = "{{DomainName}} 요청")
public class {{DomainName}}Request {
    
    @Schema(description = "{{Field1}} 설명", example = "예시값", required = true)
    @NotBlank(message = "{{Field1}}은 필수입니다")
    @Size(max = 255, message = "{{Field1}}은 255자 이하여야 합니다")
    private String {{field1}};
    
    @Schema(description = "{{Field2}} 설명", example = "예시값")
    @Min(value = 1, message = "{{Field2}}는 1 이상이어야 합니다")
    private {{Field2Type}} {{field2}};
    
    @Schema(description = "{{Field3}} 설명", example = "예시값")
    @NotNull(message = "{{Field3}}은 필수입니다")
    private {{Field3Type}} {{field3}};
}
```

### Response DTO Template
```java
package com.dong.bible.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * {{DomainName}} 응답 DTO
 */
@Getter
@Builder
@Schema(description = "{{DomainName}} 응답")
public class {{DomainName}}Response {
    
    @Schema(description = "{{DomainName}} ID", example = "1")
    private final Long id;
    
    @Schema(description = "{{Field1}} 설명", example = "예시값")
    private final String {{field1}};
    
    @Schema(description = "{{Field2}} 설명", example = "예시값")
    private final {{Field2Type}} {{field2}};
    
    @Schema(description = "{{Field3}} 설명", example = "예시값")
    private final {{Field3Type}} {{field3}};
    
    @Schema(description = "생성 일시", example = "2024-01-01T00:00:00")
    private final LocalDateTime createdAt;
    
    @Schema(description = "수정 일시", example = "2024-01-01T00:00:00")
    private final LocalDateTime updatedAt;
}
```

## 🔄 Mapper Templates

### Request Mapper Template
```java
package com.dong.bible.web.mapper;

import com.dong.bible.application.dto.command.{{DomainName}}CommandDto;
import com.dong.bible.web.dto.request.{{DomainName}}Request;
import org.springframework.stereotype.Component;

/**
 * {{DomainName}} Request → Command 매퍼
 */
@Component
public class {{DomainName}}RequestMapper {
    
    /**
     * Request → Command 변환
     */
    public {{DomainName}}CommandDto toCommand({{DomainName}}Request request) {
        if (request == null) {
            return null;
        }
        
        return {{DomainName}}CommandDto.builder()
            .{{field1}}(request.get{{Field1}}())
            .{{field2}}(request.get{{Field2}}())
            .{{field3}}(request.get{{Field3}}())
            .build();
    }
}
```

### Response Mapper Template
```java
package com.dong.bible.web.mapper;

import com.dong.bible.application.dto.query.{{DomainName}}QueryDto;
import com.dong.bible.web.dto.response.{{DomainName}}Response;
import org.springframework.stereotype.Component;

/**
 * {{DomainName}} Query → Response 매퍼
 */
@Component
public class {{DomainName}}ResponseMapper {
    
    /**
     * Query → Response 변환
     */
    public {{DomainName}}Response toResponse({{DomainName}}QueryDto dto) {
        if (dto == null) {
            return null;
        }
        
        return {{DomainName}}Response.builder()
            .id(dto.getId())
            .{{field1}}(dto.get{{Field1}}())
            .{{field2}}(dto.get{{Field2}}())
            .{{field3}}(dto.get{{Field3}}())
            .createdAt(dto.getCreatedAt())
            .updatedAt(dto.getUpdatedAt())
            .build();
    }
}
```

## 🧪 Test Templates

### Repository Test Template
```java
package com.dong.bible.infrastructure.persistence.repository;

import com.dong.bible.domain.{{domain}}.{{DomainName}};
import com.dong.bible.infrastructure.persistence.entity.{{EntityName}}Entity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import({{DomainName}}RepositoryImpl.class)
@DisplayName("{{DomainName}}Repository 테스트")
class {{DomainName}}RepositoryTest {
    
    @Autowired
    private {{DomainName}}Repository {{domainName}}Repository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    @DisplayName("ID로 {{DomainName}} 조회 성공")
    void findById_Success() {
        // Given
        {{EntityName}}Entity entity = create{{EntityName}}Entity();
        {{EntityName}}Entity saved = entityManager.persistAndFlush(entity);
        
        // When
        Optional<{{DomainName}}> result = {{domainName}}Repository.findById(saved.getId());
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(saved.getId());
        assertThat(result.get().get{{Field1}}()).isEqualTo(entity.get{{Field1}}());
    }
    
    @Test
    @DisplayName("존재하지 않는 ID로 조회 시 Empty 반환")
    void findById_NotFound() {
        // When
        Optional<{{DomainName}}> result = {{domainName}}Repository.findById(999L);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("{{FieldName}}으로 {{DomainName}} 목록 조회 성공")
    void findBy{{FieldName}}_Success() {
        // Given
        {{FieldType}} {{fieldName}} = "test{{FieldName}}";
        {{EntityName}}Entity entity1 = create{{EntityName}}Entity({{fieldName}});
        {{EntityName}}Entity entity2 = create{{EntityName}}Entity({{fieldName}});
        {{EntityName}}Entity entity3 = create{{EntityName}}Entity("other{{FieldName}}");
        
        entityManager.persistAndFlush(entity1);
        entityManager.persistAndFlush(entity2);
        entityManager.persistAndFlush(entity3);
        
        // When
        List<{{DomainName}}> result = {{domainName}}Repository.findBy{{FieldName}}({{fieldName}});
        
        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting({{DomainName}}::get{{FieldName}})
            .containsOnly({{fieldName}});
    }
    
    @Test
    @DisplayName("{{DomainName}} 저장 성공")
    void store_Success() {
        // Given
        {{DomainName}} {{domainName}} = create{{DomainName}}();
        
        // When
        {{DomainName}} result = {{domainName}}Repository.store({{domainName}});
        
        // Then
        assertThat(result.getId()).isNotNull();
        assertThat(result.get{{Field1}}()).isEqualTo({{domainName}}.get{{Field1}}());
        
        // DB 확인
        {{EntityName}}Entity saved = entityManager.find({{EntityName}}Entity.class, result.getId());
        assertThat(saved).isNotNull();
        assertThat(saved.get{{Field1}}()).isEqualTo({{domainName}}.get{{Field1}}());
    }
    
    @Test
    @DisplayName("{{DomainName}} 삭제 성공")
    void removeById_Success() {
        // Given
        {{EntityName}}Entity entity = entityManager.persistAndFlush(create{{EntityName}}Entity());
        Long id = entity.getId();
        
        // When
        {{domainName}}Repository.removeById(id);
        entityManager.flush();
        
        // Then
        {{EntityName}}Entity deleted = entityManager.find({{EntityName}}Entity.class, id);
        assertThat(deleted).isNull();
    }
    
    @Test
    @DisplayName("페이징으로 {{DomainName}} 목록 조회 성공")
    void findAll_WithPaging_Success() {
        // Given
        for (int i = 0; i < 5; i++) {
            entityManager.persistAndFlush(create{{EntityName}}Entity());
        }
        
        PageRequest pageRequest = PageRequest.of(0, 3);
        
        // When
        Page<{{DomainName}}> result = {{domainName}}Repository.findAll(pageRequest);
        
        // Then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }
    
    private {{EntityName}}Entity create{{EntityName}}Entity() {
        return {{EntityName}}Entity.builder()
            .{{field1}}("test{{Field1}}")
            .{{field2}}({{defaultValue2}})
            .{{field3}}({{defaultValue3}})
            .build();
    }
    
    private {{EntityName}}Entity create{{EntityName}}Entity({{FieldType}} {{fieldName}}) {
        return {{EntityName}}Entity.builder()
            .{{field1}}({{fieldName}})
            .{{field2}}({{defaultValue2}})
            .{{field3}}({{defaultValue3}})
            .build();
    }
    
    private {{DomainName}} create{{DomainName}}() {
        return {{DomainName}}.builder()
            .{{field1}}("test{{Field1}}")
            .{{field2}}({{defaultValue2}})
            .{{field3}}({{defaultValue3}})
            .build();
    }
}
```

### Service Test Template
```java
package com.dong.bible.application.service;

import com.dong.bible.application.dto.query.{{DomainName}}QueryDto;
import com.dong.bible.domain.{{domain}}.{{DomainName}};
import com.dong.bible.domain.{{domain}}.{{DomainName}}Repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("{{DomainName}}ApplicationService 테스트")
class {{DomainName}}ApplicationServiceTest {
    
    @Mock
    private {{DomainName}}Repository {{domainName}}Repository;
    
    @InjectMocks
    private {{DomainName}}ApplicationService {{domainName}}ApplicationService;
    
    @Test
    @DisplayName("{{DomainName}} 단일 조회 성공")
    void get{{DomainName}}_Success() {
        // Given
        Long id = 1L;
        {{DomainName}} {{domainName}} = create{{DomainName}}(id);
        
        given({{domainName}}Repository.findById(id)).willReturn(Optional.of({{domainName}}));
        
        // When
        {{DomainName}}QueryDto result = {{domainName}}ApplicationService.get{{DomainName}}(id);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.get{{Field1}}()).isEqualTo({{domainName}}.get{{Field1}}());
        
        verify({{domainName}}Repository).findById(id);
    }
    
    @Test
    @DisplayName("존재하지 않는 {{DomainName}} 조회 시 예외 발생")
    void get{{DomainName}}_NotFound_ThrowsException() {
        // Given
        Long id = 999L;
        
        given({{domainName}}Repository.findById(id)).willReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> {{domainName}}ApplicationService.get{{DomainName}}(id))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("{{DomainName}} not found: id=" + id);
        
        verify({{domainName}}Repository).findById(id);
    }
    
    @Test
    @DisplayName("{{DomainName}} 목록 조회 성공")
    void getAll{{DomainName}}s_Success() {
        // Given
        List<{{DomainName}}> {{domainName}}s = List.of(
            create{{DomainName}}(1L),
            create{{DomainName}}(2L)
        );
        
        given({{domainName}}Repository.findAll()).willReturn({{domainName}}s);
        
        // When
        List<{{DomainName}}QueryDto> result = {{domainName}}ApplicationService.getAll{{DomainName}}s();
        
        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting({{DomainName}}QueryDto::getId)
            .containsExactly(1L, 2L);
        
        verify({{domainName}}Repository).findAll();
    }
    
    @Test
    @DisplayName("페이징으로 {{DomainName}} 목록 조회 성공")
    void getAll{{DomainName}}s_WithPaging_Success() {
        // Given
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<{{DomainName}}> {{domainName}}s = List.of(create{{DomainName}}(1L));
        Page<{{DomainName}}> {{domainName}}Page = new PageImpl<>({{domainName}}s, pageRequest, 1);
        
        given({{domainName}}Repository.findAll(pageRequest)).willReturn({{domainName}}Page);
        
        // When
        Page<{{DomainName}}QueryDto> result = {{domainName}}ApplicationService.getAll{{DomainName}}s(pageRequest);
        
        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getPageable()).isEqualTo(pageRequest);
        
        verify({{domainName}}Repository).findAll(pageRequest);
    }
    
    @Test
    @DisplayName("{{FieldName}}으로 {{DomainName}} 조회 성공")
    void get{{DomainName}}sBy{{FieldName}}_Success() {
        // Given
        {{FieldType}} {{fieldName}} = "test{{FieldName}}";
        List<{{DomainName}}> {{domainName}}s = List.of(create{{DomainName}}(1L));
        
        given({{domainName}}Repository.findBy{{FieldName}}({{fieldName}})).willReturn({{domainName}}s);
        
        // When
        List<{{DomainName}}QueryDto> result = {{domainName}}ApplicationService
            .get{{DomainName}}sBy{{FieldName}}({{fieldName}});
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).get{{Field1}}()).isEqualTo("test{{Field1}}");
        
        verify({{domainName}}Repository).findBy{{FieldName}}({{fieldName}});
    }
    
    @Test
    @DisplayName("null {{FieldName}}으로 조회 시 예외 발생")
    void get{{DomainName}}sBy{{FieldName}}_NullParameter_ThrowsException() {
        // When & Then
        assertThatThrownBy(() -> {{domainName}}ApplicationService.get{{DomainName}}sBy{{FieldName}}(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("{{FieldName}} must not be null");
        
        verify({{domainName}}Repository, never()).findBy{{FieldName}}(any());
    }
    
    private {{DomainName}} create{{DomainName}}(Long id) {
        return {{DomainName}}.builder()
            .id(id)
            .{{field1}}("test{{Field1}}")
            .{{field2}}({{defaultValue2}})
            .{{field3}}({{defaultValue3}})
            .build();
    }
}
```

### Controller Test Template
```java
package com.dong.bible.web.controller;

import com.dong.bible.application.dto.query.{{DomainName}}QueryDto;
import com.dong.bible.application.service.{{DomainName}}ApplicationService;
import com.dong.bible.application.service.{{DomainName}}CommandService;
import com.dong.bible.web.dto.request.{{DomainName}}Request;
import com.dong.bible.web.mapper.{{DomainName}}RequestMapper;
import com.dong.bible.web.mapper.{{DomainName}}ResponseMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({{DomainName}}Controller.class)
@DisplayName("{{DomainName}}Controller 테스트")
class {{DomainName}}ControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private {{DomainName}}ApplicationService applicationService;
    
    @MockBean
    private {{DomainName}}CommandService commandService;
    
    @MockBean
    private {{DomainName}}RequestMapper requestMapper;
    
    @MockBean
    private {{DomainName}}ResponseMapper responseMapper;
    
    @Test
    @DisplayName("{{DomainName}} 단일 조회 성공")
    void get{{DomainName}}_Success() throws Exception {
        // Given
        Long id = 1L;
        {{DomainName}}QueryDto dto = create{{DomainName}}QueryDto(id);
        
        given(applicationService.get{{DomainName}}(id)).willReturn(dto);
        given(responseMapper.toResponse(dto)).willReturn(create{{DomainName}}Response(id));
        
        // When & Then
        mockMvc.perform(get("/api/{{api-path}}/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(id))
            .andExpect(jsonPath("$.data.{{field1}}").value("test{{Field1}}"));
        
        verify(applicationService).get{{DomainName}}(id);
        verify(responseMapper).toResponse(dto);
    }
    
    @Test
    @DisplayName("존재하지 않는 {{DomainName}} 조회 시 404 에러")
    void get{{DomainName}}_NotFound_Returns404() throws Exception {
        // Given
        Long id = 999L;
        
        given(applicationService.get{{DomainName}}(id))
            .willThrow(new EntityNotFoundException("{{DomainName}} not found: id=" + id));
        
        // When & Then
        mockMvc.perform(get("/api/{{api-path}}/{id}", id))
            .andExpect(status().isNotFound());
        
        verify(applicationService).get{{DomainName}}(id);
    }
    
    @Test
    @DisplayName("{{DomainName}} 목록 조회 성공")
    void get{{DomainName}}s_Success() throws Exception {
        // Given
        PageRequest pageRequest = PageRequest.of(0, 20);
        List<{{DomainName}}QueryDto> dtos = List.of(create{{DomainName}}QueryDto(1L));
        Page<{{DomainName}}QueryDto> dtoPage = new PageImpl<>(dtos, pageRequest, 1);
        
        given(applicationService.getAll{{DomainName}}s(any(PageRequest.class))).willReturn(dtoPage);
        
        // When & Then
        mockMvc.perform(get("/api/{{api-path}}"))
            .andExpected(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content").isArray())
            .andExpect(jsonPath("$.data.totalElements").value(1));
        
        verify(applicationService).getAll{{DomainName}}s(any(PageRequest.class));
    }
    
    @Test
    @DisplayName("{{DomainName}} 생성 성공")
    void create{{DomainName}}_Success() throws Exception {
        // Given
        {{DomainName}}Request request = create{{DomainName}}Request();
        Long createdId = 1L;
        
        given(commandService.create{{DomainName}}(any())).willReturn(createdId);
        
        // When & Then
        mockMvc.perform(post("/api/{{api-path}}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(createdId));
        
        verify(requestMapper).toCommand(any({{DomainName}}Request.class));
        verify(commandService).create{{DomainName}}(any());
    }
    
    @Test
    @DisplayName("잘못된 요청으로 {{DomainName}} 생성 시 400 에러")
    void create{{DomainName}}_InvalidRequest_Returns400() throws Exception {
        // Given
        {{DomainName}}Request invalidRequest = new {{DomainName}}Request();
        // 필수 필드를 설정하지 않음
        
        // When & Then
        mockMvc.perform(post("/api/{{api-path}}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }
    
    private {{DomainName}}QueryDto create{{DomainName}}QueryDto(Long id) {
        return {{DomainName}}QueryDto.builder()
            .id(id)
            .{{field1}}("test{{Field1}}")
            .{{field2}}({{defaultValue2}})
            .{{field3}}({{defaultValue3}})
            .build();
    }
    
    private {{DomainName}}Response create{{DomainName}}Response(Long id) {
        return {{DomainName}}Response.builder()
            .id(id)
            .{{field1}}("test{{Field1}}")
            .{{field2}}({{defaultValue2}})
            .{{field3}}({{defaultValue3}})
            .build();
    }
    
    private {{DomainName}}Request create{{DomainName}}Request() {
        {{DomainName}}Request request = new {{DomainName}}Request();
        request.set{{Field1}}("test{{Field1}}");
        request.set{{Field2}}({{defaultValue2}});
        request.set{{Field3}}({{defaultValue3}});
        return request;
    }
}
```

## 🔧 Utility Templates

### Exception Handler Template
```java
package com.dong.bible.common.exception;

import com.dong.bible.common.response.AppResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public AppResponse<Void> handleEntityNotFound(EntityNotFoundException e) {
        log.warn("Entity not found: {}", e.getMessage());
        return AppResponse.error("ENTITY_NOT_FOUND", e.getMessage());
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppResponse<Void> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Invalid argument: {}", e.getMessage());
        return AppResponse.error("INVALID_ARGUMENT", e.getMessage());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppResponse<Void> handleValidationError(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .findFirst()
            .orElse("Validation failed");
        
        log.warn("Validation error: {}", message);
        return AppResponse.error("VALIDATION_FAILED", message);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppResponse<Void> handleConstraintViolation(ConstraintViolationException e) {
        log.warn("Constraint violation: {}", e.getMessage());
        return AppResponse.error("CONSTRAINT_VIOLATION", e.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AppResponse<Void> handleGenericError(Exception e) {
        log.error("Unexpected error occurred", e);
        return AppResponse.error("INTERNAL_ERROR", "An unexpected error occurred");
    }
}
```

## 📝 Template Usage Instructions

### How to Use These Templates

1. **Find and Replace Variables**: Replace all `{{variable}}` placeholders with actual values
2. **Customize Business Logic**: Adapt validation rules and business logic to your domain
3. **Update Field Names**: Change field names and types according to your entity
4. **Add Domain-Specific Methods**: Include additional methods specific to your domain
5. **Configure Annotations**: Update validation annotations and constraints
6. **Customize Error Messages**: Modify error messages to match your application

### Template Variables Reference

- `{{DomainName}}`: 도메인 객체 이름 (예: Sermon, BibleVerse)
- `{{domainName}}`: 소문자 도메인 이름 (예: sermon, bibleVerse)
- `{{EntityName}}`: JPA 엔티티 이름 (예: SermonEntity)
- `{{domain}}`: 도메인 패키지 이름 (예: sermon, verse)
- `{{api-path}}`: API 경로 (예: sermons, bible-verses)
- `{{field1}}`, `{{field2}}`: 도메인 필드 이름
- `{{Field1}}`, `{{Field2}}`: 대문자 필드 이름 (getter/setter용)
- `{{FieldType}}`: 필드 타입 (예: String, Long, LocalDate)
- `{{defaultValue}}`: 테스트용 기본값

### Quick Start Example

For a Sermon entity:
- `{{DomainName}}` → `Sermon`
- `{{domainName}}` → `sermon`
- `{{EntityName}}` → `SermonEntity`
- `{{domain}}` → `sermon`
- `{{api-path}}` → `sermons`
- `{{field1}}` → `title`
- `{{Field1}}` → `Title`
- `{{FieldType}}` → `String`