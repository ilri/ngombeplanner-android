package org.cgiar.ilri.mistro.farmer;

import android.content.Context;

import com.actionbarsherlock.view.MenuItem;

import org.cgiar.ilri.mistro.farmer.backend.Locale;

/**
 * Created by jrogena on 14/05/14.
 */
public interface MistroActivity {
    public void initTextInViews();
    public class Language{
        public static boolean processLanguageMenuItemSelected(MistroActivity mistroActivity, Context context, MenuItem item){
            if(item.getItemId() == R.id.action_english) {
                Locale.switchLocale(Locale.LOCALE_ENGLISH, context);
                mistroActivity.initTextInViews();
                return true;
            }
            else if(item.getItemId() == R.id.action_swahili) {
                Locale.switchLocale(Locale.LOCALE_SWAHILI, context);
                mistroActivity.initTextInViews();
                return true;
            }
            else if(item.getItemId() == R.id.action_lutsotso) {
                Locale.switchLocale(Locale.LOCALE_LUTSOTSO, context);
                mistroActivity.initTextInViews();
                return true;
            }
            else if(item.getItemId() == R.id.action_nandi) {
                Locale.switchLocale(Locale.LOCALE_NANDI, context);
                mistroActivity.initTextInViews();
                return true;
            }
            else if(item.getItemId() == R.id.action_kikabras) {
                Locale.switchLocale(Locale.LOCALE_KIKABRAS, context);
                mistroActivity.initTextInViews();
                return true;
            }
            else if(item.getItemId() == R.id.action_kipsigis) {
                Locale.switchLocale(Locale.LOCALE_KIPSIGIS, context);
                mistroActivity.initTextInViews();
                return true;
            }
            return false;
        }
    }
}
