package org.vocabulum.data;

import java.util.Objects;

import org.vocabulum.data.Data;


public class Word extends Data {
    private String text;
    private String annotation;

    public Word(String text) {
        this(text, null);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Word) 
            && Objects.equals(((Word) o).getText(), text)
            && Objects.equals(((Word) o).getAnnotation(), annotation);
    }

    @Override
    public String toString() {
        String r = "<Word: " + text;
        if (annotation != null) {
            r += " (" + annotation + ")";
        }
        r += ">";

        return r;
    }

    public Word(String text, String annotation) {
        super(0);
        this.text = text;
        this.annotation = annotation;
    }

    public String getText() {
        return text;
    }

    public String getAnnotation() {
        return annotation;
    }
}
