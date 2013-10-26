package com.facetoe.jreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class Config {
    public static final String DATA_DIR = "dataDir";
    public static final String PROFILE_DIR = "profileDir";
    public static final String CURRENT_PROFILE = "currentProfile";
    public static final String HAS_DEFAULT_PROFILE = "hasDefaultProfile";
    public static final String HAS_JAVALANG_SOURCE = "hasJavaSrc";
    public static final String HAS_EXTRACTED_SOURCE = "hasExtractedSrc";
    public static final String HAS_PARSED_DOCS = "hasParsedDocs";

    public static final String PROFILE_DIR_NAME = "profiles";
    public static final String JAVA_LANG_ZIP = "src-jdk.zip";
    public static final String CLASS_DATA_FILE_NAME = "classData.ser";
    public static final String ALL_CLASSSES_DOC_FILE = "allclasses-noframe.html";

    public static final String DATA_DIR_NAME = ".jreader";
    public static final String CONFIG_FILE_NAME = "config.properties";

    private static Properties properties = new Properties();

    static String dataDirectory = System.getProperty("user.home") +
            File.separator +
            DATA_DIR_NAME +
            File.separator;

    public static String propertiesFilePath = dataDirectory +
            CONFIG_FILE_NAME +
            File.separator;

    public static void setString(String key, String value) {
        setEntry(key, value);
    }

    public static void setBool(String key, boolean value) {
        setEntry(key, String.valueOf(value));
    }

    public static boolean getBool(String key) {
        return Boolean.parseBoolean(getEntry(key));
    }

    public static String getString(String key) {
        return getEntry(key);
    }

    private static void setEntry(String key, String value) {
        try {
            properties.load(new FileInputStream(propertiesFilePath));

            properties.setProperty(key, value);

            properties.store(new FileOutputStream(propertiesFilePath), null);

        } catch ( IOException ex ) {
            ex.printStackTrace();
        }
    }

    private static String getEntry(String key) {
        try {
            properties.load(new FileInputStream(propertiesFilePath));
        } catch ( IOException ex ) {
            ex.printStackTrace();
        }
        return properties.getProperty(key);
    }
}