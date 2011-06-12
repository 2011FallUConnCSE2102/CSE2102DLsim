package dlsim.DLSim.xml;


import java.io.*;
import java.net.*;
import java.util.*;

public class ParseReader extends FilterReader {

    public ParseReader(Reader reader) {
        super(reader);
    }

    public char readChar() throws IOException {
        int ch = read();
        if (ch == -1) throw new EOFException();
        return (char)ch;
    }

    public void skipWhitespace() throws IOException {
        char ch;
        while(true) {
            mark(1);
            ch = readChar();
            if (!CharSet.isWhitespace(ch) ) {
                reset();
                break;
            }
        }
    }

    public String readName() throws IOException {

        char ch;

        mark(1);
        ch = readChar();
        if (!CharSet.isNameStart(ch) ) throw new IOException();

        StringBuffer s = new StringBuffer();
        s.append(ch);

        while(true) {
            mark(1);
            ch = readChar();
            if (!CharSet.isNamePart(ch) ) {
                reset();
                break;
            }
            s.append(ch);
        }
        return s.toString();
    }

    public String readValue() throws IOException {

        char ch;
        char quot;

        mark(1);
        ch = readChar();
        if (!CharSet.isQuot(ch) ) throw new IOException();

        StringBuffer s = new StringBuffer();
        quot = ch;

        while(true) {
            mark(1);
            ch = readChar();
            if (ch == quot) break;
            s.append(ch);
        }

        return s.toString();
    }
}

