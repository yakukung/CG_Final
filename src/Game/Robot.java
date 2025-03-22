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
    private float x, y; // ตำแหน่งของหุ่นยนต์
    private final float width = 50, height = 50; // ขนาดของหุ่นยนต์
    private final float speed = 10.0f; // ความเร็วของหุ่นยนต์

    private int screenWidth, screenHeight;
    private int health = 3; // จำนวนชีวิตเริ่มต้น
    private boolean gameOver = false;
    private boolean isPaused = false; // สถานะหยุดเกม
    
    private TextRenderer textRenderer;
    private Texture heartTexture; // เพิ่มตัวแปรสำหรับเก็บ texture รูปหัวใจ
    private final int heartSize = 30; // ขนาดของไอคอนหัวใจ
    
    private float mazeOffsetX, mazeOffsetY;
    
    // สร้างอ็อบเจ็กต์ของคลาสอื่นๆ
    private Maze maze;
    private Item item;
    private GameUI gameUI;

    public Robot() {
        maze = new Maze();
        item = new Item();
        gameUI = new GameUI();
        resetPosition();
    }

    public void resetPosition() {
        x = mazeOffsetX + width;
        y = mazeOffsetY + height;
    }
    
    public void move(float dx, float dy) {
        if (gameOver || isPaused) return; // ถ้าเกมจบหรือหยุดอยู่ ให้ไม่เคลื่อนที่

        // คำนวณตำแหน่งที่หุ่นยนต์จะเคลื่อนที่ไป
        int nextX = (int)((x + dx - mazeOffsetX) / width); // ตำแหน่งที่คำนวณตามพิกัดของเขาวงกต
        int nextY = (int)((y + dy - mazeOffsetY) / height);

        // ตรวจสอบตำแหน่งใหม่
        if (maze.isWalkable(nextX, nextY)) { // ถ้าเป็นทางเดิน (ไม่ชนผนัง)
            x += dx;
            y += dy;
            
            // ตรวจสอบการเก็บไอเทม
            item.checkCollision(x, y, width, height);
        } else { // ถ้าชนผนัง
            takeDamage();
        }
    }

    private void takeDamage() {
        health--; // ลดจำนวนชีวิต
        System.out.println("หุ่นยนต์ชนผนัง! ชีวิตที่เหลือ: " + health);

        // ถ้าชีวิตหมด
        if (health <= 0) {
            gameOver = true;
            System.out.println("Game Over!");
        } else {
            resetPosition(); // รีเซ็ตตำแหน่งหุ่นยนต์
        }
    }

    public void togglePause() {
        isPaused = !isPaused;
        System.out.println("Paused: " + isPaused);
    }
    
    private void restartGame() {
        health = 3;
        gameOver = false;
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

        // โหลดรูปหัวใจ
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
                if (gameOver) return;

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
                case KeyEvent.VK_R:
                    if (gameOver) restartGame();
                    break;
                }
            }
        });
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        
        // วาดเขาวงกต
        maze.draw(gl, mazeOffsetX, mazeOffsetY, width, height);
        
        if (isPaused && !gameOver) {
            gameUI.drawPauseScreen(textRenderer, screenWidth, screenHeight);
            return;
        }

        // วาดหุ่นยนต์
        if (!gameOver) {
            gl.glColor3f(0.5f, 0.5f, 0.8f);
            gl.glBegin(GL2.GL_QUADS);
            gl.glVertex2f(x, y);
            gl.glVertex2f(x + width, y);
            gl.glVertex2f(x + width, y + height);
            gl.glVertex2f(x, y + height);
            gl.glEnd();
        }

        // วาดหัวใจสำหรับชีวิต
        if (heartTexture != null) {
            gl.glEnable(GL2.GL_TEXTURE_2D);
            gl.glEnable(GL2.GL_BLEND);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
            
            heartTexture.bind(gl);
            gl.glColor3f(1.0f, 1.0f, 1.0f);

            for (int i = 0; i < health; i++) { // วาดหัวใจตามจำนวนชีวิตที่เหลือ
                int heartX = 20 + (i * (heartSize + 5));
                int heartY = screenHeight - 40 - heartSize / 2;

                gl.glBegin(GL2.GL_QUADS);
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(heartX, heartY);
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(heartX + heartSize, heartY);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(heartX + heartSize, heartY + heartSize);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(heartX, heartY + heartSize);
                gl.glEnd();
            }
            
            gl.glDisable(GL2.GL_BLEND);
            gl.glDisable(GL2.GL_TEXTURE_2D);
        }
        
        // วาดไอเทม
        item.draw(gl);
        
        // วาด UI
        if (gameOver) {
            gameUI.drawGameOverScreen(textRenderer, screenWidth, screenHeight);
        }

        gl.glFlush();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();

        screenWidth = width;
        screenHeight = height;

        // คำนวณตำแหน่งเริ่มต้นของเขาวงกตให้อยู่กลางหน้าจอ
        mazeOffsetX = (screenWidth - maze.getWidth() * this.width) / 2.0f;
        mazeOffsetY = (screenHeight - maze.getHeight() * this.height) / 2.0f;

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, width, 0, height, -1, 1);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        resetPosition();
        gameUI.updateDimensions(width, height);
        item.updatePosition(mazeOffsetX, mazeOffsetY, this.width, this.height, maze);
    }
    
    @Override
    public void dispose(GLAutoDrawable drawable) {
        textRenderer.dispose();
        if (heartTexture != null) {
            heartTexture.destroy(drawable.getGL().getGL2());
        }
    }
}