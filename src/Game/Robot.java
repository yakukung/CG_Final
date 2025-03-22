// Robot
package Game;

import com.jogamp.opengl.GL2; 
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.newt.opengl.GLWindow;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.io.File;
import java.io.IOException;
import java.awt.Font;

public class Robot implements GLEventListener {
    private float x, y;
    private final float width = 50, height = 50;
    private final float speed = 10.0f;

    private int screenWidth, screenHeight;
    private int health = 3;
    private boolean gameOver = false;
    private boolean isPaused = false;
    private boolean hasWon = false;
    
    private TextRenderer textRenderer;
    private Texture heartTexture;
    private final int heartSize = 30;
    
    private float mazeOffsetX;
    private float mazeOffsetY;
    
    private Maze maze;
    private Item item;
    private GameUI gameUI;

    public Robot() {
        maze = new Maze();
        item = new Item();
        gameUI = new GameUI();
        resetPosition();
    }

    private void calculateCenterPosition() {
        mazeOffsetX = (screenWidth - (maze.getWidth() * width)) / 2;
        mazeOffsetY = (screenHeight - (maze.getHeight() * height)) / 2;
    }

    public void resetPosition() {
        x = mazeOffsetX + width;
        y = mazeOffsetY + height;
    }
   
    public void move(float dx, float dy) {
        if (gameOver || isPaused) return;

        int nextX = (int)((x + dx - mazeOffsetX) / width);
        int nextY = (int)((y + dy - mazeOffsetY) / height);

        System.out.println("Move to: (" + nextX + ", " + nextY + "), Value: " + maze.getMazeData()[nextY][nextX]); // ✅ Debug

        if (maze.isWalkable(nextX, nextY)) {
        	if (maze.isExit(nextX, nextY)) {
        	    if (item.isCollected()) { // ✅ ตรวจสอบว่าเก็บไอเทมหรือยัง
        	        System.out.println("Robot รู้ว่าไอเทมถูกเก็บแล้ว!");
        	        System.out.println("🎉 Game Win! 🎉");
        	        gameOver = true;
        	        hasWon = true;
        	    } else {
        	        System.out.println("🚫 ต้องเก็บไอเทมก่อนถึงจะออกได้!");
                    gameOver = true;
                    hasWon = false;
                    System.out.println("Game Over!");
        	    }
        	    return;
        	}


            x += dx;
            y += dy;
            item.checkCollision(x, y, width, height);
        } else {
            takeDamage();
        }
    }

    private void takeDamage() {
        health--;
        System.out.println("หุ่นยนต์ชนผนัง! ชีวิตที่เหลือ: " + health);

        if (health <= 0) {
            gameOver = true;
            hasWon = false;
            System.out.println("Game Over!");
        } else {
            resetPosition();
        }
    }

    public void togglePause() {
        isPaused = !isPaused;
        System.out.println("Paused: " + isPaused);
    }
    
    private void restartGame() {
        health = 3;
        gameOver = false;
        hasWon = false;
        resetPosition();
        item.reset(mazeOffsetX, mazeOffsetY, width, height, maze);
        System.out.println("เริ่มเกมใหม่!");
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        textRenderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 24));

        screenWidth = drawable.getSurfaceWidth();
        screenHeight = drawable.getSurfaceHeight();
        
        calculateCenterPosition();

        try {
            File heartFile = new File("/Users/yakukung/eclipse-workspace/MyFinal/heart.png");
            if (heartFile.exists()) {
                heartTexture = TextureIO.newTexture(heartFile, true);
                System.out.println("โหลดรูปหัวใจสำเร็จ");
            } else {
                System.err.println("ไม่พบไฟล์รูปหัวใจ: " + heartFile.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("ไม่สามารถโหลดรูปหัวใจได้: " + e.getMessage());
            e.printStackTrace();
        }

        resetPosition();
        gameUI.init(screenWidth, screenHeight);
        item.init(mazeOffsetX, mazeOffsetY, width, height, maze);

        GLWindow window = (GLWindow) drawable;
        window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameOver) {
                    if (e.getKeyCode() == KeyEvent.VK_R) {
                        restartGame();
                    }
                    return;
                }

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W:
                        move(0, speed);
                        break;
                    case KeyEvent.VK_S:
                        move(0, -speed);
                        break;
                    case KeyEvent.VK_A:
                        move(-speed, 0);
                        break;
                    case KeyEvent.VK_D:
                        move(speed, 0);
                        break;
                    case KeyEvent.VK_P:
                        togglePause();
                        break;
                }
            }
        });
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

        maze.draw(gl, mazeOffsetX, mazeOffsetY, width, height);

        if (isPaused && !gameOver) {
            gameUI.drawPauseScreen(textRenderer, screenWidth, screenHeight);
            return;
        }

        if (!gameOver) {
            // Draw the robot
            gl.glColor3f(0.5f, 0.5f, 0.8f);
            gl.glBegin(GL2.GL_QUADS);
            gl.glVertex2f(x, y);
            gl.glVertex2f(x + width, y);
            gl.glVertex2f(x + width, y + height);
            gl.glVertex2f(x, y + height);
            gl.glEnd();
            
            // Draw items
            item.draw(gl);
            
            // Draw health
            drawHealth(gl);
        } else {
            // Check if player has won or lost
            if (hasWon) {
                gameUI.drawWinScreen(textRenderer, screenWidth, screenHeight);
            } else {
                gameUI.drawGameOverScreen(textRenderer, screenWidth, screenHeight);
            }
        }
    }
    
    private void drawHealth(GL2 gl) {
        if (heartTexture != null) {
            gl.glEnable(GL2.GL_TEXTURE_2D);
            heartTexture.bind(gl);
            gl.glColor3f(1.0f, 1.0f, 1.0f);
            
            for (int i = 0; i < health; i++) {
                float heartX = 20 + i * (heartSize + 5);
                float heartY = screenHeight - heartSize - 20;
                
                gl.glBegin(GL2.GL_QUADS);
                gl.glTexCoord2f(0, 0); gl.glVertex2f(heartX, heartY);
                gl.glTexCoord2f(1, 0); gl.glVertex2f(heartX + heartSize, heartY);
                gl.glTexCoord2f(1, 1); gl.glVertex2f(heartX + heartSize, heartY + heartSize);
                gl.glTexCoord2f(0, 1); gl.glVertex2f(heartX, heartY + heartSize);
                gl.glEnd();
            }
            
            gl.glDisable(GL2.GL_TEXTURE_2D);
        } else {
            // Fallback if texture loading failed
            textRenderer.beginRendering(screenWidth, screenHeight);
            textRenderer.setColor(1.0f, 0.0f, 0.0f, 1.0f);
            textRenderer.draw("Health: " + health, 20, screenHeight - 40);
            textRenderer.endRendering();
        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        if (heartTexture != null) {
            heartTexture.destroy(drawable.getGL().getGL2());
        }
        if (textRenderer != null) {
            textRenderer.dispose();
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, width, 0, height, -1, 1);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        
        screenWidth = width;
        screenHeight = height;
        calculateCenterPosition();
        resetPosition();
        item.updatePositions(mazeOffsetX, mazeOffsetY, this.width, this.height);
    }
}