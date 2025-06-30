package com.example.shop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class FileService {

    public String uploadFile(String uploadPath, String originalFileName,
                             byte[] fileData) throws Exception {
        UUID uuid = UUID.randomUUID();

        //sampletest.jpg
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        log.info("originalFileName.substring : {}", extension);

        String savedFileName = uuid.toString()+ extension;

       String fileUploadFullUrl = uploadPath + "/" + savedFileName;

       FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
       fos.write(fileData);
       fos.close();

       return savedFileName;
    }

    public void deleteFile(String filePath) throws Exception {

        File deleteFile = new File(filePath);

        if(deleteFile.exists()) {
            deleteFile.delete();
            log.info("파일을 삭제하였습니다. : {}", filePath);
        }else{
            log.info("파일이 존재하지 않습니다. : {}", filePath);
        }
    }
}
