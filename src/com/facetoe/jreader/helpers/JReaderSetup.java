/*   
*    Copyright (C) 2013  facetoe - facetoe@ymail.com
*
*    This program is free software; you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation; either version 2 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License along
*    with this program; if not, write to the Free Software Foundation, Inc.,
*    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/
package com.facetoe.jreader.helpers;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: facetoe
 * Date: 5/10/13
 * Time: 5:59 PM
 */

/**
 * Utility methods for setting up JReader.
 */
public class JReaderSetup {
    private static final Logger log = Logger.getLogger(JReaderSetup.class);

    /**
     * @return Whether or not we need to install JReader.
     */
    public static boolean needsInstallation() {
        return !(new File(Config.configFilePath).exists()
                && Config.getBool(Config.HAS_DEFAULT_PROFILE));
    }

    /**
     * Creates the data directory and config file in the users home.
     *
     * @throws IOException
     */
    public static void createDirectoriesAndConfig() throws IOException {
        File dataDir = Util.getFileFromPathElements(
                System.getProperty("user.home"),
                Config.DATA_DIR_NAME,
                File.separator);
        maybeCreateDataDir(dataDir);
        maybeCreateConfigFile(dataDir);
        maybeCreateProfileDir(dataDir);
    }

    private static void maybeCreateProfileDir(File dataDir) throws IOException {
        boolean wasSuccess;
        File profileDir = Util.getFileFromPathElements(dataDir.getAbsolutePath(), Config.PROFILE_DIR_NAME);
        if (!profileDir.exists()) {
            wasSuccess = profileDir.mkdirs();
            if (wasSuccess) {
                Config.setString(Config.PROFILE_DIR, profileDir.getAbsolutePath());
            } else {
                throw new IOException("Failed to create profile directory at: " + profileDir.getAbsolutePath());
            }
        }
    }

    private static void maybeCreateConfigFile(File dataDir) throws IOException {
        boolean wasSuccess;
        File configFile = new File(Config.configFilePath);
        if (!configFile.exists()) {
            wasSuccess = configFile.createNewFile();
            if (wasSuccess) {
                Config.setString(Config.DATA_DIR, dataDir.getAbsolutePath() + File.separator);
                Config.setString(Config.CURRENT_PROFILE, "");
                Config.setBool(Config.HAS_DEFAULT_PROFILE, false);
            } else {
                String errMsg = "Failed to create config file at:" + configFile.getAbsolutePath();
                log.error(errMsg);
                throw new IOException(errMsg);
            }
        } else {
            log.warn("Config already exists.");
        }
    }

    private static void maybeCreateDataDir(File dataDir) throws IOException {
        boolean wasSuccess;
        if (!dataDir.exists()) {
            wasSuccess = dataDir.mkdirs();
            if (!wasSuccess) {
                String errMsg = "Failed to create data directory at: " + dataDir.getAbsolutePath();
                log.error(errMsg);
                throw new IOException(errMsg);
            }
        } else {
            log.warn("Data Directory already exists.");
        }
    }
}
