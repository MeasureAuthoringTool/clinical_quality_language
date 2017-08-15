package org.cqframework.cql.tools.formatter;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * The CQL Formatter Tool wrapper.
 *
 * @author Jack Meyer (7/25/2017)
 */
public class CQLFormatter {

    /**
     * Formats the string passed to it
     * @param cqlString the cql string to format
     */
    public static String format(String cqlString) throws IOException {

        InputStream is = null;
        if(!cqlString.isEmpty()) {
            is = new ByteArrayInputStream(cqlString.getBytes(StandardCharsets.UTF_8));
        }

        return format(is);
    }

    /**
     * Formats the file passed to it
     * @param cqlFile the cql file to format
     */
    public static void format(File cqlFile) throws IOException {
        InputStream is = null;
        if (cqlFile != null) {
            is = new FileInputStream(cqlFile);
        }

        String output = format(is);

        PrintWriter writer = new PrintWriter(new FileWriter(cqlFile));
        writer.println(output);
        writer.flush();
        writer.close();
    }

    /**
     * Formats the stream passed to it and returns a formatted cql string
     * @param is the input stream
     * @return the formatted cql string
     * @throws IOException
     */
    private static String format(InputStream is) throws IOException {
        return CqlFormatterVisitor.getFormattedOutput(is);
    }

    /**
     * Converts a cql file to a cql string
     * @param file the file to convert
     * @return the cql string
     */
    private static String cqlFileToString(File file) {
        byte[] bytes = new byte[0];
        try {
            bytes = Files.readAllBytes(file.toPath());
            return new String(bytes, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        File file = new File("C:\\Users\\jmeyer\\Development\\test_cql\\large_test\\test.cql");
        String toFormat = cqlFileToString(file);
        CQLFormatter.format(file);
    }
}
