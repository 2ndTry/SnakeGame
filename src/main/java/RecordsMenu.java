import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class RecordsMenu extends JFrame {

    private JTextArea textArea; // поле, в котором будут выводиться рекорды
    private JPanel recordsPanel; // панель для компановки элементов
    private JButton mainMenuButton; // кнопка возврата на главный экран
    private BufferedReader  fileReader;
    private BufferedWriter fileWriter;
    private Image scoreScreen; // задник для окна с рекордами
    private ArrayList<Integer> records = new ArrayList<Integer>();
    private String url = "results" + File.separator + "result.dat";

    public RecordsMenu() throws IOException {
        setTitle("Меню рекордов"); // заголовок окна
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // закрывает на крестик
        setSize(336, 365);
        setLocation(MainMenu.locationWindow);
        setFocusable(true);
        loadImages();

        recordsPanel = new JPanel() {  // объект панели для компановки элементов, переопределение метода отрисовки через анонимный класс
            @Override
            public void paint (Graphics g) { // переопределяем метод для отрисовки окна
                g.drawImage(scoreScreen, 0, 0, this); // рисует картинку фона на все окно
                super.printComponents(g); // стандартная отрисовка компонентов
            }
        };
        recordsPanel.setLayout(new BoxLayout(recordsPanel, BoxLayout.X_AXIS)); // панель для элеметов

        textArea = new JTextArea(); // текстовое поле для отображения рекордов
        textArea.setBackground(Color.lightGray); // задаем фон для текстового поля
        textArea.setFont(new Font("Arial", Font.PLAIN, 18)); // шрифт для заполнения окна рекордов
        textArea.setEditable(false); // запрет на редактирование поля

        sortRecords(); // метод, в котором сортируются рекорды и отбирается 10 самых лучших

        fileReader = new BufferedReader(new FileReader(url)); // объект для чтения рекордов
        int i = 1;
        while(fileReader.ready()) { // вывод в поле
            if(i != 10) { // чтобы выровнять результаты
                textArea.append("  " + i + ": " + fileReader.readLine() + "\r\n"); // компануем вывод результатов
            }
            else {
                textArea.append(i + ": " + fileReader.readLine() + "\r\n");
            }
            i++; // счетчик
        }

        mainMenuButton = new JButton("Меню"); // кнопка главного меню

        recordsPanel.add(BorderLayout.EAST, textArea); // добавляем в панель текстовый файл с записью рекордов
        recordsPanel.add(BorderLayout.WEST, mainMenuButton); // добавляем кнопку в панель

        add(BorderLayout.CENTER, recordsPanel); // добавляем панель в окно
        System.out.println("запуск таблицы рекордов"); // консольный вывод

        setVisible(true); // видимость окна

        ActionListener menuListener = new ActionListener() { // прослушка кнопки выхода в меню
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    MainMenu.locationWindow = getLocation(); // передаем последние координаты окна
                    new MainMenu();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                setVisible(false); // закрытие окна
            }
        };
        mainMenuButton.addActionListener(menuListener); // подключаем прослушку к кнопке
    }

    private void sortRecords() throws IOException { // сортировка рекордов
        fileReader = new BufferedReader(new FileReader(url)); // объект для чтения рекордов

        while (fileReader.ready()) { // пока в файле есть строки
                records.add(Integer.parseInt(fileReader.readLine())); // вносим в лист все значения
            }

        fileReader.close(); // закрываем чтение
        fileWriter = new BufferedWriter(new FileWriter(url)); // объект для перезаписи списка рекордов

        Collections.sort(records); // сортируем рекорды
        if(records.size() > 10) { // если рекордов больше 10, то записываем обратно в файл только 10
            for (int i = records.size() - 1; i > records.size() - 11; i--) { // цикл на 10 итераций
                    fileWriter.write(records.get(i) + "\r\n"); // записываем самые большие рекорды в файл, остальное не сохраняется
            }
        }
        else { // если рекордов в файле меньше 10, то сортируем и перезаписываем все
            for (int i = records.size() - 1; i > -1; i--) { // записываем все, что есть
                    fileWriter.write(records.get(i) + "\r\n");
                    fileWriter.flush(); // выгрузка из кэша
            }
        }
        fileWriter.close(); // закрываем поток записи
    }

    private void loadImages() throws IOException {
        scoreScreen = ImageIO.read(Objects.requireNonNull(RecordsMenu.class.getClassLoader().getResourceAsStream("scoreScreen.png"))); // картинка для меню очков
    }

}
