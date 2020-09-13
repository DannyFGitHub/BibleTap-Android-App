package com.touchmediaproductions.bibletap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class Book{
    private String abbrev;
    private LinkedList<LinkedList<Verse>> chapters;
    private String name;

    public Book(JSONObject jsonChapter) throws JSONException {
        this.abbrev = jsonChapter.getString("abbrev");
        JSONArray jsonArrayChapters = jsonChapter.getJSONArray("chapters");
        this.name = jsonChapter.getString("name");

        chapters = new LinkedList<>();
        for (int i=0; i< jsonArrayChapters.length(); i++) {
            JSONArray verseArray = jsonArrayChapters.getJSONArray(i);
            LinkedList<Verse> chapterVerses = new LinkedList<>();
            for (int j = 0; j < verseArray.length(); j++) {
                String currentReference = this.getName() + " " + (i + 1) + ":" + (j + 1);
                Verse currentVerse = new Verse(currentReference, verseArray.getString(j));
                chapterVerses.add(currentVerse);
            }
            chapters.add(chapterVerses);
        }
    }

    public String getAbbrev() {
        return abbrev;
    }

    public LinkedList<LinkedList<Verse>> getChapters() {
        return chapters;
    }

    public String getName() {
        return name;
    }
}