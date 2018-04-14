package org.vocabulum.report;

import java.util.List;
import java.util.ArrayList;

import org.vocabulum.data.Relation;


public class Reporter {
    public class Answer {
        public Relation relation;
        public boolean  correct;
    }

    private List<Answer> answers;
    private int rightAnswers;
    private int wrongAnswers;

    public Reporter() {
        answers = new ArrayList<Answer>();
        rightAnswers = wrongAnswers = 0;
    }

    public void addAnswer(Relation r, boolean correct) {
        Answer answer = new Answer();
        answer.relation = r;
        answer.correct = correct;
        if (correct) {
            rightAnswers += 1;
        } else {
            wrongAnswers += 1;
        }
        answers.add(answer);
    }

    public Answer[] getAnswers() {
        Answer[] ret = new Answer[answers.size()];
        return answers.toArray(ret);
    }

    public int getRightAnswerCount() {
        return rightAnswers;
    }

    public int getWrongAnswerCount() {
        return wrongAnswers;
    }

    public int getAnswerCount() {
        return rightAnswers + wrongAnswers;
    }

    public int getRightAnswerPercentage() {
        return (100 * rightAnswers) / getAnswerCount();
    }

    public int getWrongAnswerPercentage() {
        return (100 * wrongAnswers) / getAnswerCount();
    }
}
