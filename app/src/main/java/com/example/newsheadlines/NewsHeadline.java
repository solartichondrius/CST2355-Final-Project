package com.example.newsheadlines;

import android.graphics.Bitmap;

import java.sql.Date;

/**
 * Class for storing all the information about News Headlines
 */
public class NewsHeadline {

    private String source;
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;
    private String content;

    /**
     * Constructor for creating News Headline objects, using all the parameter found in the API
     * @param title the title of the News Headline
     * @param author the author of the News Headline (First and Last name, sometimes middle initial)
     * @param source the source of the News Headline (organization/company)
     * @param description short description of the News Headline
     * @param url the url of the original News Headline
     * @param urlToImage the url to the image that goes with the News Headline
     * @param publishedAt the date/time the News Headline was published, using ISO-8601 standard date format (YYYY-MM-DDTHH:MM:SS, example for Christmas: 2019-12-25T12:00:00)
     * @param content the content of the News Headline (the actual article containing the paragraphs of words)
     */
    NewsHeadline(String source, String author, String title, String description, String url, String urlToImage, String publishedAt, String content){
        this.source = source;
        this.author = author;
        this.title = title;
        this.description = description;
        this.url=url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
        this.content = content;
    }

    /**
     * getter for the private variable "source" so that it may be accessed from outside this class
     * @return source: the source of the News Headline (organization/company)
     */
    public String getSource() {
        return source;
    }
    /**
     * setter for the private variable "source" so that it may be set/changed from outside this class
     * @param source the source of the News Headline (organization/company)
     */
    public void setSource(String source) {
        this.source = source;
    }
    /**
     * getter for the private variable "title" so that it may be accessed from outside this class
     * @return title: the title of the News Headline
     */
    public String getTitle() {
        return title;
    }
    /**
     * setter for the private variable "title" so that it may be set/changed from outside this class
     * @param title the title of the News Headline
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * getter for the private variable "author" so that it may be accessed from outside this class
     * @return author: the author of the News Headline (First and Last name, sometimes middle initial)
     */
    public String getAuthor() {
        return author;
    }
    /**
     * setter for the private variable "author" so that it may be set/changed from outside this class
     * @param author the author of the News Headline (First and Last name, sometimes middle initial)
     */
    public void setAuthor(String author) {
        this.author = author;
    }
    /**
     * getter for the private variable "url" so that it may be accessed from outside this class
     * @return url: the url of the original News Headline
     */
    public String getUrl() {
        return url;
    }
    /**
     * setter for the private variable "url" so that it may be set/changed from outside this class
     * @param url the url of the original News Headline
     */
    public void setUrl(String url) {
        this.url = url;
    }
    /**
     * getter for the private variable "image" so that it may be accessed from outside this class
     * @return urlToImage: the url to the image that goes with the News Headline
     */
    public String getUrlToImage() {
        return urlToImage;
    }
    /**
     * setter for the private variable "image" so that it may be set/changed from outside this class
     * @param urlToImage the url to the image that goes with the News Headline
     */
    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }
    /**
     * getter for the private variable "publishedAt" so that it may be accessed from outside this class
     * @return publishedAt: the date/time the News Headline was published, using ISO-8601 standard date format (YYYY-MM-DDTHH:MM:SS, example for Christmas: 2019-12-25T12:00:00)
     */
    public String getPublishedAt() {
        return publishedAt;
    }
    /**
     * setter for the private variable "publishedAt" so that it may be set/changed from outside this class
     * @param publishedAt the date/time the News Headline was published, using ISO-8601 standard date format (YYYY-MM-DDTHH:MM:SS, example for Christmas: 2019-12-25T12:00:00)
     */
    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }
    /**
     * getter for the private variable "content" so that it may be accessed from outside this class
     * @return content: the content of the News Headline (the actual article containing the paragraphs of words)
     */
    public String getContent() {
        return content;
    }
    /**
     * setter for the private variable "content" so that it may be set/changed from outside this class
     * @param content the content of the News Headline (the actual article containing the paragraphs of words)
     */
    public void setContent(String content) {
        this.content = content;
    }
    /**
     * getter for the private variable "description" so that it may be accessed from outside this class
     * @return description: short description of the News Headline
     */
    public String getDescription() {
        return description;
    }
    /**
     * setter for the private variable "description" so that it may be set/changed from outside this class
     * @param description short description of the News Headline
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
