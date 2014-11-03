package org.vosie.wikicards;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

public class AudioVizDialogTest extends ActivityInstrumentationTestCase2<MainActivity> {

  private AudioVizDialog dialog;

  public AudioVizDialogTest() {
    super(MainActivity.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    dialog = new AudioVizDialog(getActivity(), null);
    dialog.show();
  }

  @Override
  protected void tearDown() throws Exception {
    dialog.dismiss();
    super.tearDown();
  }

  public void testSetMaxRecordLength() {
    dialog.setMaxRecordLength(10);
    TextView txtMaxLength =
            (TextView) dialog.findViewById(R.id.textview_max_record_length);

    assertEquals("10", txtMaxLength.getText());
  }

  public void testSetCurrentRecordLength() {
    dialog.setCurrentRecordLength(10);
    TextView txtMaxLength =
            (TextView) dialog.findViewById(R.id.textview_current_record_length);

    assertEquals("10", txtMaxLength.getText());
  }
}
