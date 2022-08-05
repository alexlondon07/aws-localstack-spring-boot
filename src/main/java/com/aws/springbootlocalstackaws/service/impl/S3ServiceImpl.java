package com.aws.springbootlocalstackaws.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.ValidationException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.HeadBucketRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.waiters.WaiterParameters;
import com.aws.springbootlocalstackaws.service.AwsS3Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class S3ServiceImpl implements AwsS3Service {

    private final AmazonS3 amazonS3;

    @Override
    public List<String> getAllDocumentsFromBuckets(String bucketName) {

        this.validateIfBucketExist(bucketName);

        return amazonS3.listObjectsV2(bucketName).getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public List<Bucket> getAllBuckets() {
        return amazonS3.listBuckets();
    }

    @Override
    public void createBucket(String bucketName) {

        this.validateIfBucketIsAvailable(bucketName);
        
        amazonS3.createBucket(bucketName);
        log.info("Request to create " + bucketName + " sent");
    }

    @Override
    public void deleteBucketByName(String bucketName) {

        this.validateIfBucketExist(bucketName);

        amazonS3.deleteBucket(bucketName);

        // assure bucket is deleted
        amazonS3.waiters().bucketNotExists().run(new WaiterParameters<>(
                new HeadBucketRequest(bucketName)));

        log.info("Bucket " + bucketName + " is deleted");
    }


    public void uploadDocument_(MultipartFile file, String bucketName)
            throws IOException {

        String tempFileName = UUID.randomUUID() + file.getName();
        File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + tempFileName);

        file.transferTo(tempFile);
        amazonS3.putObject(bucketName, UUID.randomUUID() + file.getName(), tempFile);
        tempFile.deleteOnExit();
    }

    @Override
    public void uploadDocument(MultipartFile file, String bucketName) {
        try {

            this.validateIfBucketExist(bucketName);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            amazonS3.putObject(bucketName, file.getName(), file.getInputStream(), metadata);
            log.info("File uploaded: " + file.getName());

        } catch (IOException ioe) {
            log.error("IOException: " + ioe.getMessage());

        } catch (AmazonServiceException serviceException) {
            log.info("AmazonServiceException: " + serviceException.getMessage());
            throw serviceException;

        } catch (AmazonClientException clientException) {
            log.info("AmazonClientException Message: " + clientException.getMessage());
            throw clientException;
        }
        log.error("File not uploaded: " + file.getName());
    }

    private void validateIfBucketExist(String bucketName) {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            log.info("Bucket not exist, try again with different Bucket name");
            throw new NoSuchElementException("Bucket not exist");
        }
    }

    private void validateIfBucketIsAvailable(String bucketName) {
        if (amazonS3.doesBucketExistV2(bucketName)) {
            throw new ValidationException("Bucket name is not available."
                    + " Try again with a different Bucket name.");
        }
    }

}
