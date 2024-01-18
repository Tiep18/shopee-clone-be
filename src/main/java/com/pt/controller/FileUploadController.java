package com.pt.controller;

import com.pt.dto.res.ResponseDTO;
import com.pt.entity.Account;
import com.pt.repository.AccountRepository;
import com.pt.service.AccountService;
import com.pt.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin
public class FileUploadController {
    @Autowired
    private StorageService storageService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    // Upload avatar
    @PostMapping(value = "/user/upload-avatar-local", consumes = {"multipart/form-data"})
    public ResponseEntity<ResponseDTO> uploadAvatar(@RequestParam("image") MultipartFile image,
                                                    Authentication authentication) throws Exception {
        Account account = accountService.findByEmail(authentication.getName());
        String fileName = storageService.storageFile(image, "avatar");
        account.setAvatar(fileName);
        accountRepository.save(account);
        return ResponseEntity.ok(new ResponseDTO("Update avatar successfully", fileName));
    }

    // Upload product image
    @PostMapping(value = "/products/upload-images", consumes = {"multipart/form-data"})
    public String uploadProductImage(@RequestParam("file") MultipartFile file) throws IOException {
        return storageService.storageFile(file, "product");
    }

    @GetMapping("/images/{file}")
    public ResponseEntity<?> getFile(@PathVariable String file) throws IOException {
        byte[] bytes = storageService.readFileContent(file);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_JPEG).body(bytes);
    }
}
