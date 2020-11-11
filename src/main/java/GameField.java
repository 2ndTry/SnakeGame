import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Objects;
import java.util.Random;

public class GameField extends JPanel implements ActionListener {
    private final int SIZE = 320; // размер панели
    private final int DOT_SIZE = 16; // сколько пикселей занимает одна ячейка змейки
    private final int ALL_DOTS = 400; // максимальное количество ячеек змеи, которое помещается в поле 320*320
    private Image dot; // логотип змеи
    private Image apple; // логотип яблока
    private Image slowlyDot; // логотип замедления
    private Image speedDot; // логотип ускорения
    private Image backendGame; // задник для поля со змеей
    private Image gameOver; // задник для конца игры
    private Image borderIcon; // значок для барьера
    private int appleX; // X позиция яблока
    private int appleY; // Y позиция яблока
    private int slowlyX; // позиция необычного яблока в пространстве
    private int slowlyY; // позиция необычного яблока в пространстве
    private int speedX; // позиция необычного яблока в пространстве
    private int speedY; // позиция необычного яблока в пространстве
    private int[] x = new int[ALL_DOTS]; // массив, который хранит перемещения змейки по X
    private int[] y = new int[ALL_DOTS]; // массив, который хранит перемещения змейки по Y
    private int dots; // размер змейки
    private Timer timer; // таймер
    private boolean left = false; // выражение, хранящее информацию, движемся и мы влево
    private boolean right = true; // выражение, хранящее информацию, движемся и мы вправо
    private boolean up = false; // выражение, хранящее информацию, движемся и мы вверх
    private boolean down = false; // выражение, хранящее информацию, движемся и мы вниз
    private boolean inGame = true; // выражение, хранящее информацию, жива ли еще змея
    private int score; // счет в игре
    private boolean isPause = false; // для паузы
    private boolean speedApple = false; // индикатор, активно ли ускоряющее яблоко
    private boolean slowlyApple = false; // индикатор, активно ли замедляющее яблоко
    private int timeFTUA = 25; // время для необычного яблока, прежде чем его не станет
    private String url = "results" + File.separator + "result.dat";
    public GameField() throws IOException {
        loadImages(); // загрузка изображений в начале игры
        startTimer(); // запуск таймера для отслеживания действий
        initGame(); // начальная прорисовка змеи
        addKeyListener(new FieldKeyListener()); // добавляем прослушку нажатий клавишь
        setFocusable(true); // установка фокуса на игровом поле
        setPreferredSize(new Dimension(SIZE, SIZE)); // добавил, чтобы не выходило за пределы окна
        setFocusable(true);
        createApple(); // создаем яблоко
    }

    private void initGame() {
         left = false;
         right = true;
         up = false;
         down = false;
         inGame = true;
         dots = 3;
        for (int i = 0; i < dots; i++) { // инициализируем начальные значения для змейки
            x[i] = 48 - i*DOT_SIZE; // по иксу от 48 до нуля змейка ( левый край)
            y[i] = 48; // по игрек она неизменна
        }
        timer.start();
        timer.setDelay(250); // сбрасываем таймер до 250
        score = 0; // сбрасываем счет
    }

    private void startTimer() {
        timer = new Timer(250, this); // пауза 250 мл.с, раз в это время передается слово слушателю (этому объекту)
        timer.start(); // запустили таймер
    }

    private void createApple() {
        if(OptionsMenu.withoutBarriers.isSelected()) { // вариант обработки, когда выбрано отсутствие барьеров
            this.appleX = new Random().nextInt(20) * DOT_SIZE; // координаты яблока в пределах поля
            this.appleY = new Random().nextInt(20) * DOT_SIZE; // координаты яблока в пределах поля
        }
        if(OptionsMenu.verticalBarriers.isSelected()) { // вариант обработки, когда выбраны вертикальные барьеры
            this.appleX = new Random().nextInt(20) * DOT_SIZE; // координаты яблока в пределах поля
            this.appleY = (new Random().nextInt(18) + 1)* DOT_SIZE; // координаты яблока в пределах поля
        }
        if(OptionsMenu.horizontalBarriers.isSelected()) { // вариант обработки, когда выбраны горизонтальные барьеры
            this.appleX = (new Random().nextInt(18) + 1)* DOT_SIZE; // координаты яблока в пределах поля
            this.appleY = new Random().nextInt(20) * DOT_SIZE; // координаты яблока в пределах поля
        }
        if(OptionsMenu.perimeterBarriers.isSelected()) { // вариант обработки, когда выбраны барьеры по периметру
            this.appleX = (new Random().nextInt(18) + 1)* DOT_SIZE; // координаты яблока в пределах поля
            this.appleY = (new Random().nextInt(18) + 1)* DOT_SIZE; // координаты яблока в пределах поля
        }
        newApple(); // проверка, попадает ли яблоко на змею

        if(!speedApple && !slowlyApple) { // вероятность появления возникает только тогда, когда ни одно из бонусных яблок уже не активно
            if (new Random().nextBoolean()) { // метод, чтобы случайно генерить необычные яблоки / вероятность создания - 0,25
                if (new Random().nextBoolean()) {
                    if (new Random().nextBoolean()) { //либо ускоряющее
                        createSpeedApple();
                        speedApple = true;
                    } else { // либо замедляющее
                        createSlowlyApple();
                        slowlyApple = true;
                    }
                }
            }
        }
    }

    private void newApple() {
        for (int i = dots; i > 0; i--) {
            if(this.x[i] == this.appleX && this.y[i] == this.appleY) {
                createApple();
            }
        }
    }

    private void createSpeedApple() { // метод, генерирующий скоростное яблоко
        if(OptionsMenu.withoutBarriers.isSelected()) { // вариант обработки, когда выбрано отсутствие барьеров
            this.speedX = new Random().nextInt(20) * DOT_SIZE; // координаты скоростного яблока в пределах поля
            this.speedY = new Random().nextInt(20) * DOT_SIZE; // координаты скоростного яблока в пределах поля
        }
        if(OptionsMenu.verticalBarriers.isSelected()) { // вариант обработки, когда выбраны вертикальные барьеры
            this.speedX = new Random().nextInt(20) * DOT_SIZE; // координаты скоростного яблока в пределах поля
            this.speedY = (new Random().nextInt(18) + 1)* DOT_SIZE; // координаты скоростного яблока в пределах поля
        }
        if(OptionsMenu.horizontalBarriers.isSelected()) { // вариант обработки, когда выбраны горизонтальные барьеры
            this.speedX = (new Random().nextInt(18) + 1)* DOT_SIZE; // координаты скоростного яблока в пределах поля
            this.speedY = new Random().nextInt(20) * DOT_SIZE; // координаты скоростного яблока в пределах поля
        }
        if(OptionsMenu.perimeterBarriers.isSelected()) { // вариант обработки, когда выбраны барьеры по периметру
            this.speedX = (new Random().nextInt(18) + 1)* DOT_SIZE; // координаты скоростного яблока в пределах поля
            this.speedY = (new Random().nextInt(18) + 1)* DOT_SIZE; // координаты скоростного яблока в пределах поля
        }
        newSpeedDot(); // метод для исключения перекрытий объектов, по типу newApple();
    }

    private void newSpeedDot() { // проверка, чтобы бонус выпал не в занятые точки
        for (int i = dots; i > 0; i--) { // попадает ли в змею
            if(this.x[i] == this.speedX && this.y[i] == this.speedY) {
                createSpeedApple();
            }
        }
        if(this.appleX == this.speedX && this.appleY == this.speedY) { // попадает ли на яблоко
            createSpeedApple();
        }
    }

    private void createSlowlyApple() { // метод, генерирующий замедляющее яблоко
        if(OptionsMenu.withoutBarriers.isSelected()) { // вариант обработки, когда выбрано отсутствие барьеров
            this.slowlyX = new Random().nextInt(20) * DOT_SIZE; // координаты замемдляющего яблока в пределах поля
            this.slowlyY = new Random().nextInt(20) * DOT_SIZE; // координаты замемдляющего яблока в пределах поля
        }
        if(OptionsMenu.verticalBarriers.isSelected()) { // вариант обработки, когда выбраны вертикальные барьеры
            this.slowlyX = new Random().nextInt(20) * DOT_SIZE; // координаты замемдляющего яблока в пределах поля
            this.slowlyY = (new Random().nextInt(18) + 1)* DOT_SIZE; // координаты замемдляющего яблока в пределах поля
        }
        if(OptionsMenu.horizontalBarriers.isSelected()) { // вариант обработки, когда выбраны горизонтальные барьеры
            this.slowlyX = (new Random().nextInt(18) + 1)* DOT_SIZE; // координаты замемдляющего яблока в пределах поля
            this.slowlyY = new Random().nextInt(20) * DOT_SIZE; // координаты замемдляющего яблока в пределах поля
        }
        if(OptionsMenu.perimeterBarriers.isSelected()) { // вариант обработки, когда выбраны барьеры по периметру
            this.slowlyX = (new Random().nextInt(18) + 1)* DOT_SIZE; // координаты замемдляющего яблока в пределах поля
            this.slowlyY = (new Random().nextInt(18) + 1)* DOT_SIZE; // координаты замемдляющего яблока в пределах поля
        }
        newSlowlyDot(); // метод для исключения перекрытий объектов, по типу newApple();
    }

    private void newSlowlyDot() { // проверка, чтобы бонус выпал не в занятые точки
        for (int i = dots; i > 0; i--) { // попадает ли в змею
            if(this.x[i] == this.slowlyX && this.y[i] == this.slowlyY) {
                createSlowlyApple();
            }
        }
        if(this.appleX == this.slowlyX && this.appleY == this.slowlyY) { // попадает ли на яблоко
            createSlowlyApple();
        }
    }

    private void loadImages() throws IOException { // метод для загрузки картинок
         apple = ImageIO.read(Objects.requireNonNull(GameField.class.getClassLoader().getResourceAsStream("plus.png"))); // загружаем картинку в объект
         dot = ImageIO.read(Objects.requireNonNull(GameField.class.getClassLoader().getResourceAsStream("star.png"))); // аналогично с дотом (частью) змеи
         slowlyDot = ImageIO.read(Objects.requireNonNull(GameField.class.getClassLoader().getResourceAsStream("slowly.png"))); // картинка для замедления
         speedDot = ImageIO.read(Objects.requireNonNull(GameField.class.getClassLoader().getResourceAsStream("speed.png"))); // картинка для ускорения
         backendGame = ImageIO.read(Objects.requireNonNull(GameField.class.getClassLoader().getResourceAsStream("backendGame.png"))); // картинка для задника игры
         gameOver = ImageIO.read(Objects.requireNonNull(GameField.class.getClassLoader().getResourceAsStream("gameOver.png"))); // заставка конца игры
         borderIcon = ImageIO.read(Objects.requireNonNull(GameField.class.getClassLoader().getResourceAsStream("borderIcon.png"))); // картинка для бордюра
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // классическая перерисовка базовых компонентов окна
     if(inGame) {
         g.drawImage(backendGame, 0, 0, this); // прорисовка задника для поля
         g.drawImage(apple, appleX, appleY, this); // перерисовываем яблоко (1 - объект, 2, 3 - координаты, 4 - кто перерисовывает)
         for (int i = 0; i < dots; i++) { // перерисовываем всю змейку
             g.drawImage(dot, x[i], y[i], this);
         }
         if(speedApple) { // если ускоряющее яблоко активно, то отрисовываем его
             g.drawImage(speedDot, speedX, speedY, this);
         }
         if(slowlyApple) { // если ускоряющее яблоко активно, то отрисовываем его
             g.drawImage(slowlyDot, slowlyX, slowlyY, this);
         }

         // Прорисовка окна в зависимости от выбранного режима игры

         // если выбран вариант без барьеров, то никакой дополнительной прорисовки
         if(OptionsMenu.verticalBarriers.isSelected()) { // если выбран варинат с вертикальными барьерами, то отрисовываем вертикальные барьеры
             for (int i = 0; i < SIZE; i+= DOT_SIZE) {
                 g.drawImage(borderIcon, i, 0, this);
                 g.drawImage(borderIcon, i, SIZE - DOT_SIZE, this);
             }
         }
         if(OptionsMenu.horizontalBarriers.isSelected()) { // если выбран вариант с горизонтальными барьерами, то отрисовываем там
             for (int i = 0; i < SIZE; i+= DOT_SIZE) {
                 g.drawImage(borderIcon, 0, i, this);
                 g.drawImage(borderIcon, SIZE - DOT_SIZE, i, this);
             }
         }
         if(OptionsMenu.perimeterBarriers.isSelected()) { // если выбран вариант с барьерами по периметру, то отрисовываем там
             for (int i = 0; i < SIZE; i+= DOT_SIZE) {
                 g.drawImage(borderIcon, 0, i, this);
                 g.drawImage(borderIcon, SIZE - DOT_SIZE, i, this);
                 g.drawImage(borderIcon, i, 0, this);
                 g.drawImage(borderIcon, i, SIZE - DOT_SIZE, this);
             }
         }
     }
     else {
         g.drawImage(gameOver, 0, 0, this);
         String gameOver = "Game Over. \r\n" + "Score: " + score;
         Font f = new Font("Arial", Font.BOLD, 14); // создаем шрифт эриал 14 размера жирный
         g.setColor(Color.white); // цвет отрисовки
         g.setFont(f); // шрифт отрисовки
         g.drawString(gameOver, 90, SIZE/2 + 15); // отрисовываем гейм овер в заданных координатах
         try {
             BufferedWriter fileWriter = new BufferedWriter(new FileWriter(url, true)); // для записи результата игры
             fileWriter.write(score + "\r\n"); // записываем результат
             fileWriter.flush();
         } catch (IOException e) {
             e.printStackTrace();
         }
         timer.stop();
     }
    }

    private void move() {
        for (int i = dots; i > 0; i--) {
            x[i] = x[i - 1]; // сдвигаем координаты x к концу (двигаем тело)
            y[i] = y[i - 1]; // сдвигаем координаты y к концу (двигаем тело)
        }
        if(left) {
            x[0] -= DOT_SIZE; // если движется влево - присваиваем первой координате точку на одну клетку левее нынешней
        }
        if(right) {
            x[0] += DOT_SIZE; // если движется вправо - присваиваем первой координате точку на одну клетку правее нынешней
        }
        if(up) {
            y[0] -= DOT_SIZE; // если движется вверх - присваиваем первой координате точку на одну клетку выше нынешней
        }
        if(down) {
            y[0] += DOT_SIZE; // если движется вниз - присваиваем первой координате точку на одну клетку ниже нынешней
        }
    }

    private void checkApple() {
        if(x[0] == appleX && y[0] == appleY) { // если голова встретилась с яблоком, то
            dots++; // увеличиваем змею
            score++;
            createApple(); // пересоздаем яблоко

            if(score == 5) { // ускоряем змею в зависимости от съеденных яблок
                timer.setDelay(220);
            }
            if(score == 10) {
                timer.setDelay(200);
            }
            if(score == 15) {
                timer.setDelay(180);
            }
            if(score == 20) {
                timer.setDelay(160);
            }
            if(score == 30) {
                timer.setDelay(140);
            }
            if(score == 40) {
                timer.setDelay(120);
            }
            if(score == 50) {
                timer.setDelay(100);
            }
            if(score == 60) {
                timer.setDelay(80);
            }
        }

        if(speedApple) { // если активно ускоряющее яблоко
            if (x[0] == speedX && y[0] == speedY) { // если змея съела ускоряющее яблоко
                speedApple = false; // оно исчезает
                timeFTUA = 25; // таймер для эффекта сбрасывается
                this.timer.setDelay(100); // ускоряет змею
            }
        }
        if(slowlyApple) { // если активно замедляющее яблоко
            if (x[0] == slowlyX && y[0] == slowlyY) { // если змея съела замедляющее яблоко
                slowlyApple = false; // оно исчезает
                timeFTUA = 25; // таймер для эффекта сбрасывается
                this.timer.setDelay(300); // замедляет игру
            }
        }
    }

    private void checkCollisions() {
        for (int i = dots; i > 0; i--) { // встретилась ли змея сама с собой (тут я поставил 3, а не 4, т.к. длина змеи 4 - уже достаточно)
            if(i > 3 && x[0] == x[i] && y[0] == y[i]) {
                inGame = false;
            }
        }
        if(OptionsMenu.withoutBarriers.isSelected()) { // если выбран вариант без барьеров, то реализация стандартная
            if (x[0] > SIZE - DOT_SIZE) { // если змея убежала вправо за поле
                x[0] = 0; // перемещаем ее в левый край
            }
            if (x[0] < 0) { // если змея убежала влево за поле
                x[0] = SIZE - DOT_SIZE; // перемещаем ее в правый край
            }
            if (y[0] > SIZE - DOT_SIZE) { // если змея убежала вниз за поле
                y[0] = 0; // перемещаем ее в верхний край
            }
            if (y[0] < 0) { // если змея убежала вверх за поле
                y[0] = SIZE - DOT_SIZE; // перемещаем ее в нижний край
            }
        }

        // Проверки, когда барьеры активны

        if(OptionsMenu.verticalBarriers.isSelected()) { // если выбран вариант с вертикальными арьерами
            checkVerticalCollisions();
        }
        if(OptionsMenu.horizontalBarriers.isSelected()) { // если выбран вариант с горизонтальными барьерами
            checkHorizontalCollisions();
        }
        if(OptionsMenu.perimeterBarriers.isSelected()) { // если выбран вариант с барьерами по периметру
            checkPerimeterCollisions();
        }
    }

    private void checkVerticalCollisions() {
        if (x[0] > SIZE - DOT_SIZE) { // если змея убежала вправо за поле
            x[0] = 0; // перемещаем ее в левый край
        }
        if (x[0] < 0) { // если змея убежала влево за поле
            x[0] = SIZE - DOT_SIZE; // перемещаем ее в правый край
        }
        if (y[0] > SIZE - DOT_SIZE * 2) { // если змея убежала вниз за поле
            inGame = false;
        }
        if (y[0] < DOT_SIZE) { // если змея убежала вверх за поле
            inGame = false;
        }
    }

    private void checkHorizontalCollisions() {
        if (x[0] > SIZE - DOT_SIZE * 2) { // если змея убежала вправо за поле
            inGame = false;
        }
        if (x[0] < DOT_SIZE) { // если змея убежала влево за поле
            inGame = false;
        }
        if (y[0] > SIZE - DOT_SIZE) { // если змея убежала вниз за поле
            y[0] = 0; // перемещаем ее в верхний край
        }
        if (y[0] < 0) { // если змея убежала вверх за поле
            y[0] = SIZE - DOT_SIZE; // перемещаем ее в нижний край
        }
    }

    private void checkPerimeterCollisions() {
        if (x[0] > SIZE - DOT_SIZE * 2) { // если змея убежала вправо за поле
            inGame = false;
        }
        if (x[0] < DOT_SIZE) { // если змея убежала влево за поле
            inGame = false;
        }
        if (y[0] > SIZE - DOT_SIZE * 2) { // если змея убежала вниз за поле
            inGame = false;
        }
        if (y[0] < DOT_SIZE) { // если змея убежала вверх за поле
            inGame = false;
        }
    }


    public void actionPerformed(ActionEvent e) { // метод, вызываемвый при прослушивании
        if(inGame) { // если в игре
            checkCollisions(); // проверяем, не встретилась ли змея с препятствием
            checkApple(); // проверяем, не встретилась ли змея с яблоком
            move(); // метод для передвигания змейки
            if(slowlyApple || speedApple) { // при каждой остановке таймера, если яблоко создано, его время жизни уменьшается
                timeFTUA--;
            }
            if(timeFTUA == 0) { // если счетчик на нуле, то исчезает яблоко
                if(slowlyApple) { // либо замедления
                    slowlyApple = false; // перестает отображаться
                    timeFTUA = 25; // таймер обнуляется
                }
                else if(speedApple) { // либо скоростное
                    speedApple = false; // перестает отображаться
                    timeFTUA = 25; // таймер обнуляется
                }
            }
        }
        repaint(); // перерисовывает поле либо системой либо нашим вызовом
    }

    class FieldKeyListener extends KeyAdapter { // класс для переопределения нажатий на клавиши

        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            int key = e.getKeyCode(); // смотрим код клавиши, которая была нажата

            if(OptionsMenu.controlOnArrow.isSelected()) { // если выбран вариант управления стрелками
                if (key == KeyEvent.VK_LEFT && !right) { // если нажата левая и не движется вправо, то левая тру
                    left = true;
                    up = false;
                    down = false;
                }
                if (key == KeyEvent.VK_RIGHT && !left) { // если нажата правая и не движется влево, то правая тру
                    right = true;
                    up = false;
                    down = false;
                }
                if (key == KeyEvent.VK_DOWN && !up) { // если нажата вниз и не движется вверх, то вниз тру
                    down = true;
                    left = false;
                    right = false;
                }
                if (key == KeyEvent.VK_UP && !down) { // если нажата вверх и не движется вниз, то вверх тру
                    up = true;
                    left = false;
                    right = false;
                }
            }

            else if(OptionsMenu.controlOnWasd.isSelected()) { // если выбран вариант управления WASD
                if (key == KeyEvent.VK_A && !right) { // если нажата A и не движется вправо, то левая тру
                    left = true;
                    up = false;
                    down = false;
                }
                if (key == KeyEvent.VK_D && !left) { // если нажата D и не движется влево, то правая тру
                    right = true;
                    up = false;
                    down = false;
                }
                if (key == KeyEvent.VK_S && !up) { // если нажата S и не движется вверх, то вниз тру
                    down = true;
                    left = false;
                    right = false;
                }
                if (key == KeyEvent.VK_W && !down) { // если нажата W и не движется вниз, то вверх тру
                    up = true;
                    left = false;
                    right = false;
                }
            }

            if(key == KeyEvent.VK_P) { // обработка клавиши Р, пауза
                if(isPause) {
                    timer.start();
                    isPause = false;
                }
                else {
                    timer.stop();
                    isPause = true;
                }
            }
            if(key == KeyEvent.VK_ENTER) { // обработка клавиши Enter, выход в главное меню
                try {
                    MainMenu.locationWindow = MainMenu.gameWindow.getLocation(); // передаем последние координаты окна
                    MainMenu.gameWindow.setVisible(false);
                    new MainMenu();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

}
