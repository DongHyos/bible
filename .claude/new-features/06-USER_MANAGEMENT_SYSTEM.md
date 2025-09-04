# User Management System Implementation Guide

## 📋 Overview
사용자 관리, 인증/인가, 즐겨찾기, 읽기 진행상황 등 사용자 중심 기능을 구현하는 포괄적 가이드입니다.

## 🎯 System Architecture Overview

### Core Features
- **User Authentication**: 회원가입, 로그인, JWT 토큰 기반 인증
- **User Authorization**: Role 기반 접근 제어
- **User Favorites**: 구절 즐겨찾기 관리
- **Reading History**: 읽기 진행상황 추적
- **User Statistics**: 개인 통계 및 분석
- **Personalization**: 개인화 추천 시스템

## 📂 Directory Structure

```
src/main/java/com/dong/bible/
├── domain/
│   ├── user/
│   │   ├── User.java
│   │   ├── UserRepository.java
│   │   ├── UserRole.java
│   │   └── UserStatus.java
│   ├── favorite/
│   │   ├── UserFavorite.java
│   │   └── UserFavoriteRepository.java
│   ├── history/
│   │   ├── ReadingHistory.java
│   │   └── ReadingHistoryRepository.java
│   └── auth/
│       ├── RefreshToken.java
│       └── RefreshTokenRepository.java
├── application/
│   ├── service/
│   │   ├── UserApplicationService.java
│   │   ├── AuthApplicationService.java
│   │   ├── FavoriteApplicationService.java
│   │   └── ReadingHistoryApplicationService.java
│   └── dto/
│       ├── command/
│       │   ├── UserRegistrationCommand.java
│       │   ├── LoginCommand.java
│       │   └── UpdateUserProfileCommand.java
│       └── query/
│           ├── UserProfileQuery.java
│           ├── UserStatisticsQuery.java
│           └── ReadingProgressQuery.java
├── infrastructure/
│   ├── persistence/
│   │   ├── entity/
│   │   │   ├── UserEntity.java
│   │   │   ├── UserFavoriteEntity.java
│   │   │   └── ReadingHistoryEntity.java
│   │   └── repository/
│   │       ├── UserJpaRepository.java
│   │       └── UserRepositoryImpl.java
│   └── security/
│       ├── JwtTokenProvider.java
│       ├── SecurityConfig.java
│       └── CustomUserDetailsService.java
└── web/
    ├── controller/
    │   ├── AuthController.java
    │   ├── UserController.java
    │   └── FavoriteController.java
    └── dto/
        ├── request/
        │   ├── UserRegistrationRequest.java
        │   └── LoginRequest.java
        └── response/
            ├── UserProfileResponse.java
            ├── AuthenticationResponse.java
            └── UserStatisticsResponse.java
```

## 🎯 Phase 1: Domain Models

### 1.1 User Domain Object
**File**: `src/main/java/com/dong/bible/domain/user/User.java`

```java
package com.dong.bible.domain.user;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 사용자 도메인 객체
 */
@Getter
@Builder(toBuilder = true)
public class User {
    
    private final Long id;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final String displayName;
    private final UserRole role;
    private final UserStatus status;
    private final String profileImageUrl;
    private final String bio;
    private final LocalDateTime lastLoginAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    /**
     * 새 사용자 생성
     */
    public static User createNew(String username, String email, String plainPassword, 
                                String displayName, PasswordEncoder passwordEncoder) {
        validateUserCreation(username, email, plainPassword, displayName);
        
        return User.builder()
            .username(username.trim().toLowerCase())
            .email(email.trim().toLowerCase())
            .passwordHash(passwordEncoder.encode(plainPassword))
            .displayName(displayName.trim())
            .role(UserRole.USER)
            .status(UserStatus.ACTIVE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    /**
     * 비밀번호 검증
     */
    public boolean isPasswordValid(String plainPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(plainPassword, this.passwordHash);
    }
    
    /**
     * 로그인 시간 업데이트
     */
    public User updateLastLogin() {
        return this.toBuilder()
            .lastLoginAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    /**
     * 프로필 업데이트
     */
    public User updateProfile(String displayName, String bio, String profileImageUrl) {
        return this.toBuilder()
            .displayName(displayName != null ? displayName.trim() : this.displayName)
            .bio(bio != null ? bio.trim() : this.bio)
            .profileImageUrl(profileImageUrl != null ? profileImageUrl.trim() : this.profileImageUrl)
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    /**
     * 비밀번호 변경
     */
    public User changePassword(String newPassword, PasswordEncoder passwordEncoder) {
        if (newPassword == null || newPassword.trim().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        
        return this.toBuilder()
            .passwordHash(passwordEncoder.encode(newPassword))
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    /**
     * 사용자 상태 변경
     */
    public User changeStatus(UserStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        
        return this.toBuilder()
            .status(newStatus)
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    /**
     * 관리자 권한 확인
     */
    public boolean isAdmin() {
        return UserRole.ADMIN.equals(this.role);
    }
    
    /**
     * 활성 사용자 확인
     */
    public boolean isActive() {
        return UserStatus.ACTIVE.equals(this.status);
    }
    
    /**
     * 이메일 중복 방지를 위한 equals/hashCode
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
    
    /**
     * 사용자 생성 시 유효성 검증
     */
    private static void validateUserCreation(String username, String email, 
                                           String password, String displayName) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        
        if (username.trim().length() < 3 || username.trim().length() > 30) {
            throw new IllegalArgumentException("Username must be between 3 and 30 characters");
        }
        
        if (!username.matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalArgumentException("Username can only contain letters, numbers, underscore, and dash");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new IllegalArgumentException("Display name is required");
        }
        
        if (displayName.trim().length() > 50) {
            throw new IllegalArgumentException("Display name must not exceed 50 characters");
        }
    }
}
```

### 1.2 User Enums
**File**: `src/main/java/com/dong/bible/domain/user/UserRole.java`

```java
package com.dong.bible.domain.user;

/**
 * 사용자 역할
 */
public enum UserRole {
    USER("일반 사용자"),
    MODERATOR("운영자"), 
    ADMIN("관리자"),
    SUPER_ADMIN("최고 관리자");
    
    private final String description;
    
    UserRole(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean hasAdminPrivileges() {
        return this == ADMIN || this == SUPER_ADMIN;
    }
    
    public boolean canModerate() {
        return this == MODERATOR || hasAdminPrivileges();
    }
}
```

**File**: `src/main/java/com/dong/bible/domain/user/UserStatus.java`

```java
package com.dong.bible.domain.user;

/**
 * 사용자 상태
 */
public enum UserStatus {
    ACTIVE("활성"),
    INACTIVE("비활성"),
    SUSPENDED("정지"),
    DELETED("삭제됨"),
    EMAIL_UNVERIFIED("이메일 미인증");
    
    private final String description;
    
    UserStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean canLogin() {
        return this == ACTIVE;
    }
    
    public boolean isBlocked() {
        return this == SUSPENDED || this == DELETED;
    }
}
```

### 1.3 UserFavorite Domain Object
**File**: `src/main/java/com/dong/bible/domain/favorite/UserFavorite.java`

```java
package com.dong.bible.domain.favorite;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 사용자 즐겨찾기 도메인 객체
 */
@Getter
@Builder(toBuilder = true)
public class UserFavorite {
    
    private final Long id;
    private final Long userId;
    private final Long verseId;
    private final String note;
    private final String tags;
    private final Integer displayOrder;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    /**
     * 새 즐겨찾기 생성
     */
    public static UserFavorite create(Long userId, Long verseId, String note) {
        validateCreation(userId, verseId);
        
        return UserFavorite.builder()
            .userId(userId)
            .verseId(verseId)
            .note(note != null ? note.trim() : null)
            .displayOrder(0)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    /**
     * 메모 업데이트
     */
    public UserFavorite updateNote(String newNote) {
        return this.toBuilder()
            .note(newNote != null ? newNote.trim() : null)
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    /**
     * 태그 업데이트
     */
    public UserFavorite updateTags(String newTags) {
        return this.toBuilder()
            .tags(newTags != null ? newTags.trim() : null)
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    /**
     * 표시 순서 업데이트
     */
    public UserFavorite updateDisplayOrder(Integer newOrder) {
        return this.toBuilder()
            .displayOrder(newOrder != null ? newOrder : 0)
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    /**
     * 즐겨찾기 고유성을 위한 equals/hashCode
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserFavorite that = (UserFavorite) o;
        return Objects.equals(userId, that.userId) && 
               Objects.equals(verseId, that.verseId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId, verseId);
    }
    
    private static void validateCreation(Long userId, Long verseId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        
        if (verseId == null) {
            throw new IllegalArgumentException("Verse ID is required");
        }
    }
}
```

### 1.4 ReadingHistory Domain Object
**File**: `src/main/java/com/dong/bible/domain/history/ReadingHistory.java`

```java
package com.dong.bible.domain.history;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 읽기 이력 도메인 객체
 */
@Getter
@Builder(toBuilder = true)
public class ReadingHistory {
    
    private final Long id;
    private final Long userId;
    private final Long bookId;
    private final Integer chapter;
    private final Integer lastVerseRead;
    private final Integer totalVersesInChapter;
    private final Double progressPercentage;
    private final LocalDate readDate;
    private final LocalDateTime lastReadAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    /**
     * 새 읽기 이력 생성
     */
    public static ReadingHistory create(Long userId, Long bookId, Integer chapter, 
                                       Integer verseNumber, Integer totalVerses) {
        validateCreation(userId, bookId, chapter, verseNumber);
        
        double progress = totalVerses > 0 ? 
            (double) verseNumber / totalVerses * 100 : 0.0;
        
        return ReadingHistory.builder()
            .userId(userId)
            .bookId(bookId)
            .chapter(chapter)
            .lastVerseRead(verseNumber)
            .totalVersesInChapter(totalVerses)
            .progressPercentage(progress)
            .readDate(LocalDate.now())
            .lastReadAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    /**
     * 읽기 진행상황 업데이트
     */
    public ReadingHistory updateProgress(Integer verseNumber, Integer totalVerses) {
        if (verseNumber == null || verseNumber < 1) {
            throw new IllegalArgumentException("Verse number must be positive");
        }
        
        double progress = totalVerses != null && totalVerses > 0 ? 
            (double) verseNumber / totalVerses * 100 : 0.0;
        
        return this.toBuilder()
            .lastVerseRead(verseNumber)
            .totalVersesInChapter(totalVerses)
            .progressPercentage(progress)
            .readDate(LocalDate.now())
            .lastReadAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    /**
     * 장 완독 여부 확인
     */
    public boolean isChapterCompleted() {
        return totalVersesInChapter != null && lastVerseRead != null &&
               lastVerseRead >= totalVersesInChapter;
    }
    
    /**
     * 읽기 진행 백분율 반환
     */
    public double getProgressPercentage() {
        return progressPercentage != null ? progressPercentage : 0.0;
    }
    
    /**
     * 고유성을 위한 equals/hashCode (사용자, 책, 장별)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReadingHistory that = (ReadingHistory) o;
        return Objects.equals(userId, that.userId) && 
               Objects.equals(bookId, that.bookId) && 
               Objects.equals(chapter, that.chapter);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId, bookId, chapter);
    }
    
    private static void validateCreation(Long userId, Long bookId, 
                                       Integer chapter, Integer verseNumber) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        
        if (bookId == null) {
            throw new IllegalArgumentException("Book ID is required");
        }
        
        if (chapter == null || chapter < 1) {
            throw new IllegalArgumentException("Chapter must be positive");
        }
        
        if (verseNumber == null || verseNumber < 1) {
            throw new IllegalArgumentException("Verse number must be positive");
        }
    }
}
```

## 🎯 Phase 2: Repository Interfaces

### 2.1 User Repository
**File**: `src/main/java/com/dong/bible/domain/user/UserRepository.java`

```java
package com.dong.bible.domain.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 사용자 도메인 저장소 인터페이스
 */
public interface UserRepository {
    
    // ===== 기본 CRUD =====
    Optional<User> findById(Long id);
    List<User> findAll();
    Page<User> findAll(Pageable pageable);
    User save(User user);
    void deleteById(Long id);
    
    // ===== 조회 메서드 =====
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    
    // ===== 조건부 조회 =====
    List<User> findByRole(UserRole role);
    List<User> findByStatus(UserStatus status);
    Page<User> findByStatus(UserStatus status, Pageable pageable);
    
    // ===== 검색 =====
    Page<User> searchByDisplayName(String displayName, Pageable pageable);
    Page<User> searchByEmail(String email, Pageable pageable);
    
    // ===== 통계 =====
    long countByRole(UserRole role);
    long countByStatus(UserStatus status);
    long countByCreatedAtAfter(LocalDateTime date);
    
    // ===== 관리자 기능 =====
    List<User> findRecentUsers(int limit);
    List<User> findActiveUsersWithinDays(int days);
}
```

### 2.2 UserFavorite Repository
**File**: `src/main/java/com/dong/bible/domain/favorite/UserFavoriteRepository.java`

```java
package com.dong.bible.domain.favorite;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 즐겨찾기 저장소 인터페이스
 */
public interface UserFavoriteRepository {
    
    // ===== 기본 CRUD =====
    Optional<UserFavorite> findById(Long id);
    UserFavorite save(UserFavorite favorite);
    void deleteById(Long id);
    void delete(UserFavorite favorite);
    
    // ===== 사용자별 조회 =====
    List<UserFavorite> findByUserId(Long userId);
    Page<UserFavorite> findByUserId(Long userId, Pageable pageable);
    List<UserFavorite> findByUserIdOrderByDisplayOrder(Long userId);
    
    // ===== 구절별 조회 =====
    List<UserFavorite> findByVerseId(Long verseId);
    long countByVerseId(Long verseId);
    
    // ===== 중복 확인 =====
    boolean existsByUserIdAndVerseId(Long userId, Long verseId);
    Optional<UserFavorite> findByUserIdAndVerseId(Long userId, Long verseId);
    
    // ===== 태그 검색 =====
    List<UserFavorite> findByUserIdAndTagsContaining(Long userId, String tag);
    
    // ===== 통계 =====
    long countByUserId(Long userId);
    List<UserFavorite> findMostFavoritedVerses(int limit);
}
```

### 2.3 ReadingHistory Repository
**File**: `src/main/java/com/dong/bible/domain/history/ReadingHistoryRepository.java`

```java
package com.dong.bible.domain.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 읽기 이력 저장소 인터페이스
 */
public interface ReadingHistoryRepository {
    
    // ===== 기본 CRUD =====
    Optional<ReadingHistory> findById(Long id);
    ReadingHistory save(ReadingHistory history);
    void deleteById(Long id);
    
    // ===== 사용자별 조회 =====
    List<ReadingHistory> findByUserId(Long userId);
    Page<ReadingHistory> findByUserId(Long userId, Pageable pageable);
    
    // ===== 책/장별 조회 =====
    Optional<ReadingHistory> findByUserIdAndBookIdAndChapter(Long userId, Long bookId, Integer chapter);
    List<ReadingHistory> findByUserIdAndBookId(Long userId, Long bookId);
    
    // ===== 날짜별 조회 =====
    List<ReadingHistory> findByUserIdAndReadDate(Long userId, LocalDate date);
    List<ReadingHistory> findByUserIdAndReadDateBetween(Long userId, LocalDate start, LocalDate end);
    
    // ===== 통계 및 진행상황 =====
    long countByUserId(Long userId);
    long countByUserIdAndReadDate(Long userId, LocalDate date);
    List<ReadingHistory> findCompletedChaptersByUserId(Long userId);
    double calculateReadingProgressByUserId(Long userId);
    
    // ===== 최근 읽기 이력 =====
    List<ReadingHistory> findRecentReadingByUserId(Long userId, int limit);
}
```

## 🎯 Phase 3: Application Services

### 3.1 Authentication Service
**File**: `src/main/java/com/dong/bible/application/service/AuthApplicationService.java`

```java
package com.dong.bible.application.service;

import com.dong.bible.application.dto.command.LoginCommand;
import com.dong.bible.application.dto.command.UserRegistrationCommand;
import com.dong.bible.application.dto.query.AuthenticationQuery;
import com.dong.bible.domain.user.User;
import com.dong.bible.domain.user.UserRepository;
import com.dong.bible.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 관련 애플리케이션 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthApplicationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    /**
     * 사용자 회원가입
     */
    public Long registerUser(UserRegistrationCommand command) {
        log.info("User registration attempt: username={}, email={}", 
            command.getUsername(), command.getEmail());
        
        // 중복 확인
        if (userRepository.existsByEmail(command.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + command.getEmail());
        }
        
        if (userRepository.existsByUsername(command.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + command.getUsername());
        }
        
        // 사용자 생성
        User newUser = User.createNew(
            command.getUsername(),
            command.getEmail(), 
            command.getPassword(),
            command.getDisplayName(),
            passwordEncoder
        );
        
        User savedUser = userRepository.save(newUser);
        
        log.info("User registered successfully: id={}, username={}", 
            savedUser.getId(), savedUser.getUsername());
        
        return savedUser.getId();
    }
    
    /**
     * 로그인 처리
     */
    public AuthenticationQuery authenticateUser(LoginCommand command) {
        log.info("Login attempt: identifier={}", command.getIdentifier());
        
        // 이메일 또는 사용자명으로 찾기
        User user = findUserByEmailOrUsername(command.getIdentifier())
            .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        
        // 비밀번호 검증
        if (!user.isPasswordValid(command.getPassword(), passwordEncoder)) {
            log.warn("Invalid password for user: {}", command.getIdentifier());
            throw new IllegalArgumentException("Invalid credentials");
        }
        
        // 사용자 상태 확인
        if (!user.isActive()) {
            log.warn("Inactive user login attempt: {}", command.getIdentifier());
            throw new IllegalArgumentException("User account is not active");
        }
        
        // 로그인 시간 업데이트
        User updatedUser = user.updateLastLogin();
        userRepository.save(updatedUser);
        
        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(
            updatedUser.getId(), updatedUser.getEmail(), updatedUser.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(updatedUser.getId());
        
        log.info("User authenticated successfully: id={}, username={}", 
            updatedUser.getId(), updatedUser.getUsername());
        
        return AuthenticationQuery.builder()
            .userId(updatedUser.getId())
            .username(updatedUser.getUsername())
            .email(updatedUser.getEmail())
            .displayName(updatedUser.getDisplayName())
            .role(updatedUser.getRole())
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresIn(jwtTokenProvider.getAccessTokenValidityInSeconds())
            .build();
    }
    
    /**
     * 토큰 갱신
     */
    public AuthenticationQuery refreshToken(String refreshToken) {
        log.info("Token refresh attempt");
        
        if (!jwtTokenProvider.isTokenValid(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (!user.isActive()) {
            throw new IllegalArgumentException("User account is not active");
        }
        
        // 새 토큰 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(
            user.getId(), user.getEmail(), user.getRole());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        
        log.info("Token refreshed successfully: userId={}", userId);
        
        return AuthenticationQuery.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .displayName(user.getDisplayName())
            .role(user.getRole())
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .expiresIn(jwtTokenProvider.getAccessTokenValidityInSeconds())
            .build();
    }
    
    /**
     * 비밀번호 변경
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        log.info("Password change attempt: userId={}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // 현재 비밀번호 검증
        if (!user.isPasswordValid(currentPassword, passwordEncoder)) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        // 새 비밀번호로 변경
        User updatedUser = user.changePassword(newPassword, passwordEncoder);
        userRepository.save(updatedUser);
        
        log.info("Password changed successfully: userId={}", userId);
    }
    
    /**
     * 로그아웃 (토큰 무효화는 클라이언트에서 처리)
     */
    public void logout(Long userId) {
        log.info("User logout: userId={}", userId);
        // 필요한 경우 추가 로그아웃 로직 구현
        // 예: 리프레시 토큰 삭제, 로그 기록 등
    }
    
    private Optional<User> findUserByEmailOrUsername(String identifier) {
        // 이메일 형식인지 확인
        if (identifier.contains("@")) {
            return userRepository.findByEmail(identifier);
        } else {
            return userRepository.findByUsername(identifier);
        }
    }
}
```

### 3.2 User Management Service
**File**: `src/main/java/com/dong/bible/application/service/UserApplicationService.java`

```java
package com.dong.bible.application.service;

import com.dong.bible.application.dto.command.UpdateUserProfileCommand;
import com.dong.bible.application.dto.query.UserProfileQuery;
import com.dong.bible.application.dto.query.UserStatisticsQuery;
import com.dong.bible.domain.user.User;
import com.dong.bible.domain.user.UserRepository;
import com.dong.bible.domain.user.UserRole;
import com.dong.bible.domain.user.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 사용자 관리 애플리케이션 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserApplicationService {
    
    private final UserRepository userRepository;
    private final FavoriteApplicationService favoriteService;
    private final ReadingHistoryApplicationService historyService;
    
    /**
     * 사용자 프로필 조회
     */
    public UserProfileQuery getUserProfile(Long userId) {
        log.info("Getting user profile: userId={}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        
        return UserProfileQuery.from(user);
    }
    
    /**
     * 사용자 프로필 업데이트
     */
    @Transactional
    public void updateUserProfile(Long userId, UpdateUserProfileCommand command) {
        log.info("Updating user profile: userId={}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        
        User updatedUser = user.updateProfile(
            command.getDisplayName(),
            command.getBio(),
            command.getProfileImageUrl()
        );
        
        userRepository.save(updatedUser);
        
        log.info("User profile updated successfully: userId={}", userId);
    }
    
    /**
     * 사용자 통계 정보 조회
     */
    public UserStatisticsQuery getUserStatistics(Long userId) {
        log.info("Getting user statistics: userId={}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        
        // 즐겨찾기 수
        long favoriteCount = favoriteService.getFavoriteCount(userId);
        
        // 읽기 진행상황
        double readingProgress = historyService.getReadingProgress(userId);
        
        // 최근 활동 통계
        long recentReadingDays = historyService.getRecentReadingDays(userId, 30);
        
        return UserStatisticsQuery.builder()
            .userId(userId)
            .username(user.getUsername())
            .displayName(user.getDisplayName())
            .joinDate(user.getCreatedAt().toLocalDate())
            .lastLoginDate(user.getLastLoginAt() != null ? 
                user.getLastLoginAt().toLocalDate() : null)
            .favoriteVerseCount(favoriteCount)
            .totalReadingProgress(readingProgress)
            .recentReadingDays(recentReadingDays)
            .build();
    }
    
    /**
     * 사용자 목록 조회 (관리자용)
     */
    public Page<UserProfileQuery> getAllUsers(Pageable pageable) {
        log.info("Getting all users: page={}, size={}", 
            pageable.getPageNumber(), pageable.getPageSize());
        
        Page<User> userPage = userRepository.findAll(pageable);
        
        return userPage.map(UserProfileQuery::from);
    }
    
    /**
     * 사용자 검색
     */
    public Page<UserProfileQuery> searchUsers(String keyword, Pageable pageable) {
        log.info("Searching users: keyword={}", keyword);
        
        Page<User> userPage = userRepository.searchByDisplayName(keyword, pageable);
        
        return userPage.map(UserProfileQuery::from);
    }
    
    /**
     * 역할별 사용자 조회
     */
    public List<UserProfileQuery> getUsersByRole(UserRole role) {
        log.info("Getting users by role: {}", role);
        
        List<User> users = userRepository.findByRole(role);
        
        return users.stream()
            .map(UserProfileQuery::from)
            .collect(Collectors.toList());
    }
    
    /**
     * 사용자 상태 변경 (관리자용)
     */
    @Transactional
    public void changeUserStatus(Long userId, UserStatus newStatus, Long adminUserId) {
        log.info("Changing user status: userId={}, newStatus={}, adminId={}", 
            userId, newStatus, adminUserId);
        
        // 관리자 권한 확인
        User admin = userRepository.findById(adminUserId)
            .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
        
        if (!admin.isAdmin()) {
            throw new IllegalArgumentException("Insufficient privileges");
        }
        
        // 사용자 상태 변경
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        User updatedUser = user.changeStatus(newStatus);
        userRepository.save(updatedUser);
        
        log.info("User status changed successfully: userId={}, status={}", userId, newStatus);
    }
    
    /**
     * 최근 가입자 조회
     */
    public List<UserProfileQuery> getRecentUsers(int limit) {
        log.info("Getting recent users: limit={}", limit);
        
        List<User> users = userRepository.findRecentUsers(limit);
        
        return users.stream()
            .map(UserProfileQuery::from)
            .collect(Collectors.toList());
    }
    
    /**
     * 활성 사용자 통계
     */
    public Map<String, Long> getUserStatisticsSummary() {
        log.info("Getting user statistics summary");
        
        Map<String, Long> stats = new HashMap<>();
        
        stats.put("totalUsers", userRepository.countByStatus(UserStatus.ACTIVE));
        stats.put("adminUsers", userRepository.countByRole(UserRole.ADMIN));
        stats.put("recentUsers", userRepository.countByCreatedAtAfter(
            LocalDateTime.now().minusDays(30)));
        
        return stats;
    }
}
```

## ✅ Implementation Checklist

### Domain Layer
- [ ] Create User domain object with business logic
- [ ] Create UserRole and UserStatus enums
- [ ] Create UserFavorite domain object
- [ ] Create ReadingHistory domain object
- [ ] Define repository interfaces

### Infrastructure Layer
- [ ] Implement JPA entities for all domain objects
- [ ] Implement repository implementations
- [ ] Create JWT token provider
- [ ] Configure Spring Security
- [ ] Add password encoding configuration

### Application Layer
- [ ] Create AuthApplicationService for authentication
- [ ] Create UserApplicationService for user management
- [ ] Create FavoriteApplicationService for favorites
- [ ] Create ReadingHistoryApplicationService for reading history
- [ ] Create all command and query DTOs

### Web Layer
- [ ] Create AuthController for login/register endpoints
- [ ] Create UserController for profile management
- [ ] Create FavoriteController for favorite management
- [ ] Create request/response DTOs
- [ ] Add proper security annotations

### Testing
- [ ] Unit tests for all domain objects
- [ ] Unit tests for all application services
- [ ] Integration tests for authentication flow
- [ ] Controller tests with security context
- [ ] End-to-end tests for complete user journeys

## 🎯 Success Criteria

구현 완료 후:
- ✅ JWT 기반 인증/인가 시스템 완전 동작
- ✅ 사용자 회원가입, 로그인, 프로필 관리 가능
- ✅ 구절 즐겨찾기 및 태그 관리 기능
- ✅ 읽기 진행상황 추적 및 통계 제공
- ✅ Role 기반 접근 제어로 관리자 기능 분리
- ✅ 개인화된 사용자 경험 제공

이 가이드를 따라 구현하면 완전한 사용자 관리 시스템이 완성됩니다.