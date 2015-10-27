package com.example.admin.biojima;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity
implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String PREFERENCE_KEY = "seekBarPreference";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);
        addPreferencesFromResource(R.xml.preferences);

        PreferenceScreen root = this.getPreferenceScreen();

//        <string name = "time_Selection_label_00_06"> 0시 - 6시 </string>
//        <string name = "time_Selection_label_06_12"> 6시 - 12시 </string>
//        <string name = "time_Selection_label_12_18"> 12시 - 18시 </string>
//        <string name = "time_Selection_label_18_24"> 18시 - 24시 </string>
//        <string name = "time_Selection_key" translatable="false" > ChooseTime </string>
//        <string name = "time_Selection_00_06" translatable="false"> 3 </string>
//        <string name = "time_Selection_06_12" translatable="false"> 0 </string>
//        <string name = "time_Selection_12_18" translatable="false"> 1 </string>
//        <string name = "time_Selection_18_24" translatable="false"> 2 </string>




        // Register for changes (for example only)
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        //bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.date_Selection_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.time_Selection_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.search_criteria_key)));


    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    static int mprefIndex=0;
    static int prefIndex=0;
    static String mprefIndexStr=null;
    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;

            //        <string name = "time_Selection_label_00_06"> 0시 - 6시 </string>
//        <string name = "time_Selection_label_06_12"> 6시 - 12시 </string>
//        <string name = "time_Selection_label_12_18"> 12시 - 18시 </string>
//        <string name = "time_Selection_label_18_24"> 18시 - 24시 </string>


            Date testday = new Date();
            SimpleDateFormat testSTR = new SimpleDateFormat("HH");
            String str = testSTR.format(testday);
            int time = new Integer(str);

            String[] times  = {"0시 - 6시","6시 - 12시","12시 - 18시","18시 - 24시"};
            String[] timeValue  = {"3", "0", "1", "2"};



            prefIndex = listPreference.findIndexOfValue(stringValue);
            if(listPreference.getKey().compareTo("ChooseDate")==0) {

                ListPreference mlistPreference = (ListPreference) findPreference("ChooseTime");

                String SelectionDate = listPreference.getEntries()[prefIndex].toString();
                if (SelectionDate.compareTo("오늘")==0) {

                    ArrayList<String> timeList = new ArrayList<String>();
                    ArrayList<String> timeValueList = new ArrayList<String>();
                    for(int i = time/6 ; i< 4; i++)
                    {
                        timeList.add(times[i]);
                        timeValueList.add(timeValue[i]);
                    }

                    mlistPreference.setEntries(timeList.toArray(new String[timeList.size()]));
                    mlistPreference.setEntryValues(timeValueList.toArray(new String[timeValueList.size()]));

                }
                else if (SelectionDate.toString().compareTo("내일")==0) {

                    mlistPreference.setEntries(times);
                    mlistPreference.setEntryValues(timeValue);

                }
                else if (SelectionDate.toString().compareTo("모레")==0) {

                    mlistPreference.setEntries(new String[]{"0시 - 6시", "6시 - 12시"});
                    mlistPreference.setEntryValues(new String[]{"3", "0"});

                }


              try {
                          mlistPreference.setDefaultValue(mlistPreference.getEntryValues()[mprefIndex].toString());
                          mlistPreference.setValueIndex(mprefIndex);
                          mlistPreference.setSummary(mlistPreference.getEntries()[mprefIndex]);
                          mlistPreference.setPersistent(true);
              }catch (Exception e)
              {
                  mlistPreference.setDefaultValue(mlistPreference.getEntryValues()[0].toString());
                  mlistPreference.setValueIndex(0);
                  mlistPreference.setSummary(mlistPreference.getEntries()[0]);
                  mlistPreference.setPersistent(true);
              }

            }
            if(listPreference.getKey().compareTo("ChooseTime")==0)
            {
                mprefIndex = listPreference.findIndexOfValue(stringValue);
                mprefIndexStr = listPreference.getEntryValues()[mprefIndex].toString();
            }



            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }

        return true;
    }


    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PREFERENCE_KEY)) {
            // Notify that value was really changed
            int value = sharedPreferences.getInt(PREFERENCE_KEY, 0);
            //Toast.makeText(this, getString(R.string.summary, value), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        // Unregister from changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.xml.anim_100to0, R.xml.anim0to100);
    }
}