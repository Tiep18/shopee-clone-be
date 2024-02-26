package com.pt.controller;

import com.pt.dto.res.ResponseDTO;
import com.pt.entity.Account;
import com.pt.repository.AccountRepository;
import com.pt.service.AccountService;
import com.pt.utils.AmazonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping
public class S3UploadFile {
    private AmazonClient amazonClient;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    S3UploadFile(AmazonClient amazonClient) {
        this.amazonClient = amazonClient;
    }

    @PostMapping("/user/upload-avatar")
    public ResponseEntity<ResponseDTO> uploadFile(@RequestParam(value = "image") MultipartFile file,
                                                  Authentication authentication) throws Exception {
        Account account = accountService.findByEmail(authentication.getName());
        String fileName = this.amazonClient.uploadFile(file, "avatar");
        account.setAvatar(fileName);
        accountRepository.save(account);
        return ResponseEntity.ok(new ResponseDTO("Update avatar successfully", fileName));
    }

//    @DeleteMapping("/deleteFile")
//    public String deleteFile(@RequestParam(value = "url") String fileUrl) {
//        return this.amazonClient.deleteFileFromS3Bucket(fileUrl);
//    }
}
