package meafocus.asteriodes.data;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class DrawerData {

    private final Paint[] paints;

    public DrawerData(Paint... paints) {
        this.paints = paints;
    }

    public Paint paint(int paint) {
        return paints[paint];
    }

    public abstract boolean draw(Canvas canvas, float speed);

}
