package org.vosie.wikicards;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;

public class Recorder {
  private MediaRecorder mRecorder;
  private File recordFile;
  
  public Recorder(File recordFile){
    this.recordFile = recordFile;
  }
  
  public void start()
          throws IllegalStateException, IOException {
    if (mRecorder == null) {
      mRecorder = new MediaRecorder();
      mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
      mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
      mRecorder.setOutputFile(recordFile.getAbsolutePath());
      mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
      mRecorder.prepare();
      mRecorder.start();
    }
  }

  public void stop() {
    if (mRecorder != null) {
      mRecorder.stop();
      mRecorder.release();
      mRecorder = null;
    }
  }
  
  public boolean isRecording() {
    return mRecorder != null;
  }
  
  public double getAmplitude() {
    if (mRecorder != null)
      return mRecorder.getMaxAmplitude();
    else
      return 0;
  }
  
}
