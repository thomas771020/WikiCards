package org.vosie.wikicards;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.vosie.wikicards.test.mock.data.MockSoundStorage;
import org.vosie.wikicards.test.mock.utils.MockPlayerUtils;
import org.vosie.wikicards.test.mock.utils.MockReordUtils;

import android.app.AlertDialog;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.TextView;

public class CardActivityTest extends
                             ActivityInstrumentationTestCase2<CardActivity> {

  private CardActivity mCardActivity;
  private View mNextButton;
  private View mPreviousButton;

  public CardActivityTest() {
    super(CardActivity.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    Settings.selectedLanguageCode = "base";
    Settings.selectedCategory = 1;
    Settings.nativeTongue = "tw";
    setActivityInitialTouchMode(true);
    mCardActivity = getActivity();
    mNextButton = mCardActivity.findViewById(R.id.button_next);
    mPreviousButton = mCardActivity.findViewById(R.id.button_previous);
  }

  public void testRemeberCardPosotion_save() throws Throwable {
    assertEquals(0, mCardActivity.getCardPosition());
    this.runTestOnUiThread(new Runnable() {
      @Override
      public void run() {
        mNextButton.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();
    mCardActivity.finish();
    this.setActivity(null);
    mCardActivity = getActivity();
    assertEquals(1, mCardActivity.getCardPosition());
    this.runTestOnUiThread(new Runnable() {
      @Override
      public void run() {
        mPreviousButton.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();
    mCardActivity.finish();
    this.setActivity(null);
    mCardActivity = getActivity();
    assertEquals(0, mCardActivity.getCardPosition());
  }

  public void testPositionSelectorDialog_show() throws Throwable {
    final TextView idxTv =
            (TextView) mCardActivity.findViewById(R.id.textview_index);
    CardPositionSelector selector = mCardActivity.getPositionSelector();
    AlertDialog dialog = selector.getDialog();

    performClick(idxTv);
    assertTrue(dialog.isShowing());
    performClick(dialog.getButton(AlertDialog.BUTTON_NEGATIVE));
    assertFalse(dialog.isShowing());

    performClick(idxTv);
    assertTrue(dialog.isShowing());
    performClick(dialog.getButton(AlertDialog.BUTTON_POSITIVE));
    assertFalse(dialog.isShowing());
  }

  public void testCardPositionSelector_select() throws Throwable {
    final TextView idxTv =
            (TextView) mCardActivity.findViewById(R.id.textview_index);
    final CardPositionSelector selector = mCardActivity.getPositionSelector();
    AlertDialog dialog = selector.getDialog();

    performClick(idxTv);
    this.runTestOnUiThread(new Runnable() {
      @Override
      public void run() {
        selector.setCurrentItem(10);
      }
    });
    getInstrumentation().waitForIdleSync();
    performClick(dialog.getButton(AlertDialog.BUTTON_POSITIVE));
    assertEquals(10, mCardActivity.getCardPosition());

    performClick(idxTv);
    this.runTestOnUiThread(new Runnable() {
      @Override
      public void run() {
        selector.setCurrentItem(0);
      }
    });
    getInstrumentation().waitForIdleSync();
    performClick(dialog.getButton(AlertDialog.BUTTON_POSITIVE));
    assertEquals(0, mCardActivity.getCardPosition());
  }

  public void testCardPositionSelector_showCorrectPosition() throws Throwable {
    final TextView idxTv =
            (TextView) mCardActivity.findViewById(R.id.textview_index);
    CardPositionSelector selector = mCardActivity.getPositionSelector();
    AlertDialog dialog = selector.getDialog();

    mCardActivity.setCardPosition(10);
    performClick(idxTv);
    assertEquals(10, selector.getCurrentItem());
    performClick(dialog.getButton(AlertDialog.BUTTON_NEGATIVE));

    mCardActivity.setCardPosition(0);
    performClick(idxTv);
    assertEquals(0, selector.getCurrentItem());
    performClick(dialog.getButton(AlertDialog.BUTTON_NEGATIVE));
  }

  private void performClick(final View view) throws Throwable {
    runTestOnUiThread(new Runnable() {
      @Override
      public void run() {
        view.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();
  }

  private File createTestResource() throws IOException {
    InputStream is = this.getInstrumentation().getContext().getAssets()
            .open("ZWE.mp3");
    File tmpFile = new File(this.getActivity().getCacheDir(), "tmp.mp3");
    if (tmpFile.exists()) {
      tmpFile.delete();
    }
    FileOutputStream fos = new FileOutputStream(tmpFile);
    try {
      IOUtils.copy(is, fos);
    } finally {
      is.close();
      fos.close();
    }
    return tmpFile;
  }

  public void testSayWord() throws Throwable {
    View sayWord = mCardActivity.findViewById(R.id.button_say_word);
    final MockSoundStorage mockedSS = new MockSoundStorage(mCardActivity);
    mCardActivity.soundStorage = mockedSS;
    performClick(sayWord);
    assertEquals("base", mockedSS.gotLang);
    assertEquals("country/ZWE", mockedSS.gotServerID);
    assertNotNull(mockedSS.gotListener);
    // We need to change the langCode to en because the langCode of base db is
    // en.
    mCardActivity.langCode = "en";
    final File testFile = createTestResource();
    MockPlayerUtils mockedPlayer = MockPlayerUtils.createInstance();
    this.runTestOnUiThread(new Runnable() {

      @Override
      public void run() {
        mockedSS.gotListener.onReady(testFile);
      }

    });
    this.getInstrumentation().waitForIdleSync();
    // we may hear a little bit of audio file but not all of it.
    assertNotNull(mockedPlayer.createdPlayer);

  }

  public void testRecordWordButton() throws Throwable {
    View recordWord = mCardActivity.findViewById(R.id.button_record_word);
    AlertDialog dialog = mCardActivity.recordingDialog;
    MockReordUtils mockedReccordUtils = MockReordUtils.createInstance();
    performClick(recordWord);
    assertTrue(mockedReccordUtils.createdRecorder.isRecording());
    assertTrue(dialog.isShowing());
    performClick(dialog.getButton(AlertDialog.BUTTON_POSITIVE));
    assertFalse(mockedReccordUtils.createdRecorder.isRecording());
    assertFalse(dialog.isShowing());
  }

  public void testRecordWordButton_autoStopAfterThreeSec() throws Throwable {
    View recordWord = mCardActivity.findViewById(R.id.button_record_word);
    AlertDialog dialog = mCardActivity.recordingDialog;
    MockReordUtils mockedReccordUtils = MockReordUtils.createInstance();
    performClick(recordWord);
    assertTrue(mockedReccordUtils.createdRecorder.isRecording());
    assertTrue(dialog.isShowing());
    synchronized (this) {
      wait(3500);
    }
    assertFalse(mockedReccordUtils.createdRecorder.isRecording());
    assertFalse(dialog.isShowing());
  }

  public void testPlayUserSound() throws Throwable {
    View recordWord = mCardActivity.findViewById(R.id.button_record_word);
    AlertDialog dialog = mCardActivity.recordingDialog;
    MockReordUtils mockedReccordUtils = MockReordUtils.createInstance();
    performClick(recordWord);
    assertTrue(mockedReccordUtils.createdRecorder.isRecording());
    assertTrue(dialog.isShowing());
    performClick(dialog.getButton(AlertDialog.BUTTON_POSITIVE));
    assertFalse(mockedReccordUtils.createdRecorder.isRecording());
    assertFalse(dialog.isShowing());

    // We need to change the langCode to en because the langCode of base db is
    // en.
    mCardActivity.langCode = "en";
    View playRecord = mCardActivity.findViewById(R.id.button_play_record);
    MockPlayerUtils mockedPlayer = MockPlayerUtils.createInstance();
    performClick(playRecord);
    assertNotNull(mockedPlayer.createdPlayer);
  }

}
