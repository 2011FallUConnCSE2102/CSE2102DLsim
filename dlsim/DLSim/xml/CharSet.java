package dlsim.DLSim.xml;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * CharSet
 * @author Kazuhiko Arase
 */
public class CharSet {

    private CharSet() {
    }

    public static boolean isQuot(char ch) {
        return (ch == '\'' || ch == '\"');
    }

    public static boolean isNameStart(char ch) {
        return (ch == '_' || ch == ':' || isLetter(ch) );
    }

    public static boolean isNamePart(char ch) {
        return isNameChar(ch);
    }

    public static boolean isNameChar(char ch) {
        return (
            ch == '_' || ch == ':' || ch == '.' || ch == '_' ||
            isLetter(ch)    || isDigit(ch) ||
            isCombining(ch) || isExtender(ch)
        );
    }

    public static boolean isDigit(char ch) {
        return Character.isDigit(ch);
    }

    public static boolean isLetter(char ch) {
        return Character.isLetter(ch);
    }

    public static boolean isWhitespace(char ch) {
        switch(ch) {
        case '\t' :
        case '\r' :
        case '\f' :
        case '\n' :
        case ' '  :
        case (char)0x1a :
            return true;
        default :
            return false;
        }
    }

    public static boolean isCombining(char ch) {
        return false;
    }

    public static boolean isExtender(char ch) {
        return false;
    }
}

