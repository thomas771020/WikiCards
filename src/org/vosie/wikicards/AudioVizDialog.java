package org.vosie.wikicards;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class AudioVizDialog extends AlertDialog {

  public static interface Listener {
    public void onDoneClick();
  }

  private int soundLevelCircleMaxDiameter;
  private View soundLevelView;
  private TextView maxLengthTextView;
  private TextView currentLengthTextView;
  private double maxSoundLevel;

  public AudioVizDialog(Activity act, final Listener listener) {
    super(act);
    
    // set the dialog size as 80% width, 50% height of screen.
    Rect displayRectangle = new Rect();
    Window window = act.getWindow();
    window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
    LayoutInflater inflater = LayoutInflater.from(act);
    View builderView = inflater.inflate(R.layout.recording_dialog, null);
    int dialogMinWidth = (int) (displayRectangle.width() * 0.8f);
    int dialogMinHeight = (int) (displayRectangle.height() * 0.5f);
    builderView.setMinimumWidth(dialogMinWidth);
    builderView.setMinimumHeight(dialogMinHeight);

    soundLevelCircleMaxDiameter = (int) (dialogMinWidth * 0.5f);
    soundLevelView = (View) builderView.findViewById(R.id.view_sound_level);
    maxLengthTextView = 
            (TextView) builderView.findViewById(R.id.textview_max_record_length);
    currentLengthTextView =
            (TextView) builderView.findViewById(R.id.textview_current_record_length);

    this.setView(builderView);
    this.setCancelable(false);
    this.setButton(AlertDialog.BUTTON_POSITIVE,
            act.getString(R.string.dialog_button_done), new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                listener.onDoneClick();
              }
            });

  }

  public void setSoundLevel(double soundLevel) {
    int circleDiameter;
    double radio = soundLevel / maxSoundLevel;

    if (radio >= 1) {
      circleDiameter = soundLevelCircleMaxDiameter;
      maxSoundLevel = soundLevel;
    } else {
      circleDiameter = (int) (soundLevelCircleMaxDiameter * radio);
    }

    LayoutParams params = soundLevelView.getLayoutParams();
    params.width = circleDiameter;
    params.height = circleDiameter;
    soundLevelView.setLayoutParams(params);
  }
  
  public void setMaxRecordLength(int sec){
    maxLengthTextView.setText(String.valueOf(sec));
  }
  
  public void setCurrentRecordLength(int sec){
    currentLengthTextView.setText(String.valueOf(sec));
  }
}
