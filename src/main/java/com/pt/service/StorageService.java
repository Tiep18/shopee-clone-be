package com.pt.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    String storageFile(MultipartFile file, String folder) throws IOException;


    public Stream<Path> loadAllFiles();

    public byte[] readFileContent(String filename) throws IOException;

    public void deleteFile();
}
