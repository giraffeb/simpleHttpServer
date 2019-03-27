package logic;

import http.SimpleHttpParser;
import io.SimpleIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class GenericServer {

    public static final int BUFFER_SIZE = 1024 * 1024 * 2;

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private SelectionKey selectionKey;
    private int serverPostNumber;

    private SimpleIO simpleIO;
    private SimpleHttpParser simpleHttpParser;



    public GenericServer(int serverPostNumber) {
        this.serverPostNumber = serverPostNumber;
        init();
    }

    public void init(){
        try {
            this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel.configureBlocking(false);
            this.serverSocketChannel.bind(new InetSocketAddress(this.serverPostNumber));

            this.selector = Selector.open();
            this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);

            this.simpleIO = new SimpleIO();
            this.simpleHttpParser = new SimpleHttpParser();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        int selectCount = 0;
        Set<SelectionKey> selectionKeySets;
        Iterator<SelectionKey> selectionKeyIterator;

        SelectionKey selectedKey;


        while(true){
            try {
                selectCount = this.selector.selectNow();

                if(selectCount <= 0){
                    continue;
                }

                selectionKeySets = this.selector.selectedKeys();
                selectionKeyIterator = selectionKeySets.iterator();

                while(selectionKeyIterator.hasNext()){
                    selectedKey = selectionKeyIterator.next();

                    if(selectedKey.isAcceptable()){
                        System.out.println("ACCEPT");
                        SocketChannel socketChannel = ((ServerSocketChannel)selectedKey.channel()).accept();
                        socketChannel.configureBlocking(false);
                        ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);

                        socketChannel.register(this.selector, SelectionKey.OP_READ, byteBuffer);

                    }else if(selectedKey.isReadable()){
                        System.out.println("READABLE");
                        SocketChannel socketChannel = (SocketChannel)selectedKey.channel();
                        ByteBuffer byteBuffer = (ByteBuffer) selectedKey.attachment();

                        //TODO: this.simpleIO
                        byte[] message = this.simpleIO.readByteArray(socketChannel, byteBuffer);
                        //TODO: this.simepleHttpParser
                        String msg = this.simpleHttpParser.byteToString(message);

                        System.out.println(msg);

                        String content = "<html><head></head><body><h1>Hello new world</h1></body></html>";
                        String response = "HTTP/1.1 200 OK\n" +
                                "Content-Length: "+content.length()+"\n" +
                                "Content-Type: text/html\n" +
                                "\n" +
                                content +
                                "\n";

                        System.out.println(response);
                        this.simpleIO.sendByteArray(socketChannel, byteBuffer, response.getBytes());

                    }

                    selectionKeyIterator.remove();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public void destroy(){

    }
}
