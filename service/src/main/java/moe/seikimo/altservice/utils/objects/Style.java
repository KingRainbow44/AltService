package moe.seikimo.altservice.utils.objects;

import lombok.Builder;
import lombok.Data;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/* Text style container. */
@Builder
@Data
public final class Style {
    private static final Map<Character, String> ansi = new HashMap<>();

    static {
        // Add the Minecraft color codes to the color map.
        ansi.put('r', "\u001B[0m"); // Reset
        ansi.put('0', "\u001B[30m"); // Black
        ansi.put('1', "\u001B[34m"); // Dark Blue
        ansi.put('2', "\u001B[32m"); // Dark Green
        ansi.put('3', "\u001B[36m"); // Dark Aqua
        ansi.put('4', "\u001B[31m"); // Dark Red
        ansi.put('5', "\u001B[35m"); // Dark Purple
        ansi.put('6', "\u001B[33m"); // Gold
        ansi.put('7', "\u001B[37m"); // Gray
        ansi.put('8', "\u001B[90m"); // Dark Gray
        ansi.put('9', "\u001B[94m"); // Blue
        ansi.put('a', "\u001B[92m"); // Green
        ansi.put('b', "\u001B[96m"); // Aqua
        ansi.put('c', "\u001B[91m"); // Red
        ansi.put('d', "\u001B[95m"); // Light Purple
        ansi.put('e', "\u001B[93m"); // Yellow
        ansi.put('f', "\u001B[97m"); // White
    }

    @Builder.Default private Color color = null;

    /**
     * Replaces detected sequences of &color with the specified text.
     *
     * @param input The input text.
     * @return The replaced text.
     */
    public static String replaceTerminal(String input) {
        // Check if the input string is null or empty
        if (input == null || input.isEmpty()) {
            return "";
        }

        var output = new StringBuilder();
        var i = 0;
        while (i < input.length()) {
            var c = input.charAt(i);
            if (c == '&' || c == '§') {
                var character = c == '&' ? '§' : '&';

                // Check if the Minecraft color code is valid
                if (i + 1 < input.length() && ansi.containsKey(input.charAt(i + 1))) {
                    // Append the ANSI escape code
                    output.append(ansi.get(input.charAt(i + 1)));

                    // Move the index past the Minecraft color code
                    i += 2;

                    // Find the end of the color code span
                    var end = input.indexOf(character, i);
                    if (end == -1) {
                        end = input.length();
                    }

                    // Append the text within the color code span
                    output.append(input, i, end);

                    // Reset the color back to default
                    output.append("\u001B[0m");

                    // Move the index to the end of the color code span
                    i = end;
                } else {
                    // Invalid Minecraft color code, treat it as regular text
                    output.append(c);
                    i++;
                }
            } else {
                // Append regular text
                output.append(c);
                i++;
            }
        }

        return output.toString();
    }

    /**
     * Wraps the text in the style. Formatted for terminal clients.
     *
     * @param text The text to wrap.
     * @return The wrapped text.
     */
    public String toTerminal(String text) {
        // Check for color.
        if (this.color == null) return Style.replaceTerminal(text);

        // Convert the color to an ANSI color.
        var ansiColor =
                this.color.getRed() > 127
                        ? this.color.getGreen() > 127
                                ? this.color.getBlue() > 127 ? 15 : 11
                                : this.color.getBlue() > 127 ? 13 : 9
                        : this.color.getGreen() > 127
                                ? this.color.getBlue() > 127 ? 14 : 10
                                : this.color.getBlue() > 127 ? 12 : 8;

        // Return the text with the ANSI color.
        // Reset the color at the end.
        return "\u001B[38;5;" + ansiColor + "m" + Style.replaceTerminal(text) + "\u001B[0m";
    }
}
