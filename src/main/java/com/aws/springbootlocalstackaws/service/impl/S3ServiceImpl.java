package com.aws.springbootlocalstackaws.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import javax.validation.ValidationException;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.HeadBucketRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import com.amazonaws.waiters.WaiterParameters;
import com.aws.springbootlocalstackaws.model.DownloadedResource;
import com.aws.springbootlocalstackaws.service.AwsS3Service;
import com.aws.springbootlocalstackaws.util.Utility;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class S3ServiceImpl implements AwsS3Service {

    private static final String FILE_EXTENSION = "fileExtension";
    private final AmazonS3 amazonS3;

    @Override
    public List<S3ObjectSummary> getAllDocumentsFromBuckets(String bucketName) {

        this.validateIfBucketExist(bucketName);

        log.info("Retrieving object summaries for bucket '{}'", bucketName);
        ObjectListing objectListing = amazonS3.listObjects(bucketName);
        return objectListing.getObjectSummaries();
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

    @Override
    public void uploadDocument(MultipartFile file, String bucketName) throws IOException {
        try {

            this.validateIfBucketExist(bucketName);

            String tempFileName = UUID.randomUUID() + file.getName();
            File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + tempFileName);

            file.transferTo(tempFile);
            amazonS3.putObject(bucketName, UUID.randomUUID() + file.getName(), tempFile);
            tempFile.deleteOnExit();
            log.info("File uploaded: " + file.getName());
            log.info("===================== Upload File - Done! =====================");

        } catch (AmazonServiceException serviceException) {
            log.info("Caught an AmazonServiceException");
            showMessageFromException(serviceException);
            throw serviceException;

        } catch (AmazonClientException clientException) {
            log.info("Caught an AmazonClientException: ");
            log.info("Error Message: " + clientException.getMessage());
            throw clientException;
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.error("File not uploaded: " + file.getName());
    }

    @Override
    public void downloadFile(String keyName, String bucketName) {
        try {

            log.info("===================== Downloading an Object! =====================");
            S3Object s3object = amazonS3.getObject(new GetObjectRequest(bucketName, keyName));

            log.info("===================== Content-Type! =====================");
            log.info(s3object.getObjectMetadata().getContentType());

            Utility.displayText(s3object.getObjectContent());
            log.info("===================== Import File - Done! =====================");

        } catch (AmazonServiceException serviceException) {
            log.info("Caught an AmazonServiceException");
            showMessageFromException(serviceException);
        } catch (AmazonClientException ace) {
            log.info("Caught an AmazonClientException: ");
            log.info("Error Message: " + ace.getMessage());
        } catch (IOException ioe) {
            log.info("IOE Error Message: " + ioe.getMessage());
        }
    }

    @Override
    // @Async annotation ensures that the method is executed in a different background thread
    // but not consume the main thread.
    @Async
    public byte[] downloadFileV2(String keyName, String bucketName) {

        byte[] content = null;
        log.info("Downloading an object with key= " + keyName);
        final S3Object s3Object = amazonS3.getObject(bucketName, keyName);
        final S3ObjectInputStream stream = s3Object.getObjectContent();
        try {
            content = IOUtils.toByteArray(stream);
            log.info("File downloaded successfully.");
            s3Object.close();
        } catch(final IOException ex) {
            log.info("IO Error Message= " + ex.getMessage());
        }
        return content;
    }

    @Override
    public DownloadedResource download(String keyName, String bucketName) {
        S3Object s3Object = amazonS3.getObject(bucketName, keyName);
        String filename = keyName + "." + s3Object
                .getObjectMetadata()
                .getUserMetadata()
                .get(FILE_EXTENSION);
        Long contentLength = s3Object.getObjectMetadata().getContentLength();

        return DownloadedResource.builder()
                .id(keyName)
                .fileName(filename)
                .contentLength(contentLength)
                .inputStream(s3Object.getObjectContent())
                .build();
    }

    private void showMessageFromException(AmazonServiceException serviceException) {
        log.info("Error Message:    " + serviceException.getMessage());
        log.info("HTTP Status Code: " + serviceException.getStatusCode());
        log.info("AWS Error Code:   " + serviceException.getErrorCode());
        log.info("Error Type:       " + serviceException.getErrorType());
        log.info("Request ID:       " + serviceException.getRequestId());
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
