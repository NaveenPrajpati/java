package snackLadder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

class Player {
    public Player(String name, int position) {
        this.name = name;
        this.position = position;
    }

    private String name;
    private int position;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}

class Board {
    private int size;
    private Map<Integer, Integer> snakes;
    private Map<Integer, Integer> ladders;

    public Board(int size) {
        this.size = size;
        this.snakes = new HashMap<>();
        this.ladders = new HashMap<>();
    }

    public void addSnake(int start, int end) {
        snakes.put(start, end);
    }

    public void addLadder(int start, int end) {
        ladders.put(start, end);
    }

    public int getSize() {
        return size;
    }

    public Map<Integer, Integer> getSnakes() {
        return snakes;
    }

    public Map<Integer, Integer> getLadders() {
        return ladders;
    }

}

interface Dice {
    int roll();
}

class NormalDice implements Dice {
    private int faces;

    public NormalDice(int faces) {
        this.faces = faces;
    }

    @Override
    public int roll() {
        return (int) (Math.random() * faces) + 1;
    }

}

class Game {
    private Board board;
    private List<Player> players;
    private Dice dice;

    public Game(Board board, List<Player> players, Dice dice) {
        this.board = board;
        this.players = players;
        this.dice = dice;
    }

    public void start() {

        Queue<Player> queue = new LinkedList<>(players);

        boolean gameOver = false;

        while (!gameOver) {
            Player current = queue.poll(); // take first player

            System.out.println("\n" + current.getName() + "'s turn...");

            playTurn(current);

            // check if player won
            if (current.getPosition() == board.getSize()) {
                System.out.println("\n🎉 " + current.getName() + " WINS THE GAME! 🎉");
                gameOver = true;
            } else {
                queue.offer(current); // put back for next round
            }
        }
    }

    public void playTurn(Player player) {

        int roll = dice.roll();
        System.out.println(player.getName() + " rolled a " + roll);

        int newPos = player.getPosition() + roll;

        // Case 1: cannot move beyond board size
        if (newPos > board.getSize()) {
            System.out.println("Roll exceeds board size. Stay at " + player.getPosition());
            return;
        }

        // Move to tentative position first
        player.setPosition(newPos);

        // Case 2: check ladder
        if (board.getLadders().containsKey(newPos)) {
            int ladderEnd = board.getLadders().get(newPos);
            System.out.println("Ladder! Climb up from " + newPos + " to " + ladderEnd);
            player.setPosition(ladderEnd);
        }

        // Case 3: check snake
        if (board.getSnakes().containsKey(newPos)) {
            int snakeEnd = board.getSnakes().get(newPos);
            System.out.println("Snake! Slide down from " + newPos + " to " + snakeEnd);
            player.setPosition(snakeEnd);
        }

        // Case 4: check win condition
        if (player.getPosition() == board.getSize()) {
            System.out.println(player.getName() + " wins the game!");
            // End game logic handled at Game.start()
        }
    }
}

public class SnackLadder {

    public static void main(String[] args) {

        Board board = new Board(100);

        // Snakes
        board.addSnake(99, 10);
        board.addSnake(90, 50);
        board.addSnake(70, 30);

        // Ladders
        board.addLadder(5, 25);
        board.addLadder(20, 60);
        board.addLadder(35, 85);

        // Players
        Player p1 = new Player("Naveen", 0);
        Player p2 = new Player("Rahul", 0);

        List<Player> players = Arrays.asList(p1, p2);

        Dice dice = new NormalDice(6);

        Game game = new Game(board, players, dice);
        game.start();
    }
}
