package com.model.pseudo;

/**
 * Mỗi giá trị enum tương ứng với một dòng mã giả (pseudocode)
 * sẽ được highlight trên giao diện khi thuật toán thực thi.
 *
 * Quy ước đặt tên: OPERATION_STEP
 * - Prefix = nhóm thao tác (CREATE, INSERT, DEL, SEARCH, UPDATE, TRAVERSE, ...)
 * - Suffix = bước cụ thể bên trong thao tác đó
 */
public enum AlgorithmStep {

    /*
     * ================================================================
     * CHUNG — Dùng được cho mọi loại cây
     * ================================================================
     */

    // --- Create (tạo root) ---
    CREATE_CHECK_EMPTY, // if tree is empty
    CREATE_NEW_NODE, // node ← new Node(value)
    CREATE_SET_ROOT, // root ← node

    // --- Search ---
    SEARCH_START, // search(value) bắt đầu
    SEARCH_CHECK_NULL, // if current == null → not found
    SEARCH_COMPARE, // compare value with current.value
    SEARCH_FOUND, // current.value == value → found
    SEARCH_GO_LEFT, // value < current.value → go left
    SEARCH_GO_RIGHT, // value > current.value → go right
    SEARCH_ITERATE_CHILDREN, // for each child in current.children (General Tree)
    SEARCH_NOT_FOUND, // value not in tree

    // --- Update ---
    UPDATE_CHECK_EMPTY, // if tree is empty → return false
    UPDATE_CHECK_SAME_VALUE, // if currentValue == newValue
    UPDATE_CHECK_DUPLICATE, // if newValue already exists → return false
    UPDATE_FIND_NODE, // find node with currentValue
    UPDATE_NODE_NOT_FOUND, // node not found → return false
    UPDATE_SET_VALUE, // node.value ← newValue (BinaryTree, GeneralTree)
    UPDATE_DELETE_OLD, // delete(currentValue) (BST, AVL, RBT)
    UPDATE_INSERT_NEW, // insert(newValue) (BST, AVL, RBT)

    // --- Traversal ---
    TRAVERSE_START, // traverse(type) bắt đầu
    TRAVERSE_CHECK_NULL, // if node == null → return
    TRAVERSE_VISIT, // result.add(node.value)
    TRAVERSE_GO_LEFT, // recurse(node.left)
    TRAVERSE_GO_RIGHT, // recurse(node.right)
    TRAVERSE_GO_CHILDREN, // for each child → recurse(child) (General Tree)
    TRAVERSE_ENQUEUE, // queue.add(node) (BFS)
    TRAVERSE_DEQUEUE, // current ← queue.poll() (BFS)

    /*
     * ================================================================
     * BINARY TREE — insert/delete theo parent, không tự sắp xếp
     * ================================================================
     */

    // --- Insert (by parent) ---
    BT_INSERT_CHECK_EMPTY, // if tree is empty → return false
    BT_INSERT_CHECK_DUPLICATE, // if value already exists → return false
    BT_INSERT_FIND_PARENT, // parentNode ← findNode(parentValue)
    BT_INSERT_PARENT_NOT_FOUND, // parentNode == null → return false
    BT_INSERT_CHECK_LEFT, // if parentNode.left == null
    BT_INSERT_SET_LEFT, // parentNode.left ← new Node(value)
    BT_INSERT_CHECK_RIGHT, // if parentNode.right == null
    BT_INSERT_SET_RIGHT, // parentNode.right ← new Node(value)
    BT_INSERT_FULL, // parent đã có 2 con → return false

    // --- Delete (xóa cả subtree) ---
    BT_DEL_CHECK_EMPTY, // if root == null → return false
    BT_DEL_CHECK_ROOT, // if root.value == value
    BT_DEL_CLEAR_ROOT, // root ← null
    BT_DEL_SEARCH_CHILDREN, // duyệt left/right tìm node cần xóa
    BT_DEL_FOUND_IN_LEFT, // current.left.value == value → set left null
    BT_DEL_FOUND_IN_RIGHT, // current.right.value == value → set right null
    BT_DEL_NOT_FOUND, // không tìm thấy → return false

    /*
     * ================================================================
     * GENERAL TREE — insert/delete trên danh sách con
     * ================================================================
     */

    // --- Insert (add child to parent) ---
    GEN_INSERT_CHECK_EMPTY, // if tree is empty → return false
    GEN_INSERT_FIND_PARENT, // parentNode ← findNode(parentValue)
    GEN_INSERT_PARENT_NOT_FOUND, // parentNode == null → return false
    GEN_INSERT_CHECK_DUPLICATE, // if value already exists → return false
    GEN_INSERT_ADD_CHILD, // parentNode.addChild(new GenericNode(value))

    // --- Delete (xóa subtree) ---
    GEN_DEL_CHECK_EMPTY, // if tree is empty → return false
    GEN_DEL_CHECK_ROOT, // if root.value == value
    GEN_DEL_CLEAR_ROOT, // root ← null
    GEN_DEL_ITERATE_CHILDREN, // for each child in current.children
    GEN_DEL_FOUND_CHILD, // child.value == value → removeChild(child)
    GEN_DEL_RECURSE, // recurse into child's subtree
    GEN_DEL_NOT_FOUND, // không tìm thấy → return false

    /*
     * ================================================================
     * BINARY SEARCH TREE — insert/delete theo BST rules
     * ================================================================
     */

    // --- Insert ---
    BST_INSERT_CHECK_DUPLICATE, // if search(value) → return false
    BST_INSERT_CALL_REC, // root ← insertRec(root, newNode)
    BST_INSERT_CHECK_NULL, // if current == null → return newNode
    BST_INSERT_COMPARE, // compare newNode.value vs current.value
    BST_INSERT_GO_LEFT, // current.left ← insertRec(current.left, newNode)
    BST_INSERT_GO_RIGHT, // current.right ← insertRec(current.right, newNode)
    BST_INSERT_RETURN, // return current

    // --- Delete (Hibbard deletion) ---
    BST_DEL_CHECK_EXISTS, // if !search(value) → return false
    BST_DEL_CALL_REC, // root ← deleteRec(root, value)
    BST_DEL_CHECK_NULL, // if current == null → return null
    BST_DEL_COMPARE, // compare value vs current.value
    BST_DEL_GO_LEFT, // current.left ← deleteRec(left, value)
    BST_DEL_GO_RIGHT, // current.right ← deleteRec(right, value)
    BST_DEL_FOUND, // current.value == value → node found
    BST_DEL_NO_LEFT, // left == null → return right
    BST_DEL_NO_RIGHT, // right == null → return left
    BST_DEL_TWO_CHILDREN, // cả 2 con đều tồn tại
    BST_DEL_FIND_SUCCESSOR, // successor ← minimum(right)
    BST_DEL_COPY_SUCCESSOR, // current.value ← successor.value
    BST_DEL_REMOVE_SUCCESSOR, // current.right ← deleteMinimum(right)

    /*
     * ================================================================
     * AVL TREE — kế thừa BST + rebalance
     * ================================================================
     */

    // --- Insert (override BST) ---
    AVL_INSERT_CHECK_DUPLICATE, // if search(value) → return false
    AVL_INSERT_CALL_REC, // root ← insertRec(root, value)
    AVL_INSERT_CHECK_NULL, // if node == null → return new AVLNode
    AVL_INSERT_COMPARE, // compare value vs node.value
    AVL_INSERT_GO_LEFT, // node.left ← insertRec(left, value)
    AVL_INSERT_GO_RIGHT, // node.right ← insertRec(right, value)

    // --- Delete (override BST) ---
    AVL_DEL_CALL_REC, // root ← deleteRec(root, value)
    AVL_DEL_CHECK_NULL, // if node == null → return null
    AVL_DEL_COMPARE, // compare value vs node.value
    AVL_DEL_GO_LEFT, // node.left ← deleteRec(left, value)
    AVL_DEL_GO_RIGHT, // node.right ← deleteRec(right, value)
    AVL_DEL_FOUND, // node found
    AVL_DEL_NO_LEFT, // return right child
    AVL_DEL_NO_RIGHT, // return left child
    AVL_DEL_TWO_CHILDREN, // find successor
    AVL_DEL_FIND_SUCCESSOR, // successor ← minimum(right)
    AVL_DEL_COPY_SUCCESSOR, // node.value ← successor.value
    AVL_DEL_REMOVE_SUCCESSOR, // node.right ← deleteRec(right, successor.value)

    // --- Rebalance (dùng chung cho insert và delete AVL) ---
    AVL_UPDATE_HEIGHT, // node.height ← 1 + max(left.h, right.h)
    AVL_CHECK_BALANCE, // balance ← height(left) - height(right)
    AVL_LEFT_HEAVY, // balance > 1
    AVL_RIGHT_HEAVY, // balance < -1
    AVL_CHECK_LEFT_CHILD_BALANCE, // check balanceFactor(node.left)
    AVL_CHECK_RIGHT_CHILD_BALANCE, // check balanceFactor(node.right)
    AVL_ROTATE_LEFT, // leftRotate(node) or leftRotate(node.left)
    AVL_ROTATE_RIGHT, // rightRotate(node) or rightRotate(node.right)
    AVL_BALANCED, // |balance| ≤ 1 → return node (không cần quay)

    /*
     * ================================================================
     * RED-BLACK TREE — insert/delete + fix-up
     * ================================================================
     */

    // --- Create (override) ---
    RBT_CREATE_NODE, // node ← new RBNode(value)
    RBT_CREATE_SET_BLACK, // node.color ← BLACK
    RBT_CREATE_SET_ROOT, // root ← node

    // --- Insert ---
    RBT_INSERT_CHECK_DUPLICATE, // if search(value) → return false
    RBT_INSERT_CREATE_RED, // newNode ← new RBNode(value); color ← RED
    RBT_INSERT_CHECK_ROOT_NULL, // if root == null
    RBT_INSERT_SET_ROOT_BLACK, // newNode.color ← BLACK; root ← newNode
    RBT_INSERT_BST_START, // insertBST(root, newNode) bắt đầu
    RBT_INSERT_BST_COMPARE, // compare newNode.value vs current.value
    RBT_INSERT_BST_GO_LEFT, // go to left subtree
    RBT_INSERT_BST_GO_RIGHT, // go to right subtree
    RBT_INSERT_BST_LINK, // current.left/right ← newNode; newNode.parent ← current

    // --- Fix Insert ---
    RBT_FIX_INSERT_START, // fixInsert(newNode) bắt đầu
    RBT_FIX_INSERT_CHECK_PARENT, // while node ≠ root AND parent.color == RED
    RBT_FIX_INSERT_FIND_UNCLE, // uncle ← grandparent.left/right
    RBT_FIX_INSERT_UNCLE_RED, // Case 1: uncle.color == RED → recolor
    RBT_FIX_INSERT_RECOLOR, // parent ← BLACK, uncle ← BLACK, grandparent ← RED
    RBT_FIX_INSERT_MOVE_UP, // node ← grandparent (di chuyển lên)
    RBT_FIX_INSERT_TRIANGLE, // Case 2: node là inner child → rotate để thành line
    RBT_FIX_INSERT_LINE, // Case 3: node là outer child → rotate + recolor
    RBT_FIX_INSERT_ROTATE_LEFT, // leftRotate(node/grandparent)
    RBT_FIX_INSERT_ROTATE_RIGHT, // rightRotate(node/grandparent)
    RBT_FIX_INSERT_ROOT_BLACK, // root.color ← BLACK (kết thúc)

    // --- Delete ---
    RBT_DEL_FIND_NODE, // z ← findNode(root, value)
    RBT_DEL_NOT_FOUND, // z == null → return false
    RBT_DEL_SAVE_COLOR, // originalColor ← y.color
    RBT_DEL_NO_LEFT, // z.left == null → x ← z.right; transplant(z, z.right)
    RBT_DEL_NO_RIGHT, // z.right == null → x ← z.left; transplant(z, z.left)
    RBT_DEL_TWO_CHILDREN, // cả 2 con tồn tại
    RBT_DEL_FIND_SUCCESSOR, // y ← minimum(z.right)
    RBT_DEL_SUCCESSOR_IS_CHILD, // y.parent == z → xParent ← y
    RBT_DEL_SUCCESSOR_NOT_CHILD, // transplant(y, y.right); y.right ← z.right
    RBT_DEL_TRANSPLANT, // transplant(z, y)
    RBT_DEL_COPY_LEFT, // y.left ← z.left
    RBT_DEL_COPY_COLOR, // y.color ← z.color
    RBT_DEL_CHECK_FIX_NEEDED, // if originalColor == BLACK → fixDelete

    // --- Fix Delete ---
    RBT_FIX_DEL_START, // fixDelete(x, xParent) bắt đầu
    RBT_FIX_DEL_CHECK_LOOP, // while x ≠ root AND colorOf(x) == BLACK
    RBT_FIX_DEL_FIND_SIBLING, // sibling ← parent.left/right
    RBT_FIX_DEL_CASE1, // Case 1: sibling RED → recolor + rotate
    RBT_FIX_DEL_CASE2, // Case 2: sibling BLACK, cả 2 nephew BLACK → sibling ← RED, move up
    RBT_FIX_DEL_CASE3, // Case 3: sibling BLACK, close nephew RED → recolor + rotate
    RBT_FIX_DEL_CASE4, // Case 4: sibling BLACK, far nephew RED → recolor + rotate → done
    RBT_FIX_DEL_RECOLOR, // thay đổi màu sắc các node liên quan
    RBT_FIX_DEL_ROTATE_LEFT, // leftRotate(parent/sibling)
    RBT_FIX_DEL_ROTATE_RIGHT, // rightRotate(parent/sibling)
    RBT_FIX_DEL_MOVE_UP, // x ← parent; parent ← x.parent
    RBT_FIX_DEL_SET_BLACK, // x.color ← BLACK (kết thúc vòng lặp)
    RBT_FIX_DEL_ROOT_BLACK, // root.color ← BLACK (đảm bảo property)

    // --- Transplant (phụ trợ cho RBT delete) ---
    RBT_TRANSPLANT, // thay thế oldNode bằng newNode trong cây

    // --- Rotation (phụ trợ cho RBT) ---
    RBT_ROTATE_LEFT, // leftRotate(x) — xử lý parent pointers
    RBT_ROTATE_RIGHT, // rightRotate(x) — xử lý parent pointers

    /*
     * ================================================================
     * UTILITY — Trạng thái điều khiển
     * ================================================================
     */
    FINISHED, // Thao tác hoàn tất
    RETURN_RESULT // Trả về kết quả (true/false/List)
}
