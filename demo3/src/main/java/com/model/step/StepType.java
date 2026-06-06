package com.model.step;

/**
 * Các loại bước (hành động) mà thuật toán của Cây thực hiện.
 * Enum này dùng để chuẩn hóa các "từ vựng" mà Cây dùng để giao tiếp với Controller.
 */
public enum StepType {
    // Duyệt / Tìm kiếm / Lấy kết quả
    VISIT,              // Đang xét node này
    COMPARE,            // So sánh giá trị
    GO_LEFT,            // Đi sang nhánh trái
    GO_RIGHT,           // Đi sang nhánh phải
    GO_CHILD,           // Đi xuống node con (dùng cho GeneralTree)
    ADD_TO_RESULT,      // Thêm node vào danh sách kết quả duyệt cây
    
    // Thay đổi cấu trúc cơ bản
    INSERT_NODE,        // Tạo hoặc chèn node mới
    DELETE_NODE,        // Cắt bỏ/xóa node
    REPLACE_VALUE,      // Thay thế giá trị của node (thường dùng khi chép successor lên)
    
    // Cân bằng cho AVL Tree
    UPDATE_HEIGHT,      // Cập nhật lại height
    CHECK_BALANCE,      // Kiểm tra chỉ số Balance Factor
    ROTATE_LEFT,        // Xoay trái
    ROTATE_RIGHT,       // Xoay phải
    
    // Đặc thù Red-Black Tree
    RECOLOR,            // Đổi màu node (Đỏ <-> Đen)
    FIX_START,          // Bắt đầu chu trình sửa lỗi (Fix-up)
    TRANSPLANT,         // Nhổ cây (thay thế nguyên 1 subtree này bằng subtree khác)
    
    // Đặc thù General Tree
    ITERATE_CHILDREN,   // Duyệt qua danh sách các con
    ADD_CHILD,          // Thêm một con vào cuối danh sách
    REMOVE_CHILD,       // Xóa một con
    
    // Kết quả
    FOUND,              // Tìm thấy
    NOT_FOUND,          // Không tìm thấy
    DONE                // Hoàn tất thao tác
}
