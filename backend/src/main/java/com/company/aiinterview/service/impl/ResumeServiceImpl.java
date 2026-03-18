package com.company.aiinterview.service.impl;

import com.company.aiinterview.common.ErrorCode;
import com.company.aiinterview.domain.entity.Resume;
import com.company.aiinterview.exception.ApiException;
import com.company.aiinterview.repository.InMemoryStore;
import com.company.aiinterview.service.ResumeService;
import com.company.aiinterview.util.IdUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;

@Service
public class ResumeServiceImpl implements ResumeService {
    private final InMemoryStore store;

    public ResumeServiceImpl(InMemoryStore store) {
        this.store = store;
    }

    @Override
    public Resume upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "file required");
        }
        String text = extractText(file);
        Resume resume = new Resume();
        resume.setId(IdUtil.newId());
        resume.setFileName(file.getOriginalFilename());
        resume.setText(text);
        resume.setCreatedAt(Instant.now());
        store.getResumes().put(resume.getId(), resume);
        return resume;
    }

    @Override
    public Resume saveText(String text) {
        Resume resume = new Resume();
        resume.setId(IdUtil.newId());
        resume.setFileName("text-input");
        resume.setText(text);
        resume.setCreatedAt(Instant.now());
        store.getResumes().put(resume.getId(), resume);
        return resume;
    }

    @Override
    public Resume getById(String id) {
        Resume resume = store.getResumes().get(id);
        if (resume == null) {
            throw new ApiException(ErrorCode.NOT_FOUND, "resume not found");
        }
        return resume;
    }

    private String extractText(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name != null && name.toLowerCase().endsWith(".pdf")) {
            try (PDDocument doc = PDDocument.load(file.getInputStream())) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(doc);
            } catch (IOException e) {
                throw new ApiException(ErrorCode.INTERNAL_ERROR, "pdf parse failed");
            }
        }
        try {
            return new String(file.getBytes());
        } catch (IOException e) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR, "file read failed");
        }
    }
}
