package com.pt.service.implement;

import com.pt.exception.NotFoundException;
import com.pt.service.StorageService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class StorageServiceImpl implements StorageService {
    private final Path storageFolder = Paths.get("src/main/resources/static/upload");

    public StorageServiceImpl() {
        try {
            Files.createDirectories(storageFolder);
            System.out.println(storageFolder.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isImageFile(MultipartFile file) {
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        assert fileExtension != null;
        return Arrays.asList(new String[]{"png", "jpg", "jpeg", "bmp"}).contains(fileExtension.trim().toLowerCase());
    }

    @Override
    public String storageFile(MultipartFile file, String folder) throws IOException {
        if (file.isEmpty()) throw new IllegalArgumentException("File must not be empty");
        if (!isImageFile(file)) throw new IllegalArgumentException("File must be an image file");

        // check file size
        float fileSize = file.getSize() / 1_000_000f;
        if (fileSize > 5f) throw new IllegalArgumentException("File size must be less than or equal to 5Mb");

        // rename file
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        String generatedFilename = UUID.randomUUID().toString() + "." + fileExtension;

        // create file path
        Path folderPath = this.storageFolder.resolve(Paths.get(folder));
        if (!Files.exists(folderPath) && !Files.isDirectory(folderPath)) {
            Files.createDirectory(folderPath);
        }
        Path destinationFilePath = folderPath.resolve(Paths.get(generatedFilename))
                .normalize().toAbsolutePath();
        if (!destinationFilePath.getParent().equals(folderPath.toAbsolutePath())) {
            throw new RuntimeException("Cannot store file outside current directory");
        }

        // store file
        InputStream inputStream = file.getInputStream();
        Files.copy(inputStream, destinationFilePath);

        return generatedFilename;
    }

    @Override
    public Stream<Path> loadAllFiles() {
        return null;
    }

    @Override
    public byte[] readFileContent(String filename) throws IOException {
        Optional<Path> foundFiles;
        Stream<Path> walkStream = Files.walk(storageFolder);
        foundFiles = walkStream.filter(p -> p.toFile().isFile())
                .filter(p -> p.toString().endsWith(filename))
                .findFirst();
        if (foundFiles.isPresent()) {
            Path file = foundFiles.get();
            Resource resource = new UrlResource(file.toUri());
            if (!resource.isFile() || !resource.isReadable())
                throw new MalformedURLException("Cannot read file " + filename);
            return StreamUtils.copyToByteArray(resource.getInputStream());
        }
        throw new NotFoundException("File not found");
    }

    @Override
    public void deleteFile() {

    }
}
