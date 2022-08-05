package com.aws.springbootlocalstackaws.controller;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.validation.ValidationException;

import org.apache.logging.log4j.util.Strings;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.Bucket;
import com.aws.springbootlocalstackaws.service.AwsS3Service;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/documents")
@RequiredArgsConstructor
public class S3BucketStorageController {

    private final AwsS3Service s3Service;

    @GetMapping
    public List<String> getAllDocumentsFromBuckets(@Param("bucketName") String bucketName) {

        this.validateBucketName(bucketName);

        return s3Service.getAllDocumentsFromBuckets(bucketName);
    }

    @GetMapping("/buckets-list")
    public List<Bucket> getAllBuckets() {
        return s3Service.getAllBuckets();
    }

    @PostMapping("/file-upload")
    public ResponseEntity saveDocument(@RequestParam(value = "file") MultipartFile file,
                                       @Param("bucketName") String bucketName) throws IOException {
        if (file.isEmpty()) {
            throw new ValidationException("file is required");
        }
        s3Service.uploadDocument(file, bucketName);

        return ResponseEntity.created(URI.create("SOME-LOCATION")).build();
    }

    @PostMapping("/create-bucket")
    public ResponseEntity<String> createBucket(@Param("bucketName") String bucketName) {

        this.validateBucketName(bucketName);

        s3Service.createBucket(bucketName);

        return ResponseEntity.ok(bucketName);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteBucket(@Param("bucketName") String bucketName) {

        this.validateBucketName(bucketName);

        s3Service.deleteBucketByName(bucketName);

        return ResponseEntity.ok(bucketName);
    }

    private void validateBucketName(String bucketName) {
        if (Strings.isEmpty(bucketName)) {
            throw new ValidationException("Bucket name is required");
        }
    }
}
