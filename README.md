https://docs.oracle.com/javase/7/docs/api/java/io/InputStream.html#read(byte[])

> Reads some number of bytes from the input stream and stores them into the buffer array b. The number of bytes actually read is returned as an integer. This method blocks until input data is available, end of file is detected, or an exception is thrown.
If the length of b is zero, then no bytes are read and 0 is returned; otherwise, there is an attempt to read at least one byte. If no byte is available because the stream is at the end of the file, the value -1 is returned; otherwise, at least one byte is read and stored into b.
The first byte read is stored into element b[0], the next one into b[1], and so on. The number of bytes read is, at most, equal to the length of b. Let k be the number of bytes actually read; these bytes will be stored in elements b[0] through b[k-1], leaving elements b[k] through b[b.length-1] unaffected.

This means that `read` method can return `0` only when zero length byte array is passed in.
Otherwise read must block until there is at least 1 byte of data available, or return -1 for end of stream.

Currently in commons-compress 1.18, class org.apache.commons.compress.compressors.deflate64.Deflate64CompressorInputStream
returns `0` for some buffer sizes and some Zip files.

See src/test/java/com/mycompany/app/AppTest.java for the testcase.
