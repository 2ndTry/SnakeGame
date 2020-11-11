import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Objects;

public class MainMenu extends JFrame {

    private JButton newGame = new JButton("New game"); // кнопки
    private JButton exit = new JButton("Exit");
    private JButton records = new JButton("Records");
    private JButton options = new JButton("Options");
    private JPanel mainMenuPanel; // панель для элементов кнопок
    private Image mainScreen; // задник для главного окна
    public static Point locationWindow; // статическая переменная, к которой можно обращаться из всех окон, чтобы их расположить где надо
    static {
        if(locationWindow == null) { // если первое включение окна, то создаем объект с начальными координатами
            locationWindow = new Point(400, 200);
        }
    }

    public static MainWindow gameWindow; // статический класс для того, чтобы отключать окно при смерти змеи


    public MainMenu() throws IOException {
        setTitle("Main menu"); // заголовок
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // закрытие на крестик
        setSize(336, 365); // размеры окна
        setLocation(locationWindow);
        setFocusable(true);
        loadImages();

        mainMenuPanel = new JPanel() { // панель для компановки элементов, переопределение метода отрисовки через анонимный класс
            @Override
            public void paint (Graphics g) { // переопределяем метод для отрисовки окна
                g.drawImage(mainScreen, 0, 0, this); // рисует картинку фона на все окно
                super.printComponents(g); // стандартная отрисовка компонентов
            }
        };
        setFocusable(true); // фокусировка по умолчанию на окно
        mainMenuPanel.add(newGame); // добавляем кнопки на панель
        mainMenuPanel.add(exit);
        mainMenuPanel.add(records);
        mainMenuPanel.add(BorderLayout.SOUTH, options);
        mainMenuPanel.setVisible(true); // панель видима
        add(BorderLayout.CENTER, mainMenuPanel); // добавляем панель на окно
        setVisible(true); // окно видимо
        System.out.println("Запуск главного меню"); // консольный вывод

        ActionListener startGameListener = new ActionListener() { // обработка нажатия newGame

            public void actionPerformed(ActionEvent e) {
                try {
                    locationWindow = getLocation(); // передаем последние координаты окна при закрытии
                    gameWindow = new MainWindow(); // создаем объект окна, которое запускает игру
                    setVisible(false); // удаляем текущее окно

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };
        newGame.addActionListener(startGameListener); // включаем прослушку кнопки newGame

        ActionListener exitGameListener = new ActionListener() { // обработка нажатия выхода из игры

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };
        exit.addActionListener(exitGameListener); // прослушка кнопки выхода

        ActionListener recordsListener = new ActionListener() { // обработка кнопки с рекордами

            public void actionPerformed(ActionEvent e) {
                try {
                    locationWindow = getLocation(); // передаем последние координаты окна при закрытии
                    new RecordsMenu(); // создаем объект окна с рекордами
                    setVisible(false); // удаляем это окно
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };
        records.addActionListener(recordsListener); // прослушка кнопки с рекордами

        ActionListener optionsListener = new ActionListener() {

            public void actionPerformed(ActionEvent e) { // обработка кнопки с опциями
          locationWindow = getLocation(); // последние координаты окна
          new OptionsMenu(); // объект окна с опциями
          setVisible(false); // отключение этого окна
            }
        };
        options.addActionListener(optionsListener); // прослушка кнопки с опциями
    }

    private void loadImages() throws IOException {
        mainScreen = ImageIO.read(Objects.requireNonNull(MainMenu.class.getClassLoader().getResourceAsStream("mainScreen.png"))); // картинка для главного меню
    }

    public static void main(String[] args) throws IOException {
        new MainMenu(); // создание объекта начального окна / точка входа в программу
    }
}
