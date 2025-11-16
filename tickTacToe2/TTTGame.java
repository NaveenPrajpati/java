package tickTacToe2;

import java.util.Scanner;

//Strategy Pattern
class Player {
    public Player(String name, char symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    private String name;
    private char symbol;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getSymbol() {
        return symbol;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }
}

class Board {
    private char[][] grid = new char[3][3];

    public Board(char[][] grid) {
        this.grid = grid;
    }

    public void printBoard() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }

    public boolean setMove(Move move, Player player) {
        int r = move.getRow();
        int c = move.getCol();

        if (grid[r][c] != '\0') { // position already filled
            System.out.println("Invalid move! Cell already taken.");
            return false;
        }

        grid[r][c] = player.getSymbol();
        return true;
    }

    public char[][] getGrid() {
        return grid;
    }

    public void setGrid(char[][] grid) {
        this.grid = grid;
    }

    public boolean isBoardFull() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == '\0')
                    return false;
            }
        }
        return true;
    }
}

class Move {
    public Move(int row, int col) {
        this.row = row;
        this.col = col;
    }

    int row, col;

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}

interface WinStrategy {
    boolean checkWin(Board board, Player player);
}

class ThreeByThreeWinStrategy implements WinStrategy {

    @Override
    public boolean checkWin(Board board, Player player) {

        for (int i = 0; i < 3; i++) {
            if (board.getGrid()[i][0] == player.getSymbol() && board.getGrid()[i][1] == player.getSymbol()
                    && board.getGrid()[i][2] == player.getSymbol()) {
                return true;
            }
        }

        for (int j = 0; j < 3; j++) {
            if (board.getGrid()[0][j] == player.getSymbol() && board.getGrid()[1][j] == player.getSymbol()
                    && board.getGrid()[2][j] == player.getSymbol()) {
                return true;
            }
        }

        if (board.getGrid()[0][0] == player.getSymbol() && board.getGrid()[1][1] == player.getSymbol()
                && board.getGrid()[2][2] == player.getSymbol()) {
            return true;
        }

        if (board.getGrid()[0][2] == player.getSymbol() && board.getGrid()[1][1] == player.getSymbol()
                && board.getGrid()[2][0] == player.getSymbol()) {
            return true;
        }

        return false;
    }

}

class Game {

    private Board board;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private WinStrategy winStrategy;

    public Game(Player p1, Player p2, WinStrategy strategy) {
        this.board = new Board(new char[3][3]);
        this.player1 = p1;
        this.player2 = p2;
        this.currentPlayer = p1;
        this.winStrategy = strategy;
    }

    public void makeMove(Move move) {
        boolean moveSuccess = board.setMove(move, currentPlayer);
        if (!moveSuccess)
            return;

        board.printBoard();

        if (winStrategy.checkWin(board, currentPlayer)) {
            System.out.println(currentPlayer.getName() + " wins!");
            return;
        }

        if (board.isBoardFull()) {
            System.out.println("Game is a draw!");
            return;
        }

        switchPlayer();
    }

    private void switchPlayer() {
        if (currentPlayer == player1)
            currentPlayer = player2;
        else
            currentPlayer = player1;
    }

    public void startGame() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println(currentPlayer.getName() + "'s turn. Enter row and col:");

            int row = sc.nextInt();
            int col = sc.nextInt();

            // Validate coordinates
            if (row < 0 || row > 2 || col < 0 || col > 2) {
                System.out.println("Invalid position! Try again.");
                continue;
            }

            makeMove(new Move(row, col));

            // Stop game after win or draw
            if (winStrategy.checkWin(board, currentPlayer)) {
                System.out.println("Game Over!");
                break;
            }

            if (board.isBoardFull()) {
                System.out.println("It's a draw!");
                break;
            }
        }

        sc.close();
    }
}

public class TTTGame {

    public static void main(String[] args) {

        Player p1 = new Player("Player 1", 'X');
        Player p2 = new Player("Player 2", 'O');

        Game game = new Game(p1, p2, new ThreeByThreeWinStrategy());
        game.startGame();
    }
}
