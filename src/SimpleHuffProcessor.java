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
    private boolean preCompressed;

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
        this.headerFormat = headerFormat;
        BitInputStream bits = new BitInputStream(in);
        if (headerFormat != STORE_COUNTS && headerFormat != STORE_TREE) {
            showError("Unsupported header format (custom format is not supported).");
            bits.close();
            return 0;
        }
        counts = new int[ALPH_SIZE];
        getOgBits(bits);
        huffTree = new HuffmanTree(counts);
        codings = huffTree.getCodings();
        int headerSize;
        if (headerFormat == STORE_COUNTS) {
            headerSize = ALPH_SIZE * BITS_PER_INT;
        } else {
            headerSize = BITS_PER_INT + huffTree.getFlattenedTreeSize();
        }
        int encodedSize = 0;
        for (int i = 0; i < counts.length; i++) {
            if (codings[i] != null) {
                encodedSize += counts[i] * codings[i].length();
            }
        }
        encodedSize += codings[PSEUDO_EOF].length();
        compressedBits = 2 * BITS_PER_INT +  headerSize + encodedSize;
        bits.close();
        preCompressed = true;
        return ogBits - compressedBits;
    }

    private void getOgBits(BitInputStream bits) throws IOException {
        ogBits = 0;
        int val = bits.readBits(BITS_PER_WORD);
        while (val != -1) {
            counts[val]++;
            ogBits += BITS_PER_WORD;
            val = bits.readBits(BITS_PER_WORD);
        }
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
        if (!preCompressed) {
            throw new IllegalArgumentException("Cannot compress until preProcessCompress has been" +
                    " called.");
        }
        if (!force && ogBits - compressedBits < 0) {
            showString("No output file created. Failed to force or there were no saved bits.");
            return 0;
        }
        BitInputStream bits = new BitInputStream(in);
        BitOutputStream outBits = new BitOutputStream(out);
        int written = writeHeader(bits, outBits) + encode(bits, outBits);
        bits.close();
        outBits.close();
        showString(written + "");
        return written;
    }

    private int writeHeader(BitInputStream bits, BitOutputStream outBits) throws IOException {
        int written = 0;
        outBits.writeBits(BITS_PER_INT, MAGIC_NUMBER);
        written += BITS_PER_INT;
        outBits.writeBits(BITS_PER_INT, headerFormat);
        written += BITS_PER_INT;
        if (headerFormat == STORE_COUNTS) {
            for (int i = 0; i < ALPH_SIZE; i++) {
                outBits.writeBits(BITS_PER_INT, counts[i]);
                written += BITS_PER_INT;
            }
        } else if (headerFormat == STORE_TREE) {
            outBits.writeBits(BITS_PER_INT, huffTree.getFlattenedTreeSize());
            written += BITS_PER_INT;
            written += huffTree.writeFlattenedTree(outBits);
        }
        return written;
    }

    private int encode(BitInputStream bits, BitOutputStream outBits) throws IOException {
        int written = 0;
        int val = bits.readBits(BITS_PER_WORD);
        while (val != -1) {
            String code = codings[val];
            for (int i = 0; i < code.length(); i++) {
                outBits.writeBits(1, code.charAt(i) == '1' ? 1 : 0);
                written++;
            }
            val = bits.readBits(BITS_PER_WORD);
        }
        String eofCode = codings[PSEUDO_EOF];
        for (int i = 0; i < eofCode.length(); i++) {
            outBits.writeBits(1, eofCode.charAt(i) == '1' ? 1 : 0);
            written++;
        }
        return written;
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
        BitInputStream bits = new BitInputStream(in);
        BitOutputStream outBits = new BitOutputStream(out);
        int magic = bits.readBits(BITS_PER_INT);
        if (magic != MAGIC_NUMBER) {
            throw new IllegalArgumentException("magic number not found");
        }
        int format = bits.readBits(BITS_PER_INT);
        if (format != STORE_COUNTS && format != STORE_TREE) {
            showError("Unsupported header format in compressed file.");
            bits.close();
            outBits.close();
            return 0;
        }
        HuffmanTree tree = buildTree(bits, format);
        int written = tree.decodeTree(bits, outBits);
        bits.close();
        outBits.close();
        return written;
    }

    private HuffmanTree buildTree(BitInputStream bits, int format) throws IOException {
        if (format == STORE_COUNTS) {
            int[] readCounts = new int[ALPH_SIZE];
            for (int i = 0; i < ALPH_SIZE; i++) {
                readCounts[i] = bits.readBits(BITS_PER_INT);
            }
            return new HuffmanTree(readCounts);
        } else { // Else STF and create trees
            int flatTreeSize = bits.readBits(BITS_PER_INT);
            return new HuffmanTree(bits);
        }
    }

    public void setViewer(IHuffViewer viewer) {
        myViewer = viewer;
    }

    private void showString(String s) {
        if (myViewer != null) {
            myViewer.update(s);
        }
    }

    private void showError(String s) {
        if (myViewer != null) {
            myViewer.showError(s);
        }
    }
}

