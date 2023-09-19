package io.github.lunasaw;

import io.github.lunasaw.dto.DownloadFileInfoDto;
import io.github.lunasaw.dto.FileInfoDto;
import io.github.lunasaw.dto.UploadFileInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.*;


@Service
@Slf4j
public class FileService {

    /**
     * 编码
     */
    private static final String UTF_8 = "utf-8";

    /**
     * 上传文件路径
     */
    @Value("${file.upload-path}")
    private String uploadPath;


    /**
     * 下载指定文件
     */
    @Value("${file.download-file}")
    private String downloadFile;

    /**
     * 下载地址
     */
    @Value("${file.download-path}")
    private String downloadPath;


    /**
     * 分片下载每一片大小为5M
     */
    private static final Long PER_SLICE = 1024 * 1024 * 5L;

    RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();


    /**
     * 定义分片下载线程池
     */
    private final ExecutorService executorService = new ThreadPoolExecutor(4, 10, 1000, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100), Thread::new, handler);


    /**
     * final string
     */
    private static final String RANGE = "Range";


    /**
     * 上传文件
     */
    public void upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //获取ServletFileUpload
        ServletFileUpload servletFileUpload = getServletFileUpload();
        List<FileItem> items = servletFileUpload.parseRequest(new ServletRequestContext(request));
        //获取文件信息
        UploadFileInfoDto uploadFileInfoDto = getFileDataDto(items);
        //写临时文件
        writeTempFile(items, uploadFileInfoDto);
        //判断是否合并
        mergeFile(uploadFileInfoDto);
        //返回结果
        response.setCharacterEncoding(UTF_8);
        response.getWriter().write("上传成功");
    }

    private void mergeFile(UploadFileInfoDto uploadFileInfoDto) throws IOException, InterruptedException {
        Integer currentChunk = uploadFileInfoDto.getCurrentChunk();
        Integer chunks = uploadFileInfoDto.getChunks();
        //如果当前分片等于总分片那么合并文件
        if (currentChunk != null && chunks != null && currentChunk.equals(chunks - 1)) {
            File tempFile = new File(uploadPath, uploadFileInfoDto.getFileName());
            try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(tempFile))) {
                for (int i = 0; i < chunks; i++) {
                    File file = new File(uploadPath, i + "_" + uploadFileInfoDto.getFileName());
                    //并发情况 需要判断所有  因为可能最后一个分片传完，之前有的还没传完
                    while (!file.exists()) {
                        //不存在休眠100毫秒后在从新判断
                        Thread.sleep(100);
                    }
                    //分片存在  读入数组中
                    byte[] bytes = FileUtils.readFileToByteArray(file);
                    os.write(bytes);
                    os.flush();
                    file.delete();
                }
                os.flush();
            }
        }

    }

    private void writeTempFile(List<FileItem> items, UploadFileInfoDto uploadFileInfoDto) throws Exception {
        //取出文件基本信息后
        for (FileItem item : items) {
            if (!item.isFormField()) {
                //有分片需要临时目录
                String tempFileName = uploadFileInfoDto.getFileName();
                if (StringUtils.isNotBlank(tempFileName)) {
                    if (uploadFileInfoDto.getCurrentChunk() != null) {
                        tempFileName = uploadFileInfoDto.getCurrentChunk() + "_" + uploadFileInfoDto.getFileName();
                    }
                    //判断文件是否存在
                    File tempFile = new File(uploadPath, tempFileName);
                    //断点续传  判断文件是否存在，若存在则不传
                    if (!tempFile.exists()) {
                        item.write(tempFile);
                    }
                }
            }
        }
    }

    private UploadFileInfoDto getFileDataDto(List<FileItem> items) throws IOException {

        UploadFileInfoDto rs = new UploadFileInfoDto();
        for (FileItem item : items) {
            if (item.isFormField()) {
                //获取分片数赋值给遍量
                if ("chunk".equals(item.getFieldName())) {
                    rs.setCurrentChunk(Integer.parseInt(item.getString(UTF_8)));
                }
                if ("chunks".equals(item.getFieldName())) {
                    rs.setChunks(Integer.parseInt(item.getString(UTF_8)));
                }
                if ("name".equals(item.getFieldName())) {
                    rs.setFileName(item.getString(UTF_8));
                }
            }
        }
        return rs;
    }


    /**
     * 获取ServletFileUpload： High level API for processing file uploads
     */
    private ServletFileUpload getServletFileUpload() {
        //设置缓冲区大小  先读到内存里在从内存写
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(1024);
        factory.setRepository(new File(uploadPath));
        //解析
        ServletFileUpload upload = new ServletFileUpload(factory);
        //设置单个大小与最大大小
        upload.setFileSizeMax(5 * 1024 * 1024 * 1024L);
        upload.setSizeMax(100 * 1024 * 1024 * 1024L);
        return upload;

    }


    public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {

        //获取文件
        File file = new File(downloadFile);
        //获取下载文件信息
        DownloadFileInfoDto downloadFileInfoDto = getDownloadFileInfoDto(file.length(), request, response);
        //设置响应头
        setResponse(response, file.getName(), downloadFileInfoDto);
        //下载文件
        try (InputStream is = new BufferedInputStream(Files.newInputStream(file.toPath()));
             OutputStream os = new BufferedOutputStream(response.getOutputStream())) {
            //跳过已经读取文件
            is.skip(downloadFileInfoDto.getPos());
            byte[] buffer = new byte[1024];
            long sum = 0;
            //读取
            while (sum < downloadFileInfoDto.getRangeLength()) {
                int length = is.read(buffer, 0, (downloadFileInfoDto.getRangeLength() - sum) <= buffer.length ? (int) (downloadFileInfoDto.getRangeLength() - sum) : buffer.length);
                sum = sum + length;
                os.write(buffer, 0, length);
            }
        }

    }


    /**
     * 有两个map，我要去判断里面相同键的值一致不一致，除了双重for循环，有没有别的好办法
     */

    private DownloadFileInfoDto getDownloadFileInfoDto(long fSize, HttpServletRequest request, HttpServletResponse response) {
        long pos = 0;
        long last = fSize - 1;
        //前端需要分片下载
        if (request.getHeader(RANGE) != null) {
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            String numRange = request.getHeader(RANGE).replace("bytes=", "");
            String[] strRange = numRange.split("-");
            if (strRange.length == 2) {
                pos = Long.parseLong(strRange[0].trim());
                last = Long.parseLong(strRange[1].trim());
                //若结束字节超出文件大小 取文件大小
                if (last > fSize - 1) {
                    last = fSize - 1;
                }
            } else {
                //若只给一个长度  开始位置一直到结束
                pos = Long.parseLong(numRange.replace("-", "").trim());
            }
        }
        long rangeLength = last - pos + 1;
        String contentRange = "bytes " + pos + "-" + last + "/" + fSize;
        return new DownloadFileInfoDto(fSize, pos, last, rangeLength, contentRange);
    }


    /**
     * 设置响应头
     */
    private void setResponse(HttpServletResponse response, String fileName, DownloadFileInfoDto downloadFileInfoDto) throws UnsupportedEncodingException {
        response.setCharacterEncoding(UTF_8);
        response.setContentType("application/x-download");
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, UTF_8));
        //支持分片下载
        response.setHeader("Accept-Range", "bytes");
        response.setHeader("fSize", String.valueOf(downloadFileInfoDto.getFSize()));
        response.setHeader("fName", URLEncoder.encode(fileName, UTF_8));
        //range响应头
        response.setHeader("Content-Range", downloadFileInfoDto.getContentRange());
        response.setHeader("Content-Length", String.valueOf(downloadFileInfoDto.getRangeLength()));
    }

    /**
     * 分片下载
     *
     * @param start 分片起始位置
     * @param end   分片结束位置
     * @param page  第几个分片, page=-1时是探测下载
     */
    private FileInfoDto sliceDownload(long start, long end, long page, String fName) throws IOException {

        //断点下载
        File file = new File(downloadPath, page + "-" + fName);
        //如果当前文件已经存在 并且不是探测任务 并且文件的长度等于分片的大小 那么不用下载当前文件
        if (file.exists() && page != -1 && file.length() == PER_SLICE) {
            return null;
        }
        //创建HttpClient
        HttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:9000/download");
        httpGet.setHeader(RANGE, "bytes=" + start + "-" + end);
        ClassicHttpResponse httpResponse = client.execute(httpGet, response -> response);
        String fSize = httpResponse.getFirstHeader("fSize").getValue();
        fName = URLDecoder.decode(httpResponse.getFirstHeader("fName").getValue(), UTF_8);
        HttpEntity entity = httpResponse.getEntity();
        //下载
        try (InputStream is = entity.getContent();
             FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int ch;
            while ((ch = is.read(buffer)) != -1) {
                fos.write(buffer, 0, ch);
            }
            fos.flush();
        }
        //判断是否是最后一个分片，如果是那么合并
        if (end - Long.parseLong(fSize) > 0) {
            mergeFile(fName, page);
        }

        return new FileInfoDto(Long.parseLong(fSize), fName);

    }

    private void mergeFile(String fName, long page) throws IOException {

        File file = new File(downloadPath, fName);

        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file))) {
            for (int i = 0; i <= page; i++) {
                File tempFile = new File(downloadPath, i + "-" + fName);
                //文件不存在或文件没写完
                while (!tempFile.exists() || (i != page && tempFile.length() < PER_SLICE)) {
                    Thread.sleep(100);
                }

                byte[] bytes = FileUtils.readFileToByteArray(tempFile);
                os.write(bytes);
                os.flush();
                tempFile.delete();
            }
            //删除探测文件
            File f = new File(downloadPath, "-1" + "-null");
            if (f.exists()) {
                f.delete();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void sliceDownload() throws IOException {

        //探测下载，获取文件相关信息
        FileInfoDto fileInfoDto = sliceDownload(1, 10, -1, null);
        //如果不为空，执行分片下载
        if (fileInfoDto != null) {
            //计算有多少分片
            long pages = fileInfoDto.getFileSize() / PER_SLICE;
            //适配最后一个分片
            for (long i = 0; i <= pages; i++) {
                long start = i * PER_SLICE;
                long end = (i + 1) * PER_SLICE - 1;
                executorService.execute(new SliceDownloadRunnable(start, end, i, fileInfoDto.getFileName()));
            }
        }
    }


    private class SliceDownloadRunnable implements Runnable {

        private final long start;
        private final long end;
        private final long page;
        private final String fName;

        private SliceDownloadRunnable(long start, long end, long page, String fName) {
            this.start = start;
            this.end = end;
            this.page = page;
            this.fName = fName;
        }


        @Override
        public void run() {
            try {
                sliceDownload(start, end, page, fName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
