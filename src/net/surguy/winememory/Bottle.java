package net.surguy.winememory;

/**
 * A record for a single bottle, suitable to be stored in the database.
 *
 * @todo Make this immutable?
 *
 * @author Inigo Surguy
 */
public class Bottle {

    private int id;
    private String name;
    private String description;
    private float rating;
    private String filePath;

    public Bottle() {
    }

    public Bottle(String name, String description, float rating, String filePath) {
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.filePath = filePath;
    }

    public Bottle(int id, String name, String description, float rating, String filePath) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
