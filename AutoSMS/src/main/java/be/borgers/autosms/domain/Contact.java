package be.borgers.autosms.domain;

import android.net.Uri;

public class Contact implements Comparable<Contact>{
    private long id;
    private String name;
    private String number;
    private boolean hasPhoto;
    private Uri photoUri;

    public Contact(long id, String name,String number, Uri photoUri, boolean hasPhoto) {
        this.id = id;
        this.number = number;
        this.hasPhoto = hasPhoto;
        this.photoUri = photoUri;
        this.name = name;
    }

    public Contact(long id, String name, Uri photoUri, boolean hasPhoto) {
       this(id, name, null, photoUri, hasPhoto);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public boolean hasPhoto() {
        return hasPhoto;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public int compareTo(Contact contact) {
        return getName().compareTo(contact.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        if (hasPhoto != contact.hasPhoto) return false;
        if (id != contact.id) return false;
        if (name != null ? !name.equals(contact.name) : contact.name != null) return false;
        if (number != null ? !number.equals(contact.number) : contact.number != null) return false;
        if (photoUri != null ? !photoUri.equals(contact.photoUri) : contact.photoUri != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (number != null ? number.hashCode() : 0);
        result = 31 * result + (hasPhoto ? 1 : 0);
        result = 31 * result + (photoUri != null ? photoUri.hashCode() : 0);
        return result;
    }
}
