package io.github.lunasaw;

import java.nio.IntBuffer;

public class BufferTest {

    public static void main(String[] args) {
        // 同理对应的还有:ByteBuffer,IntBuffer,FloatBuffer,CharBuffer,ShortBuffer,DoubleBuffer,LongBuffer
        // 创建一个Buffer,大小为5
        IntBuffer buffer = IntBuffer.allocate(5);
        // 存放数据
        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.put(i);
        }
        // 切换成读模式. 读写切换
        buffer.flip();
        while (buffer.hasRemaining()) {
            System.out.println(buffer.get()); // 0 1 2 3 4
        }
    }

}