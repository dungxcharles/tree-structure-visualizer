package com.model.step;

/**
 * Interface "Microphone" để Model thông báo cho ai đó đang nghe (Controller).
 */
public interface TreeOperationListener {
    /**
     * Được Model gọi mỗi khi hoàn thành xong một bước thuật toán quan trọng.
     *
     * @param type      Hành động vừa làm
     * @param nodeValue Giá trị của node đang xét
     * @param message   Thông điệp giải thích thêm (nếu có)
     */
    void onStep(StepType type, int nodeValue, String message);
}
