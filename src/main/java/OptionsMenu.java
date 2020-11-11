import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class OptionsMenu  extends JFrame {

    private JButton mainMenuButton;
    public static JRadioButton withoutBarriers = new JRadioButton("Без барьеров", true); // выбрана по умолчанию
    public static JRadioButton verticalBarriers = new JRadioButton("Барьеры сверху и снизу");
    public static JRadioButton horizontalBarriers = new JRadioButton("Барьеры по бокам");
    public static JRadioButton perimeterBarriers = new JRadioButton("Барьеры по периметру");
    public static JRadioButton controlOnWasd = new JRadioButton("WASD");
    public static JRadioButton controlOnArrow = new JRadioButton("Стрелками", true); // выбран по умолчанию
    private ButtonGroup radioMap = new ButtonGroup(); // для объединения радиоБаттонов выбора карты
    private ButtonGroup radioControl = new ButtonGroup(); // для объединения радиоБаттонов выбора управления

    private JPanel optionsPanel; // панель для компановки элементов выбора карты
    private JPanel controlPanel; // панель для компановки элементов выбора управления
    private JTextArea aboutControl; // поле для описания управления

    public OptionsMenu() {
        setTitle("Опции");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(336, 365);
        setLocation(MainMenu.locationWindow);
        setFocusable(true);
//        loadImages();

        optionsPanel = new JPanel(); // создали панель для компановки элементов выбора карты
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS)); // вариант компановки элементов в панели
        optionsPanel.setBackground(Color.lightGray); // цвет задника для панели с чекбоксами
        optionsPanel.setOpaque(false); // прозрычный контур, чтобы везде был один цвет

        controlPanel = new JPanel(); // создали панель для компановки элементов выбора управления
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS)); // задали компановку
        controlPanel.setBackground(Color.lightGray); // цвет задника
        controlPanel.setOpaque(false); // прозрычный контур, чтобы везде был один цвет

        radioMap.add(withoutBarriers); // объеденили радиобаттоны выбора карты в одну логическую цепь
        radioMap.add(verticalBarriers); // объеденили радиобаттоны выбора карты в одну логическую цепь
        radioMap.add(horizontalBarriers); // объеденили радиобаттоны выбора карты в одну логическую цепь
        radioMap.add(perimeterBarriers); // объеденили радиобаттоны выбора карты в одну логическую цепь

        radioControl.add(controlOnWasd); // объеденили радиобаттоны выбора управления в одну логическую цепь
        radioControl.add(controlOnArrow); // объеденили радиобаттоны выбора управления в одну логическую цепь

        optionsPanel.add(withoutBarriers); // добавили радиобаттон выбора карты в панель
        optionsPanel.add(verticalBarriers); // добавили радиобаттон выбора карты в панель
        optionsPanel.add(horizontalBarriers); // добавили радиобаттон выбора карты в панель
        optionsPanel.add(perimeterBarriers); // добавили радиобаттон выбора карты в панель

        controlPanel.add(controlOnWasd); // добавили радиобаттон выбора управления в панель
        controlPanel.add(controlOnArrow); // добавили радиобаттон выбора управления в панель

        mainMenuButton = new JButton("Меню"); // кнопка выхода на главный экран
        optionsPanel.add(BorderLayout.CENTER, mainMenuButton); // добавили кнопку к панели

        withoutBarriers.setOpaque(false); // прозрачный фон у радиобаттона
        verticalBarriers.setOpaque(false); // прозрачный фон у радиобаттона
        horizontalBarriers.setOpaque(false); // прозрачный фон у радиобаттона
        perimeterBarriers.setOpaque(false); // прозрачный фон у радиобаттона
        controlOnWasd.setOpaque(false); // прозрачный фон у радиобаттона
        controlOnArrow.setOpaque(false); // прозрачный фон у радиобаттона

        withoutBarriers.setFont(new Font("Arial", Font.PLAIN, 16)); // установка шрифта для радиобаттона
        verticalBarriers.setFont(new Font("Arial", Font.PLAIN, 16)); // установка шрифта для радиобаттона
        horizontalBarriers.setFont(new Font("Arial", Font.PLAIN, 16)); // установка шрифта для радиобаттона
        perimeterBarriers.setFont(new Font("Arial", Font.PLAIN, 16)); // установка шрифта для радиобаттона
        controlOnWasd.setFont(new Font("Arial", Font.PLAIN, 16)); // установка шрифта для радиобаттона
        controlOnArrow.setFont(new Font("Arial", Font.PLAIN, 16)); // установка шрифта для радиобаттона

        aboutControl = new JTextArea(); // текстовое поле
        aboutControl.setOpaque(false); // чтобы был однородный цвет без бортиков
        aboutControl.setBackground(Color.lightGray); // цвет задника текстового поля
        aboutControl.setFont(new Font("Arial", Font.PLAIN, 16)); // установка шрифта
        aboutControl.append(" Пауза : P \r\n" + " Выход в главное меню : Enter \r\n" +
                "\r\n" + " Синее яблоко - замедление \r\n" + " Красное яблоко - ускорение \r\n" +
                " Зеленое яблоко - рост змеи \r\n" + "\r\n Управление:"); // текст поля
        aboutControl.setEditable(false); // запрат на редактирование поля

        add(BorderLayout.CENTER, controlPanel); // расположение элементов в окне
        add(BorderLayout.SOUTH, optionsPanel); // расположение элементов в окне
        add(BorderLayout.NORTH, aboutControl); // расположение элементов в окне

        setVisible(true); // видимость окна

        ActionListener mainMenuButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {  // реакция на взаимодействие с кнопкой
                try {
                    MainMenu.locationWindow = getLocation(); // обновляем позицию окна
                    new MainMenu(); // создаем объект главного окна
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                setVisible(false); // закрываем текущее окно
            }
        };
        mainMenuButton.addActionListener(mainMenuButtonListener); // подключаем прослушку к кнопке
    }


}
