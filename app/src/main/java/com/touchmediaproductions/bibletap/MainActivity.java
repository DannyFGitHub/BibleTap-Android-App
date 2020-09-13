package com.touchmediaproductions.bibletap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.billthefarmer.mididriver.MidiDriver;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    Bible bible;

    protected MidiDriver midiDriver;
    protected byte[] event;
    protected int[] config;

    protected Button skipButton;
    protected Button restartButton;
    protected Button resetButton;

    protected TextView counter;

    protected TextView verseId;

    protected String currentVerse = "";
    protected String currentReference = "";

    protected RecyclerView lettersListView;
    protected LettersAdapter mAdapter;
    protected LinkedList<Character> characterLinkedList;

    protected Byte currentNote = null;
    protected int currentNoteIndex = 0;
    protected Byte[] notes = new Byte[]{55,57,59,62,60,60,64,62,62,67,66,67,62,59,55,57,59,60,62,64,62,60,59,57,59,55,54,55,57,50,54,57,60,59,57,59,55,57,59,62,60,60,64,62,62,67,66,67,62,59,55,57,59,57,62,60,59,57,55,50,55,54,55,59,62,67,62,59,55,59,62,67};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //MIDI
        midiDriver = new MidiDriver();
        midiDriver.setVolume(10);
        midiDriver.start();
        //


        skipButton = findViewById(R.id.button_main_skip_medium);
        restartButton = findViewById(R.id.button_main_restart_medium);
        resetButton = findViewById(R.id.button_main_reset_medium);

        counter = findViewById(R.id.textview_main_counter);

        verseId = findViewById(R.id.textview_main_verseid);

        try {
            bible = new Bible(this);
            currentVerse = bible.getRandomVerse().getBody();
            currentReference = bible.getRandomVerse().getReference();
            verseId.setText(currentReference);
            characterLinkedList = verseToCharacterArray(currentVerse);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false){

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };

        lettersListView = findViewById(R.id.listview_main_letters);
        lettersListView.setLayoutManager(layoutManager);
        mAdapter = new LettersAdapter(characterLinkedList, this);
        lettersListView.setAdapter(mAdapter);

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!characterLinkedList.isEmpty()) {
                    stopNote();
                    loadNextNote();
                    playNote();
                    characterLinkedList.removeFirst();
                    lettersListView.getAdapter().notifyDataSetChanged();
                    if(!characterLinkedList.isEmpty() && characterLinkedList.getFirst() == ' '){
                        characterLinkedList.set(0, '_');
                        lettersListView.getAdapter().notifyDataSetChanged();
                    }
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                characterLinkedList.clear();
                currentVerse = bible.getRandomVerse().getBody();
                currentReference = bible.getRandomVerse().getReference();
                verseId.setText(currentReference);
                characterLinkedList.addAll(verseToCharacterArray(currentVerse));
                lettersListView.getAdapter().notifyDataSetChanged();
            }
        });

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                characterLinkedList.clear();
                characterLinkedList.addAll(verseToCharacterArray(currentVerse));
                lettersListView.getAdapter().notifyDataSetChanged();
                currentNoteIndex = 0;
            }
        });



    }

    private LinkedList<Character> verseToCharacterArray(String verse) {
        LinkedList<Character> charArray = new LinkedList<>();
        for (Character c : verse.toCharArray()) {
            charArray.add(c);
        }
        return charArray;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(!characterLinkedList.isEmpty()){
            char characterInput = (char) event.getUnicodeChar();
            Character firstCharacterInSentence = characterLinkedList.getFirst();

            String inputAsString = String.valueOf(characterInput);
            String correctCharacterAsString = String.valueOf(firstCharacterInSentence);

//            if((characterInput == firstCharacterInSentence) || (firstCharacterInSentence == '_' && characterInput == ' ')){
            if((inputAsString.toLowerCase().contentEquals(correctCharacterAsString.toLowerCase())) || (firstCharacterInSentence == '_' && characterInput == ' ')){
                characterLinkedList.removeFirst();
                lettersListView.getAdapter().notifyDataSetChanged();

                stopNote();
                loadNextNote();
                playNote();

                firstCharacterInSentence = characterLinkedList.getFirst();
                if (!characterLinkedList.isEmpty() && firstCharacterInSentence == ' ') {
                    characterLinkedList.set(0, '_');
                    lettersListView.getAdapter().notifyDataSetChanged();
                }
                counter.setText((Integer.parseInt(String.valueOf(counter.getText())) + 1) + "");
                return true;
            }
        }
        return false;
    }


    private void playNote() {
        // Construct a note ON message for the middle C at maximum velocity on channel 1:
        event = new byte[3];
        event[0] = (byte) (0x90 | 0x00);  // 0x90 = note On, 0x00 = channel 1
        event[1] = (byte)  this.currentNote;  // 0x3C = middle C
        event[2] = (byte) 90;  // 0x7F = the maximum velocity (127)

        // Internally this just calls write() and can be considered obsoleted:
        //midiDriver.queueEvent(event);

        // Send the MIDI event to the synthesizer.
        midiDriver.write(event);

    }

    private void stopNote() {
        if(currentNote != null) {
            // Construct a note OFF message for the middle C at minimum velocity on channel 1:
            event = new byte[3];
            event[0] = (byte) (0x80 | 0x00);  // 0x80 = note Off, 0x00 = channel 1
            event[1] = (byte)  this.currentNote;  // 0x3C = middle C
            event[2] = (byte) 0x00;  // 0x00 = the minimum velocity (0)

            // Send the MIDI event to the synthesizer.
            midiDriver.write(event);
        }
    }

    private void loadNextNote(){
        this.currentNote = notes[this.currentNoteIndex];
        this.currentNoteIndex += 1;
        if(currentNoteIndex > notes.length - 1){
            this.currentNoteIndex = 0;
        }
    }

}