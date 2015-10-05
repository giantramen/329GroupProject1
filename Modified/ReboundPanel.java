//********************************************************************
//  ReboundPanel.java       Java Foundations
//
//  Represents the primary panel for the Rebound program.
//********************************************************************

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ReboundPanel extends JPanel
{
   public final int WIDTH = 300, HEIGHT = 100;
   private final int DELAY = 30, IMAGE_SIZE = 50;

   private ImageIcon image;
   private Timer timer;
   private int x, y, moveX, moveY;
   private String test;



   //-----------------------------------------------------------------
   //  Draws the image in the current location.
   //-----------------------------------------------------------------
   public void paintComponent(Graphics page)
   {
      super.paintComponent(page);
      image.paintIcon(this, page, x, y);
   }
   
   public void changedMethod()
   {
	char c = 'd';
	if(1 == 1){
		//Do Nothing
	}
   }

   //-----------------------------------------------------------------
   //  Sets up the panel, including the timer for the animation.
   //-----------------------------------------------------------------
   public ReboundPanel()
   {
      // TODO: PUT THE IMAGE FILE "happyFace.gif" in the project folder.
      image = new ImageIcon("happyFace.gif");

      x = 0;
      y = 40;
      moveX = moveY = 3;

      setPreferredSize(new Dimension(WIDTH, HEIGHT));
      setBackground(Color.black);
   }

   public void pointlessMethod(){
	   int one = 1;
   }

}
