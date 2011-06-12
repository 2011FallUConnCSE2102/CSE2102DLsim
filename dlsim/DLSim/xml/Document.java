package dlsim.DLSim.xml;

/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */


import java.io.*;
import java.net.*;
import java.util.*;

/**
 * DMouseListener
 * @author Kazuhiko Arase
 */
public class Document {

    Element top;

    public Document(Element top) {
        this.top = top;
    }

    public Document(InputStream is) throws IOException {
        this(new ParseReader(
            new BufferedReader(new InputStreamReader(is) ) ) );
    }

    public Document(ParseReader reader) throws IOException {
        skip(reader);
        top = new Element(reader);
        readElements(top, reader);
    }

    /**
     * Called by constructor.
     */
    private static void readElements(Element parent, ParseReader reader) throws IOException {
        while (true) {
            skip(reader);
            Element e = new Element(reader);
            if (e.isEndOf(parent) ) break;
            parent.addElement(e);
            if (!e.isEmpty() ) {
                readElements(e, reader);
            }
        }
    }

    public void write(OutputStream os) throws IOException {
        write(new ParseWriter(new OutputStreamWriter(os) ) );
    }

    public void write(ParseWriter writer) throws IOException {
        writeElements(top, writer, 0);
        writer.flush();
    }

    public Element getElement() {
        return top;
    }

    private static void writeIndent(ParseWriter writer, int indent) throws IOException {
        for (int j = 0; j < indent; j++) writer.write("  ");
    }

    private static void writeElements(Element parent, ParseWriter writer, int indent) throws IOException {

        writeIndent(writer, indent);
        parent.write(writer);
        writer.newLine();

        int count = parent.getElementCount();
        for (int i = 0; i < count; i++) {
            Element e = parent.getElementAt(i);
            writeElements(e, writer, indent + 1);
        }

        if (!parent.isEmpty() ) {
            writeIndent(writer, indent);
            parent.getEnd().write(writer);
            writer.newLine();
        }
    }

    private static void skip(ParseReader reader) throws IOException {
        while (true) {
            reader.skipWhitespace();
            reader.mark(2);
            char ch;

            ch = reader.readChar();
            if (ch != '<') throw new IOException();

            ch = reader.readChar();
            if (ch == '/' || CharSet.isNameStart(ch) ) {
                reader.reset();
                break;
            } else {
                while (reader.readChar() != '>') {}
            }
        }
    }
}