package com.qianghongbao.libin.activity;

import android.os.Bundle;

import com.qianghongbao.libin.R;
import com.qianghongbao.libin.base.BaseActivity;
import com.qianghongbao.libin.config.Config;
import com.qianghongbao.libin.utils.SPUtil;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

/**
 * 设置
 */
public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        getFragmentManager().beginTransaction()
                .replace(R.id.container, new SettingsFragment())
                .commit();

    }

    public class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            //微信红包模式
            final ListPreference wxMode = (ListPreference) findPreference(Config.KEY_WECHAT_MODE);
            wxMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int value = Integer.parseInt(String.valueOf(newValue));
                    preference.setSummary(wxMode.getEntries()[value]);
                    SPUtil.put(SettingActivity.this,Config.KEY_WECHAT_MODE,value);
                    Log.e(Config.TAG,"微信红包模式"+value);
                    Log.e(Config.TAG,"微信红包模式"+wxMode.getEntries()[value]);
                    return true;
                }
            });
            wxMode.setSummary(wxMode.getEntries()[Integer.parseInt(wxMode.getValue())]);

            //打开微信红包后(如何打开红包)
            final ListPreference wxAfterOpenPre = (ListPreference) findPreference(Config.KEY_WECHAT_AFTER_OPEN_HONGBAO);
            wxAfterOpenPre.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int value = Integer.parseInt(String.valueOf(newValue));
                    preference.setSummary(wxAfterOpenPre.getEntries()[value]);
                    SPUtil.put(SettingActivity.this,Config.KEY_WECHAT_AFTER_OPEN_HONGBAO,value);
                    Log.e(Config.TAG,"打开微信红包后"+value);
                    Log.e(Config.TAG,"打开微信红包后"+wxAfterOpenPre.getEntries()[value]);
                    return true;
                }
            });
            wxAfterOpenPre.setSummary(wxAfterOpenPre.getEntries()[Integer.parseInt(wxAfterOpenPre.getValue())]);

            //获取微信红包后(拆开红包后)
            final ListPreference wxAfterGetPre = (ListPreference) findPreference(Config.KEY_WECHAT_AFTER_GET_HONGBAO);
            wxAfterGetPre.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int value = Integer.parseInt(String.valueOf(newValue));
                    preference.setSummary(wxAfterGetPre.getEntries()[value]);
                    SPUtil.put(SettingActivity.this,Config.KEY_WECHAT_AFTER_GET_HONGBAO,value);
                    Log.e(Config.TAG,"获取微信红包后"+value);
                    Log.e(Config.TAG,"获取微信红包后"+wxAfterGetPre.getEntries()[value]);
                    return true;
                }
            });
            wxAfterGetPre.setSummary(wxAfterGetPre.getEntries()[Integer.parseInt(wxAfterGetPre.getValue())]);

            //提示音
            final ListPreference wxSoundMode = (ListPreference) findPreference(Config.KEY_NOTIFY_SOUNDS);
            wxSoundMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int value = Integer.parseInt(String.valueOf(newValue));
                    preference.setSummary(wxSoundMode.getEntries()[value]);
                    SPUtil.put(SettingActivity.this,Config.KEY_NOTIFY_SOUNDS,value);
                    Log.e(Config.TAG,"设置抢红包提示音"+value);
                    Log.e(Config.TAG,"设置抢红包提示音"+wxSoundMode.getEntries()[value]);
                    return true;
                }
            });
            wxAfterGetPre.setSummary(wxAfterGetPre.getEntries()[Integer.parseInt(wxAfterGetPre.getValue())]);
        }
    }
}
