package be.borgers.autosms;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import be.borgers.autosms.domain.AutoSMSEntry;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class SMSEntryAdapter extends ArrayAdapter<AutoSMSEntry> {

    private List<AutoSMSEntry> items;
    private LayoutInflater layoutInflater;
    private SparseBooleanArray selection;

    public SMSEntryAdapter(Context context, List<AutoSMSEntry> items) {
        super(context, R.layout.main_list_item, items);
        this.items = items;
        layoutInflater = LayoutInflater.from(context);
        selection = new SparseBooleanArray();
    }

    public void clearSelection() {
        selection.clear();
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        selectView(position, !selection.get(position));
    }

    public void selectView(int position, boolean select) {
        if (select) {
            selection.put(position, select);
        } else {
            selection.delete(position);
        }

        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return selection.size();
    }

    public List<AutoSMSEntry> getSelectedItems() {
        List<AutoSMSEntry> result = new ArrayList<AutoSMSEntry>();
        for (int i = 0; i < items.size(); i++) {
            if (selection.get(i)) {
                result.add(items.get(i));
            }
        }
        return result;
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
        int color = Color.TRANSPARENT;
        if (selection.get(position)) {
            color = getContext().getResources().getColor(android.R.color.holo_blue_light);
        }
        rowView.setBackgroundColor(color);
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
