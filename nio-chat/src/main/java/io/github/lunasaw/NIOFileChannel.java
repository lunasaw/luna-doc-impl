package io.github.lunasaw;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannel {

    public static void main(String[] args) throws IOException {
        String str = "Hello,Java菜鸟程序员";
        // 创建一个输出流
        FileOutputStream fileOutputStream = new FileOutputStream("hello.txt");
        // 获取通道
        FileChannel channel = fileOutputStream.getChannel();
        // 创建缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        // 写入byteBuffer
        byteBuffer.put(str.getBytes());
        // 切换模式
        byteBuffer.flip();
        // 写入通道
        channel.write(byteBuffer);
        // 关闭
        channel.close();
        fileOutputStream.close();
    }
}