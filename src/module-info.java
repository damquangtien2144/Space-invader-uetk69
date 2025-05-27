module uet.oop.spaceshootergamejavafx { // Đặt tên module của bạn ở đây
                                      // Thường là tên package gốc hoặc tên project có quy tắc

    // Khai báo các module JavaFX mà ứng dụng của bạn yêu cầu (phụ thuộc vào)
    requires javafx.controls;  // Cho các UI controls, layouts
    requires javafx.graphics;  // Cho Canvas, GraphicsContext, Stage, Scene, Image, Color, Font...
    requires javafx.fxml;      // Nếu bạn sử dụng FXML để thiết kế giao diện.
                               // Nếu không dùng FXML, bạn có thể bỏ dòng này.
    // requires javafx.media;   // Nếu bạn có âm thanh hoặc video

    // "Mở" package chứa lớp Application chính của bạn cho module javafx.graphics
    // Điều này cho phép JavaFX khởi tạo và quản lý các thành phần UI trong package đó.
    // Nếu bạn dùng FXML, cũng cần mở cho javafx.fxml.
    opens uet.oop.spaceshootergamejavafx to javafx.graphics, javafx.fxml;

    // Nếu các lớp entities của bạn được tham chiếu trực tiếp từ FXML (ví dụ: làm kiểu dữ liệu cho TableView)
    // hoặc nếu bạn có các custom controls trong package entities mà FXML cần truy cập,
    // bạn cũng nên mở package entities.
    // Thông thường, điều này không cần thiết nếu entities chỉ là logic game.
    // opens uet.oop.spaceshootergamejavafx.entities to javafx.fxml;

    // Nếu bạn có các lớp controller FXML trong một package riêng, ví dụ:
    // opens uet.oop.spaceshootergamejavafx.controllers to javafx.fxml;


    // Bạn không cần "exports" các package trừ khi bạn đang xây dựng một thư viện
    // và muốn các module khác sử dụng các lớp trong package đó.
    // exports uet.oop.spaceshootergamejavafx;
    // exports uet.oop.spaceshootergamejavafx.entities;
}