import com.sun.tools.javac.Main;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener {

    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardHeight = rowCount*tileSize;
    private int boardWidth = columnCount*tileSize;

    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    private String[] tileMap = {
                "XXXXXXXXXXXXXXXXXXX",
                "X        X        X",
                "X XX XXX X XXX XX X",
                "X                 X",
                "X XX X XXXXX X XX X",
                "X    X       X    X",
                "XXXX XXXX XXXX XXXX",
                "OOOX X       X XOOO",
                "XXXX X XXrXX X XXXX",
                "X       bpo       X",
                "XXXX X XXXXX X XXXX",
                "OOOX X       X XOOO",
                "XXXX X XXXXX X XXXX",
                "X        X        X",
                "X XX XXX X XXX XX X",
                "X  X     P     X  X",
                "XX X X XXXXX X X XX",
                "X    X   X   X    X",
                "X XXXXXX X XXXXXX X",
                "X                 X",
                "XXXXXXXXXXXXXXXXXXX"
        };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    Timer gameLoop;


    PacMan () {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        wallImage = new ImageIcon(App.class.getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(App.class.getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(App.class.getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(App.class.getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(App.class.getResource("./redGhost.png")).getImage();

        pacmanUpImage = new ImageIcon(App.class.getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(App.class.getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(App.class.getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(App.class.getResource("./pacmanRight.png")).getImage();

        loadMap();
        gameLoop = new Timer(50, this);

    }

    public void loadMap() {
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                String row = tileMap[r];
                char tileChar = row.charAt(c);

                int x = c*tileSize;
                int y = r*tileSize;

                if(tileChar=='X') {
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);
                } else if (tileChar == 'b') {
                    Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tileChar == 'o') {
                    Block ghost = new Block(orangeGhostImage, x, y, tileSize,tileSize);
                    ghosts.add(ghost);
                } else if (tileChar == 'p') {
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tileChar == 'r') {
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tileChar == 'P') {
                    pacman = new Block(pacmanLeftImage, x, y, tileSize, tileSize);
                } else if (tileChar == ' ') {
                    Block food = new Block(null, x+14, y+14, 4, 4);
                    foods.add(food);
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for (Block wall : walls) {
            g.drawImage(wallImage, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setColor(Color.WHITE);
        for(Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {
        System.out.println("Key event: " + e.getKeyCode());
    }

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }
    }
}
