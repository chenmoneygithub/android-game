package edu.stanford.cs108.rabbit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.view.View;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;


import android.graphics.Canvas;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.content.Context;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.content.Context;

import org.json.JSONObject;
import org.json.JSONException;

import java.util.Iterator;


/**
 * Created by qianyu on 2017/2/26.
 */

public class Shape {
    // fields

    String name;
    String page;
    RectF rectF;
    JSONObject script;
    String image;
    String text;
    boolean hidden;
    boolean movable;
    String soundName = null;
    int order;

    Paint textPaint;
    Bitmap imageBitmap;

    static float viewWidth;
    static float viewHeight;
    static GameView gameView;
    static int currentShapeNumber = 0; // counting the current shapes in our app

    final String[] scriptPrimitives = new String[] {"goto", "play", "hide", "show"};
    final String[] scriptTriggers = new String[] {"onClick", "onEnter", "onDrop"};

    static final String PACKAGE_NAME = "edu.stanford.cs108.rabbit";
    static final String DRAWABLE = "drawable";
    protected static final String RAW = "raw";
    static Context context;

    protected MediaPlayer mp;

    // methods

    public Shape() {
        this.rectF = new RectF(0,0,0,0);
    }

    public Shape(String image, String text, String soundName, String name, String page, String script, int order, boolean hidden, boolean movable, float left, float top, float right, float bottom) {
        //currentShapeNumber++; // every time constructing a new shape, increment the counter

        this.image = image;
        this.text = text;
        this.soundName = soundName;
        this.page = page;
        this.name = name;
        this.order = order;
        this.hidden = hidden;
        this.movable = movable;
        rectF = new RectF(left, top, right, bottom);

        try{
            this.script = new JSONObject(script);
        } catch (Exception e) {

        }

        initBitmapDrawable();
        initPaint();

    }

    public Shape(String image, String text, String name, String page) {
        this();
        this.image = image;
        this.text = text;
        this.page = page;

        hidden = false;
        movable = false;

        script = new JSONObject();

        try {                       //parsing script into individual Json objects for 3 action triggers
            scriptParser();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        initBitmapDrawable();
        initPaint();
    }

    public static void setViewWidth(float viewWidth) {
        Shape.viewWidth = viewWidth;
    }

    public static void setViewHeight(float viewHeight) {
        Shape.viewHeight = viewHeight;
    }

    public static void setContext(Context contextPass) {
        context = contextPass;
    }

    public static void setGameView(GameView gameViewPass) {
        gameView = gameViewPass;
    }

    public JSONObject parseStringToJson(String script) {
        return new JSONObject();
    }

    public String getId() {
        return name;
    }

    public void setId(String id) {
        this.name = id;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public RectF getRectF() {
        return rectF;
    }

    public void setRectF(float left, float top, float right, float bottom) {
        this.rectF = new RectF(left, top, right, bottom);
        if (imageBitmap != null) {
            rectF.right = rectF.left + imageBitmap.getWidth();
            rectF.bottom = rectF.top + imageBitmap.getHeight();
        }
    }

    public JSONObject getScript() {
        return script;
    }

    public void setScript(JSONObject script) {
        this.script = script;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isMovable() {
        return movable;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public String getSoundName() {
        return soundName;
    }

    public void setSoundName(String soundName) {
        this.soundName = soundName;
    }









    //test script in json format.
    private String testScript = "{"
            + "\"on click\": {\"goto\": \"page1\", \"show\": \"carrot\"},"
            + "\"on enter\": {\"show\": \"carrot\"},"
            + "\"on drop\": {\"show\": \"diamond\"}"
            + "}";

    private String testScript2 = "{\"on click\": {\"goto\": \"page1\", \"show\": \"carrot\"}}";


    //ivars for this script processing

    JSONObject onClickJSON;
    JSONObject onDropJSON;
    JSONObject onEnterJSON;


    //Parse the script (a JSONObject) into trigger-specific JSONObjects
    private void scriptParser() throws JSONException {
        JSONObject jsonObject = new JSONObject(testScript2);

        onClickJSON = jsonObject.getJSONObject("on click");
        onEnterJSON = jsonObject.getJSONObject("on enter");
        onDropJSON = jsonObject.getJSONObject("on drop");
    }

    //Trigger handlers implementation of onTouchEvent in custom view.
    public void onClick() throws JSONException {
        actionProcessor(onClickJSON);
    }

    public void onDrop() throws JSONException {
        actionProcessor(onDropJSON);
    }

    public void onEnter() throws JSONException {
        actionProcessor(onEnterJSON);
    }

    //Parse actions in the trigger-specific JSONObject and call the corresponding action handler.
    private void actionProcessor(JSONObject jsonObject) throws JSONException {
        Iterator<String> keys = jsonObject.keys();

        while(keys.hasNext()) {
            String key = keys.next();
            String value = jsonObject.getString(key);

            switch (key) {
                case "goto": onGoto(value);
                    break;
                case "show": onShow(value);
                    break;
                case "hide": onHide(value);
                    break;
                case "play": onPlay(value);
            }
        }
    }


    //Actions handlers: onGoto, onShow, onHide, onPlay.
    public void onGoto(String pageName) {
        GameDatabase gameDatabase = GameDatabase.getInstance();
        Page newPage = gameDatabase.getPage(pageName);  //Implemented a dummy selectPage() in GameDatabase, should delete that.
        gameView.setCurrentPage(newPage);
    }

    //TODO: waiting for db's update shape method
    public void onShow(String shapeName) {
        //need to see if this shape "shapeName" is in the current page, if so, invalidate();
    }

    //TODO: waiting for db's update shape method
    public void onHide(String shapeName) {
        //need to see if this shape "shapeName" is in the current page, if so, invalidate();

    }

    //TODO: merge this will playSound()
    public void onPlay(String soundName) {
        int soundId = context.getResources().getIdentifier(soundName, RAW, context.getPackageName());
        mp = MediaPlayer.create(context, soundId);
        mp.start();
    }




    //initialize BitmapDrawable of the shape's associated image

    private void initBitmapDrawable() {
        if (image == null || image.equals("")) {
            imageBitmap = null;
        } else {

            BitmapFactory.Options opts = new BitmapFactory.Options();

            opts.inSampleSize = 1;
            opts.inJustDecodeBounds = false;
            int imageId = context.getResources().getIdentifier(image, DRAWABLE, context.getPackageName());
            imageBitmap = BitmapFactory.decodeResource(context.getResources(), imageId, opts);

            rectF = new RectF(rectF.left, rectF.top, imageBitmap.getWidth(), imageBitmap.getHeight());

        }
    }

    //initialize the paint for the shape's associate text (can be made a static varaible of shape)

    private void initPaint() {
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(35);
    }

    //Draw the shape-self; text takes precedence over image
    public void draw(Canvas canvas) {
        if (text != null && !text.equals(""))
            canvas.drawText(text, rectF.centerX(), rectF.centerY(), textPaint);
        else if (imageBitmap != null)
            canvas.drawBitmap(imageBitmap, rectF.left, rectF.top, new Paint());
    }


    //TODO: can be merged with onPlay();
    //e.g. pass this when creating
    public void playSound() {
        System.out.println(context == null);
        int soundId = context.getResources().getIdentifier(soundName, RAW, context.getPackageName());
        mp = MediaPlayer.create(context, soundId);
        mp.start();

    }



}
