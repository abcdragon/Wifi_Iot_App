package bro.myapplication;

public class Item {
    String title;
    String value;
    int image;

    int getImage() {return this.image;}
    String getTitle() {
        return this.title;
    }
    String getValue() {return this.value;}

    Item(int image, String title, String value) {
        this.title = title;
        this.value = value;
        this.image = image;
    }
}
