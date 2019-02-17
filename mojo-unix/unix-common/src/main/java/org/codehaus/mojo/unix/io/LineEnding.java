package org.codehaus.mojo.unix.io;

import fj.*;

import java.io.*;

import static fj.P.p;

/**
 * TODO: This should support "keep" too.
 * <p/>
 * http://maven.apache.org/plugins/maven-assembly-plugin/assembly.html#class_fileSet
 */
public enum LineEnding
{
    keep( null ),
    unix( "\n" ),
    windows( "\r\n" );

    private final String eol;

    private LineEnding( String eol )
    {
        this.eol = eol;
    }

    public byte[] eol()
    {
        if ( eol == null )
        {
            throw new RuntimeException( "This line ending type (" + this.name() + " doesn't have a eol" );
        }

        return eol.getBytes();
    }

    public boolean isKeep()
    {
        return this == keep;
    }

    public static P2<InputStream, LineEnding> detect( InputStream is )
        throws IOException
    {
        byte[] buffer = new byte[1000];
        final PushbackInputStream inputStream = new PushbackInputStream( is, buffer.length );

        int read = inputStream.read();

        if ( read == -1 )
        {
            throw new IOException( "Could not detect line endings." );
        }

        LineEnding lineEnding = null;
        byte prev = buffer[0] = (byte) ( read & 0xff );
        int i = 1;
        while ( i < buffer.length )
        {
            read = inputStream.read();
            if ( read == -1 )
            {
                break;
            }

            byte x = (byte) ( read & 0xff );
            buffer[i++] = x;
            if ( prev == '\r' && x == '\n' )
            {
                lineEnding = windows;
                break;
            }
            else if ( prev == '\n' )
            {
                lineEnding = unix;
                break;
            }
            prev = x;
        }

        if ( lineEnding == null && prev == '\n' )
        {
            lineEnding = unix;
        }

        if ( lineEnding == null )
        {
            throw new IOException( "Could not detect line endings in " + buffer.length + " bytes." );
        }

        inputStream.unread( buffer, 0, i );

        final LineEnding finalLineEnding = lineEnding;
        return p( (InputStream) inputStream, finalLineEnding );
    }
}
