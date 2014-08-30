package org.vosie.wikicards.test;

import org.vosie.wikicards.CardActivity;
import org.vosie.wikicards.Settings;

import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;

public class CardActivityTest extends
                             ActivityInstrumentationTestCase2<CardActivity> {

  private CardActivity mCardActivity;

  public CardActivityTest() {
    super(CardActivity.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    Settings.selectedLanguageCode = "en";
    Settings.nativeTongue = "tw";
    setActivityInitialTouchMode(true);
    mCardActivity = getActivity();
  }

  public void testRemeberCardPosotion_get() throws Throwable {
    SharedPreferences settings =
            mCardActivity.getSharedPreferences("CardActivity", 0);
    
    settings.edit().putInt("cardPosition", 0).commit();
    mCardActivity.finish();
    this.setActivity(null);
    mCardActivity = getActivity();
    assertEquals(0, mCardActivity.getCardPosition());
    
    settings.edit().putInt("cardPosition", 1).commit();
    mCardActivity.finish();
    this.setActivity(null);
    mCardActivity = getActivity();
    assertEquals(1, mCardActivity.getCardPosition());
  }
  
  public void testRemeberCardPosotion_save() throws Throwable {
    SharedPreferences settings =
            mCardActivity.getSharedPreferences("CardActivity", 0);
    
    mCardActivity.setCardPosition(1);
    getInstrumentation().callActivityOnStop(mCardActivity);
    assertEquals(1, settings.getInt("cardPosition", 0));
    
    mCardActivity.setCardPosition(2);
    getInstrumentation().callActivityOnStop(mCardActivity);
    assertEquals(2, settings.getInt("cardPosition", 0));
  }
}
