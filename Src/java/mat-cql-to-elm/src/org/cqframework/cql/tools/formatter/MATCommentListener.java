package org.cqframework.cql.tools.formatter;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

/**
 * The MAT Version of the Comment Listener.
 *
 * We needed to look for a different regex in order to remove some extraneous white space.
 *
 * @author Jack Meyer
 */
public class MATCommentListener extends CommentListener {

    public MATCommentListener(CommonTokenStream tokens) {
        super(tokens);
    }

    @Override
    public String refineOutput(String output) {
        // Case where comments are at top of library - before any other constructs
        for (Token token : commentsAtTop) {
            output = token.getText() + "\r\n" + output;
        }

        // Comments preserve whitespace, which can lead to extra newlines before statements
        return output.replaceAll("\\n\\r\\n\\r\\n", "\n");
    }

}
