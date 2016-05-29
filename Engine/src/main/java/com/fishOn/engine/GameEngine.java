package main.java.com.fishOn.engine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class GameEngine extends JFrame implements Runnable
{
	private static final long serialVersionUID = -5024743624066497330L;
	static boolean isRunning = true;
	static int fps, windowWidth, windowHeight, x, y, dx, dy;

	private static Thread thread;
	private static boolean running = false;
	static BufferedImage bufferedImage;
	static Color color;
	static ImageHandler imageHandler;
	static InputHandler input;
	static Insets insets;
	Sprite mudkip;
	HitBox island;
	
	public synchronized void start() {
		running = true;
		thread = new Thread(this, "Display");
		thread.start();
	}
	
	public synchronized void stop() {
		running = true;
		try {
			thread.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run()
	{
		initialize();

		while(isRunning)
		{
			long time;

			time = System.currentTimeMillis();

			update();
			draw();

			time = (1000 / fps) - (System.currentTimeMillis() - time);

			if(time > 0)
			{
				try
				{
					Thread.sleep(time);
				}
				catch (InterruptedException e)
				{
					System.err.println("");
					e.printStackTrace();
				}
			}
		}
		setVisible(false);
	}

	public void initialize()
	{
		fps = Integer.parseInt(ResourceString.getString("FPS"));		
		imageHandler = new ImageHandler();
		input = new InputHandler(this);
		mudkip = new Sprite("mudkip");
		windowWidth = Integer.parseInt(ResourceString.getString("WindowWidth"));
		windowHeight = Integer.parseInt(ResourceString.getString("WindowHeight"));
		
		x = 10;
		y = 10;
		
		setTitle(ResourceString.getString("Title"));
		setSize(windowWidth, windowHeight);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		createBufferStrategy(2);
		
		island = new HitBox(200, 200, 200, 200);
		
		addMouseListener(input);
		insets = getInsets();
		setSize(insets.left + windowWidth + insets.right, insets.top + windowHeight + insets.bottom);
		bufferedImage = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_RGB);
	}

	public void update()
	{
		move(10);
	}
	
	public void move(int step)
	{
		if(input.isKeyDown(KeyEvent.VK_A))
		{
			if ((x < step) && (island.isCollide(mudkip.getHitBox().rectangle)))
				x = step;
			else
				x -= step;
		}

		if(input.isKeyDown(KeyEvent.VK_D))
		{
			if ((x > (windowWidth - step - mudkip.getHitBox().getWidth())) && (island.isCollide(mudkip.getHitBox().rectangle)))
				x = (int) (windowWidth - step - mudkip.getHitBox().getWidth());
			else
				x += step;
		}

		if(input.isKeyDown(KeyEvent.VK_W))
		{
			if ((y < step) && (island.isCollide(mudkip.getHitBox().rectangle)))
				y = step;
			else
				y -= step;
		}

		if(input.isKeyDown(KeyEvent.VK_S))
		{
			if ((y > (windowHeight - step - mudkip.getHitBox().getHeight())) && (island.isCollide(mudkip.getHitBox().rectangle)))
				y = (int) (windowHeight - step - mudkip.getHitBox().getHeight());
			else
				y += step;
		}
		
		mudkip.setPosx(x);
		mudkip.setPosy(y);
		
		if(island.isCollide(mudkip.getHitBox().rectangle))
		{
			System.out.println("COLLIDE!");
		}
	}

	public void draw()
	{
		BufferStrategy bufferStrategy;
		Graphics graphics, background;

		background = bufferedImage.getGraphics();
		
		bufferStrategy = getBufferStrategy();
		if(bufferStrategy == null)
		{
			createBufferStrategy(3);
		}

		background.setColor(Color.CYAN);
		background.fillRect(0, 0, windowWidth, windowHeight);
		background.setColor(Color.GREEN);
		background.fillRect(200, 200, 200, 200);
		background.drawImage(mudkip.getIcon(), mudkip.getPosx(), mudkip.getPosy(), this);
		graphics = bufferStrategy.getDrawGraphics();
		graphics.drawImage(bufferedImage, insets.left, insets.top, this);
		graphics.dispose();
		bufferStrategy.show();
	}
}