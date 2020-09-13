package com.touchmediaproductions.bibletap;

public class Verse {


    private String reference;
    private String body;

    public Verse(String reference, String body){
        this.reference = reference;
        this.body = body;
    }

    public String getReference() {
        return reference;
    }

    public String getBody() {
        return body;
    }

}
