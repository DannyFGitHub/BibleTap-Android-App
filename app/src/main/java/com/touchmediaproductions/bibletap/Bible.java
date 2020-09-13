package com.touchmediaproductions.bibletap;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Random;

public class Bible {

    private Context context;
    private LinkedList<Book> jsonBible;
    private int booksCount;

    public Bible(Context context) throws JSONException {
        this.context = context;
        jsonBible = new LinkedList<>();

        JSONArray bibleJSONChapterArray = new JSONArray(getBibleAsString());

        for (int i = 0; i < bibleJSONChapterArray.length(); i++) {
            jsonBible.add(new Book(bibleJSONChapterArray.getJSONObject(i)));
        }

        booksCount = jsonBible.size();
    }

    private String getBibleAsString(){
        BufferedReader reader = null;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("en_kjv.json"), "UTF-8"));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                //process line
                stringBuffer.append(mLine);
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        return stringBuffer.toString();
    }

    public Verse getRandomVerse(){
        Random random = new Random();
        Book randomBook = jsonBible.get(random.nextInt(booksCount));
        LinkedList<LinkedList<Verse>> chapters = randomBook.getChapters();
        LinkedList<Verse> verses = chapters.get(random.nextInt(chapters.size()));
        Verse verse = verses.get(random.nextInt(verses.size()));
        return verse;
    }

}
