package meafocus.asteriodes.data;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;

public class BoxData {

    public Bitmap boxBitmap;
    public float x, y, yDiff, rotation, rotationDiff;
    public Rect position;
    public BoxOpenedListener listener;

    public BoxData(Bitmap boxBitmap, BoxOpenedListener listener) {
        this.boxBitmap = boxBitmap;
        this.listener = listener;
        x = (float) Math.random();
        y = -boxBitmap.getHeight();
        rotationDiff = (float) Math.random() - 0.5f;
        yDiff = (float) (Math.random() * 3) + 1;
    }

    /**
     * Opens the box; gives the player whatever is inside it.
     */
    public void open() {
        listener.onBoxOpened(this);
    }

    /**
     * Calculates the position Matrix of the box to draw in the
     * next frame.
     *
     * @param speed         The speed of the box.
     * @param width         The width of the drawing canvas.
     * @param height        The height of the drawing canvas.
     * @return              The position Matrix of the box. Equals
     *                      null if the box can no longer be drawn
     *                      within the given width/height.
     */
    public Matrix next(float speed, int width, int height) {
        if ((y - boxBitmap.getHeight()) < height) {
            y += yDiff * speed;
            rotation += rotationDiff;
        } else return null;

        float left = x * width, top = y;
        position = new Rect(
                (int) left - (boxBitmap.getWidth() / 2),
                (int) top - (boxBitmap.getHeight() / 2),
                (int) left + (boxBitmap.getWidth() / 2),
                (int) top + (boxBitmap.getHeight() / 2)
        );

        Matrix matrix = new Matrix();
        matrix.postTranslate(-boxBitmap.getWidth() / 2, -boxBitmap.getHeight() / 2);
        matrix.postRotate(rotation);
        matrix.postTranslate(left, top);

        return matrix;
    }

    public interface BoxOpenedListener {
        void onBoxOpened(BoxData box);
    }

}
