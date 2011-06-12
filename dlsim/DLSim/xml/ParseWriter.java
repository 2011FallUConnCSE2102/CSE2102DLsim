package dlsim.DLSim.xml;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * ParseWriter
 * @author Kazuhiko Arase
 */
public class ParseWriter extends BufferedWriter {
    public ParseWriter(Writer writer) {
        super(writer);
    }
}
