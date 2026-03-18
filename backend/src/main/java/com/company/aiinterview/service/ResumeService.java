package com.company.aiinterview.service;

import com.company.aiinterview.domain.entity.Resume;
import org.springframework.web.multipart.MultipartFile;

public interface ResumeService {
    Resume upload(MultipartFile file);

    Resume saveText(String text);

    Resume getById(String id);
}
