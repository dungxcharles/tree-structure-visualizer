package com.model.step;

/**
 * Một "dòng ghi chép" lưu lại một hành động cụ thể tại một thời điểm của thuật
 * toán.
 * Controller sẽ thu thập một List các đối tượng này để làm Animation.
 */

public class AnimationStep {
    // type là hành động vừa làm , toàn bộ đã được mô tả trong StepType enum
    // mainNodeValue là giá trị của node chính đang được thao tác (ví dụ: node đang
    // xét, node mới được thêm vào, node bị xóa đi, v.v.)
    // message là một chuỗi mô tả ngắn gọn về hành động này, có thể dùng để hiển thị
    // trên giao diện hoặc để debug. Ví dụ: "So sánh 5 với 3", "Đi sang trái", "Thêm
    // node 7 vào cây
    private final StepType type;
    private final int mainNodeValue;
    private final String message;

    public AnimationStep(StepType type, int mainNodeValue, String message) {
        this.type = type;
        this.mainNodeValue = mainNodeValue;
        this.message = message;
    }

    public StepType getType() {
        return type;
    }

    public int getMainNodeValue() {
        return mainNodeValue;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "AnimationStep{" +
                "type=" + type +
                ", mainNodeValue=" + mainNodeValue +
                ", message='" + message + '\'' +
                '}';
    }
}
