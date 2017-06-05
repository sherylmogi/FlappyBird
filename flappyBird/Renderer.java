package flappyBird;
import java.awt.Graphics;
import javax.swing.JPanel;
public class Renderer extends JPanel
{
    
  protected void paintComponent (Graphics g) {
      super.paintComponent(g); // calling the code in parent class JPanel
      
      FlappyBird.flappyBird.repaint(g);//continuosly updates
    }
}
