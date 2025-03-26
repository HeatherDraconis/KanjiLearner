package com.example.kanjilearner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.*;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity {
    private final List<String> kanjiList = new ArrayList<>();
    private int days, count, firstKyoikuKanjiLength, secondKyoikuKanjiLength,
            thirdKyoikuKanjiLength, fourthKyoikuKanjiLength, fifthKyoikuKanjiLength, sixthKyoikuKanjiLength, joyoKanjiLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstKyoikuKanjiLength = getResources().getStringArray(R.array.kyoiku_kanji_first_grade).length;
        secondKyoikuKanjiLength = firstKyoikuKanjiLength + getResources().getStringArray(R.array.kyoiku_kanji_second_grade).length;
        thirdKyoikuKanjiLength = secondKyoikuKanjiLength + getResources().getStringArray(R.array.kyoiku_kanji_third_grade).length;
        fourthKyoikuKanjiLength = thirdKyoikuKanjiLength + getResources().getStringArray(R.array.kyoiku_kanji_fourth_grade).length;
        fifthKyoikuKanjiLength = fourthKyoikuKanjiLength + getResources().getStringArray(R.array.kyoiku_kanji_fifth_grade).length;
        sixthKyoikuKanjiLength = fifthKyoikuKanjiLength + getResources().getStringArray(R.array.kyoiku_kanji_sixth_grade).length;
        joyoKanjiLength = sixthKyoikuKanjiLength + getResources().getStringArray(R.array.joyo_kanji_secondary_school).length;
        setDays();
        setKanjiArray();
        homePage();
    }

    private void homePage() {
        setContentView(R.layout.activity_main);

        this.<Button>findViewById(R.id.learn_button).setOnClickListener(view -> learnNewKanji());

        this.<Button>findViewById(R.id.revisit_button).setOnClickListener(view -> {
            count = 0;
            revisitPastKanji(getRevisitKanjiArray(), 0);
        });
    }

    private void learnNewKanji() {
        setContentView(R.layout.activity_learn_kanji);
        TextView textKanji = findViewById(R.id.kanji_text);
        TextView textTitle = findViewById(R.id.title_text);
        TextView textDescription = findViewById(R.id.description_text);
        Button buttonNext = findViewById(R.id.next_button);

        String[] kanji;
        if (days < kanjiList.size()) {
            kanji = kanjiList.get(days).split("[*]");
        } else {
            kanji = kanjiList.get(new Random().nextInt(kanjiList.size())).split("[*]");
        }

        textKanji.setText(kanji[0]);
        textTitle.setText(kanji[4]);
        textDescription.setText(kanji[1]);

        buttonNext.setOnClickListener(view -> {
            textTitle.setText(getResources().getString(R.string.on));
            textDescription.setText(kanji[2]);
            buttonNext.setOnClickListener(view1 -> {
                textTitle.setText(getResources().getString(R.string.kun));
                textDescription.setText(kanji[3]);
                buttonNext.setOnClickListener(view11 -> {
                    if (days < kanjiList.size()) {
                        increaseDays();
                    }
                    homePage();
                });
            });
        });
    }

    private void revisitPastKanji(String[][] revisitKanjiArray, int i) {
        switch (new Random().nextInt(3) + 1) {
            case 1: question1(revisitKanjiArray, i);
                break;
            case 2: question2(revisitKanjiArray, i);
                break;
            case 3: question3(revisitKanjiArray, i);
                break;
            default:
                throw new IllegalStateException("Unexpected value from Random ");
        }
    }

    private void question1(String[][] revisitKanjiArray, int i) {
        setContentView(R.layout.activity_revisit_kanji_1);
        TextView textKanji = findViewById(R.id.kanji_text);
        EditText textAnswer = findViewById(R.id.answer_text);
        Button buttonNext = findViewById(R.id.next_button);

        textKanji.setText(revisitKanjiArray[i][0]);

        buttonNext.setOnClickListener(view -> {
            boolean isCorrect = false;
            for (String s: revisitKanjiArray[i][1].split("/")) {
                if (s.trim().equals(textAnswer.getText().toString().toLowerCase().trim())) {
                    isCorrect = true;
                }
            }
            showResult(isCorrect, revisitKanjiArray, i);
        });
    }

    private void question2(String[][] revisitKanjiArray, int i) {
        setContentView(R.layout.activity_revisit_kanji_2);
        TextView textKanji = findViewById(R.id.kanji_text);
        Button buttonOne = findViewById(R.id.button_1);
        Button buttonTwo = findViewById(R.id.button_2);
        Button buttonThree = findViewById(R.id.button_3);

        textKanji.setText(revisitKanjiArray[i][0]);
        String answer = revisitKanjiArray[i][1].split("/")[0];

        Random random = new Random();

        String alternate1;
        do {
            alternate1 = kanjiList.get(random.nextInt(kanjiList.size())).split("[*]")[4].split("/")[0];
        } while (alternate1.equals(answer));

        String alternate2;
        do {
            alternate2 = kanjiList.get(random.nextInt(kanjiList.size())).split("[*]")[4].split("/")[0];
        } while (alternate2.equals(alternate1) || alternate2.equals(answer));

        int button = random.nextInt(3);
        switch (button) {
            case 0:
                buttonOne.setText(answer);
                buttonTwo.setText(alternate1);
                buttonThree.setText(alternate2);
                buttonOne.setOnClickListener(view -> showResult(true, revisitKanjiArray, i));
                buttonTwo.setOnClickListener(view -> showResult(false, revisitKanjiArray, i));
                buttonThree.setOnClickListener(view -> showResult(false, revisitKanjiArray, i));
                break;
            case 1:
                buttonOne.setText(alternate1);
                buttonTwo.setText(answer);
                buttonThree.setText(alternate2);
                buttonOne.setOnClickListener(view -> showResult(false, revisitKanjiArray, i));
                buttonTwo.setOnClickListener(view -> showResult(true, revisitKanjiArray, i));
                buttonThree.setOnClickListener(view -> showResult(false, revisitKanjiArray, i));
                break;
            case 2:
                buttonOne.setText(alternate1);
                buttonTwo.setText(alternate2);
                buttonThree.setText(answer);
                buttonOne.setOnClickListener(view -> showResult(false, revisitKanjiArray, i));
                buttonTwo.setOnClickListener(view -> showResult(false, revisitKanjiArray, i));
                buttonThree.setOnClickListener(view -> showResult(true, revisitKanjiArray, i));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + button);
        }
    }

    private void question3(String[][] revisitKanjiArray, int i) {
        setContentView(R.layout.activity_revisit_kanji_3);
        TextView textDescription = findViewById(R.id.description_text);
        Button buttonOne = findViewById(R.id.button_1);
        Button buttonTwo = findViewById(R.id.button_2);
        Button buttonThree = findViewById(R.id.button_3);

        textDescription.setText(revisitKanjiArray[i][1]);
        String answer = revisitKanjiArray[i][0];

        Random random = new Random();

        String alternate1;
        do {
            alternate1 = kanjiList.get(random.nextInt(kanjiList.size())).split("[*]")[0];
        } while (alternate1.equals(answer));

        String alternate2;
        do {
            alternate2 = kanjiList.get(random.nextInt(kanjiList.size())).split("[*]")[0];
        } while (alternate2.equals(alternate1) || alternate2.equals(answer));

        int button = random.nextInt(3);
        switch (button) {
            case 0:
                buttonOne.setText(answer);
                buttonTwo.setText(alternate1);
                buttonThree.setText(alternate2);
                buttonOne.setOnClickListener(view -> showResult(true, revisitKanjiArray, i));
                buttonTwo.setOnClickListener(view -> showResult(false, revisitKanjiArray, i));
                buttonThree.setOnClickListener(view -> showResult(false, revisitKanjiArray, i));
                break;
            case 1:
                buttonOne.setText(alternate1);
                buttonTwo.setText(answer);
                buttonThree.setText(alternate2);
                buttonOne.setOnClickListener(view -> showResult(false, revisitKanjiArray, i));
                buttonTwo.setOnClickListener(view -> showResult(true, revisitKanjiArray, i));
                buttonThree.setOnClickListener(view -> showResult(false, revisitKanjiArray, i));
                break;
            case 2:
                buttonOne.setText(alternate1);
                buttonTwo.setText(alternate2);
                buttonThree.setText(answer);
                buttonOne.setOnClickListener(view -> showResult(false, revisitKanjiArray, i));
                buttonTwo.setOnClickListener(view -> showResult(false, revisitKanjiArray, i));
                buttonThree.setOnClickListener(view -> showResult(true, revisitKanjiArray, i));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + button);
        }
    }

    private void showResult(boolean isCorrect, String[][] revisitKanjiArray, int i) {
        setContentView(R.layout.activity_result);
        TextView textResult1 = findViewById(R.id.result_text);
        TextView textKanji = findViewById(R.id.kanji_text);
        TextView textDescription = findViewById(R.id.description_text);
        Button buttonNext1 = findViewById(R.id.next_button);

        textKanji.setText(revisitKanjiArray[i][0]);
        textDescription.setText(revisitKanjiArray[i][1]);

        if (isCorrect) {
            textResult1.setText(getResources().getString(R.string.positive));
            count++;
        } else {
            textResult1.setText(getResources().getString(R.string.negative));
        }

        buttonNext1.setOnClickListener(view -> {
            if (i + 1 < revisitKanjiArray.length) {
                revisitPastKanji(revisitKanjiArray, i + 1);
            } else {
                setContentView(R.layout.activity_final_result);
                TextView textScore = findViewById(R.id.score_text);
                TextView textResult2 = findViewById(R.id.result_text);
                Button buttonNext2 = findViewById(R.id.next_button);
                textScore.setText(String.format(Locale.UK,"%d/%d", count, revisitKanjiArray.length));
                textDescription.setText("");

                if (count > 0) {
                    textResult2.setText(getResources().getString(R.string.positive));
                } else {
                    textResult2.setText(getResources().getString(R.string.negative));
                }

                buttonNext2.setOnClickListener(view1 -> {
                    if (days >= kanjiList.size()) {
                        increaseDays();
                    }
                    homePage();
                });
            }
        });
    }

    private String[][] getRevisitKanjiArray() {
        List<Integer> daysList = new ArrayList<>();

        if (days > 1 && days < kanjiList.size() + 1) daysList.add(days - 1);
        if (days > 3 && days < kanjiList.size() + 3) daysList.add(days - 3);
        if (days > 5  && days < kanjiList.size() + 5) daysList.add(days - 5);
        if (days > 7  && days < kanjiList.size() + 7) daysList.add(days - 7);
        if (days > 14  && days < kanjiList.size() + 14) daysList.add(days - 14);
        if (days > 21 && days < kanjiList.size() + 21) daysList.add(days - 21);
        if (days > 28 && days < kanjiList.size() + 28) daysList.add(days - 28);
        if (days > 59 && days < kanjiList.size() + 59) daysList.add(days - 59);
        if (days > 90 && days < kanjiList.size() + 90) daysList.add(days - 90);
        if (days > 121 && days < kanjiList.size() + 121) daysList.add(days - 121);
        if (days > 243 && days < kanjiList.size() + 243) daysList.add(days - 243);
        if (days > 365 && days < kanjiList.size() + 365) daysList.add(days - 365);

        Random random = new Random();
        int day;

        if (days > firstKyoikuKanjiLength) {
            day = random.nextInt(firstKyoikuKanjiLength);
            if (!daysList.contains(day)) daysList.add(day);
        }
        if (days > secondKyoikuKanjiLength) {
            day = random.nextInt(secondKyoikuKanjiLength - firstKyoikuKanjiLength) + firstKyoikuKanjiLength;
            if (!daysList.contains(day)) daysList.add(day);
        }
        if (days > thirdKyoikuKanjiLength) {
            day = random.nextInt(thirdKyoikuKanjiLength - secondKyoikuKanjiLength) + secondKyoikuKanjiLength;
            if (!daysList.contains(day)) daysList.add(day);
        }
        if (days > fourthKyoikuKanjiLength) {
            day = random.nextInt(fourthKyoikuKanjiLength - thirdKyoikuKanjiLength) + thirdKyoikuKanjiLength;
            if (!daysList.contains(day)) daysList.add(day);
        }
        if (days > fifthKyoikuKanjiLength) {
            day = random.nextInt(fifthKyoikuKanjiLength - fourthKyoikuKanjiLength) + fourthKyoikuKanjiLength;
            if (!daysList.contains(day)) daysList.add(day);
        }
        if (days > sixthKyoikuKanjiLength) {
            day = random.nextInt(sixthKyoikuKanjiLength - fifthKyoikuKanjiLength) + fifthKyoikuKanjiLength;
            if (!daysList.contains(day)) daysList.add(day);
        }
        if (days > joyoKanjiLength) {
            day = random.nextInt(joyoKanjiLength - sixthKyoikuKanjiLength) + sixthKyoikuKanjiLength;
            if (!daysList.contains(day)) daysList.add(day);
        }

        int bound = Math.min(days, joyoKanjiLength);
        for (int i = daysList.size(); i < 20; i++) {
            day = random.nextInt(bound);
            if (!daysList.contains(day)) {
                daysList.add(day);
            }
        }

        String[][] revisitKanjiArray = new String[daysList.size()][2];
        IntStream.range(0, daysList.size()).forEach(i -> {
            String[] kanji = kanjiList.get(daysList.get(i)).split("[*]");
            revisitKanjiArray[i][0] = kanji[0];
            revisitKanjiArray[i][1] = kanji[4];
        });
        return revisitKanjiArray;
    }

    private void setKanjiArray() {
        if (days >= kanjiList.size() && kanjiList.size() < firstKyoikuKanjiLength) {
            kanjiList.addAll(Arrays.asList(getResources().getStringArray(R.array.kyoiku_kanji_first_grade)));
        }
        if (days >= kanjiList.size() && kanjiList.size() < secondKyoikuKanjiLength) {
            kanjiList.addAll(Arrays.asList(getResources().getStringArray(R.array.kyoiku_kanji_second_grade)));
        }
        if (days >= kanjiList.size() && kanjiList.size() < thirdKyoikuKanjiLength) {
            kanjiList.addAll(Arrays.asList(getResources().getStringArray(R.array.kyoiku_kanji_third_grade)));
        }
        if (days >= kanjiList.size() && kanjiList.size() < fourthKyoikuKanjiLength) {
            kanjiList.addAll(Arrays.asList(getResources().getStringArray(R.array.kyoiku_kanji_fourth_grade)));
        }
        if (days >= kanjiList.size() && kanjiList.size() < fifthKyoikuKanjiLength) {
            kanjiList.addAll(Arrays.asList(getResources().getStringArray(R.array.kyoiku_kanji_fifth_grade)));
        }
        if (days >= kanjiList.size() && kanjiList.size() < sixthKyoikuKanjiLength) {
            kanjiList.addAll(Arrays.asList(getResources().getStringArray(R.array.kyoiku_kanji_sixth_grade)));
        }
        if (days >= kanjiList.size() && kanjiList.size() < joyoKanjiLength) {
            kanjiList.addAll(Arrays.asList(getResources().getStringArray(R.array.joyo_kanji_secondary_school)));
        }
    }

    private void increaseDays() {
        days++;
        getSharedPreferences(getString(R.string.preference_file_days), MODE_PRIVATE).edit().putInt(getString(R.string.preference_file_days), days).apply();
        setKanjiArray();
    }

    private void setDays() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_days), MODE_PRIVATE);
        days = sharedPreferences.getInt(getString(R.string.preference_file_days), 0);
    }
}