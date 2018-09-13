package davserver.utils;

import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

public class MyLineParser extends BasicLineParser {

    /**
     * Parses a request line.
     *
     * @param buffer    a buffer holding the line to parse
     *
     * @return  the parsed request line
     *
     * @throws ParseException        in case of a parse error
     */
    @Override
    public RequestLine parseRequestLine(final CharArrayBuffer buffer,
                                        final ParserCursor cursor) throws ParseException {

        Args.notNull(buffer, "Char array buffer");
        Args.notNull(cursor, "Parser cursor");
        final int indexFrom = cursor.getPos();
        final int indexTo = cursor.getUpperBound();

        System.out.println("parse request line...");
        try {
            skipWhitespace(buffer, cursor);
            int i = cursor.getPos();

            int blank = buffer.indexOf(' ', i, indexTo);
            if (blank < 0) {
                throw new ParseException("Invalid request line: " +
                        buffer.substring(indexFrom, indexTo));
            }
            final String method = buffer.substringTrimmed(i, blank);
            cursor.updatePos(blank);

            skipWhitespace(buffer, cursor);
            i = cursor.getPos();

            blank = buffer.indexOf(' ', i, indexTo);
            if (blank < 0) {
                throw new ParseException("Invalid request line: " +
                        buffer.substring(indexFrom, indexTo));
            }
            final String uri = buffer.substringTrimmed(i, blank);
            cursor.updatePos(blank);

            final ProtocolVersion ver = parseProtocolVersion(buffer, cursor);

            skipWhitespace(buffer, cursor);
            if (!cursor.atEnd()) {
                throw new ParseException("Invalid request line: " +
                        buffer.substring(indexFrom, indexTo));
            }

            return createRequestLine(method, uri, ver);
        } catch (final IndexOutOfBoundsException e) {
            throw new ParseException("Invalid request line: " +
                                     buffer.substring(indexFrom, indexTo));
        }
    } // parseRequestLine

}
