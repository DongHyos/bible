package com.dong.bible.common.service;

import com.dong.bible.common.error.BizException;
import com.dong.bible.common.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class ImageStorageService {

    @Value("${app.upload.image-dir}")
    private String uploadDir;

    @Value("${app.upload.domain}")
    private String uploadDomain;

    public String saveImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return null;
        }

        try {
            File uploadDirectory = new File(uploadDir);
            if (!uploadDirectory.exists()) {
                boolean created = uploadDirectory.mkdirs();
                if (!created) {
                    throw new IOException("디렉토리 생성 실패");
                }
            }

            String originalFilename = image.getOriginalFilename();
            String newFileName = UUID.randomUUID() + "_" + originalFilename;
            File dest = new File(uploadDirectory, newFileName);
            image.transferTo(dest);

            // ✅ DB에는 접근 가능한 URL 저장
            return uploadDomain + "/upload/" + newFileName;
        } catch (IOException e) {
            log.error("이미지 저장 실패", e);
            throw new BizException(ResponseCode.SERVER_ERROR, "이미지 저장 실패", e);
        }
    }
}
