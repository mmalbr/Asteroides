package meafocus.asteriodes.data.drawer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.StringRes;
import meafocus.asteriodes.data.DrawerData;

public class MessageDrawer extends DrawerData {

    private static final long MESSAGE_TRANSITION = 250;

    private final List<String> messages;
    private long messageTime;

    private final Paint paint;

    public MessageDrawer(Paint paint) {
        this.paint = paint;
        messages = new ArrayList<>();
    }

    /**
     * Display a new message! Gets a translated string resource then passes
     * the string to the overloaded method which then does the actual thing.
     *
     * @param context           An active context instance.
     * @param messageRes        The string resource of the message to display.
     */
    public void drawMessage(Context context, @StringRes int messageRes) {
        drawMessage(context.getString(messageRes));
    }

    /**
     * Display a new message! Shows it on the screen for an amount
     * of time, then fades out.
     *
     * @param message The message string to display.
     */
    public void drawMessage(String message) {
        messages.add(message);
        if (messages.size() == 1)
            messageTime = System.currentTimeMillis();
    }

    /**
     * Clears all pending messages.
     */
    public void clear() {
        while (messages.size() > 1)
            messages.remove(1);
    }

    @Override
    public boolean draw(Canvas canvas, float speed) {
        long diff = Math.abs(System.currentTimeMillis() - messageTime);

        if (messages.size() > 0) {
            String str = messages.get(0);
            long delay = Math.max(3000, str.length() * 100);
            if (diff < delay) {
                if (diff < MESSAGE_TRANSITION) {
                    paint.setAlpha((int) (255 * ((float) diff / MESSAGE_TRANSITION)));
                } else if (delay - diff < MESSAGE_TRANSITION) {
                    paint.setAlpha((int) (255 * ((float) (delay - diff) / MESSAGE_TRANSITION)));
                } else paint.setAlpha(255);

                float width = paint.measureText(str);
                int offsetX = (int) ((width / 2) - (width * ((float) diff / delay)));

                canvas.drawText(messages.get(0), (canvas.getWidth() / 2) + offsetX, canvas.getHeight() / 2, paint);
            } else {
                messages.remove(0);
                if (messages.size() > 0)
                    messageTime = System.currentTimeMillis();
            }
        }

        return true;
    }
}
