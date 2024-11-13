package io.ssafy.p.k11a405.backend.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import io.ssafy.p.k11a405.backend.exception.BusinessException;
import io.ssafy.p.k11a405.backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final String drawSrcField = "drawingSrc";

    private final StringRedisTemplate stringRedisTemplate;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    public void uploadAnswerDrawing(MultipartFile file, String userId) throws IOException {
        String userKey = "user:" + userId;
        // 실제로는 이미지를 저장하고, 그 경로를 가져와야 한다.
        String drawingSrc = uploadImage(file, "drawing/" + userId);

        stringRedisTemplate.opsForHash().put(userKey, drawSrcField, drawingSrc);
    }

    private String uploadImage(MultipartFile image, String path) {
        //image 파일 확장자 검사
        this.validateImageFileExtension(image.getOriginalFilename());
        return this.uploadToS3(image, path);
    }

    private void validateImageFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");

        //확장자가 존재하지 않음
        if (lastDotIndex == -1) throw new BusinessException(ErrorCode.S3Exception);

        String extention = fileName.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        //확장자가 유효하지 않음
        if (!allowedExtentionList.contains(extention)) throw new BusinessException(ErrorCode.S3Exception);
    }

    private String uploadToS3(MultipartFile image, String path) {
        String originalFilename = image.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String s3Filename = path + "/" + UUID.randomUUID().toString().substring(0,10) + originalFilename;
        String url = "";

        try {
            InputStream is = image.getInputStream();
            byte[] bytes = IOUtils.toByteArray(is);
            ObjectMetadata metadata = new ObjectMetadata(); //metadata 생성
            metadata.setContentType("image/" + extension);
            metadata.setContentLength(bytes.length);

            //S3에 요청할 때 사용할 byteInputStream 생성
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

            //S3로 putObject 할 때 사용할 요청 객체
            //생성자 : bucket 이름, 파일 명, byteInputStream, metadata
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, s3Filename, byteArrayInputStream, metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead);

            //실제로 S3에 이미지 데이터를 넣기
            amazonS3.putObject(putObjectRequest);
            url = amazonS3.getUrl(bucketName, s3Filename).toString();

            return url;
        } catch(Exception e) {
            throw new BusinessException(ErrorCode.S3Exception);
        }
    }
}
