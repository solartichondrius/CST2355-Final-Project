package com.example.recipes;

public class Recipe  {
    private String title;
    private String url;
    private String image;
    private long id;

    public Recipe(String title, String url, String image){
        this.title = title;
        this.url = url;
        this.image = image;
    }

    public Recipe(String title, String url, String image, long id){
        this.title = title;
        this.url = url;
        this.image = image;
        this.id = id;
    }

    public String getTitle(){ return this.title; }

    public String getUrl(){ return this.url; }

    public String getImage(){ return this.image; }

    public long getId(){return this.id; }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof  Recipe)) {
            return false;
        }
        Recipe recipe = (Recipe) other;
        return this.id == ((Recipe) other).id;
    }

}
