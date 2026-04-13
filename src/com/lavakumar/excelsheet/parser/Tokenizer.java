package com.lavakumar.excelsheet.parser;

import com.lavakumar.excelsheet.ExcelSheetDemo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tokenizer {
    private final String input;

    Tokenizer(String input) {
        this.input = Objects.requireNonNull(input);
    }

    List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        int i = 0;

        while (i < input.length()) {
            char ch = input.charAt(i);

            if (Character.isWhitespace(ch)) {
                i++;
                continue;
            }

            if (ch == '+') {
                tokens.add(new Token(TokenType.PLUS, "+"));
                i++;
                continue;
            }

            if (ch == '-') {
                tokens.add(new Token(TokenType.MINUS, "-"));
                i++;
                continue;
            }

            if (ch == '*') {
                tokens.add(new Token(TokenType.STAR, "*"));
                i++;
                continue;
            }

            if (ch == '/') {
                tokens.add(new Token(TokenType.SLASH, "/"));
                i++;
                continue;
            }

            if (ch == '(') {
                tokens.add(new Token(TokenType.LEFT_PAREN, "("));
                i++;
                continue;
            }

            if (ch == ')') {
                tokens.add(new Token(TokenType.RIGHT_PAREN, ")"));
                i++;
                continue;
            }

            if (Character.isDigit(ch) || ch == '.') {
                int start = i;
                i++;

                while (i < input.length()) {
                    char current = input.charAt(i);
                    if (Character.isDigit(current) || current == '.') {
                        i++;
                    } else {
                        break;
                    }
                }

                tokens.add(new Token(TokenType.NUMBER, input.substring(start, i)));
                continue;
            }

            if (Character.isLetter(ch)) {
                int start = i;
                i++;

                while (i < input.length() && Character.isLetter(input.charAt(i))) {
                    i++;
                }

                while (i < input.length() && Character.isDigit(input.charAt(i))) {
                    i++;
                }

                String cellRef = input.substring(start, i);
                tokens.add(new Token(TokenType.CELL_REF, cellRef));
                continue;
            }

            throw new IllegalArgumentException("Invalid character in formula: " + ch);
        }

        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }
}
