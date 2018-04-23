package org.vocabulum.question;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Collection;

import org.vocabulum.data.Relation;
import org.vocabulum.data.Word;
import org.vocabulum.question.Questioner;


public class PlainTextQuestioner extends Questioner<String> {
    private Iterator<Relation> iterRelation = null;
    private Relation currentRelation = null;
    private int currentDirection;
    private Random rand = new Random();

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
        Set<Word> possibilities;

        currentDirection = currentRelation.getDirection();
        if (currentDirection == Relation.BOTH) {
            currentDirection = randomDirection();
        }

        if (currentDirection == Relation.LEFT_TO_RIGHT) {
            possibilities = currentRelation.getLeft();
        } else {
            possibilities = currentRelation.getRight();
        }

        //int index = new Random().nextInt(possibilities.size());
        //Word w = possibilities.get(index);
        Word w = choice(possibilities, rand);
        String text = w.getText();
        if (w.getAnnotation() != null) {
            text += " (" + w.getAnnotation() + ")";
        }
        return text;
    }

    public static <E> E choice(Collection<? extends E> coll, Random rand) throws IllegalArgumentException {
        if (coll.size() == 0) {
            throw new IllegalArgumentException();
        }

        int index = rand.nextInt(coll.size());
        if (coll instanceof List) {
            return ((List<? extends E>) coll).get(index);
        } else {
            Iterator<? extends E> iter = coll.iterator();
            while (index > 0) {
                iter.next();
                index -= 1;
            }
            return iter.next();
        }
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

    private Set<Word> getAnswers() {
        if (currentDirection == Relation.LEFT_TO_RIGHT) {
            return currentRelation.getRight();
        } else {
            return currentRelation.getLeft();
        }
    }

    public boolean answer(String ans) {
        Set<Word> answers;
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
        Set<String> answers = new HashSet<>();

        for (Word a : getAnswers()) {
            answers.add(a.toString());
        }

        return String.join(" | ", answers);
    }
}
