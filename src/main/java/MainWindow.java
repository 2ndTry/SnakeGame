import javax.swing.*;
import java.io.IOException;

public class MainWindow extends JFrame {
    public MainWindow() throws IOException {
        setTitle("Змейка");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(336, 365);
        setLocation(MainMenu.locationWindow); // распологаем по последним координатам окна
        add(new GameField());
        setVisible(true);
    }
}
