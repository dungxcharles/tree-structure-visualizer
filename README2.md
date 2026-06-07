# OOP.20252-14
Trong package step thì file enum  :  

+) StepType.java

dùng để lưu tất cả các hành động mà thuật toán có thể thực hiện
Thay vì lúc thì báo bằng chữ "tôi rẽ trái", lúc lại báo "đi sang nhánh trái" (rất dễ gõ sai chính tả và làm Controller hiểu nhầm), thì file này chốt cứng một bộ từ vựng chuẩn như: COMPARE, GO_LEFT, ROTATE_RIGHT, RECOLOR, v.v.

Controller sẽ đọc cái mã này để biết phải chạy hiệu ứng gì (ví dụ: thấy GO_LEFT thì vẽ mũi tên chạy xuống bên trái, thấy RECOLOR thì đổi màu hình tròn sang đỏ).

+) AnimationStep.java là một dòng ghi chép , nó sẽ chứa hành động của cây hiện tại, đang thao tác trên node nào, và message
cho người đọc
vD : [Hành động: ROTATE_LEFT] - [Tại Node: 20] - [Lời nhắn: "Mất cân bằng, xoay trái tại 20"].

+) TreeOperationListener.java

Interface định nghĩa Listener cho các thao tác trên cây.
Khi model thực hiện insert/delete/search, nó sẽ gọi `onStep()` của listener để báo cáo từng bước.

```java
public interface TreeOperationListener {
    void onStep(StepType type, int nodeValue, String message);
}
```

---

## Kiến Trúc Chính (Main Architecture)

### 1. Model Layer (Package: com.model)
Chứa logic cây (insert, delete, search, traverse, etc.)
- **AbstractTree.java** - Base class chứa listener & fireStep()
- **BinaryTree.java** - Tree cơ bản
- **BinarySearchTree.java** - BST
- **AVLTree.java** - Self-balancing tree
- **RedBlackTree.java** - Color-based balancing
- **GeneralTree.java** - N-ary tree

### 2. Step Layer (Package: com.model.step)
Định nghĩa các bước mà model phát ra
- **StepType.java** - Enum loại bước (25+ loại)
- **TreeOperationListener.java** - Interface listener
- **AlgorithmStep.java** - Fine-grained step mapping

### 3. Controller Layer (Package: com.controller)
Điều khiển giao diện và xử lý user input
- **WorkspaceController.java** - FXML controller chính
- Gắn listener vào model
- Xử lý button clicks
- Cập nhật UI khi nhận bước từ model

### 4. View Layer (FXML + Canvas)
Hiển thị giao diện người dùng
- Canvas vẽ cây
- Buttons/TextFields nhập liệu
- TextArea hiển thị bước

---

## Quy Trình Hoạt Động (Workflow)

```
1. User nhấn button "Insert"
   ↓
2. Controller.handleInsertButton() được gọi
   ↓
3. Controller gọi tree.insert(value)
   ↓
4. Model thực hiện insert:
   - Traverse cây
   - So sánh giá trị
   - Mỗi bước → fireStep(type, value, message)
   ↓
5. fireStep() gọi listener.onStep()
   ↓
6. Controller's onStep() callback được gọi
   - Log bước
   - Cập nhật UI
   - Highlight node
   ↓
7. insert() hoàn thành
```

---

## Getting Started

### Cho Model Developer
1. Tất cả model đã xong - không cần sửa
2. Mỗi tree class đã gọi `fireStep()` tại các bước quan trọng
3. Xem `demo3/README.md` để hiểu chi tiết

### Cho Controller Developer
1. Tạo tree từ model
2. Gắn listener bằng `tree.setListener(new TreeOperationListener() { ... })`
3. Implement `onStep()` để xử lý bước từ model
4. Gọi `tree.insert()`, `tree.delete()`, etc. từ event handlers
5. Trong callback, dùng `Platform.runLater()` để cập nhật UI

### Ví Dụ Nhanh
```java
// Tạo tree
AbstractTree<?> tree = TreeFactory.create(TreeType.BINARY_SEARCH);

// Gắn listener
tree.setListener((type, nodeValue, message) -> {
    Platform.runLater(() -> {
        System.out.println("[" + type + "] Node: " + nodeValue + " | " + message);
        updateUI(message);
    });
});

// Gọi model operation
tree.insert(10);  // Model phát bước, callback được gọi
tree.delete(5);
tree.search(8);
```

---

## Danh Sách File Quan Trọng

### Model
- `src/main/java/com/model/tree/AbstractTree.java`
- `src/main/java/com/model/tree/BinarySearchTree.java`
- `src/main/java/com/model/tree/AVLTree.java`
- `src/main/java/com/model/tree/RedBlackTree.java`
- `src/main/java/com/model/step/StepType.java`
- `src/main/java/com/model/step/TreeOperationListener.java`

### Controller
- `src/main/java/com/controller/workspace/WorkspaceController.java`

### Tài Liệu Chi Tiết
- `demo3/README.md` - Hướng dẫn đầy đủ cho developer

---

## Lưu Ý Quan Trọng

1. **Model đã sẵn sàng** - Không cần sửa model, chỉ cần gắn listener
2. **Listener là optional** - Tree vẫn hoạt động mà không cần listener
3. **Thread safety** - Luôn dùng `Platform.runLater()` khi update UI
4. **Message là tiếng Việt** - Model phát message tiếng Việt, dùng trực tiếp
5. **StepType là semantic** - Mỗi loại bước có ý nghĩa rõ ràng

---

## Câu Hỏi Thường Gặp

**Q: Model cần sửa gì?**  
A: Không cần. Model đã xong.

**Q: Controller ở đâu?**  
A: `src/main/java/com/controller/workspace/WorkspaceController.java` - gắn listener ở đây.

**Q: Làm sao để có step-by-step animation?**  
A: Implement `onStep()` callback, dùng `Platform.runLater()` và `Thread.sleep()` để tạm dừng.

**Q: Có thể gắn nhiều listener?**  
A: Hiện tại AbstractTree chỉ lưu 1 listener. Dùng wrapper để attach multiple listeners.

---

## Liên Hệ & Tài Liệu

- Chi tiết kiến trúc: `demo3/README.md`
- Code mẫu: `demo3/README.md` - section "Ví Dụ Hoàn Chỉnh"
- StepType enum: `src/main/java/com/model/step/StepType.java`