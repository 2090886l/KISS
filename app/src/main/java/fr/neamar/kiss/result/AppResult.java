package fr.neamar.kiss.result;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import fr.neamar.kiss.R;
import fr.neamar.kiss.pojo.AppPojo;

public class AppResult extends Result {
    private final AppPojo appPojo;

    private final ComponentName className;

    public AppResult(AppPojo appPojo) {
        super();
        this.pojo = this.appPojo = appPojo;

        className = new ComponentName(appPojo.packageName, appPojo.activityName);
    }

    @Override
    public View display(final Context context, int position, View v) {
        if (v == null)
            v = inflateFromId(context, R.layout.item_app);

        TextView appName = (TextView) v.findViewById(R.id.item_app_name);
        appName.setText(enrichText(appPojo.displayName));

        final ImageView appIcon = (ImageView) v.findViewById(R.id.item_app_icon);
        if (position < 10) {
            appIcon.setImageDrawable(this.getDrawable(context));
        } else {
            // Do actions on a message queue to avoid performance issues on main thread
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    appIcon.setImageDrawable(AppResult.this.getDrawable(context));
                }
            });
        }

        return v;
    }

    @Override
    public Drawable getDrawable(Context context) {
        try {
            return context.getPackageManager().getActivityIcon(className);
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    @Override
    public void doLaunch(Context context, View v) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(className);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Application was just removed?
            Toast.makeText(context, R.string.application_not_found, Toast.LENGTH_LONG).show();
        }
    }
}
