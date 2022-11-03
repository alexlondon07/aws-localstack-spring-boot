package com.aws.springbootlocalstackaws.controller;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.validation.ValidationException;

import org.apache.logging.log4j.util.Strings;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.aws.springbootlocalstackaws.model.DownloadedResource;
import com.aws.springbootlocalstackaws.service.AwsS3Service;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/documents")
@RequiredArgsConstructor
public class S3BucketStorageController {

    private final AwsS3Service s3Service;

    @GetMapping
    public List<S3ObjectSummary> getAllDocumentsFromBuckets(@Param("bucketName") String bucketName) {

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


    @GetMapping("/download-file")
    public ResponseEntity<Resource> downloadFile(@Param("keyName") String keyName,
                                                 @Param("bucketName") String bucketName) {

        this.validateBucketName(bucketName);

        DownloadedResource downloadedResource = s3Service.download(keyName, bucketName);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + downloadedResource.getFileName())
                .contentLength(downloadedResource.getContentLength()).contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(downloadedResource.getInputStream()));
    }


    @GetMapping(value = "/download-fileV2")
    public ResponseEntity<ByteArrayResource> downloadFileV2(@Param(value = "keyName") final String keyName,
                                                            @Param("bucketName") String bucketName) {
        final byte[] data = s3Service.downloadFileV2(keyName, bucketName);
        final ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + keyName.concat(".pdf") + "\"")
                .body(resource);
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
