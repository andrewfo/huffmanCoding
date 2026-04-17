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

/**
 * Huffman code tree. Builds from a frequency array or a flattened-tree header,
 * and supports producing codes and reading/writing the flattened tree format.
 */
public class HuffmanTree implements IHuffConstants {
    private TreeNode root;
    private String[] codings;
    private int leafSize;

    /** Build a tree from the given frequency array (PEOF is added automatically). */
    public HuffmanTree(int[] counts) {
        FairPriorityQueue<TreeNode> pq = new FairPriorityQueue<>();
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] > 0) {
                pq.enqueue(new TreeNode(i, counts[i]));
                leafSize++;
            }
        }
        pq.enqueue(new TreeNode(PSEUDO_EOF, 1));
        leafSize++;
        while (pq.size() > 1) {
            TreeNode left = pq.dequeue();
            TreeNode right = pq.dequeue();
            TreeNode parent = new TreeNode(left, -1, right);
            pq.enqueue(parent);
        }
        root = pq.dequeue();
        codings = new String[ALPH_SIZE + 1];
        buildCodes(root, "");
    }

    /** Build a tree by reading a flattened tree header from the given stream. */
    public HuffmanTree (BitInputStream bits) throws IOException {
        root = readFlattenedTree(bits);
        codings = new String[ALPH_SIZE + 1];
        buildCodes(root, "");
    }

    /** Write the tree in flattened (STF) form and return the bits written. */
    public int writeFlattenedTree(BitOutputStream outBits) {
        return flattenedTreeHelper(root, outBits);
    }

    private int flattenedTreeHelper(TreeNode node, BitOutputStream out) {
        if (node.isLeaf()) {
            out.writeBits(1, 1);
            out.writeBits(BITS_PER_WORD + 1, node.getValue());
            return 1 + BITS_PER_WORD + 1;
        } else {
            out.writeBits(1, 0);
            int written = 1;
            written += flattenedTreeHelper(node.getLeft(), out);
            written += flattenedTreeHelper(node.getRight(), out);
            return written;
        }
    }

    private TreeNode readFlattenedTree(BitInputStream bits) throws IOException {
        int bit = bits.readBits(1);
        if (bit == 1) {
            int value = bits.readBits(BITS_PER_WORD + 1);
            leafSize++;
            return new TreeNode(value, 0);
        } else{
            TreeNode left = readFlattenedTree(bits);
            TreeNode right = readFlattenedTree(bits);
            return new TreeNode(left, 0, right);
        }
    }

    private void buildCodes(TreeNode node, String path) {
        if (node != null) {
            if (node.isLeaf()) {
                codings[node.getValue()] = path.isEmpty() ? "0" : path;
            } else {
                buildCodes(node.getLeft(), path + "0");
                buildCodes(node.getRight(), path + "1");
            }
        }
    }

    /**
     * Decode encoded bits from bits, writing original bytes to outBits until PEOF.
     * @return the number of bits written to outBits.
     */
    public int decodeTree(BitInputStream bits, BitOutputStream outBits) throws IOException {
        int written = 0;
        TreeNode curr = root;
        boolean done = false;
        while (!done) {
            int bit = bits.readBits(1);
            if (bit == -1) {
                throw new IllegalArgumentException("No EOF found");
            }
            curr = (bit == 0) ? curr.getLeft() : curr.getRight();
            if (curr.isLeaf()) {
                if (curr.getValue() == PSEUDO_EOF) {
                    done = true;
                } else {
                    outBits.writeBits(BITS_PER_WORD, curr.getValue());
                    written += BITS_PER_WORD;
                    curr = root;
                }
            }
        }
        return written;
    }

    /** Array of Huffman codes indexed by byte value (and PEOF). */
    public String[] getCodings() {
        return codings;
    }

    /** Number of bits the flattened tree representation will occupy. */
    public int getFlattenedTreeSize() {
        final int BITSPERLEAF = 10;
        return BITSPERLEAF * leafSize + leafSize - 1;
    }

}