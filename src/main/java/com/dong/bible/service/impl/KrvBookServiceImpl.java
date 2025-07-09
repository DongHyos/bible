package com.dong.bible.service.impl;

import com.dong.bible.ENUM.Testament;
import com.dong.bible.web.dto.response.BibleBookDto;
import com.dong.bible.infrastructure.persistence.entity.KrvBook;
import com.dong.bible.mapstruct.KrvBookMapper;
import com.dong.bible.infrastructure.persistence.jpa.KrvBookRepository;
import com.dong.bible.service.KrvBookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class KrvBookServiceImpl implements KrvBookService {
    private final KrvBookRepository repository;

    private final KrvBookMapper mapper;

    // 전체 성경 목록
    @Override
    public List<BibleBookDto> getAllBooks() {
        List<KrvBook> books = repository.findAllByOrderByBookOrder();
        return mapper.toDtoList(books); // KrvBookMapper 메서드 사용
    }

    // 구약/신약별 조회
    @Override
    public List<BibleBookDto> getBooksByTestament(String testament) {
        Testament testamentEnum = Testament.fromString(testament);
        List<KrvBook> books = repository.findByTestamentOrderByBookOrder(testamentEnum);
        return mapper.toDtoList(books); // KrvBookMapper 메서드 사용
    }

    // 구약/신약으로 그룹핑 (프론트 탭용)
    @Override
    public Map<String, List<BibleBookDto>> getBooksByTestamentGrouped() {
        List<KrvBook> allBooks = repository.findAllByOrderByBookOrder();

        return allBooks.stream()
                .map(mapper::toDto) // KrvBookMapper 메서드 사용
                .collect(Collectors.groupingBy(BibleBookDto::getTestament));
    }

    // 성경 통계
    @Override
    public Map<String, Integer> getBibleStatistics() {
        Map<String, Integer> stats = new HashMap<>();

        List<Object[]> testamentCounts = repository.countByTestament();
        for (Object[] row : testamentCounts) {
            Testament testament = (Testament) row[0];
            Long count = (Long) row[1];
            stats.put(testament.getValue(), count.intValue());
        }

        return stats;
    }
}
