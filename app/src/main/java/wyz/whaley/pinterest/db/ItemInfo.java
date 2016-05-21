package wyz.whaley.pinterest.db;

public class ItemInfo {
    public static final String ID = "id";
    public static final String IMAGE_NAME = "imageName";
    public static final String IMAGE_URL = "imageURL";
    public static final String IMAGE_WIDTH = "imageWidth";
    public static final String IMAGE_HEIGHT = "imageHeight";

    private long id;
    private String imageName;
    private String imageURL;
    private int height;
    private int width;

    public ItemInfo() {
    }

    public ItemInfo(long id, String imageName, String imageURL, int width, int height) {
        this.id = id;
        this.imageName = imageName;
        this.imageURL = imageURL;
        this.width = width;
        this.height = height;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

}
