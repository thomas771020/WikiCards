package org.vosie.wikicards;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

import org.vosie.wikicards.data.DownloadWordListener;
import org.vosie.wikicards.data.Word;
import org.vosie.wikicards.data.WordsStorage;
import org.vosie.wikicards.utils.DialogUtils;
import org.vosie.wikicards.utils.ErrorUtils;
import org.vosie.wikicards.utils.IconFontUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.google.analytics.tracking.android.EasyTracker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tekle.oss.android.animation.AnimationFactory;
import com.tekle.oss.android.animation.AnimationFactory.FlipDirection;

public class CardActivity extends Activity {

  private static final String TAG = "CardActivity";

  private static int CARD_POSITION = 0;

  private Button previousButton;
  private Button nextButton;
  private TextView indexTextView;
  private String langCode;
  private WordsStorage wordsStorage;
  private String[] serverIDs;
  private int total;
  private int frontFailOccurIndex;
  private int backFailOccurIndex;
  private ProgressDialog progress;
  private View currentCard;
  private View cardFront;
  private View cardBack;
  private ViewAnimator viewAnimator;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_card);
    initVariables();
    initViews();
    loadWordAndShow(serverIDs[CARD_POSITION]);
  }

  @Override
  protected void onStart() {
    super.onStart();
    EasyTracker.getInstance(this).activityStart(this);
  }

  @Override
  protected void onStop() {
    super.onStop();
    EasyTracker.getInstance(this).activityStop(this);
  }

  private void initVariables() {
    wordsStorage = new WordsStorage(this, Constants.CATEGORY_COUNTRY);
    serverIDs = wordsStorage.getServerIDs();
    frontFailOccurIndex = backFailOccurIndex = total = serverIDs.length;
    langCode = Settings.selectedLanguageCode;
  }

  private void initViews() {
    initNavBar();
    initFlipCard();
  }

  private void initNavBar() {
    previousButton = (Button) findViewById(R.id.button_previous);
    nextButton = (Button) findViewById(R.id.button_next);
    indexTextView = (TextView) findViewById(R.id.textview_index);

    previousButton.setText(IconFontUtils.get(IconFontUtils.ARROW_LEFT));
    nextButton.setText(IconFontUtils.get(IconFontUtils.ARROW_RIGHT));
    previousButton.setTypeface(Settings.iconFont);
    nextButton.setTypeface(Settings.iconFont);

    previousButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        loadWordAndShow(serverIDs[--CARD_POSITION]);
        updateNavBar();
      }
    });

    nextButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        loadWordAndShow(serverIDs[++CARD_POSITION]);
        updateNavBar();
      }
    });

    final View CardPositonWheelView =
            LayoutInflater
                    .from(CardActivity.this)
                    .inflate(R.layout.card_positon_wheel, null);

    WheelView numberWheel =
            (WheelView) CardPositonWheelView.findViewById(R.id.current_position);

    ArrayWheelAdapter<Integer> adapter =
            new ArrayWheelAdapter<Integer>(this, new Integer[] { 1, 2, 3, 4, 5 });
    adapter.setTextSize(18);
    numberWheel.setViewAdapter(adapter);

    indexTextView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        DialogUtils.get().showConfirmDialog(CardActivity.this,
                R.drawable.ic_launcher,
                getString(R.string.dialog_title_option_menu),
                CardPositonWheelView, // menus
                "OK",
                "Chena",
                true, // cancelable
                new DialogInterface.OnClickListener() {

                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub

                  }
                },
                new DialogInterface.OnCancelListener() {
                  @Override
                  public void onCancel(DialogInterface dialog) {
                    // TODO Auto-generated method stub

                  }
                });
      }
    });

    updateNavBar();
  }

  private void updateNavBar() {
    int failIndex = isCradFront() ? frontFailOccurIndex : backFailOccurIndex;
    previousButton.setEnabled(CARD_POSITION > 0);
    nextButton.setEnabled(CARD_POSITION < total - 1
            && CARD_POSITION < failIndex - 1);
    indexTextView.setText(String.valueOf(CARD_POSITION + 1) +
            "/" + String.valueOf(total));
  }

  private boolean isCradFront() {
    return currentCard == cardFront;
  }

  private void initFlipCard() {
    viewAnimator = (ViewAnimator) this.findViewById(R.id.viewFlipper);
    cardFront = LayoutInflater.from(this).inflate(R.layout.card, null);
    cardBack = LayoutInflater.from(this).inflate(R.layout.card, null);
    viewAnimator.addView(cardFront);
    viewAnimator.addView(cardBack);
    currentCard = cardFront;

    viewAnimator.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        flipCard();
        updateNavBar();
      }
    });
  }

  private void flipCard() {
    currentCard = isCradFront() ? cardBack : cardFront;
    langCode = isCradFront() ?
            Settings.selectedLanguageCode : Settings.nativeTongue;
    loadWordAndShow(serverIDs[CARD_POSITION]);
    AnimationFactory.flipTransition(viewAnimator, FlipDirection.LEFT_RIGHT, 250);
  }

  private void loadWordAndShow(String serverID) {
    Word word = wordsStorage.getWordFromLocal(serverID, langCode);
    if (word != null) {
      showWord(word);
    } else {
      progress = ProgressDialog.show(this,
              getString(R.string.dialog_title_downloading),
              getString(R.string.dialog_desc_downloading), true);
      wordsStorage.getWordFromServer(serverID, langCode,
              new DownloadWordListener() {

                @Override
                public void onWordReceived(Word word) {
                  progress.dismiss();
                  showWord(word);
                }

                @Override
                public void onError(int errorType, Exception e) {
                  updateFailOccurIndex(CARD_POSITION);
                  showErrorCard(errorType);
                  updateNavBar();
                  progress.dismiss();
                  ErrorUtils.get().handleDownloadkError(CardActivity.this, errorType,
                          CARD_POSITION == 0);
                  Log.e(TAG, "error while downloading word", e);
                }
              });
    }
  }

  private void showWord(final Word word) {
    currentCard.findViewById(R.id.general_card).setVisibility(View.VISIBLE);
    currentCard.findViewById(R.id.error_card).setVisibility(View.GONE);

    TextView wordTextView =
            (TextView) currentCard.findViewById(R.id.textview_word);
    TextView descriptionTextView =
            (TextView) currentCard.findViewById(R.id.textview_desc);
    Button goToWikiButton =
            (Button) currentCard.findViewById(R.id.button_go_to_wiki);

    goToWikiButton.setText(IconFontUtils.get(IconFontUtils.WIKIPEDIA));
    goToWikiButton.setTypeface(Settings.iconFont);
    wordTextView.setText(word.label);
    loadImage(word.imageURL);
    descriptionTextView.setText(word.shortDesc);
    goToWikiButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(word.url));
        startActivity(intent);
      }
    });
  }

  private void loadImage(String url) {

    final ImageView picImageView =
            (ImageView) currentCard.findViewById(R.id.imageview_photo);
    final ProgressBar loadingImageProgressBar =
            (ProgressBar) currentCard.findViewById(R.id.progressBar_loadingimage);

    picImageView.setVisibility(View.INVISIBLE);
    loadingImageProgressBar.setVisibility(View.VISIBLE);

    if (url.equals("")) {
      Picasso.with(this).load(R.drawable.ic_launcher).into(picImageView);
      picImageView.setVisibility(View.VISIBLE);
      loadingImageProgressBar.setVisibility(View.INVISIBLE);
      return;
    }

    Picasso.with(this)
            .load(url)
            .error(R.drawable.ic_launcher)
            .into(picImageView, new Callback() {

              @Override
              public void onSuccess() {
                picImageView.setVisibility(View.VISIBLE);
                loadingImageProgressBar.setVisibility(View.INVISIBLE);
              }

              @Override
              public void onError() {
                picImageView.setVisibility(View.VISIBLE);
                loadingImageProgressBar.setVisibility(View.INVISIBLE);
              }
            });
  }

  private void updateFailOccurIndex(int index) {
    if (isCradFront()) {
      frontFailOccurIndex = index;
    } else {
      backFailOccurIndex = index;
    }
  }

  private void showErrorCard(int errorType) {
    String msg = getString(R.string.label_card_error_msg) + "\n"
            + ErrorUtils.get().getErrorDesc(this, errorType);

    TextView msgTextView =
            (TextView) currentCard.findViewById(R.id.textview_error_msg);
    msgTextView.setText(msg);

    currentCard.findViewById(R.id.general_card).setVisibility(View.GONE);
    currentCard.findViewById(R.id.error_card).setVisibility(View.VISIBLE);
  }

  public int getCardPosition() {
    return CARD_POSITION;
  }
}
