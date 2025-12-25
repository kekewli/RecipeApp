package com.example.recipeapp.api;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import java.lang.ref.WeakReference;
public class SafeContext {
    private static final String TAG = "SafeContext";
    private final WeakReference<Context> contextRef;
    private final String contextInfo;
    public SafeContext(Context context) {
        this.contextRef = new WeakReference<>(context);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            this.contextInfo = activity.getClass().getSimpleName();
            Log.d(TAG, "SafeContext создан для" + contextInfo);
        } else {
            this.contextInfo = context.getClass().getSimpleName();
            Log.d(TAG, "SafeContext создан для" + contextInfo);
        }
    }
    public Context get() {
        Context ctx = contextRef.get();
        if (ctx == null) {
            Log.w(TAG, "Контекст отсутсвует " + contextInfo);
        }
        return ctx;
    }
    public boolean isAlive() {
        Context ctx = contextRef.get();
        if (ctx == null) {
            Log.w(TAG, "Ошибка  " + contextInfo + " - Контекст пуст");
            return false;
        }
        if (ctx instanceof Activity) {
            Activity activity = (Activity) ctx;
            boolean isAlive = !activity.isDestroyed() && !activity.isFinishing();
            if (!isAlive) {
                Log.w(TAG, "Ошибка  " + contextInfo + " - isDestroyed=" + activity.isDestroyed() +
                        ", isFinishing=" + activity.isFinishing());
            }
            return isAlive;
        }
        return true;
    }
}