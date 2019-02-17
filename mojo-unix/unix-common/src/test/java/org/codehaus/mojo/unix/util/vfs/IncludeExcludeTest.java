package org.codehaus.mojo.unix.util.vfs;

/*
 * The MIT License
 *
 * Copyright 2009 The Codehaus.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import junit.framework.*;
import org.codehaus.mojo.unix.io.*;
import org.codehaus.mojo.unix.io.fs.*;
import org.codehaus.mojo.unix.util.*;

import java.util.*;

import static org.codehaus.mojo.unix.io.IncludeExcludeFilter.*;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 */
public class IncludeExcludeTest
    extends TestCase
{
    LocalFs fs = new LocalFs( new TestUtil( this ).getTestFile( "src/test/resources/my-project" ) );

    IncludeExcludeFilter filter = includeExcludeFilter().
        addInclude( new PathExpression( "/src/main/unix/files/**" ) ).
        addInclude( new PathExpression( "*.java" ) ).
        addExclude( new PathExpression( "**/huge-file" ) ).
        create();

    public void setUp()
    {
        assertTrue( fs.basedir.isDirectory() );
    }

    public void testFilter()
        throws Exception
    {
        List<LocalFs> list = toList( fs.find( filter ) );
        assertEquals( 8, list.size() );
        assertTrue( list.contains( fs ) );
        assertTrue( list.contains( fs.resolve( "src/main/unix/files/opt" ) ) );
        assertTrue( list.contains( fs.resolve( "src/main/unix/files/opt/comp" ) ) );
        assertTrue( list.contains( fs.resolve( "src/main/unix/files/opt/comp/myapp" ) ) );
        assertTrue( list.contains( fs.resolve( "src/main/unix/files/opt/comp/myapp/etc" ) ) );
        assertTrue( list.contains( fs.resolve( "src/main/unix/files/opt/comp/myapp/etc/myapp.conf" ) ) );
        assertTrue( list.contains( fs.resolve( "src/main/unix/files/opt/comp/myapp/lib/" ) ) );
        assertTrue( list.contains( fs.resolve( "Included.java" ) ) );
    }

    public void testFilterForFilesOnly()
        throws Exception
    {
        List<LocalFs> list = toList( fs.find( filter, true ) );
        assertEquals( 2, list.size() );
        assertTrue( list.contains( fs.resolve( "src/main/unix/files/opt/comp/myapp/etc/myapp.conf" ) ) );
        assertTrue( list.contains( fs.resolve( "Included.java" ) ) );
    }

    private static <T> List<T> toList( Iterable<T> iterable )
    {
        ArrayList<T> list = new ArrayList<T>();
        for ( T t : iterable )
        {
            list.add( t );
        }
        return list;
    }
}
