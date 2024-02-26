package com.pt.utils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jakarta.annotation.PostConstruct;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Service
public class AmazonClient {
    private AmazonS3 s3client;
    @Value("${aws.s3.endpoint.url}")
    private String endpointUrl;
    @Value("${aws.s3.bucket.name}")
    private String bucketName;
    @Value("${aws.access.key}")
    private String accessKey;
    @Value("${aws.secret.access.key}")
    private String secretKey;

    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.s3client = new AmazonS3Client(credentials);
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private void uploadFileTos3bucket(String fileName, File file, String folder) {
        s3client.putObject(new PutObjectRequest(bucketName + "/" + folder, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    private String generateFileName(MultipartFile multiPart) {
        String fileExtension = FilenameUtils.getExtension(multiPart.getOriginalFilename());
        return UUID.randomUUID().toString() + "." + fileExtension;
    }

    private boolean isImageFile(MultipartFile file) {
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        assert fileExtension != null;
        return Arrays.asList(new String[]{"png", "jpg", "jpeg", "bmp"}).contains(fileExtension.trim().toLowerCase());
    }

    public String uploadFile(MultipartFile multipartFile, String folder) {

        if (multipartFile.isEmpty()) throw new IllegalArgumentException("File must not be empty");
        if (!isImageFile(multipartFile)) throw new IllegalArgumentException("File must be an image file");

        // check file size
        float fileSize = multipartFile.getSize() / 1_000_000f;
        if (fileSize > 5f) throw new IllegalArgumentException("File size must be less than or equal to 5Mb");

        String fileUrl = "";
        try {
            File file = convertMultiPartToFile(multipartFile);
            String fileName = generateFileName(multipartFile);
            fileUrl = endpointUrl + "/" + bucketName + "/" + folder + "/" + fileName;
            uploadFileTos3bucket(fileName, file, folder);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileUrl;
    }
}
