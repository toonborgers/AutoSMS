package be.borgers.autosms.domain;

public class AutoSMSEntry {
    private int id;
    private String name;
    private String number;
    private String text;

    public AutoSMSEntry(int id, String name, String number, String text) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.text = text;
    }

    public AutoSMSEntry(String name, String number, String text) {
        this(-1, name, number, text);
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getText() {
        return text;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "AutoSMSEntry{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
