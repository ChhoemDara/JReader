package com.facetoe.jreader.gui;

import com.facetoe.jreader.NewProfileWindow;
import com.facetoe.jreader.ProfileManager;
import com.facetoe.jreader.utilities.Config;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: facetoe
 * Date: 28/10/13
 * Time: 9:48 AM
 */

/**
 * This class creates the menu bar along the top of JReader
 */
public class JReaderMenuBar extends JMenuBar {

    private final JReader jReader;
    private JMenuItem mnuNewSource;
    private JMenu subMenuChangeProfile;
    private JMenu subMenuDeleteProfile;
    private final ProfileManager profileManager = ProfileManager.getInstance();

    public JReaderMenuBar(JReader jReader) {
        this.jReader = jReader;
        initMenus();
    }

    /**
     * Build all the menus.
     */
    private void initMenus() {
        initFileMenu();
        initWindowMenu();
        initFindMenu();
    }

    /**
     * Build the Window menu.
     */
    private void initWindowMenu() {
        JMenu windowMenu = new JMenu("Window");
        mnuNewSource = new JMenuItem();
        mnuNewSource.setAction(new NewSourceTabAction(jReader));
        windowMenu.add(mnuNewSource);

        JMenuItem mnuNewTab = new JMenuItem();
        mnuNewTab.setAction(new NewReaderTabAction(jReader));
        windowMenu.add(mnuNewTab);
        add(windowMenu);
    }

    /**
     * Build the File menu.
     */
    private void initFileMenu() {
        JMenu fileMenu = new JMenu("File");
        final JMenuItem itmNewProfile = new JMenuItem("New Profile");
        itmNewProfile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                jReader.removeJavaDocClassNames();
                /* Select the new profile */
                new NewProfileWindow();
                jReader.addJavaDocClassNames();


                /* Change to the new profile's home so the user can tell something happened */
                if ( jReader.getCurrentTab() instanceof JReaderPanel ) {
                    JReaderPanel panel = ( JReaderPanel ) jReader.getCurrentTab();
                    panel.home();
                }
            }
        });
        fileMenu.add(itmNewProfile);

        subMenuChangeProfile = new JMenu("Change Profile");

        /* This listener rebuilds the menu each time it is selected, otherwise
         * deleted profiles will still be visible and new profiles won't show up. */
        subMenuChangeProfile.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                subMenuChangeProfile.removeAll();
                ArrayList<String> profiles = profileManager.getProfileNames();

                for ( String profile : profiles ) {
                    JMenuItem item = new JMenuItem(profile);
                    final String profileName = profile;
                    item.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {

                            /* Remove the old profile's auto complete data and change profiles */
                            jReader.removeJavaDocClassNames();
                            profileManager.setCurrentProfile(profileName);

                            /* Navigate to the new profile's home so the user knows we've changed */
                            if(jReader.getCurrentTab() instanceof JReaderPanel) {
                                JReaderPanel panel = (JReaderPanel)jReader.getCurrentTab();
                                panel.home();
                            }
                            jReader.addJavaDocClassNames();
                        }
                    });
                    subMenuChangeProfile.add(item);
                }
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });
        fileMenu.add(subMenuChangeProfile);


        subMenuDeleteProfile = new JMenu("Delete Profile");

        /* Rebuild the delete menu each time it is selected. */
        subMenuDeleteProfile.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                subMenuDeleteProfile.removeAll();
                ArrayList<String> profiles = profileManager.getProfileNames();

                for ( String profile : profiles ) {
                    JMenuItem item = new JMenuItem(profile);

                    /* Don't allow the user to delete the default profile or we will have problems. */
                    if(profile.equals(Config.DEFAULT_PROFILE_NAME)) {
                        item.setEnabled(false);

                    } else {
                        final String profileName = profile;
                        item.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                int result = JOptionPane.showConfirmDialog(null,
                                        "Are you sure you want to delete " + profileName + "?",
                                        "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                                if(result == JOptionPane.YES_OPTION) {

                                    /* profileManager.deleteProfile sets the profile to Default. */
                                    profileManager.deleteProfile(profileName);
                                    if(jReader.getCurrentTab() instanceof JReaderPanel) {
                                        JReaderPanel panel = (JReaderPanel) jReader.getCurrentTab();
                                        panel.home();
                                    }
                                }
                            }
                        });
                    }
                    subMenuDeleteProfile.add(item);
                }
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });
        fileMenu.add(subMenuDeleteProfile);

        JMenuItem itmQuit = new JMenuItem(new QuitAction(jReader));
        fileMenu.add(itmQuit);
        add(fileMenu);
    }

    /**
     * Build the Find menu.
     */
    private void initFindMenu() {
        final JMenu mnuFind = new JMenu("Find");
        add(mnuFind);

        /* Rebuild the menu each time it is selected so that the search options match those in the current profile. */
        mnuFind.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                mnuFind.removeAll();

                final JCheckBoxMenuItem chkWholeWord = new JCheckBoxMenuItem("Whole Word");
                /* Whole word doesn't make sense with regexp. */
                if(profileManager.regexpIsEnabled()) {
                    chkWholeWord.setEnabled(false);
                } else {

                    chkWholeWord.setState(profileManager.wholeWordIsEnabled());
                    chkWholeWord.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            profileManager.getSearchContext().setWholeWord(chkWholeWord.getState());
                            profileManager.setWholeWordEnabled(chkWholeWord.getState());
                        }
                    });
                }
                mnuFind.add(chkWholeWord);

                final JCheckBoxMenuItem chkMatchCase = new JCheckBoxMenuItem("Match Case");
                /* Match case doesn't make sense with regexp. */
                if(profileManager.regexpIsEnabled()) {
                    chkMatchCase.setEnabled(false);
                } else {
                    chkMatchCase.setState(profileManager.matchCaseIsEnabled());
                    chkMatchCase.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            profileManager.getSearchContext().setWholeWord(chkMatchCase.getState());
                            profileManager.setMatchCaseEnabled(chkMatchCase.getState());
                        }
                    });
                }
                mnuFind.add(chkMatchCase);


                final JCheckBoxMenuItem chkRegexp = new JCheckBoxMenuItem("Regular Expression");
                /* Don't enable regexp if matchCase or wholeWord is enabled. */
                if(profileManager.matchCaseIsEnabled() || profileManager.wholeWordIsEnabled()) {
                    chkRegexp.setEnabled(false);
                } else {

                    chkRegexp.setState(profileManager.regexpIsEnabled());
                    chkRegexp.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            profileManager.getSearchContext().setRegularExpression(chkRegexp.getState());
                            profileManager.setRegexpEnabled(chkRegexp.getState());
                        }
                    });
                }
                mnuFind.add(chkRegexp);
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });
    }

    /**
     * Disable the New Source option in the menu bar.
     */
    public void disableNewSourceOption() {
        mnuNewSource.setEnabled(false);
    }

    /**
     * Enable the New Source option in the menu bar.
     */
    public void enableNewSourceOption() {
        mnuNewSource.setEnabled(true);
    }

}