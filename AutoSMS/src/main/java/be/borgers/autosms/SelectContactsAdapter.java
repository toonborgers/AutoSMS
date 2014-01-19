package be.borgers.autosms;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import be.borgers.autosms.domain.Contact;
import be.borgers.autosms.domain.ParcelableContact;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class SelectContactsAdapter extends ArrayAdapter<Contact> {
    private final LayoutInflater inflater;
    private final List<Contact> contacts;
    private SparseBooleanArray selection;

    public SelectContactsAdapter(Context context, List<Contact> contacts) {
        super(context, R.layout.select_contacts_item, contacts);
        this.inflater = LayoutInflater.from(context);
        this.contacts = contacts;
        selection = new SparseBooleanArray();
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

    public ArrayList<ParcelableContact> getSelectedItems() {
        ArrayList<ParcelableContact> result = new ArrayList<ParcelableContact>();
        for (int i = 0; i < contacts.size(); i++) {
            if (selection.get(i)) {
                result.add(new ParcelableContact(contacts.get(i)));
            }
        }
        return result;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result = convertView;
        if (convertView == null) {
            result = inflater.inflate(R.layout.select_contacts_item, null);
            ViewHolder viewHolder = new ViewHolder(result);
            result.setTag(viewHolder);
        }

        Contact contact = contacts.get(position);
        ViewHolder viewHolder = (ViewHolder) result.getTag();
        viewHolder.number.setText(contact.getNumber());
        viewHolder.name.setText(contact.getName());
        if (contact.hasPhoto()) {
            viewHolder.contactImage.setImageURI(contact.getPhotoUri());
        } else {
            viewHolder.contactImage.setImageResource(R.drawable.ic_contact_picture);
        }
        int color = Color.TRANSPARENT;
        if (selection.get(position)) {
            color = getContext().getResources().getColor(android.R.color.holo_blue_light);
        }
        result.setBackgroundColor(color);
        return result;
    }

    static class ViewHolder {
        @InjectView(R.id.select_contacts_image)
        ImageView contactImage;
        @InjectView(R.id.select_contacts_name)
        TextView name;
        @InjectView(R.id.select_contacts_number)
        TextView number;

        public ViewHolder(View v) {
            ButterKnife.inject(this, v);
        }
    }
}
