package io.github.lunasaw.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @author luna
 */
public class GroupChatServer {

    // 定义属性
    private Selector            selector;
    private ServerSocketChannel listenChannel;
    private static final int    PORT = 6667;

    public GroupChatServer() {
        try {
            // 获得选择器
            selector = Selector.open();
            // listenChannel
            listenChannel = ServerSocketChannel.open();
            // 绑定端口
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            // 设置非阻塞模式
            listenChannel.configureBlocking(false);
            // 将该listenChannel注册到Selector
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // 创建一个服务器对象
        GroupChatServer groupChatServer = new GroupChatServer();
        // 监听
        groupChatServer.listen();
    }

    /**
     * 监听
     */
    public void listen() {
        try {
            // 如果返回的>0，表示已经获取到关注的事件
            while (selector.select() > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                // 判断是否有事件
                while (iterator.hasNext()) {
                    // 获得事件
                    SelectionKey key = iterator.next();
                    // 如果是OP_ACCEPT，表示有新的客户端连接
                    if (key.isAcceptable()) {
                        SocketChannel socketChannel = listenChannel.accept();
                        // 设置为非阻塞
                        socketChannel.configureBlocking(false);
                        // 注册到Selector
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        System.out.println("获取到一个客户端连接 : " + socketChannel.getRemoteAddress() + " 上线!");
                    } else if (key.isReadable()) {
                        // 如果是读事件,就读取数据
                        readData(key);
                    }
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * 读取客户端消息
     */
    private void readData(SelectionKey key) {
        SocketChannel channel = null;
        try {
            // 得到channel
            channel = (SocketChannel)key.channel();
            // 创建buffer
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            if (channel.read(buffer) != -1) {
                String msg = new String(buffer.array());
                System.out.println(msg);
                // 转发消息给其它客户端(排除自己)
                sendInfoOtherClients(msg, channel);
            }
        } catch (Exception e) {
            try {
                System.out.println(channel.getRemoteAddress() + " 下线了!");
                // 关闭通道
                key.cancel();
                channel.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } finally {

        }
    }

    /**
     * 转发消息给其它客户端(排除自己)
     */

    private void sendInfoOtherClients(String msg, SocketChannel self) throws IOException {
        // 服务器转发消息
        System.out.println("服务器转发消息中...");
        // 遍历所有注册到selector的socketChannel并排除自身
        for (SelectionKey key : selector.keys()) {
            // 反向获取通道
            Channel targetChannel = key.channel();
            // 排除自身
            if (targetChannel instanceof SocketChannel && targetChannel != self) {
                // 转型
                SocketChannel dest = (SocketChannel)targetChannel;
                // 将msg存储到buffer中
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                // 将buffer中的数据写入通道
                dest.write(buffer);
            }
        }
    }

}