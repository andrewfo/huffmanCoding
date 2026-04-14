public class HuffmanTree implements IHuffConstants {
    private TreeNode root;
    private String[] codings;

    public HuffmanTree(int[] counts) {
        FairPriorityQueue<TreeNode> pq = new FairPriorityQueue<>();
        // leaf nodes
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] > 0) {
                pq.enqueue(new TreeNode(i, counts[i]));
            }
        }

        pq.enqueue(new TreeNode(PSEUDO_EOF, 1));

        while (pq.size() > 1) {
            TreeNode left = pq.dequeue();
            TreeNode right = pq.dequeue();
            TreeNode parent = new TreeNode(left, left.getFrequency() + right.getFrequency(), right);
            pq.enqueue(parent);
        }
        root = pq.dequeue();
        codings = new String[ALPH_SIZE + 1];
        buildCodes(root, "");

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

    public int getFlattenedSize() {
        return getFlattenedSizeHelper(root);
    }

    private int getFlattenedSizeHelper(TreeNode node) {
        if (node.isLeaf()) {
            return 10;
        } else {
            return 1 + getFlattenedSizeHelper(node.getLeft())
                    + getFlattenedSizeHelper(node.getRight());
        }
    }

    public String getCode(int value) {
        if (value < 0 || value >= codings.length) {
            return null;
        }
        return codings[value];
    }

    public String[] getCodings() {
        return codings;
    }

    public TreeNode getRoot() {
        return root;
    }

}
