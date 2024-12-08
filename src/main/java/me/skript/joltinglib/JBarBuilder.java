package me.skript.joltinglib;

import org.bukkit.ChatColor;

public class JBarBuilder {

    private ChatColor fullColor = ChatColor.GREEN;
    private ChatColor warningColor = ChatColor.YELLOW;
    private ChatColor emptyColor = ChatColor.RED;
    private double percent;
    private int sizeOfBar = 20;
    private char character = '|';

    public JBarBuilder(double percent) {
        this.percent = percent;
    }

    // Setters
    public JBarBuilder setFullColor(ChatColor fullColor) {
        this.fullColor = fullColor;
        return this;
    }

    public JBarBuilder setWarningColor(ChatColor warningColor) {
        this.warningColor = warningColor;
        return this;
    }

    public JBarBuilder setEmptyColor(ChatColor emptyColor) {
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
            // Calculate the minimum health percentage required to fill the current segment
            double minPercentageForSegment = segmentPercentage * i;
            // Calculate the percentage threshold for showing the warning color
            double warningThreshold = minPercentageForSegment + segmentPercentage * 0.5;

            if (percent > warningThreshold) {
                // Health is within the top half of the segment
                bar.append(fullColor).append(character);
            } else if (percent > minPercentageForSegment) {
                // Health is within the bottom half of the segment
                bar.append(warningColor).append(character);
            } else {
                // Segment represents health that's already lost
                bar.append(emptyColor).append(character);
            }
        }
        return bar.toString();
    }
}
