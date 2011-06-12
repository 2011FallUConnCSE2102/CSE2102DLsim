package dlsim.DLSim.xml;


import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Element
 * @author Kazuhiko Arase
 */
public class Element {

    private static final int TYPE_EMPTY = 0;
    private static final int TYPE_START = 1;
    private static final int TYPE_END   = 2;

    int       type;
    String    name;
    Hashtable attr;

    Element parent;
    Vector children;

    public Element(String name) {
        this(name, true);
    }

    public Element(String name, boolean empty) {
        this(name, empty? TYPE_EMPTY : TYPE_START);
    }

    private Element(String name, int type) {
        this.name = name;
        this.type = type;
    }

    Element(ParseReader reader) throws IOException {

        // init.
        type = TYPE_START;

        char ch;

        ch = reader.readChar();
        if (ch != '<') throw new IOException();

        reader.mark(1);
        ch = reader.readChar();
        if (ch == '/') {
            type = TYPE_END;
        } else {
            reader.reset();
        }

        reader.skipWhitespace();
        name = reader.readName();

        while(true) {

            reader.skipWhitespace();

            reader.mark(1);
            ch = reader.readChar();
            if (ch == '>') {
                break;
            } else if (ch == '/') {
                type = TYPE_EMPTY;
                ch = reader.readChar();
                if (ch == '>') {
                    break;
                } else {
                    throw new IOException();
                }
            }
            reader.reset();

            reader.skipWhitespace();
            String key = reader.readName();

            reader.skipWhitespace();
            ch = reader.readChar();
            if (ch != '=') throw new IOException();

            reader.skipWhitespace();
            String value = reader.readValue();

            setAttribute(key, value);
        }
    }

    public Element getEnd() {
        return new Element(name, TYPE_END);
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public boolean isEmpty() {
        return type == TYPE_EMPTY;
    }

    public boolean isEndOf(Element e) {
        return type == TYPE_END && name.equals(e.name);
    }

    public void setAttribute(String name, String value) {
//	if (name == null) throw new NullPointerException();
        if (attr == null) attr = new Hashtable();
        attr.put(name, value);
    }

    public String getAttribute(String name) {
        return getAttribute(name, null);
    }

    public String getAttribute(String name, String defaultValue) {
        String value = (attr != null)? (String)attr.get(name) : null;
        return (value != null)? value : defaultValue;
    }

    public void addElement(Element e) {
        if (children == null) children = new Vector();
        if (e.parent != null) e.parent.removeElement(e);
        children.addElement(e);
        e.parent = this;
    }

    public void removeElement(Element e) {
        if (children != null && children.contains(e) ) {
            children.removeElement(e);
            e.parent = null;
        }
    }

    public Element getElementAt(int index) {
        return (Element)children.elementAt(index);
    }

    public int getElementCount() {
        return (children != null)? children.size() : 0;
    }

    public String toString() {

        StringBuffer s = new StringBuffer();

        s.append('<');
        if (type == TYPE_END) s.append('/');
        s.append(name);

        if (attr != null) {

            Enumeration keys = attr.keys();

            while(keys.hasMoreElements() ) {

                String key   = (String)keys.nextElement();
                String value = (String)attr.get(key);

                char quot = 0;

                if (value.indexOf('\"') == -1) {
                    quot = '\"';
                } else if (value.indexOf('\'') == -1) {
                    quot = '\'';
                } else {
                    continue;
                }

                s.append(' ');
                s.append(key);
                s.append('=');
                s.append(quot);
                s.append(value);
                s.append(quot);
            }
        }
        if (type == TYPE_EMPTY) s.append('/');
        s.append('>');
        return s.toString();
    }

    public void write(ParseWriter writer) throws IOException {
        writer.write(toString() );
    }


}

