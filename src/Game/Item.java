// Item
package Game;

import com.jogamp.opengl.GL2;
import java.util.Random;

public class Item {
    private float x, y; // ตำแหน่งของไอเทม
    private final float size = 20; // ขนาดของไอเทม
    private boolean collected = false; // สถานะการเก็บไอเทม
    private Random random = new Random(); // Random number generator
    private int collectedCount = 0; // Counter for item collection
    private final int maxDrops = 2; // จำนวนการดรอปของไอเทม
    
    public Item() {
        // Constructor
    }
    
    public void init(float mazeOffsetX, float mazeOffsetY, float cellWidth, float cellHeight, Maze maze) {
        respawnItem(mazeOffsetX, mazeOffsetY, cellWidth, cellHeight, maze);
    }
    
    public void reset(float mazeOffsetX, float mazeOffsetY, float cellWidth, float cellHeight, Maze maze) {
        collected = false;
        collectedCount = 0;
        respawnItem(mazeOffsetX, mazeOffsetY, cellWidth, cellHeight, maze);
    }
    
    public void updatePosition(float mazeOffsetX, float mazeOffsetY, float cellWidth, float cellHeight, Maze maze) {
        if (collected) {
            respawnItem(mazeOffsetX, mazeOffsetY, cellWidth, cellHeight, maze);
        }
    }
    
    public void checkCollision(float playerX, float playerY, float playerWidth, float playerHeight) {
        if (!collected) {
            // ตรวจสอบการชนกันระหว่างผู้เล่นและไอเทม
            boolean collisionX = playerX + playerWidth >= x && x + size >= playerX;
            boolean collisionY = playerY + playerHeight >= y && y + size >= playerY;
            
            if (collisionX && collisionY) {
                collected = true;
                collectedCount++;
                System.out.println("เก็บไอเทมได้! จำนวนที่เก็บได้: " + collectedCount);
            }
        }
    }
    
    private void respawnItem(float mazeOffsetX, float mazeOffsetY, float cellWidth, float cellHeight, Maze maze) {
        if (collectedCount >= maxDrops) {
            collected = true; // ไม่แสดงไอเทมอีกถ้าเก็บครบแล้ว
            return;
        }

        int[][] mazeData = maze.getMazeData();
        int mazeWidth = maze.getWidth();
        int mazeHeight = maze.getHeight();

        // ตรวจสอบว่ามีช่องว่าง (0) ใน maze หรือไม่
        boolean hasValidSpot = false;
        for (int i = 0; i < mazeWidth; i++) {
            for (int j = 0; j < mazeHeight; j++) {
                if (mazeData[i][j] == 0) {
                    hasValidSpot = true;
                    break;
                }
            }
            if (hasValidSpot) break;
        }

        // ถ้าไม่มีที่ว่างสำหรับไอเทม ก็ไม่ต้อง spawn
        if (!hasValidSpot) {
            collected = true;
            return;
        }

        int newItemX, newItemY;
        do {
            newItemX = random.nextInt(mazeWidth);
            newItemY = random.nextInt(mazeHeight);
        } while (mazeData[newItemX][newItemY] != 0); // ต้องอยู่บนช่องที่เดินได้ (0)

        // แปลงตำแหน่งในเขาวงกตเป็นพิกัดจริงบนหน้าจอ
        x = mazeOffsetX + newItemX * cellWidth + (cellWidth - size) / 2;
        y = mazeOffsetY + newItemY * cellHeight + (cellHeight - size) / 2;

        collected = false;
    }

    
    public void draw(GL2 gl) {
        if (!collected) {
            gl.glColor3f(1.0f, 0.5f, 0.0f); // สีของไอเทม (สีส้ม)
            gl.glBegin(GL2.GL_QUADS);
            gl.glVertex2f(x, y);
            gl.glVertex2f(x + size, y);
            gl.glVertex2f(x + size, y + size);
            gl.glVertex2f(x, y + size);
            gl.glEnd();
        }
    }
    
    public void updatePositions(float mazeOffsetX, float mazeOffsetY, float cellWidth, float cellHeight) {
        // Recalculate item position based on new maze offset
        if (!collected) {
            // Convert current position back to maze coordinates
            int mazeX = Math.round((x - mazeOffsetX) / cellWidth);
            int mazeY = Math.round((y - mazeOffsetY) / cellHeight);
            
            // Update to new screen coordinates
            x = mazeOffsetX + mazeX * cellWidth + (cellWidth - size) / 2;
            y = mazeOffsetY + mazeY * cellHeight + (cellHeight - size) / 2;
        }
    }
    
    public boolean isCollected() {
        return collected;
    }
    
    
}