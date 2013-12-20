package be.borgers.autosms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import be.borgers.autosms.domain.AutoSMSEntry;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class SMSEntryAdapter extends ArrayAdapter<AutoSMSEntry> {

    private List<AutoSMSEntry> items;
    private LayoutInflater layoutInflater;

    public SMSEntryAdapter(Context context, List<AutoSMSEntry> items) {
        super(context, R.layout.main_list_item, items);
        this.items = items;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            rowView = layoutInflater.inflate(R.layout.main_list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(rowView);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        final AutoSMSEntry item = items.get(position);
        holder.nameView.setText(item.getName());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SendSMSTask(getContext()).execute(item);
            }
        });
        return rowView;
    }

    static final class ViewHolder {
        @InjectView(R.id.main_list_item_tv_naam)
        TextView nameView;
        @InjectView(R.id.main_list_item_bt_send)
        ImageButton button;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
