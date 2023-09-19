package io.github.lunasaw;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/file")
public class FileDwonloadController {

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam("path") String path,
                                               @RequestHeader(value = "Range", required = false) String rangeHeader,
                                               HttpServletRequest request,
                                               HttpServletResponse response) throws IOException {

        File file = new File(path);
        InputStream inputStream = new FileInputStream(file);

        long fileSize = file.length();
        long start = 0;
        long end = fileSize - 1;

        if (rangeHeader != null) {
            String[] range = rangeHeader.split("=")[1].split("-");
            start = Long.parseLong(range[0]);
            if (range.length > 1) {
                end = Long.parseLong(range[1]);
            }
        }

        long contentLength = end - start + 1;

        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
        response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileSize);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setStatus(HttpStatus.PARTIAL_CONTENT.value());

        inputStream.skip(start);

        byte[] buffer = new byte[1024];
        int bytesRead;
        int totalBytesRead = 0;

        while ((bytesRead = inputStream.read(buffer)) != -1 && totalBytesRead < contentLength) {
            int bytesToWrite = (int) Math.min(bytesRead, contentLength - totalBytesRead);
            response.getOutputStream().write(buffer, 0, bytesToWrite);
            totalBytesRead += bytesToWrite;
        }

        inputStream.close();

        return ResponseEntity.ok().build();
    }
}
