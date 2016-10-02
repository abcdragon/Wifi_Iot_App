package bro.myapplication;

public class Item {
    String title;
    String value;

    String getTitle() {
        return this.title;
    }
    String getValue() {return this.value;}

    Item(String title, String value) {
        this.title = title;
        this.value = value;
    }
}
