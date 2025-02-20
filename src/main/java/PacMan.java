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
    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gamerOver = false;


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
        for(Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
        gameLoop = new Timer(60, this);
        gameLoop.start();

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

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if(gamerOver) {
            g.drawString("Game over: " + String.valueOf(score), tileSize/2, tileSize/2);
        } else {
            g.drawString("Lives: " + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
    }

    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        for(Block wall : walls) {
            if(collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        for(Block ghost : ghosts) {
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for(Block wall : walls) {
                if(collision(wall, ghost)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }

        Block foodEaten = null;
        for(Block food : foods) {
            if(collision(pacman, food)) {
                foodEaten = food;
                score +=10;
            }
        }
        foods.remove(foodEaten);
    }

    public boolean collision(Block a, Block b) {
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {

        System.out.println(e.getKeyCode());

        if(e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
        } else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
        }

        if(pacman.direction=='U') {
            pacman.image = pacmanUpImage;
        } else if (pacman.direction=='D') {
            pacman.image = pacmanDownImage;
        } else if (pacman.direction=='L') {
            pacman.image = pacmanLeftImage;
        } else if (pacman.direction=='R') {
            pacman.image = pacmanRightImage;
        }
    }


    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;
        char type;

        int startX;
        int startY;

        char direction = 'U';
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }
        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for(Block wall : walls) {
                if (collision(this, wall)){
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            if(this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -tileSize/4;
            } else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = tileSize/4;
            } else if(this.direction == 'L') {
                this.velocityX = -tileSize/4;
                this.velocityY = 0;
            } else if(this.direction == 'R') {
                this.velocityX = tileSize/4;
                this.velocityY = 0;
            }
        }
    }
}
