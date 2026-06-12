import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Simulation {

    private DynamicArray<DynamicArray<Cell>> grid;
    private int rows;
    private int cols;
    private int generations;

    public Simulation(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.generations = 0;

        this.grid = new DynamicArray<>(this.rows);
        for (int r = 0; r < this.rows; r++) {
            DynamicArray<Cell> rowArray = new DynamicArray<>(this.cols);
            for (int c = 0; c < this.cols; c++) {
                Cell newCell = new Cell(false);
                rowArray.add(newCell);
            }
            this.grid.add(rowArray);
        }
    }

    public DynamicArray<DynamicArray<Cell>> getGrid() {
        return this.grid;
    }

    public void toggleCell(int row, int col) {
        if (row >= 0 && row < this.rows && col >= 0 && col < this.cols) {
            Cell currentCell = this.grid.get(row).get(col);
            currentCell.setAlive();
        }
    }

    public void evolve() {
        DynamicArray<DynamicArray<Cell>> nextGrid = new DynamicArray<>(this.rows);

        for (int r = 0; r < this.rows; r++) {
            DynamicArray<Cell> nextRow = new DynamicArray<>(this.cols);
            for (int c = 0; c < this.cols; c++) {
                Cell currentCell = this.grid.get(r).get(c);
                int neighbors = countLiveNeighbors(r, c);

                Cell nextCell = new Cell(false);

                if (currentCell.isAlive()) {
                    if (neighbors == 2 || neighbors == 3) {
                        nextCell.setAlive();
                        nextCell.setAge(currentCell.getAge() + 1);
                    }
                } else {
                    if (neighbors == 3) {
                        nextCell.setAlive();
                    }
                }
                nextRow.add(nextCell);
            }
            nextGrid.add(nextRow);
        }

        this.grid = nextGrid;
        this.generations++;
    }

    public int countLiveNeighbors(int row, int col) {
        int liveCount = 0;

        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int colOffset = -1; colOffset <= 1; colOffset++) {
                if (rowOffset == 0 && colOffset == 0) {
                    continue;
                }

                int checkRow = row + rowOffset;
                int checkCol = col + colOffset;

                if (checkRow >= 0 && checkRow < this.rows && checkCol >= 0 && checkCol < this.cols) {
                    Cell neighborCell = this.grid.get(checkRow).get(checkCol);
                    if (neighborCell.isAlive()) {
                        liveCount++;
                    }
                }
            }
        }
        return liveCount;
    }

    public void reset() {
        for (int r = 0; r < this.rows; r++) {
            for (int c = 0; c < this.cols; c++) {
                Cell currentCell = this.grid.get(r).get(c);
                currentCell.reset();
            }
        }
        this.generations = 0;
    }

    public int getAliveCells() {
        int aliveCount = 0;
        for (int r = 0; r < this.rows; r++) {
            for (int c = 0; c < this.cols; c++) {
                Cell currentCell = this.grid.get(r).get(c);
                if (currentCell.isAlive()) {
                    aliveCount++;
                }
            }
        }
        return aliveCount;
    }

    public double getAverageAge() {
        int totalAge = 0;
        int aliveCount = 0;

        for (int r = 0; r < this.rows; r++) {
            for (int c = 0; c < this.cols; c++) {
                Cell currentCell = this.grid.get(r).get(c);
                if (currentCell.isAlive()) {
                    totalAge = totalAge + currentCell.getAge();
                    aliveCount++;
                }
            }
        }

        if (aliveCount == 0) {
            return 0.0;
        }
        return (double) totalAge / aliveCount;
    }

    public int getMaxAge() {
        int maxAge = 0;

        for (int r = 0; r < this.rows; r++) {
            for (int c = 0; c < this.cols; c++) {
                Cell currentCell = this.grid.get(r).get(c);
                if (currentCell.isAlive()) {
                    if (currentCell.getAge() > maxAge) {
                        maxAge = currentCell.getAge();
                    }
                }
            }
        }
        return maxAge;
    }

    public int getGenerations() {
        return this.generations;
    }

    public int getRows() {
        return this.rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return this.cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public void parseRle(DynamicArray<String> lines) {
        StringBuilder rleData = new StringBuilder();
        int width = 10;
        int height = 10;

        for (String line : lines) {
            if (line.startsWith("x")) {
                String[] parts = line.split(",");
                for (String part : parts) {
                    part = part.trim();
                    if (part.startsWith("x")) {
                        width = Integer.parseInt(part.split("=")[1].trim());
                    } else if (part.startsWith("y")) {
                        height = Integer.parseInt(part.split("=")[1].trim());
                    }
                }
            } else {
                rleData.append(line);
            }
        }

        boolean[][] pattern = new boolean[height][width];
        String rle = rleData.toString();
        int row = 0;
        int col = 0;
        int count = 0;

        for (int i = 0; i < rle.length(); i++) {
            char ch = rle.charAt(i);
            if (Character.isDigit(ch)) {
                count = count * 10 + (ch - '0');
            } else {
                int repeat = 1;
                if (count > 0) {
                    repeat = count;
                }
                count = 0;

                if (ch == 'o') {
                    for (int j = 0; j < repeat; j++) {
                        pattern[row][col] = true;
                        col++;
                    }
                } else if (ch == 'b') {
                    col = col + repeat;
                } else if (ch == '$') {
                    row = row + repeat;
                    col = 0;
                } else if (ch == '!') {
                    break;
                }
            }
        }

        applyPatternToGrid(pattern);
    }

    public void applyPatternToGrid(boolean[][] pattern) {
        reset();
        int startRow = (this.rows / 2) - (pattern.length / 2);
        int startCol = (this.cols / 2) - (pattern[0].length / 2);

        for (int r = 0; r < pattern.length; r++) {
            for (int c = 0; c < pattern[r].length; c++) {
                if (pattern[r][c] == true) {
                    int targetRow = startRow + r;
                    int targetCol = startCol + c;

                    if (targetRow >= 0 && targetRow < this.rows && targetCol >= 0 && targetCol < this.cols) {
                        this.grid.get(targetRow).get(targetCol).setAlive();
                    }
                }
            }
        }
    }
}