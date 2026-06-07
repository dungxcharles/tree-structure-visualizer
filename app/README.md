# Tree Visualization Project - Step Listener Architecture

## Tổng Quan (Overview)

Dự án này sử dụng **Observer Pattern** để kết nối Model (logic cây) và Controller (UI). Khi model thực hiện một phép toán (insert, delete, search, etc.), nó phát bước (step) cho mọi listener đang lắng nghe. Controller lắng nghe những bước này để cập nhật giao diện người dùng.

---

## Kiến Trúc (Architecture)

```
┌─────────────────┐
│   Model Layer   │  (com.model.tree.*)
│   - BinaryTree  │  • Chứa logic cây (insert, delete, search, traverse)
│   - AVLTree     │  • Gọi fireStep(...) khi hoàn thành một bước quan trọng
│   - RedBlackTree│  • Không biết/quan tâm tới UI
│   - GenericTree │
└────────┬────────┘
         │
         │ fireStep(type, nodeValue, message)
         │
    ┌────▼──────────────────┐
    │ TreeOperationListener  │ (com.model.step.TreeOperationListener)
    │ + onStep(...)          │ • Interface với 1 method: onStep()
    └────▲──────────────────┘
         │
         │ implements
         │
┌────────┴─────────────────┐
│  Controller Layer        │ (com.controller.workspace.WorkspaceController)
│  + treeModel             │ • Tạo listener & gắn vào model
│  + setListener(listener) │ • Trong onStep(), cập nhật UI/log
│  + initialize()          │ • Gọi model.insert(...) khi user click button
└──────────────────────────┘
         │
         ▼
┌──────────────────────┐
│   View Layer         │ (FXML + Canvas)
│   - Canvas           │ • Hiển thị cây
│   - Buttons          │ • UI elements
│   - TextFields       │
└──────────────────────┘
```

---

## Cho Model Developers (Lập Trình Viên Model)

### 1. Model Đã Chuẩn Bị Những Gì?

Tất cả lớp cây đều **kế thừa từ `AbstractTree`**:

```java
public abstract class AbstractTree<N extends Node> {
    protected TreeOperationListener listener = null;
    
    public void setListener(TreeOperationListener listener) {
        this.listener = listener;
    }
    
    protected void fireStep(StepType type, int nodeValue, String message) {
        if (listener != null) {
            listener.onStep(type, nodeValue, message);
        }
    }
}
```

- **`setListener(TreeOperationListener listener)`** : Controller gọi hàm này để gắn listener vào model.
- **`fireStep(type, nodeValue, message)`** : Gọi hàm này khi model muốn phát bước.

### 2. Các Cây Đã Gọi `fireStep()` Đúng Chưa?

✅ **Đã Xong:**
- `BinaryTree` ✓
- `BinarySearchTree` ✓
- `AVLTree` ✓
- `RedBlackTree` ✓
- `GeneralTree` ✓

Tất cả đã gọi `fireStep(...)` tại các bước quan trọng như:
- `INSERT_NODE` - khi chèn node
- `DELETE_NODE` - khi xóa node
- `COMPARE` - khi so sánh giá trị
- `GO_LEFT` / `GO_RIGHT` - khi đi qua nhánh
- `ROTATE_LEFT` / `ROTATE_RIGHT` - khi xoay (AVL/RB)
- `RECOLOR` - khi đổi màu (RB)
- v.v.

### 3. Điều Model Phải Làm

**Không phải làm gì thêm.** Model đã xong. Nó:
- ✅ Có listener
- ✅ Gọi fireStep()
- ✅ Không quan tâm listener là gì

---

## Cho Controller Developers (Lập Trình Viên Controller)

### 1. Controller Cần Làm Gì?

Khi khởi tạo cây, **gắn listener** vào model:

```java
// 1. Tạo cây
AbstractTree<?> tree = TreeFactory.create(TreeType.BINARY_SEARCH);

// 2. Gắn listener (implement TreeOperationListener)
tree.setListener(new TreeOperationListener() {
    @Override
    public void onStep(StepType type, int nodeValue, String message) {
        // Xử lý bước từ model
        handleStep(type, nodeValue, message);
    }
});

// 3. Gọi model
tree.insert(10);
tree.delete(5);
// ... mỗi lần model phát bước, onStep() sẽ được gọi
```

**Hoặc dùng Lambda (Java 8+):**

```java
tree.setListener((type, nodeValue, message) -> {
    handleStep(type, nodeValue, message);
});
```

### 2. Các StepType Là Gì?

Model gửi `StepType` enum. Xem file:
- **`com.model.step.StepType`** - Danh sách các loại bước

Ví dụ:
- `VISIT` - Thăm node
- `COMPARE` - So sánh giá trị
- `GO_LEFT` - Đi sang trái
- `GO_RIGHT` - Đi sang phải
- `INSERT_NODE` - Chèn node
- `DELETE_NODE` - Xóa node
- `ROTATE_LEFT` - Xoay trái
- `ROTATE_RIGHT` - Xoay phải
- `RECOLOR` - Đổi màu (Red-Black Tree)
- ... và nhiều loại khác

### 3. `onStep()` Callback Nhận Gì?

```java
void onStep(StepType type, int nodeValue, String message)
```

- **`type`** : Loại bước (enum StepType)
- **`nodeValue`** : Giá trị của node đang xét (int)
- **`message`** : Mô tả bước bằng tiếng Việt (String)

Ví dụ:
```
type = StepType.COMPARE
nodeValue = 10
message = "So sánh 5 với 10"
```

### 4. Xử Lý Bước Trong Controller

Phổ biến những cách xử lý:

```java
private void handleStep(StepType type, int nodeValue, String message) {
    // 1. Log bước cho debugging
    System.out.println("[" + type + "] Node: " + nodeValue + " | " + message);
    
    // 2. Cập nhật UI (text, label)
    updateStepLabel(message);
    
    // 3. Highlight node trên canvas
    highlightNode(nodeValue);
    
    // 4. Highlight dòng code giả (pseudo-code)
    highlightPseudoCodeLine(type);
    
    // 5. Tạm dừng animation (nếu cần)
    pauseAnimation();
    
    // 6. Log step history
    addToStepHistory(type, nodeValue, message);
}
```

### 5. Ở Đâu Để Gắn Listener?

Tùy vào kiến trúc controller của bạn:

**Cách 1: Gắn trong `initialize()` (FXML Controller)**

```java
public class WorkspaceController {
    private AbstractTree<?> tree;
    
    @FXML
    public void initialize() {
        // Tạo cây
        tree = TreeFactory.create(TreeType.BINARY_SEARCH);
        
        // Gắn listener
        tree.setListener((type, nodeValue, message) -> {
            Platform.runLater(() -> {
                handleStep(type, nodeValue, message);
            });
        });
        
        // Khởi tạo UI khác...
    }
    
    private void handleStep(StepType type, int nodeValue, String message) {
        // Xử lý bước
    }
}
```

**Cách 2: Gắn khi chọn loại cây**

```java
@FXML
void handleSelectTreeType(ActionEvent event) {
    String treeType = treeTypeComboBox.getValue();
    
    // Tạo cây mới
    tree = TreeFactory.create(getTreeTypeEnum(treeType));
    
    // Gắn listener
    tree.setListener((type, nodeValue, message) -> {
        Platform.runLater(() -> handleStep(type, nodeValue, message));
    });
    
    // Update UI...
}
```

### 6. ⚠️ Important: JavaFX Thread Safety

Model có thể gọi `fireStep()` trên **thread khác** (tuy hiện tại nó gọi trên main thread, nhưng lý tưởng là nên thread-safe). Để cập nhật UI từ callback, **luôn dùng `Platform.runLater()`**:

```java
tree.setListener((type, nodeValue, message) -> {
    Platform.runLater(() -> {
        // Cập nhật UI components ở đây
        stepsTextArea.appendText(message + "\n");
        nodeLabel.setText("Node: " + nodeValue);
    });
});
```

---

## Ví Dụ Hoàn Chỉnh (Complete Example)

### Model (Không Cần Sửa)

```java
// com.model.tree.BinarySearchTree
public void insert(int value) {
    root = insertRecursive(root, value);
}

private Node insertRecursive(Node current, int value) {
    if (current == null) {
        return new Node(value);
    }
    
    int cmp = value.compareTo(current.getValue());
    if (cmp < 0) {
        current.setLeft(insertRecursive(current.getLeft(), value));
        fireStep(StepType.GO_LEFT, value, value + " < " + current.getValue() + " -> Đi trái");
    } else if (cmp > 0) {
        current.setRight(insertRecursive(current.getRight(), value));
        fireStep(StepType.GO_RIGHT, value, value + " > " + current.getValue() + " -> Đi phải");
    } else {
        fireStep(StepType.COMPARE, value, "Giá trị " + value + " đã tồn tại");
    }
    
    return current;
}
```

### Controller (Cần Viết)

```java
import com.model.tree.*;
import com.model.step.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;

public class WorkspaceController {
    @FXML private Canvas canvas;
    @FXML private TextArea stepsTextArea;
    @FXML private Label messageLabel;
    @FXML private ComboBox<String> treeTypeCombo;
    @FXML private TextField valueTextField;
    
    private AbstractTree<?> tree;
    
    @FXML
    public void initialize() {
        // Tạo cây mặc định
        tree = TreeFactory.create(TreeType.BINARY_SEARCH);
        
        // Gắn listener
        attachListener();
        
        // Setup UI...
    }
    
    private void attachListener() {
        tree.setListener((type, nodeValue, message) -> {
            Platform.runLater(() -> {
                // Log bước
                stepsTextArea.appendText(
                    String.format("[%s] Node %d: %s\n", type, nodeValue, message)
                );
                
                // Cập nhật label
                messageLabel.setText("Bước hiện tại: " + message);
                
                // Highlight node trên canvas (tùy chọn)
                highlightNodeOnCanvas(nodeValue);
                
                // Auto-scroll
                stepsTextArea.setScrollTop(Double.MAX_VALUE);
            });
        });
    }
    
    @FXML
    void handleInsertButton(ActionEvent event) {
        String input = valueTextField.getText();
        if (input == null || input.isEmpty()) return;
        
        try {
            int value = Integer.parseInt(input);
            // Model sẽ gọi fireStep() khi insert
            tree.insert(value);
            valueTextField.clear();
        } catch (NumberFormatException ex) {
            messageLabel.setText("Lỗi: Nhập số nguyên!");
        }
    }
    
    @FXML
    void handleDeleteButton(ActionEvent event) {
        String input = valueTextField.getText();
        if (input == null || input.isEmpty()) return;
        
        try {
            int value = Integer.parseInt(input);
            // Model sẽ gọi fireStep() khi delete
            tree.delete(value);
            valueTextField.clear();
        } catch (NumberFormatException ex) {
            messageLabel.setText("Lỗi: Nhập số nguyên!");
        }
    }
    
    private void highlightNodeOnCanvas(int nodeValue) {
        // Tùy chọn: highlight node trên canvas dựa vào giá trị
        // Có thể vẽ lại node với màu khác, hoặc animate
        // Ví dụ: treeController.highlightNode(nodeValue);
        // hoặc: canvas.repaint();
    }
    
    @FXML
    void handleSelectTreeType(ActionEvent event) {
        String treeType = treeTypeCombo.getValue();
        if (treeType != null && !treeType.isEmpty()) {
            // Tạo cây mới
            tree = TreeFactory.create(getTreeTypeEnum(treeType));
            attachListener();
            messageLabel.setText("Loại cây: " + treeType);
        }
    }
    
    private TreeType getTreeTypeEnum(String treeType) {
        switch (treeType.toLowerCase()) {
            case "avl tree": return TreeType.AVL;
            case "red-black tree": return TreeType.RED_BLACK;
            case "general tree": return TreeType.GENERAL;
            default: return TreeType.BINARY_SEARCH;
        }
    }
}
```

---

## Quy Trình Hoạt Động (Flow)

```
1. Controller khởi tạo
   ↓
2. Tạo tree model từ TreeFactory
   ↓
3. Gắn listener vào tree.setListener(...)
   ↓
4. User click button "Insert"
   ↓
5. Controller gọi tree.insert(value)
   ↓
6. Model thực hiện insert:
   - Traverse tree
   - So sánh giá trị
   - Tìm vị trí chèn
   - ✓ Mỗi bước quan trọng → gọi fireStep(type, value, msg)
   ↓
7. fireStep() gọi listener.onStep(type, value, msg)
   ↓
8. Controller's onStep() được gọi
   - Log bước
   - Cập nhật UI
   - Highlight node
   ↓
9. insert() hoàn thành
   ↓
10. Repeat khi user làm hành động khác
```

---

## Danh Sách File Liên Quan (File References)

### Model Files
- `com.model.tree.AbstractTree` - Base class với listener & fireStep()
- `com.model.tree.BinaryTree` - Binary tree implementation
- `com.model.tree.BinarySearchTree` - BST
- `com.model.tree.AVLTree` - AVL tree
- `com.model.tree.RedBlackTree` - Red-Black tree
- `com.model.tree.GeneralTree` - N-ary tree
- `com.model.step.TreeOperationListener` - Listener interface
- `com.model.step.StepType` - Enum loại bước

### Controller Files
- `com.controller.workspace.WorkspaceController` - Main controller (cần gắn listener ở đây)
- `com.visualization.controller.TreeVisualizationController` - Visualization controller

---

## Checklist Cho Controller Developer

- [ ] Import `TreeOperationListener` và `StepType`
- [ ] Tạo tree từ `TreeFactory`
- [ ] Gắn listener bằng `tree.setListener(...)`
- [ ] Implement `onStep(type, nodeValue, message)` callback
- [ ] Trong callback, dùng `Platform.runLater()` để cập nhật UI
- [ ] Gọi `tree.insert()`, `tree.delete()`, etc. từ UI event handlers
- [ ] Test: nhấn button → xem step được log → xem UI cập nhật

---

## Lưu Ý Quan Trọng

1. **Model không thay đổi** - Tất cả model đã sẵn sàng.
   - Model phát bước qua `fireStep(type, nodeValue, message)`
   - Controller không cần sửa model, chỉ gắn listener

2. **Listener là optional** - Nếu không gắn listener, tree vẫn hoạt động, chỉ không phát bước.
   - Gắn listener là optional, nhưng nên gắn để có feedback cho user

3. **Thread safety** - Luôn dùng `Platform.runLater()` khi cập nhật UI từ callback.
   - UI components (Label, TextArea, etc.) chỉ được cập nhật từ JavaFX thread
   - Callback `onStep()` có thể được gọi từ thread khác

4. **StepType là semantic** - Các loại bước có ý nghĩa rõ ràng, giúp controller quyết định cách xử lý.
   - Ví dụ: `INSERT_NODE` có thể highlight node vừa chèn
   - `ROTATE_LEFT` có thể play animation xoay

5. **Message là tiếng Việt** - Các message từ model đã là tiếng Việt, dùng trực tiếp để hiển thị.
   - Có thể log hoặc hiển thị trên UI mà không cần dịch

---

## Q&A

**Q: Model cần làm gì thêm?**  
A: Không. Model đã xong. Tất cả logic insert/delete/search đã gọi `fireStep()` ở đúng chỗ.

**Q: Controller cần gắn listener ở đâu?**  
A: Ở `initialize()` (lần đầu tạo tree) hoặc khi người dùng chọn loại cây từ ComboBox.

**Q: Nếu quên gắn listener?**  
A: Tree vẫn hoạt động bình thường, nhưng `onStep()` không được gọi, UI không cập nhật bước. Chỉ thấy kết quả cuối cùng.

**Q: Có thể gắn listener sau khi tree đã được sử dụng?**  
A: Có. Nhưng chỉ những hành động **sau** khi gắn listener mới phát bước. Các bước trước đó bị bỏ lỡ.

**Q: Có thể gắn nhiều listener?**  
A: Hiện tại `AbstractTree` chỉ lưu 1 listener (ghi đè listener cũ). Nếu muốn nhiều, tạo wrapper listener:
```java
tree.setListener((type, value, msg) -> {
    listener1.onStep(type, value, msg);
    listener2.onStep(type, value, msg);
    listener3.onStep(type, value, msg);
});
```

**Q: Model có thể phát bước từ thread khác không?**  
A: Có thể. Vì vậy cần dùng `Platform.runLater()` khi update UI.

**Q: Làm sao để tạm dừng/chậm lại animation khi xem step?**  
A: Thêm `Thread.sleep()` trong `onStep()` hoặc sử dụng `Timeline`/animation delay:
```java
tree.setListener((type, value, msg) -> {
    Platform.runLater(() -> {
        handleStep(type, value, msg);
        // Tạm dừng 1 giây trước step tiếp theo
        try { Thread.sleep(1000); } catch (InterruptedException e) { }
    });
});
```

---

## Tham Khảo Thêm

- `com.model.pseudo.AlgorithmStep` - Enum các bước pseudo-code (treeviz project)
- `com.model.pseudo.PseudoCodeData` - Dữ liệu pseudo-code (treeviz project)
- `TreeFactory` - Factory để tạo tree instances
- `StepType` enum - Tất cả các loại bước model có thể phát
