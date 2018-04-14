package org.vocabulum.question;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;

import org.vocabulum.data.Relation;
import org.vocabulum.data.Word;
import org.vocabulum.question.Questioner;


public class PlainTextQuestioner extends Questioner<String> {
    private Iterator<Relation> iterRelation = null;
    private Relation currentRelation = null;
    private int currentDirection;

    public String next() {
        if (iterRelation == null) {
            iterRelation = getRelations().iterator();
        }
        if (iterRelation.hasNext()) {
            currentRelation = iterRelation.next();
            return getNextQuestion();
        } else {
            throw new NoSuchElementException();
        }
    }

    private int randomDirection() {
        int[] directions = new int[]{ Relation.LEFT_TO_RIGHT, Relation.RIGHT_TO_LEFT };
        int index = new Random().nextInt(directions.length);
        return directions[index];
    }

    private String getNextQuestion() {
        List<Word> possibilities;

        currentDirection = currentRelation.getDirection();
        if (currentDirection == Relation.BOTH) {
            currentDirection = randomDirection();
        }

        if (currentDirection == Relation.LEFT_TO_RIGHT) {
            possibilities = currentRelation.getLeft();
        } else {
            possibilities = currentRelation.getRight();
        }

        int index = new Random().nextInt(possibilities.size());
        Word w = possibilities.get(index);
        String text = w.getText();
        if (w.getAnnotation() != null) {
            text += " (" + w.getAnnotation() + ")";
        }
        return text;
    }

    public boolean hasNext() {
        if (iterRelation == null) {
            return getRelations().size() > 0;
        } else {
            return iterRelation.hasNext();
        }
    }

    public void remove() {
    }

    private List<Word> getAnswers() {
        if (currentDirection == Relation.LEFT_TO_RIGHT) {
            return currentRelation.getRight();
        } else {
            return currentRelation.getLeft();
        }
    }

    public boolean answer(String ans) {
        List<Word> answers;
        boolean correct = false;

        for (Word a : getAnswers()) {
            if (a.getText().equals(ans)) {
                correct = true;
                break;
            }
        }
        reportAnswer(currentRelation, correct);

        return correct;
    }

    public String getCorrectAnswer() {
        List<String> answers = new ArrayList<>();

        for (Word a : getAnswers()) {
            answers.add(a.toString());
        }

        return String.join(" | ", answers);
    }
}
