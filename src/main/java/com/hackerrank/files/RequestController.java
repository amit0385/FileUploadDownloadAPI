package com.hackerrank.files;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
public class RequestController {
    public static final String UPLOAD_DIR = "uploads/";
    private Path fileLocation;
    @PostMapping("/uploader")
    public ResponseEntity uploader(@RequestParam("fileName") String fileName, @RequestParam("file") MultipartFile file) {
        String filename = StringUtils.cleanPath(fileName);
        return saveFile(filename, file);
        //return null;
    }

    @GetMapping("/downloader")
    public ResponseEntity downloader(@RequestParam String fileName) {

        return downloadFile(fileName);
    }

    private ResponseEntity<Resource> downloadFile(String fileName) {
        try {
            this.fileLocation= Paths.get(UPLOAD_DIR+fileName).toAbsolutePath().normalize();
            //Path filePath = this.fileLocation.resolve(UPLOAD_DIR).normalize();
            Resource resource = new UrlResource(this.fileLocation.toUri());
            if (resource.exists()) {
                return new ResponseEntity<Resource>(resource, HttpStatus.OK);
            } else {
                return (ResponseEntity) ResponseEntity.status(HttpStatus.NOT_FOUND);
            }
        } catch (MalformedURLException exception) {
            return (ResponseEntity) ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Can be written in separte Util file, But for now using here
    private ResponseEntity saveFile(String filename, MultipartFile file) {

        try {
            this.fileLocation= Paths.get(UPLOAD_DIR+filename).toAbsolutePath().normalize();
            if (filename.contains("..")) {
                return new ResponseEntity<>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            //Path uploadLoaction = this.fileLocation.resolve(UPLOAD_DIR);
            Files.copy(file.getInputStream(), fileLocation, StandardCopyOption.REPLACE_EXISTING);
            return new ResponseEntity<>("Create", HttpStatus.CREATED);
        } catch (IOException exception) {
            return new ResponseEntity<>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
