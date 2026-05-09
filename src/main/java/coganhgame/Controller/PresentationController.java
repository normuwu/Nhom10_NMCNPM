package coganhgame.Controller;

import coganhgame.GameApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Objects;

public class PresentationController {

    @FXML private ImageView slideImageView; // Khung chứa ảnh
    @FXML private Button btnBack;
    @FXML private Button btnNext;

    // Danh sách tên các file ảnh nằm trong thư mục resources/View/images/
    private final String[] slides = {
            "1_intro.png",
            "2_plain_board.png",
            "3_start_board.png",
            "4_how.png",
            "5_ways.png",
            "6_ganh.png",
            "7_vay.png",
            "8_open.png",
            "9_end.png"
    };
    private int currentSlideIndex = 0;

    @FXML
    public void initialize() {
        loadSlide(currentSlideIndex); // Nạp ảnh đầu tiên khi mở cửa sổ
    }

    private void loadSlide(int index) {
        // Nạp ảnh từ thư mục resources (Rất quan trọng để khi Build file JAR không bị lỗi)
        String imagePath = "View/images/" + slides[index];
        Image image = new Image(Objects.requireNonNull(GameApplication.class.getResourceAsStream(imagePath)));
        slideImageView.setImage(image);

        // Khóa nút Back nếu đang ở ảnh đầu tiên, Khóa nút Next nếu đang ở ảnh cuối cùng
        btnBack.setDisable(index == 0);
        btnNext.setDisable(index == slides.length - 1);
    }

    @FXML
    public void onBackClick() {
        if (currentSlideIndex > 0) {
            currentSlideIndex--;
            loadSlide(currentSlideIndex);
        }
    }

    @FXML
    public void onNextClick() {
        if (currentSlideIndex < slides.length - 1) {
            currentSlideIndex++;
            loadSlide(currentSlideIndex);
        }
    }
}
