package Game;

import com.jogamp.opengl.GL2;

public class Maze {
    // สร้างเขาวงกต (Maze) โดยใช้ 0 เป็นทางเดิน และ 1 เป็นผนัง
    private final int[][] mazeData = {
            {1, 0, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 1, 0, 0, 0, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 1, 0, 1},
            {1, 0, 1, 0, 0, 0, 1, 0, 0, 1},
            {1, 0, 1, 1, 1, 1, 1, 0, 1, 1},
            {1, 0, 0, 0, 0, 0, 1, 0, 0, 1},
            {1, 1, 1, 1, 1, 0, 1, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 1, 0, 1},
            {1, 0, 1, 1, 1, 1, 1, 1, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 0, 1}
    };
    private final int mazeWidth = 10;
    private final int mazeHeight = 10;
    
    public Maze() {
        // Constructor
    }
    
    public int getWidth() {
        return mazeWidth;
    }
    
    public int getHeight() {
        return mazeHeight;
    }
    
    public boolean isWalkable(int x, int y) {
        // ตรวจสอบว่าพิกัดอยู่ในขอบเขตของเขาวงกตหรือไม่
        if (x < 0 || x >= mazeWidth || y < 0 || y >= mazeHeight) {
            return false;
        }
        
        // ตรวจสอบว่าเป็นทางเดิน (0) หรือไม่
        return mazeData[x][y] == 0;
    }
    
    public int[][] getMazeData() {
        return mazeData;
    }
    
    public void draw(GL2 gl, float offsetX, float offsetY, float cellWidth, float cellHeight) {
        gl.glColor3f(0.8f, 0.8f, 0.8f); // กำหนดสีของผนังเขาวงกต
        for (int i = 0; i < mazeWidth; i++) {
            for (int j = 0; j < mazeHeight; j++) {
                if (mazeData[i][j] == 1) { // ถ้าเป็นผนัง
                    float wallX = offsetX + i * cellWidth;
                    float wallY = offsetY + j * cellHeight;
                    gl.glBegin(GL2.GL_QUADS);
                    gl.glVertex2f(wallX, wallY);
                    gl.glVertex2f(wallX + cellWidth, wallY);
                    gl.glVertex2f(wallX + cellWidth, wallY + cellHeight);
                    gl.glVertex2f(wallX, wallY + cellHeight);
                    gl.glEnd();
                }
            }
        }
    }
}