package pk.projektant;


public class JSONFurniture {

    private String id="";
    private int x;
    private int y;
    private int width;
    private int height;

    public JSONFurniture(String id, int x, int y, int width, int height) {
        this.height = height;
        this.id = id;
        this.width = width;
        this.x = x;
        this.y = y;
    }
    public JSONFurniture(FurnitureView fv) {
        if(fv.reference != null) id = fv.reference.mId;
        x = fv.getRect(false).left;
        y = fv.getRect(false).top;
        width = fv.getRect(false).width();
        height = fv.getRect(false).height();
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "{" + id + "," + x + "," + y + "," + width + "," + height + "}";

    }
}