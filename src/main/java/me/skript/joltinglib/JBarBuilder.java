package me.skript.joltinglib;

import net.kyori.adventure.text.format.NamedTextColor;

public class JBarBuilder {

    private NamedTextColor fullColor = NamedTextColor.GREEN;
    private NamedTextColor warningColor = NamedTextColor.YELLOW;
    private NamedTextColor emptyColor = NamedTextColor.RED;
    private final double percent;
    private int sizeOfBar = 20;
    private char character = '|';

    public JBarBuilder(double percent) {
        this.percent = percent;
    }

    public JBarBuilder setFullColor(NamedTextColor fullColor) {
        this.fullColor = fullColor;
        return this;
    }

    public JBarBuilder setWarningColor(NamedTextColor warningColor) {
        this.warningColor = warningColor;
        return this;
    }

    public JBarBuilder setEmptyColor(NamedTextColor emptyColor) {
        this.emptyColor = emptyColor;
        return this;
    }

    public JBarBuilder setSizeOfBar(int sizeOfBar) {
        this.sizeOfBar = sizeOfBar;
        return this;
    }

    public JBarBuilder setCharacter(char character) {
        this.character = character;
        return this;
    }

    public String build() {
        StringBuilder bar = new StringBuilder();

        int totalSegments = sizeOfBar;
        double segmentPercentage = 100.0 / totalSegments;

        for (int i = 0; i < totalSegments; i++) {
            double minPercentageForSegment = segmentPercentage * i;
            double warningThreshold = minPercentageForSegment + segmentPercentage * 0.5;

            if (percent > warningThreshold) {
                bar.append(fullColor).append(character);
            } else if (percent > minPercentageForSegment) {
                bar.append(warningColor).append(character);
            } else {
                bar.append(emptyColor).append(character);
            }
        }
        return bar.toString();
    }
}
