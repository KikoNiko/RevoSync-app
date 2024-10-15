package com.kbc.kibi_coins.service;

import com.kbc.kibi_coins.model.Statement;
import com.kbc.kibi_coins.repository.StatementRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class StatementService {
    private final StatementRepository statementRepository;

    public StatementService(StatementRepository statementRepository) {
        this.statementRepository = statementRepository;
    }

    public boolean isFileProcessed(String checksum) {
        return statementRepository.existsByChecksum(checksum);
    }

    public String getFileChecksum(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            return DigestUtils.sha256Hex(inputStream);
        }
    }

    public void saveFileMetadata(String checksum) {
        Statement statement = new Statement();
        statement.setChecksum(checksum);
        statementRepository.save(statement);
    }
}
