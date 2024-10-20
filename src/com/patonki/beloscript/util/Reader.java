package com.patonki.beloscript.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Reader {
    final private int BUFFER_SIZE = 1 << 16;
    private final DataInputStream din;
    private final byte[] buffer;
    private int bufferPointer, bytesRead;

    public Reader() {
        din = new DataInputStream(System.in);
        buffer = new byte[BUFFER_SIZE];
        bufferPointer = bytesRead = 0;
    }

    public Reader(String file_name) throws IOException {
        din = new DataInputStream(
                Files.newInputStream(Paths.get(file_name)));
        buffer = new byte[BUFFER_SIZE];
        bufferPointer = bytesRead = 0;
    }


    public String nextLine() throws IOException {
        byte[] buf = new byte[128]; // line length
        int cnt = 0, c;
        while ((c = read()) != -1) {
            if (c == '\n' || c == 13) {
                if (cnt != 0) {
                    break;
                } else {
                    continue;
                }
            }
            buf[cnt++] = (byte) c;
        }
        return new String(buf, 0, cnt);
    }

    public int nextInt() throws IOException {
        int ret = 0;
        byte c = read();
        while (c <= ' ') {
            c = read();
        }
        boolean neg = (c == '-');
        if (neg)
            c = read();
        do {
            ret = ret * 10 + c - '0';
        } while ((c = read()) >= '0' && c <= '9');
        bufferPointer--;
        if (neg)
            return -ret;
        return ret;
    }
    public double nextDouble() throws IOException {
        return nextDoubleBase();
    }
    private double nextDoubleBase() throws IOException{
        double ret = 0, div = 1;
        byte c = read();
        while (c <= ' ')
            c = read();
        boolean neg = (c == '-');
        if (neg)
            c = read();

        do {
            ret = ret * 10 + c - '0';
        } while ((c = read()) >= '0' && c <= '9');

        if (c == '.') {
            while ((c = read()) >= '0' && c <= '9') {
                ret += (c - '0') / (div *= 10);
            }
        }

        if (neg)
            return -ret;
        return ret;
    }

    private void fillBuffer() throws IOException {
        bytesRead = din.read(buffer, bufferPointer = 0,
                BUFFER_SIZE);
        if (bytesRead == -1)
            buffer[0] = -1;
    }
    public char nextChar() throws IOException {
        char c = (char) read();
        if (c <= ' ') c = (char) read();
        return c;
    }

    private byte read() throws IOException {
        if (bufferPointer == bytesRead)
            fillBuffer();
        return buffer[bufferPointer++];
    }

    public void close() throws IOException {
        din.close();
    }
}
