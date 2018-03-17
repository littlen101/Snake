import java.util.LinkedList;
import java.util.HashSet;
import java.util.Random;

public class Snake {

    //List of constants to represent limiting
    //Design factors for the Game such as
    //The Key Codes and the board Bounds
    private static final double BOARD_RAD = .015;
    private static final int SIZE = 500;
    private static final int UP_KEY = 38;
    private static final int RIGHT_KEY = 39;
    private static final int LEFT_KEY = 37;
    private static final int DOWN_KEY = 40;
    private static final int DELAY = 20;
    private static final int SNAKE_RAD = 10;
    private static final int BOUNDS = 40;
    private void snake () {}

    private static final Random RNG = new Random();
    private static int gameCounter = 0;
    private static final int RIGHT = 0;
    private static final int UP = 2;
    private static final int DOWN = 4;
    private static final int LEFT = 1;
    private static int direction = UP;
    private static HashSet<Point> myBody = new HashSet<>();

    static class Board {
        private static final int RADIUS = SNAKE_RAD;
        boolean[][] table;

        Board (final int x, final int y) {
            table = new boolean[x][y];

            for (int i = 0; i < x; i++) {
                table[i][RADIUS] = true;
            }
            for (int i = 0; i < x; i++) {
                table[i][y - RADIUS] = true;
            }
            for (int i = 0; i < y; i++) {
                table[RADIUS][i] = true;
            }
            for (int i = 0; i < y; i++) {
                table[x - RADIUS][i] = true;
            }
        }
    }

//    static class obstacle {
//      //To do//
//    }

    static class BodyPart {
        private static final int SNAKE_RADUIS = 5;
        Point center;
        double radius = SNAKE_RADUIS;

         BodyPart (final int x1, final int y1) {
            center = new Point (x1, y1);
        }
    }

    static class Point {
        int x, y;

         Point (final int a, final int b) {
            x = a;
            y = b;
        }

        public void moveLeft () {
            x--;
        }

        public void moveRight () {
            x++;
        }
        public void moveUp () {
            y++;
        }
        public void moveDown () {
            y--;
        }

        @Override
        public String toString () {
            return String.format("%d:%d", x, y);
        }

        @Override
        public boolean equals (final Object obj) {
            final Point other = (Point) obj;
            return (x == other.x) && (y == other.y);
        }

        @Override
        public int hashCode () {
            return x + y;
        }
    }

    static class SnakeBody {
        private static final int RADUIS = SNAKE_RAD;
        LinkedList<BodyPart> body;
        Point tail;
        Point head;
        SnakeBody (final int size) {
            body = new LinkedList<>();
            body.addFirst((new BodyPart(size / 2, size / 2)));
            body.addFirst(new BodyPart(size / 2, size / 2 + RADUIS));
            tail = body.peekLast().center;
            head = body.peekFirst().center;
        }

        //Removes the last bodyPart from the list
        //And hides the part
        public void cutTail (final Board board) {
          board.table[body.peekLast().center.x][body.peekLast().center.y] = false;
          myBody.remove(tail);
          StdDraw.setPenColor(StdDraw.WHITE);
          StdDraw.filledCircle(body.peekLast().center.x,
                  body.peekLast().center.y, body.peekLast().radius + 1);
          body.removeLast();
          tail = body.peekLast().center;
        }

        //Increases the body of the snake in
        //the direction it is going
        public boolean increaseBody (final Board board) {
            final BodyPart next;
            final int increase = SNAKE_RAD;
            if (direction == RIGHT) {
                next = new BodyPart(head.x + increase, head.y);
            } else if (direction == LEFT) {
                next = new BodyPart(head.x - increase, head.y);
            } else if (direction == UP) {
                next = new BodyPart(head.x, head.y + increase);
            } else {
                next = new BodyPart(head.x, head.y - increase);
            }

            if (myBody.contains(next.center)) {
                return true;
            }
            body.addFirst(next);
            head = body.peekFirst().center;
            board.table[head.x][head.y] = true;
            myBody.add(head);
            return false;
        }
    }

    //Special Point that increases the length of the snake
    static class Fruit {
        private static final int COVER_UP_FRUIT = 3;
        Point location;

        Fruit (final int x, final int y) {
            location = new Point(x, y);
        }

        public void draw (final Board board) {
            StdDraw.setPenColor(StdDraw.MAGENTA);
            if (!board.table[location.x][location.y]) {
                StdDraw.filledSquare(location.x, location.y, 2);
            }
        }

        public void erase (final Board board) {
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.filledSquare(location.x, location.y, COVER_UP_FRUIT);
        }
    }

    //Initially Draws the board boards to accurately
    //Represent the boarder
    public static void drawBoard (final int size) {
      StdDraw.setPenColor(StdDraw.BLACK);
      StdDraw.setPenRadius(BOARD_RAD);
      StdDraw.setScale(0, size);
      StdDraw.line(0, 0, 0, size);
      StdDraw.line(0, 0, size, 0);
      StdDraw.line(size, 0, size, size);
      StdDraw.line(0, size, size, size);
    }

    //Draws the current instance of snake
    public static int drawSnake (final SnakeBody snake) {
        StdDraw.setPenColor(StdDraw.GREEN);
        StdDraw.setScale(0, SIZE);

        for (final BodyPart piece : snake.body) {
            StdDraw.filledCircle(piece.center.x, piece.center.y, piece.radius);
        }
        return 0;
    }

    public static void main (final String[] args) {
        //Initialize the game with a board size of 500 and creates
        //Lists to track fruit and snake body
        final int size = 500;
        final Board board = new Board(size, size);
        final SnakeBody snake = new SnakeBody(size);
        final LinkedList<Fruit> fruit = new LinkedList<Fruit>();
        drawBoard(size);
        drawSnake(snake);

        StdDraw.enableDoubleBuffering();
        int isFruit = 0;
        int fruitX = -1;
        int fruitY = -1;
        //Plays Game
        while (true) {
            //Listens for a specific key press and changes
            //direction based off of that press
            keyPressed();

            //Calls SnakeBody cutTail Method
            snake.cutTail(board);

            //Calls SnakeBody increaseBody Method
            try {
                if (snake.increaseBody(board)) {
                    break;
                }
            } catch (final ArrayIndexOutOfBoundsException ex) {
                return;
            }

            //Checks if there is a fruit in the list
            //if so draw and check to see if the snake has eaten
            if (!fruit.isEmpty()) {
                if (((snake.head.x >= fruitX - SNAKE_RAD)
                    && (snake.head.x <= fruitX + SNAKE_RAD))
                        && ((snake.head.y >= fruitY - SNAKE_RAD)
                            && (snake.head.y <= fruitY + SNAKE_RAD))) {
                    //if the snake eats the fruit increase its body
                    //erase the fruit from the board
                    //and remove the fruit from the list
                    snake.increaseBody(board);
                    fruit.peek().erase(board);
                    fruit.remove();
                    isFruit = 0;
                } else if (gameCounter % SNAKE_RAD == 0 && isFruit == 0) {
                    fruit.peek().draw(board);
                    fruitX = fruit.peek().location.x;
                    fruitY = fruit.peek().location.y;
                    isFruit = 1;
                }
            } else {
               fruit.add(new Fruit(RNG.nextInt(size - BOUNDS) + SNAKE_RAD,
                       RNG.nextInt(size - BOUNDS) + SNAKE_RAD));
            }
            drawSnake(snake);
            StdDraw.show();
            StdDraw.pause(DELAY);
            gameCounter++;
        }
    }

    public static void keyPressed () {
      //Listens for a specific key press and changes
        //direction based off of that press
        if (StdDraw.isKeyPressed(DOWN_KEY) && direction != UP) {
            direction = DOWN;
        } else if (StdDraw.isKeyPressed(LEFT_KEY) && direction != RIGHT) {
            direction = LEFT;
        } else if (StdDraw.isKeyPressed(RIGHT_KEY) && direction != LEFT) {
            direction = RIGHT;
        } else if (StdDraw.isKeyPressed(UP_KEY) && direction != DOWN) {
            direction = UP;
        }
    }
}
