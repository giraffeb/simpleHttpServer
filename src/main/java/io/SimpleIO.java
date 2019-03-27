package io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class SimpleIO {


    public byte[] readByteArray(SocketChannel socketChannel, ByteBuffer byteBuffer){

        int readCount = 0;
        byte[] resultByteArray = null;

        try{
            readCount = socketChannel.read(byteBuffer);

            if(readCount < 0){
                socketChannel.close();
            }else if(readCount == 0){

            }else{
                byteBuffer.flip();

                resultByteArray = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
                byteBuffer.get(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());

                byteBuffer.compact();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultByteArray;
    }



    public void sendByteArray(SocketChannel socketChannel, ByteBuffer byteBuffer, byte[] byteArray){
        System.out.println("WRITE bytes len : "+byteArray.length);
        if(byteArray.length == 0){
            return;
        }

        byteBuffer.put(byteArray);
        byteBuffer.flip();

        try {
            socketChannel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
