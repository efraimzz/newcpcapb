package com.emanuelef.remote_capture.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.emanuelef.remote_capture.R;

public class AppListAdapter extends ArrayAdapter<AppItem> {

    private final Context context;
    private final List<AppItem> appList;
    private String mdmPackageName;

    public AppListAdapter(Context context, List<AppItem> appList) {
        super(context, R.layout.app_list_item, appList);
        this.context = context;
        this.appList = appList;
        this.mdmPackageName = context.getPackageName();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder; // הפוך את holder ל-final

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.app_list_item, parent, false);
            holder = new ViewHolder();
            holder.appIcon = (ImageView) convertView.findViewById(R.id.app_icon);
            holder.appName = (TextView) convertView.findViewById(R.id.app_name);
            holder.appPackage = (TextView) convertView.findViewById(R.id.app_package);
            holder.appLastUpdated = (TextView) convertView.findViewById(R.id.app_last_updated);
            holder.hideCheckbox = (CheckBox) convertView.findViewById(R.id.hide_checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final AppItem appItem = appList.get(position);

        holder.appIcon.setImageDrawable(appItem.getIcon());
        holder.appName.setText(appItem.getName());
        holder.appPackage.setText(appItem.getPackageName());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String lastUpdated = "עדכון אחרון: " + sdf.format(new Date(appItem.getLastUpdateTime()));
        holder.appLastUpdated.setText(lastUpdated);

        // **חשוב**: הגדר את מצב הצ'קבוקס ללא ליסנר כאן
        // אם הצ'קבוקס כבר מסומן, וזו אפליקציית ה-MDM, ודא שהוא לא מסומן.
        if (appItem.getPackageName().equals(mdmPackageName)) {
            holder.hideCheckbox.setChecked(false); // תמיד לא מסומן עבור ה-MDM app
            holder.hideCheckbox.setEnabled(false); // בטל את האפשרות ללחוץ עליו ישירות
        } else {
            holder.hideCheckbox.setChecked(appItem.isHidden());
            holder.hideCheckbox.setEnabled(true); // ודא שהוא מופעל אם לא ה-MDM
        }

        // **הסר את ה-OnClickListener מה-CheckBox עצמו!**
        // holder.hideCheckbox.setOnClickListener(...) - הסר את הבלוק הזה לחלוטין

        // הגדר OnClickListener עבור כל ה-Item (ה-LinearLayout הראשי)
        convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // אם זו אפליקציית ה-MDM, אל תאפשר להסתיר אותה
                    if (appItem.getPackageName().equals(mdmPackageName)) {
                        Toast.makeText(context, "לא ניתן להסתיר את אפליקציית ה-MDM.", Toast.LENGTH_SHORT).show();
                        // אין צורך לשנות את מצב ה-checkbox כי הוא תמיד לא מסומן עבור ה-MDM
                    } else {
                        // שנה את מצב ה-checkbox ועדכן את ה-AppItem
                        boolean newCheckedState = !holder.hideCheckbox.isChecked();
                        holder.hideCheckbox.setChecked(newCheckedState);
                        appItem.setHidden(newCheckedState);
                    }
                }
            });

        return convertView;
    }

    static class ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView appPackage;
        TextView appLastUpdated;
        CheckBox hideCheckbox;
    }
}
