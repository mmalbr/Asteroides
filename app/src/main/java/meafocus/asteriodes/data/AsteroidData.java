package meafocus.asteriodes.data;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;

public class AsteroidData {

    public Bitmap asteroidBitmap;
    public float x, xDiff, y, yDiff, rotation, rotationDiff;
    public Rect position;

    public AsteroidData(Bitmap asteroidBitmap) {
        this.asteroidBitmap = asteroidBitmap;
        x = (float) Math.random();
        y = -asteroidBitmap.getHeight();
        rotationDiff = (float) Math.random() - 0.5f;
        xDiff = (float) (Math.random() - 0.5) * 0.002f;
        yDiff = (float) (Math.random() * 6) + 1;
    }

    /**
     * Calculates the position Matrix of the asteroid to draw
     * in the next frame.
     *
     * @param speed     The speed of the asteroid.
     * @param width     The width of the drawing canvas.
     * @param height    The height of the drawing canvas.
     * @return          The position Matrix of the asteroid.
     *                  Equals null if the asteroid can no longer
     *                  be drawn within the given width/height.
     */
    public Matrix next(float speed, int width, int height) {
        if ((y - asteroidBitmap.getHeight()) < height) {
            y += yDiff * speed;
            rotation += rotationDiff;
            x += xDiff * speed;
        } else return null;

        float left = x * width, top = y;
        position = new Rect(
                (int) left - (asteroidBitmap.getWidth() / 2),
                (int) top - (asteroidBitmap.getHeight() / 2),
                (int) left + (asteroidBitmap.getWidth() / 2),
                (int) top + (asteroidBitmap.getHeight() / 2)
        );

        Matrix matrix = new Matrix();
        matrix.postTranslate(-asteroidBitmap.getWidth() / 2, -asteroidBitmap.getHeight() / 2);
        matrix.postRotate(rotation);
        matrix.postTranslate(left, top);

        return matrix;
    }

}
