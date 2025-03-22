// Maze.java
package Game;

import com.jogamp.opengl.GL2;

public class Maze {
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
        {1, 1, 1, 1, 1, 1, 1, 1, 2, 1}
    };

    private final int mazeWidth = 10;
    private final int mazeHeight = 10;
    
    public Maze() {}

    public boolean isExit(int x, int y) {
        return y >= 0 && y < getHeight() && x >= 0 && x < getWidth() && mazeData[y][x] == 2;
    }

    public int getWidth() {
        return mazeWidth;
    }

    public int getHeight() {
        return mazeHeight;
    }

    public boolean isWalkable(int x, int y) {
        if (x < 0 || x >= mazeWidth || y < 0 || y >= mazeHeight) {
            return false;
        }
        return mazeData[y][x] == 0 || mazeData[y][x] == 2;
    }

    public int[][] getMazeData() {
        return mazeData;
    }

    public void draw(GL2 gl, float offsetX, float offsetY, float cellWidth, float cellHeight) {
        for (int j = 0; j < mazeHeight; j++) {  // ✅ ใช้ j เป็น y
            for (int i = 0; i < mazeWidth; i++) {  // ✅ ใช้ i เป็น x
                float cellX = offsetX + i * cellWidth;
                float cellY = offsetY + j * cellHeight;
                
                if (mazeData[j][i] == 1) { // ✅ ใช้ j ก่อน i
                    gl.glColor3f(0.8f, 0.8f, 0.8f); // สีของผนัง
                    gl.glBegin(GL2.GL_QUADS);
                    gl.glVertex2f(cellX, cellY);
                    gl.glVertex2f(cellX + cellWidth, cellY);
                    gl.glVertex2f(cellX + cellWidth, cellY + cellHeight);
                    gl.glVertex2f(cellX, cellY + cellHeight);
                    gl.glEnd();
                } else if (mazeData[j][i] == 2) { // ✅ ใช้ j ก่อน i
                    gl.glColor3f(0.0f, 0.8f, 0.0f); // สีเขียวสำหรับเส้นชัย
                    gl.glBegin(GL2.GL_QUADS);
                    gl.glVertex2f(cellX, cellY);
                    gl.glVertex2f(cellX + cellWidth, cellY);
                    gl.glVertex2f(cellX + cellWidth, cellY + cellHeight);
                    gl.glVertex2f(cellX, cellY + cellHeight);
                    gl.glEnd();
                }
            }
        }
    }
}
