package com.beanloaf.res;

import java.awt.Color;
import java.awt.Font;
import java.io.File;

/**
 * Holds constants for Thoughts
 * 
 * @author beanloaf
 */
public class TC {
    public static final File UNSORTED_DIRECTORY_PATH = new File("thoughts/storage/unsorted/");
    public static final File SORTED_DIRECTORY_PATH = new File("thoughts/storage/sorted/");
    public static final File SETTINGS_DIRECTORY = new File("thoughts/settings.json");

    public static final String ICON_DIRECTORY = "thoughts/src/main/java/com/beanloaf/res/icons/";
    
    public static final String DEFAULT_TITLE = "<untitled>";
    public static final String DEFAULT_BODY = "<description>";
    public static final String DEFAULT_TAG = "<untagged>";
    public static final String DEFAULT_DATE = "<date>";

    /* Fonts */
    public static Font p = new Font("Lato", Font.PLAIN, 25);
    public static Font h1 = new Font("Lato", Font.PLAIN, 50);
    public static Font h2 = new Font("Lato", Font.PLAIN, 40);
    public static Font h3 = new Font("Lato", Font.PLAIN, 30);
    public static Font h4 = new Font("Lato", Font.PLAIN, 20);
    public static Font h5 = new Font("Lato", Font.PLAIN, 15);

    public static Color darkerGray = new Color(32, 32, 32);
}
