package org.vocabulum.report;

import java.util.List;
import java.util.ArrayList;

import org.vocabulum.data.Relation;
import org.vocabulum.report.Reporter;


public class PlainTextReporter extends Reporter {
    public String getReport() {
        String ret =   "Questions:         " + getAnswerCount();;
        ret +=       "\nRight answers:     " + getRightAnswerCount() + " (" + getRightAnswerPercentage() + " %)";
        ret +=       "\nWrong answers:     " + getWrongAnswerCount();
        ret +=       "\n";

        return ret;
    }
}
