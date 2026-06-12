import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GameOfLife extends JFrame {
    private Simulation simulation;
    private Timer timer;
    private JPanel mainPanel;
    private JButton startButton;
    private JButton pauseButton;
    private JButton resetButton;
    private JButton stepButton;
    private JButton loadRleButton;
    private JLabel statusLabel;
    private JLabel statsLabel;
    private JPanel gridPanel;
    private int cellSize = 12;
    private static final int DEFAULT_ROWS = 40;
    private static final int DEFAULT_COLS = 60;

    public GameOfLife() {
        simulation = new Simulation(DEFAULT_ROWS, DEFAULT_COLS);
        setTitle("Conway's Game of Life");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        gridPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGrid(g);
            }
        };
        gridPanel.setPreferredSize(new Dimension(DEFAULT_COLS * cellSize, DEFAULT_ROWS * cellSize));
        gridPanel.setBackground(Color.WHITE);

        gridPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleGridClick(e);
            }
        });

        gridPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleGridClick(e);
            }
        });

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        startButton = new JButton("Start");
        pauseButton = new JButton("Pause");
        stepButton = new JButton("Step");
        resetButton = new JButton("Reset");
        loadRleButton = new JButton("Load RLE");

        pauseButton.setEnabled(false);

        controlPanel.add(startButton);
        controlPanel.add(pauseButton);
        controlPanel.add(stepButton);
        controlPanel.add(resetButton);
        controlPanel.add(loadRleButton);

        JPanel statusPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        statusLabel = new JLabel("Status: Paused", SwingConstants.CENTER);
        statsLabel = new JLabel("Gen: 0 | Alive: 0 | Max Age: 0 | Avg Age: 0.0", SwingConstants.CENTER);
        statusPanel.add(statusLabel);
        statusPanel.add(statsLabel);

        mainPanel.add(gridPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        mainPanel.add(statusPanel, BorderLayout.NORTH);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulation.evolve();
                updateStatistics();
                gridPanel.repaint();
            }
        });

        startButton.addActionListener(e -> {
            timer.start();
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
            stepButton.setEnabled(false);
            loadRleButton.setEnabled(false);
            statusLabel.setText("Status: Running...");
        });

        pauseButton.addActionListener(e -> {
            timer.stop();
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
            stepButton.setEnabled(true);
            loadRleButton.setEnabled(true);
            statusLabel.setText("Status: Paused");
        });

        stepButton.addActionListener(e -> {
            simulation.evolve();
            updateStatistics();
            gridPanel.repaint();
        });

        resetButton.addActionListener(e -> {
            timer.stop();
            simulation.reset();
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
            stepButton.setEnabled(true);
            loadRleButton.setEnabled(true);
            statusLabel.setText("Status: Paused");
            updateStatistics();
            gridPanel.repaint();
        });

        loadRleButton.addActionListener(e -> loadRleFile());
    }

    private void drawGrid(Graphics g) {
        int rows = simulation.getRows();
        int cols = simulation.getCols();
        DynamicArray<DynamicArray<Cell>> grid = simulation.getGrid();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = grid.get(r).get(c);
                if (cell.isAlive()) {
                    g.setColor(Color.BLACK);
                    g.fillRect(c * cellSize, r * cellSize, cellSize - 1, cellSize - 1);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                    g.drawRect(c * cellSize, r * cellSize, cellSize, cellSize);
                }
            }
        }
    }

    private void handleGridClick(MouseEvent e) {
        if (timer.isRunning()) {
            return;
        }
        int col = e.getX() / cellSize;
        int row = e.getY() / cellSize;
        simulation.toggleCell(row, col);
        updateStatistics();
        gridPanel.repaint();
    }

    private void updateStatistics() {
        int gen = simulation.getGenerations();
        int alive = simulation.getAliveCells();
        int maxAge = simulation.getMaxAge();
        double avgAge = simulation.getAverageAge();
        statsLabel.setText("Gen: " + gen + " | Alive: " + alive + " | Max Age: " + maxAge + " | Avg Age: " + String.format("%.1f", avgAge));
    }

    private void loadRleFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                DynamicArray<String> lines = new DynamicArray<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.startsWith("#")) {
                            lines.add(line);
                        }
                    }
                    simulation.parseRle(lines);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to load RLE file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        gridPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameOfLife::new);
    }
}