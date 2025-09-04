# Bible API Implementation Documentation

## 📁 폴더 구조

```
.claude/
├── README.md                           # 이 파일 - 전체 가이드
├── IMPLEMENTATION_ROADMAP.md           # 전체 로드맵 (통합 문서)
├── critical/                           # 🚨 최우선 수정 필요
│   ├── 01-SERMON_REPOSITORY_IMPLEMENTATION.md
│   └── 02-BIBLEVERSE_BOOK_NAME_RESOLUTION.md
├── high-priority/                      # ⚠️ 높은 우선순위
│   ├── 03-ELASTICSEARCH_ADVANCED_FEATURES.md
│   └── 04-MISSING_REPOSITORY_METHODS.md
├── medium-priority/                    # 📊 중간 우선순위
│   └── 05-RESPONSE_MAPPER_FIXES.md
├── new-features/                       # 🚀 신규 기능
│   ├── 06-USER_MANAGEMENT_SYSTEM.md
│   └── 07-ANALYTICS_SYSTEM.md
├── templates/                          # 📝 코드 템플릿
│   └── CODE_TEMPLATES.md
└── testing/                           # 🧪 테스트 전략
    └── TESTING_STRATEGY.md
```

## 🎯 구현 우선순위

### 🚨 Critical (1-2일 내 필수)
**현재 서비스 동작을 위한 필수 수정사항**

1. **[SermonRepository Implementation](critical/01-SERMON_REPOSITORY_IMPLEMENTATION.md)**
   - **문제**: 22개 메서드가 `UnsupportedOperationException` 발생 → Sermon 기능 전체 마비
   - **예상시간**: 2-3시간
   - **단계**: 기본 CRUD → 검색 메서드 → 고급 기능

2. **[BibleVerse Book Name Resolution](critical/02-BIBLEVERSE_BOOK_NAME_RESOLUTION.md)**
   - **문제**: 모든 구절에서 bookName이 "Book-{id}" 형태로 표시
   - **예상시간**: 1시간
   - **해결**: VerseDomainService + 캐싱으로 성능 최적화

### ⚠️ High Priority (3-5일 내)
**기존 기능 완성 및 사용성 개선**

3. **[ElasticSearch Advanced Features](high-priority/03-ELASTICSEARCH_ADVANCED_FEATURES.md)**
   - **미완성**: 동의어 검색, 다중 조건 검색 placeholder 상태
   - **예상시간**: 2-3시간
   - **개선**: SynonymService + 고급 검색 쿼리

4. **[Missing Repository Methods](high-priority/04-MISSING_REPOSITORY_METHODS.md)**
   - **문제**: `findByTextContaining` 등 핵심 메서드 누락
   - **예상시간**: 1-2시간
   - **추가**: 콘텐츠 기반 검색, 확장된 범위 검색

### 📊 Medium Priority (1주일 내)
**코드 품질 및 일관성 개선**

5. **[Response Mapper Fixes](medium-priority/05-RESPONSE_MAPPER_FIXES.md)**
   - **문제**: API 응답에서 일부 필드 누락 (Book 정보, 메타데이터 등)
   - **예상시간**: 1-2시간
   - **개선**: 모든 Mapper의 누락 필드 완성

### 🚀 New Features (2주일 내)
**새로운 가치 창출 기능**

6. **[User Management System](new-features/06-USER_MANAGEMENT_SYSTEM.md)**
   - **신규**: JWT 인증, 사용자 관리, 즐겨찾기, 읽기 진행상황
   - **예상시간**: 1주일
   - **가치**: 개인화된 사용자 경험

7. **[Analytics System](new-features/07-ANALYTICS_SYSTEM.md)**
   - **신규**: 사용 패턴 분석, 인기 구절, 개인화 추천
   - **예상시간**: 3-4일
   - **가치**: 데이터 기반 인사이트

## 📋 일일 구현 계획

### Day 1 (Critical 완성)
- [ ] SermonRepository 기본 CRUD 구현
- [ ] SermonRepository 검색 메서드 구현  
- [ ] BibleVerse VerseDomainService 구현
- [ ] 통합 테스트 및 검증

### Day 2 (High Priority 시작)
- [ ] ElasticSearch 동의어 검색 구현
- [ ] 다중 조건 검색 완성
- [ ] 누락된 Repository 메서드 추가

### Day 3-4 (High Priority 완성)
- [ ] 매치 타입 분석 완성
- [ ] 콘텐츠 기반 검색 테스트
- [ ] 성능 최적화

### Day 5 (Medium Priority)
- [ ] Response Mapper 필드 매핑 완성
- [ ] API 응답 검증 및 테스트

### Week 2 (New Features)
- [ ] User Management 도메인 설계
- [ ] 인증/인가 시스템 구현
- [ ] 사용자 기능 완성

## 🧪 테스트 전략

### 단위 테스트 (모든 단계)
- Domain 객체 비즈니스 로직
- Service 메서드 검증  
- Repository 메서드 동작

### 통합 테스트 (각 단계 완료 후)
- API 엔드포인트 정상 동작
- 데이터베이스 연동 확인
- ElasticSearch 검색 결과

### 성능 테스트 (High Priority 완료 후)
- 대용량 데이터 검색 성능
- 캐싱 효과 측정
- 응답 시간 최적화

## 📝 코드 템플릿 활용

[CODE_TEMPLATES.md](templates/CODE_TEMPLATES.md)에서 다음 템플릿 제공:
- Repository 구현 템플릿
- Service 계층 템플릿  
- Controller 템플릿
- DTO 변환 템플릿
- 테스트 코드 템플릿

## 🎯 성공 기준

### Critical 완성 후
- ✅ 모든 Sermon API 정상 동작
- ✅ 구절 표시에서 올바른 책 이름 확인

### High Priority 완성 후  
- ✅ 동의어 검색으로 풍부한 결과 제공
- ✅ 다중 조건으로 정밀한 검색 가능

### Medium Priority 완성 후
- ✅ 모든 API 응답에 완전한 정보 포함
- ✅ 일관된 코드 품질 유지

### New Features 완성 후
- ✅ 사용자 중심의 개인화된 경험 제공
- ✅ 데이터 기반 인사이트와 추천

## 🔧 개발 환경 설정

### 필수 의존성
```gradle
// 기존 dependencies에 추가
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
implementation 'org.springframework.boot:spring-boot-starter-cache'
implementation 'com.github.ben-manes.caffeine:caffeine'
```

### 데이터베이스 설정
- MySQL 8.0+ (localhost:3307)
- ElasticSearch 7.0+ (검색 기능용)
- Redis (캐싱 - 선택사항)

## 📞 문제 해결

각 구현 과정에서 문제가 발생하면:
1. 해당 폴더의 상세 가이드 문서 참조
2. 템플릿 폴더에서 유사한 패턴 확인
3. 테스트 전략 문서에서 검증 방법 확인

## 🎉 구현 완료 체크리스트

- [ ] **Critical**: Sermon 기능 복구, 구절 Book 이름 표시
- [ ] **High Priority**: 고급 검색 기능, 누락 메서드 완성
- [ ] **Medium Priority**: 응답 매핑 완성, 코드 품질 개선
- [ ] **New Features**: 사용자 시스템, 분석 시스템
- [ ] **Testing**: 모든 기능 테스트 완료
- [ ] **Documentation**: API 문서 업데이트

---

**💡 팁**: 각 단계를 순서대로 진행하되, Critical 이슈를 먼저 해결하여 서비스 안정성을 확보한 후 점진적으로 기능을 확장해 나가세요.