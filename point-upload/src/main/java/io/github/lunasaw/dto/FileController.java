package io.github.lunasaw.dto;


import io.github.lunasaw.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController
public class FileController {


    @Autowired
    private FileService fileService;


    /**
     * 单个文件上传，支持断点续传
     */
    @PostMapping("/upload")
    public void upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        fileService.upload(request, response);
    }

    /**
     * 文件下载
     */
    @GetMapping("/download")
    public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
        fileService.download(request, response);
    }

    /**
     * 分片下载
     */
    @GetMapping("/slice-download")
    public String sliceDownload() throws IOException {
        fileService.sliceDownload();
        return "success";
    }

}
