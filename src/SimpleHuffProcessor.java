/*  Student information for assignment:
 *
 *  On <MY|OUR> honor, <NAME1> (and <NAME2),
 *  this programming assignment is <MY|OUR> own work
 *  and <I|WE> have not provided this code to any other student.
 *
 *  Number of slip days used:
 *
 *  Student 1:
 *  UTEID:
 *  email address:
 *
 *  Student 2:
 *  UTEID:
 *  email address:
 *
 *  Grader name:
 *  Section number:
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SimpleHuffProcessor implements IHuffProcessor {

    private IHuffViewer myViewer;
    private int[] counts;
    private String[] codings;
    private HuffmanTree huffTree;
    private int headerFormat;
    private int ogBits;
    private int compressedBits;

    /**
     * Preprocess data so that compression is possible ---
     * count characters/create tree/store state so that
     * a subsequent call to compress will work. The InputStream
     * is <em>not</em> a BitInputStream, so wrap it int one as needed.
     *
     * @param in           is the stream which could be subsequently compressed
     * @param headerFormat a constant from IHuffProcessor that determines what kind
     *                     of
     *                     header to use, standard count format, standard tree
     *                     format, or
     *                     possibly some format added in the future.
     * @return number of bits saved by compression or some other measure
     *         Note, to determine the number of
     *         bits saved, the number of bits written includes
     *         ALL bits that will be written including the
     *         magic number, the header format number, the header to
     *         reproduce the tree, AND the actual data.
     * @throws IOException if an error occurs while reading from the input file.
     */
    public int preprocessCompress(InputStream in, int headerFormat) throws IOException {
        // Taking in either STF or SCF
        this.headerFormat = headerFormat;
        // Reads the file n number of bits at a time (8)
        BitInputStream bits = new BitInputStream(in);
        // Creates array of freqs
        counts = new int[ALPH_SIZE];
        // Original bits on file
        ogBits = 0;
        // Took in 8 bits
        int val = bits.readBits(BITS_PER_WORD);

        // until no more bits to read
        while (val != -1) {
            // Increases freq of that ascii val
            counts[val]++;
            // Increase amt of total bits in file
            ogBits += BITS_PER_WORD;
            // Next 8 bits
            val = bits.readBits(BITS_PER_WORD);
        }

        // Create tree using the freqs we created
        huffTree = new HuffmanTree(counts);
        // Get the String array for codes
        codings = huffTree.getCodings();

        // Start to determine headersize
        int headerSize = 0;

        if (headerFormat == STORE_COUNTS) {
            headerSize = ALPH_SIZE * BITS_PER_INT;
        } else if (headerFormat == STORE_TREE) {
            // Flattened tree size made up (32 for the tree, 10 * leafNodes + n-1 internal)
            headerSize = BITS_PER_INT +  huffTree.getFlattenedTreeSize();
        } else {
            throw new IllegalArgumentException("cant calculate header size with STORE_CUSTOM");
        }

        // Determine how much we encode
        int encodedSize = 0;
        // For freq
        for (int i = 0; i < counts.length; i++) {
            // If there is a coding for that freq
            if (codings[i] != null) {
                // Add the total amount of time this code appears * its length (total bits)
                encodedSize += counts[i] * codings[i].length();
            }
        }

        // Add the PEOF bits
        encodedSize += codings[PSEUDO_EOF].length();

        // Sum all bits in compressed file
        compressedBits = 2 * BITS_PER_INT +  headerSize + encodedSize;

        // Close input streams to avoid handling padding of 0's
        bits.close();
        return ogBits - compressedBits;
    }

    /**
     * Compresses input to output, where the same InputStream has
     * previously been pre-processed via <code>preprocessCompress</code>
     * storing state used by this call.
     * <br>
     * pre: <code>preprocessCompress</code> must be called before this method
     *
     * @param in    is the stream being compressed (NOT a BitInputStream)
     * @param out   is bound to a file/stream to which bits are written
     *              for the compressed file (not a BitOutputStream)
     * @param force if this is true create the output file even if it is larger than
     *              the input file.
     *              If this is false do not create the output file if it is larger
     *              than the input file.
     * @return the number of bits written.
     * @throws IOException if an error occurs while reading from the input file or
     *                     writing to the output file.
     */
    public int compress(InputStream in, OutputStream out, boolean force) throws IOException {
        throw new IOException("compress is not implemented");
        // return 0;
    }

    /**
     * Uncompress a previously compressed stream in, writing the
     * uncompressed bits/data to out.
     *
     * @param in  is the previously compressed data (not a BitInputStream)
     * @param out is the uncompressed file/stream
     * @return the number of bits written to the uncompressed file/stream
     * @throws IOException if an error occurs while reading from the input file or
     *                     writing to the output file.
     */
    public int uncompress(InputStream in, OutputStream out) throws IOException {
        throw new IOException("uncompress not implemented");
        // return 0;
    }

    public void setViewer(IHuffViewer viewer) {
        myViewer = viewer;
    }

    private void showString(String s) {
        if (myViewer != null) {
            myViewer.update(s);
        }
    }
}
