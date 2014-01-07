package be.borgers.autosms;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import be.borgers.autosms.domain.Contact;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class SelectContactsAdapter extends ArrayAdapter<Contact> {
    private final LayoutInflater inflater;
    private final List<Contact> contacts;

    public SelectContactsAdapter(Context context, List<Contact> contacts) {
        super(context, R.layout.select_contacts_item, contacts);
        this.inflater = LayoutInflater.from(context);
        this.contacts = contacts;
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
