package com.model.pseudocode;

public final class PseudocodeRepository {

    private PseudocodeRepository() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    public static PseudocodeTemplate getCreate() {
        String[] text = {
                "if (reset) root = null;",
                "if (root == null) root = new Node(v);",
                "update_ui();"
        };
        PseudocodeTemplate data = new PseudocodeTemplate(text);
        data.addMap(AlgorithmStep.CHECK_NULL, 1);
        data.addMap(AlgorithmStep.CREATE_NODE, 1);
        data.addMap(AlgorithmStep.UPDATE_UI, 2);
        data.addMap(AlgorithmStep.FINISHED, 2);
        return data;
    }

    public static PseudocodeTemplate getUpdate() {
        String[] text = {
                "Node node = search(currentValue);",
                "if (node == null) return false;",
                "if (newValue already exists) return false;",
                "node.value = newValue;",
                "update_ui();"
        };
        PseudocodeTemplate data = new PseudocodeTemplate(text);
        data.addMap(AlgorithmStep.SEARCH_START, 0);
        data.addMap(AlgorithmStep.SEARCH_CHECK_NULL, 1);
        data.addMap(AlgorithmStep.SEARCH_COMPARE, 2);
        data.addMap(AlgorithmStep.UPDATE_UI, 4);
        data.addMap(AlgorithmStep.FINISHED, 4);
        return data;
    }

    public static PseudocodeTemplate getBinaryInsert() {
        String[] text = {
                "Node p = search(parentValue);",
                "if (p == null) return false;",
                "if (p.left == null) p.left = new Node(v);",
                "else if (p.right == null) p.right = new Node(v);",
                "else return false;",
                "update_ui();"
        };
        PseudocodeTemplate data = new PseudocodeTemplate(text);
        data.addMap(AlgorithmStep.SEARCH_START, 0);
        data.addMap(AlgorithmStep.SEARCH_CHECK_NULL, 1);
        data.addMap(AlgorithmStep.GO_LEFT, 2);
        data.addMap(AlgorithmStep.GO_RIGHT, 3);
        data.addMap(AlgorithmStep.INSERT_HERE, 3);
        data.addMap(AlgorithmStep.UPDATE_UI, 5);
        return data;
    }

    public static PseudocodeTemplate getBinaryDelete() {
        String[] text = {
                "Node target = search(v);",
                "if (target == null) return false;",
                "if (target == root) root = null;",
                "else disconnect target from parent;",
                "remove target subtree;",
                "update_ui();"
        };
        PseudocodeTemplate data = new PseudocodeTemplate(text);
        data.addMap(AlgorithmStep.DEL_SEARCH, 0);
        data.addMap(AlgorithmStep.DEL_NOT_FOUND, 1);
        data.addMap(AlgorithmStep.DEL_REMOVE_ROOT, 2);
        data.addMap(AlgorithmStep.DEL_REMOVE_FROM_PARENT, 3);
        data.addMap(AlgorithmStep.UPDATE_UI, 5);
        return data;
    }

    public static PseudocodeTemplate getBinarySearch() {
        String[] text = {
                "if (node == null) return null;",
                "if (node.value == v) return node;",
                "Node res = search(node.left, v);",
                "if (res != null) return res;",
                "return search(node.right, v);"
        };
        PseudocodeTemplate data = new PseudocodeTemplate(text);
        data.addMap(AlgorithmStep.SEARCH_CHECK_NULL, 0);
        data.addMap(AlgorithmStep.SEARCH_COMPARE, 1);
        data.addMap(AlgorithmStep.FOUND, 1);
        data.addMap(AlgorithmStep.TRAVERSE_LEFT, 2);
        data.addMap(AlgorithmStep.TRAVERSE_RIGHT, 4);
        return data;
    }

    public static PseudocodeTemplate getBSTInsert() {
        String[] text = {
                "if (node == null) create(v);",
                "if (v < node.val) go_left();",
                "else if (v > node.val) go_right();",
                "else return duplicate;",
                "update_ui();"
        };
        PseudocodeTemplate data = new PseudocodeTemplate(text);
        data.addMap(AlgorithmStep.CHECK_NULL, 0);
        data.addMap(AlgorithmStep.INSERT_HERE, 0);
        data.addMap(AlgorithmStep.GO_LEFT, 1);
        data.addMap(AlgorithmStep.GO_RIGHT, 2);
        data.addMap(AlgorithmStep.FINISHED, 4);
        data.addMap(AlgorithmStep.UPDATE_UI, 4);
        return data;
    }

    public static PseudocodeTemplate getBSTDelete() {
        String[] text = {
                "if (node == null) return;",
                "if (v < node.val) delete(left);",
                "else if (v > node.val) delete(right);",
                "else { // found",
                "    if (0 or 1 child) replace node;",
                "    else copy successor and delete successor;",
                "    update_ui();",
                "}"
        };
        PseudocodeTemplate data = new PseudocodeTemplate(text);
        data.addMap(AlgorithmStep.CHECK_NULL, 0);
        data.addMap(AlgorithmStep.GO_LEFT, 1);
        data.addMap(AlgorithmStep.GO_RIGHT, 2);
        data.addMap(AlgorithmStep.DEL_FOUND, 3);
        data.addMap(AlgorithmStep.UPDATE_UI, 6);
        return data;
    }

    public static PseudocodeTemplate getBSTSearch() {
        String[] text = {
                "if (node == null) return false;",
                "if (node.val == v) return true;",
                "if (v < node.val) search(left);",
                "else search(right);"
        };
        PseudocodeTemplate data = new PseudocodeTemplate(text);
        data.addMap(AlgorithmStep.CHECK_NULL, 0);
        data.addMap(AlgorithmStep.SEARCH_COMPARE, 1);
        data.addMap(AlgorithmStep.FOUND, 1);
        data.addMap(AlgorithmStep.GO_LEFT, 2);
        data.addMap(AlgorithmStep.GO_RIGHT, 3);
        data.addMap(AlgorithmStep.FINISHED, 0);
        return data;
    }

    public static PseudocodeTemplate getAVLInsert() {
        String[] text = {
                "bst_insert(v);",
                "node.height = max(height(left), height(right)) + 1;",
                "int balance = getBalance(node);",
                "if (balance > 1 && v < left.val) rotateRight(node);",
                "if (balance < -1 && v > right.val) rotateLeft(node);",
                "if (left-right case) rotateLeft(left), rotateRight(node);",
                "if (right-left case) rotateRight(right), rotateLeft(node);"
        };
        PseudocodeTemplate data = new PseudocodeTemplate(text);
        data.addMap(AlgorithmStep.INSERT_HERE, 0);
        data.addMap(AlgorithmStep.UPDATE_HEIGHT, 1);
        data.addMap(AlgorithmStep.CHECK_BALANCE, 2);
        data.addMap(AlgorithmStep.ROTATE_RIGHT, 3);
        data.addMap(AlgorithmStep.ROTATE_LEFT, 4);
        data.addMap(AlgorithmStep.FINISHED, 6);
        return data;
    }

    public static PseudocodeTemplate getAVLDelete() {
        String[] text = {
                "bst_delete(v);",
                "node.height = update_height();",
                "int balance = getBalance(node);",
                "if (left heavy) rotateRight(node);",
                "if (right heavy) rotateLeft(node);",
                "update_ui();"
        };
        PseudocodeTemplate data = new PseudocodeTemplate(text);
        data.addMap(AlgorithmStep.DEL_FOUND, 0);
        data.addMap(AlgorithmStep.UPDATE_HEIGHT, 1);
        data.addMap(AlgorithmStep.CHECK_BALANCE, 2);
        data.addMap(AlgorithmStep.ROTATE_RIGHT, 3);
        data.addMap(AlgorithmStep.ROTATE_LEFT, 4);
        data.addMap(AlgorithmStep.UPDATE_UI, 5);
        return data;
    }

    public static PseudocodeTemplate getAVLSearch() {
        return getBSTSearch();
    }

    public static PseudocodeTemplate getRBInsert() {
        String[] text = {
                "bst_insert(v); node.color = RED;",
                "while (parent.color == RED) {",
                "    if (uncle.color == RED) {",
                "        recolor(parent, uncle, grand);",
                "    } else {",
                "        if (triangle) rotate(parent);",
                "        rotate(grand); recolor();",
                "    }",
                "}",
                "root.color = BLACK;"
        };
        PseudocodeTemplate data = new PseudocodeTemplate(text);
        data.addMap(AlgorithmStep.INSERT_HERE, 0);
        data.addMap(AlgorithmStep.CHECK_COLOR, 1);
        data.addMap(AlgorithmStep.RECOLOR, 3);
        data.addMap(AlgorithmStep.ROTATE_LEFT, 6);
        data.addMap(AlgorithmStep.ROTATE_RIGHT, 6);
        data.addMap(AlgorithmStep.FINISHED, 9);
        return data;
    }

    public static PseudocodeTemplate getRBDelete() {
        String[] text = {
                "u = bst_delete(target); // replacement",
                "if (deletedColor == BLACK) {",
                "    while (x != root && x.color == BLACK) {",
                "        if (sibling == RED) { case 1: recolor }",
                "        else if (sibling children are BLACK) {",
                "            case 2: sibling.color = RED; x = parent;",
                "        } else {",
                "            if (farChild == BLACK) { case 3 }",
                "            case 4: rotate/recolor; x = root;",
                "        }",
                "    }",
                "    x.color = BLACK;",
                "}"
        };
        PseudocodeTemplate data = new PseudocodeTemplate(text);
        data.addMap(AlgorithmStep.DEL_FOUND, 0);
        data.addMap(AlgorithmStep.CHECK_COLOR, 1);
        data.addMap(AlgorithmStep.FIX_DOUBLE_BLACK, 2);
        data.addMap(AlgorithmStep.RBT_DEL_CASE_1, 3);
        data.addMap(AlgorithmStep.RBT_DEL_CASE_2, 5);
        data.addMap(AlgorithmStep.RBT_DEL_CASE_3, 7);
        data.addMap(AlgorithmStep.RBT_DEL_CASE_4, 8);
        data.addMap(AlgorithmStep.FINISHED, 11);
        return data;
    }

    public static PseudocodeTemplate getRBSearch() {
        return getBSTSearch();
    }

    public static PseudocodeTemplate getGenericInsert() {
        String[] text = {
                "Node parent = search(parentValue);",
                "if (parent == null) return error;",
                "if (value already exists) return error;",
                "Node newNode = new Node(v);",
                "parent.children.add(newNode);",
                "update_ui();"
        };
        PseudocodeTemplate data = new PseudocodeTemplate(text);
        data.addMap(AlgorithmStep.SEARCH_START, 0);
        data.addMap(AlgorithmStep.SEARCH_CHECK_NULL, 1);
        data.addMap(AlgorithmStep.CREATE_NODE, 3);
        data.addMap(AlgorithmStep.INSERT_TO_LIST, 4);
        data.addMap(AlgorithmStep.UPDATE_UI, 5);
        return data;
    }

    public static PseudocodeTemplate getGenericDelete() {
        String[] text = {
                "Node target = search(v);",
                "if (target == null) return;",
                "Node parent = target.parent;",
                "if (parent != null)",
                "    parent.children.remove(target);",
                "else root = null;",
                "update_ui();"
        };
        PseudocodeTemplate data = new PseudocodeTemplate(text);
        data.addMap(AlgorithmStep.DEL_SEARCH, 0);
        data.addMap(AlgorithmStep.DEL_NOT_FOUND, 1);
        data.addMap(AlgorithmStep.DEL_GET_PARENT, 2);
        data.addMap(AlgorithmStep.DEL_REMOVE_FROM_PARENT, 4);
        data.addMap(AlgorithmStep.DEL_REMOVE_ROOT, 5);
        data.addMap(AlgorithmStep.UPDATE_UI, 6);
        return data;
    }

    public static PseudocodeTemplate getGenericSearch() {
        String[] text = {
                "if (node == null) return null;",
                "if (node.value == v) return node;",
                "for (Node child : node.children)",
                "    Node result = search(child, v);",
                "    if (result != null) return result;",
                "return null;"
        };
        PseudocodeTemplate data = new PseudocodeTemplate(text);
        data.addMap(AlgorithmStep.SEARCH_CHECK_NULL, 0);
        data.addMap(AlgorithmStep.SEARCH_COMPARE, 1);
        data.addMap(AlgorithmStep.TRAVERSE_CHILDREN, 2);
        data.addMap(AlgorithmStep.SEARCH_GO_DOWN, 3);
        data.addMap(AlgorithmStep.SEARCH_FOUND, 4);
        data.addMap(AlgorithmStep.FOUND, 1);
        return data;
    }

    public static PseudocodeTemplate getGenericTraversal() {
        String[] text = {
                "if (node == null) return;",
                "visit(node);",
                "for (Node child : node.children)",
                "    traverse(child);"
        };
        PseudocodeTemplate data = new PseudocodeTemplate(text);
        data.addMap(AlgorithmStep.TRAVERSE_CHECK_NULL, 0);
        data.addMap(AlgorithmStep.TRAVERSE_PRINT, 1);
        data.addMap(AlgorithmStep.TRAVERSE_CHILDREN, 3);
        return data;
    }

    public static PseudocodeTemplate getPreOrder() {
        String[] text = {
                "if (node == null) return;",
                "visit(node);",
                "preOrder(node.left);",
                "preOrder(node.right);"
        };
        PseudocodeTemplate data = new PseudocodeTemplate(text);
        data.addMap(AlgorithmStep.TRAVERSE_CHECK_NULL, 0);
        data.addMap(AlgorithmStep.TRAVERSE_PRINT, 1);
        data.addMap(AlgorithmStep.TRAVERSE_LEFT, 2);
        data.addMap(AlgorithmStep.TRAVERSE_RIGHT, 3);
        return data;
    }

    public static PseudocodeTemplate getInOrder() {
        String[] text = {
                "if (node == null) return;",
                "inOrder(node.left);",
                "visit(node);",
                "inOrder(node.right);"
        };
        PseudocodeTemplate data = new PseudocodeTemplate(text);
        data.addMap(AlgorithmStep.TRAVERSE_CHECK_NULL, 0);
        data.addMap(AlgorithmStep.TRAVERSE_LEFT, 1);
        data.addMap(AlgorithmStep.TRAVERSE_PRINT, 2);
        data.addMap(AlgorithmStep.TRAVERSE_RIGHT, 3);
        return data;
    }

    public static PseudocodeTemplate getPostOrder() {
        String[] text = {
                "if (node == null) return;",
                "postOrder(node.left);",
                "postOrder(node.right);",
                "visit(node);"
        };
        PseudocodeTemplate data = new PseudocodeTemplate(text);
        data.addMap(AlgorithmStep.TRAVERSE_CHECK_NULL, 0);
        data.addMap(AlgorithmStep.TRAVERSE_LEFT, 1);
        data.addMap(AlgorithmStep.TRAVERSE_RIGHT, 2);
        data.addMap(AlgorithmStep.TRAVERSE_PRINT, 3);
        return data;
    }

    public static PseudocodeTemplate getBFS() {
        String[] text = {
                "Queue q; q.add(root);",
                "while (!q.isEmpty()) {",
                "    node = q.poll(); visit(node);",
                "    add every child to q;",
                "}"
        };
        PseudocodeTemplate data = new PseudocodeTemplate(text);
        data.addMap(AlgorithmStep.SEARCH_START, 0);
        data.addMap(AlgorithmStep.TRAVERSE_PRINT, 2);
        data.addMap(AlgorithmStep.TRAVERSE_CHILDREN, 3);
        return data;
    }
}
