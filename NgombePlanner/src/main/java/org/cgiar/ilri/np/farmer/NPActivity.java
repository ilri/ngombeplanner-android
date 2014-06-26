package org.cgiar.ilri.np.farmer;

import android.content.Context;

import com.actionbarsherlock.view.MenuItem;

import org.cgiar.ilri.np.farmer.backend.Locale;

/**
 * Created by jrogena on 14/05/14.
 */
public interface NPActivity {
    public void initTextInViews();
    public class Language{
        public static boolean processLanguageMenuItemSelected(NPActivity nPActivity, Context context, MenuItem item){
            if(item.getItemId() == R.id.action_english) {
                Locale.switchLocale(Locale.LOCALE_ENGLISH, context);
                nPActivity.initTextInViews();
                return true;
            }
            else if(item.getItemId() == R.id.action_swahili) {
                Locale.switchLocale(Locale.LOCALE_SWAHILI, context);
                nPActivity.initTextInViews();
                return true;
            }
            else if(item.getItemId() == R.id.action_lutsotso) {
                Locale.switchLocale(Locale.LOCALE_LUTSOTSO, context);
                nPActivity.initTextInViews();
                return true;
            }
            else if(item.getItemId() == R.id.action_nandi) {
                Locale.switchLocale(Locale.LOCALE_NANDI, context);
                nPActivity.initTextInViews();
                return true;
            }
            else if(item.getItemId() == R.id.action_kikabras) {
                Locale.switchLocale(Locale.LOCALE_KIKABRAS, context);
                nPActivity.initTextInViews();
                return true;
            }
            else if(item.getItemId() == R.id.action_kipsigis) {
                Locale.switchLocale(Locale.LOCALE_KIPSIGIS, context);
                nPActivity.initTextInViews();
                return true;
            }
            return false;
        }
    }
}
