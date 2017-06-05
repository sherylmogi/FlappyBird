package flappyBird;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;

public class FlappyBird implements ActionListener, KeyListener
{

    public static FlappyBird flappyBird;

    public int WIDTH = 800, HEIGHT = 800;

    public Renderer renderer;

    public Rectangle bird;

    public ArrayList<Rectangle> columns;

    public int ticks, yMotion, score;

    public boolean gameOver, started;

    public Random rand;

    public FlappyBird()
    {
        JFrame jframe = new JFrame();
        Timer timer = new Timer(20, this);

        renderer = new Renderer();
        rand = new Random();

        jframe.add(renderer);
        jframe.setTitle("Flappy Square");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(WIDTH, HEIGHT);
        jframe.addKeyListener(this);
        jframe.setResizable(false);
        jframe.setVisible(true);

        bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
        columns = new ArrayList<Rectangle>();

        addColumn(true);
        addColumn(true);
        addColumn(true);
        addColumn(true);

        timer.start();
    }

    public void addColumn(boolean start)
    {
        int space = 300;
        int width = 100;
        int height = 50 + rand.nextInt(300);

        if (start)
        {   //bottom rectangle
            columns.add(new Rectangle(WIDTH + width+ columns.size()*300/*gap between columns*/ , HEIGHT - height - 120, width, height));
            //minus 120, subtract the height of grass
            
            //top rectangle
            columns.add(new Rectangle(WIDTH + width + (columns.size()-1 )* 300 /*gap between columns*/, 0, width, HEIGHT - height - space));
            //if game just started, put columns here
        } 
        else
        { 
            columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600, HEIGHT - height - 120, width, height));
            columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT - height - space));
            //adds more rectangles 
            //if game has not just started, align the next column with previous column
            
        }
    }

    public void paintColumn(Graphics g, Rectangle column)
    {
        g.setColor(Color.green.darker().darker().darker());
        g.fillRect(column.x, column.y, column.width, column.height);
    }

    public void jump()
    {
        if (gameOver) // reset game
        {
            bird = new Rectangle(WIDTH / 2 - 100, HEIGHT / 2 - 10, 20, 20);
            
            yMotion = 0;
            score = 0;
            columns.clear();// clears rectangles when game resets
            
            addColumn(true);
            addColumn(true);
            addColumn(true);
            addColumn(true);
            gameOver = false; 
        }

        if (!started)
        {
            started = true;
        }
        else if (!gameOver)
        {
            
            if (yMotion > 0)
            {
                yMotion =0;
            }
            yMotion -=10; // everytime space bar is pushed, it moves up by 10
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        int speed = 5;

        ticks++;

        if (started)
        {
            for (int i = 0; i < columns.size(); i++)
            {
                Rectangle column = columns.get(i);
                column.x -= speed;
                //takes the x coordinate of a column and subtracts it by the speed of the game
                //moves the columns left
            }

            if (ticks % 2 == 0 && yMotion < 15)
            {
                yMotion += 2;
                //controls gravity/ falling speed of the bird
            }

            for (int i = 0; i < columns.size(); i++)
            {
                Rectangle column = columns.get(i);

                if (column.x + column.width < 0)
                {
                    columns.remove(column);
                    //if column is out of the window, remove it
                    if (column.y == 0)
                    {
                        addColumn(false);  // columns continue to be added in a loop
                    }
                
                    
                }
            }
            bird.y += yMotion; // bird has y motion
            

            for (Rectangle column : columns)
            {
                if (column.y == 0 && bird.x + bird.width / 2 > column.x + column.width / 2 - 5 && bird.x + bird.width / 2 < column.x + column.width / 2 + 5)
                {
                    score++; //scoreboard
                }

                if (column.intersects(bird))
                {
                    gameOver = true;

                    if (bird.x <= column.x) //just checks if the bird is inside the tube, then it moves it out . fixes glitches
                    { ,
                        bird.x = column.x - bird.width; // if game over doesn't happen, bird resets outside (left) of tunnel

                    }
                    else
                    {
                        if (column.y != 0)
                        {
                            bird.y = column.y - bird.height; // makes sure bird doesn't keep flying into tunnel, resets bird coordinates
                        }
                        else if (bird.y < column.height)
                        {
                            bird.y = column.height;
                        }
                    }
                }
            }

            if (bird.y > HEIGHT - 120 || bird.y < 0)
            {
                gameOver = true; //if bird touches ceilling or floor, game over
            }

            if (bird.y + yMotion >= HEIGHT - 120)
            {
                bird.y = HEIGHT - 120 - bird.height; // when bird dies, it stays on ground. wont keep falling
                gameOver = true;
            }
        }

        renderer.repaint();
    }

    public void repaint(Graphics g)
    {
        g.setColor(Color.cyan);
        g.fillRect(0, 0, WIDTH, HEIGHT); //blue background

        g.setColor(Color.orange);
        g.fillRect(0, HEIGHT - 120, WIDTH, 120); //floor 

        g.setColor(Color.green);
        g.fillRect(0, HEIGHT - 120, WIDTH, 20);//floor

        g.setColor(Color.red);
        g.fillRect(bird.x, bird.y, bird.width, bird.height); // bird

        for (Rectangle column : columns)
        {
            paintColumn(g, column);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Comic Sans MS", Font.PLAIN , 100/*font size*/)); //set font

        if (!started)
        { g.setFont(new Font("Comic Sans MS", Font.PLAIN , 50/*font size*/));
            g.drawString("Press Spacebar to start!:D", 75, HEIGHT - 500);
        }

        if (gameOver)
        {
            g.drawString("Game Over!", 100 /*width*/, HEIGHT -500);
        } 

        if (!gameOver && started)
        {
            g.drawString(String.valueOf(score), WIDTH-500, 100);
        }
    }

    public static void main(String[] args)
    {
        flappyBird = new FlappyBird(); //creates variable as new instance of class 
    }

    
    public void keyReleased(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_SPACE)
        {
            jump();
        }
    }
    
    public void keyTyped(KeyEvent e)
    {

    }

    
    public void keyPressed(KeyEvent e)
    {

    }

}