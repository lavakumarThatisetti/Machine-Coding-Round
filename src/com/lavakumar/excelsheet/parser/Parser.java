package com.lavakumar.excelsheet.parser;

import com.lavakumar.excelsheet.expression.*;
import com.lavakumar.excelsheet.model.BinaryOperator;
import com.lavakumar.excelsheet.model.CellAddress;

import java.math.BigDecimal;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int index;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Expression parseExpression() {
        Expression left = parseTerm();

        while (match(TokenType.PLUS) || match(TokenType.MINUS)) {
            Token operator = previous();
            Expression right = parseTerm();

            if (operator.type() == TokenType.PLUS) {
                left = new BinaryExpression(left, right, BinaryOperator.ADD);
            } else {
                left = new BinaryExpression(left, right, BinaryOperator.SUBTRACT);
            }
        }

        return left;
    }

    Expression parseTerm() {
        Expression left = parseFactor();

        while (match(TokenType.STAR) || match(TokenType.SLASH)) {
            Token operator = previous();
            Expression right = parseFactor();

            if (operator.type() == TokenType.STAR) {
                left = new BinaryExpression(left, right, BinaryOperator.MULTIPLY);
            } else {
                left = new BinaryExpression(left, right, BinaryOperator.DIVIDE);
            }
        }

        return left;
    }

    Expression parseFactor() {
        if (match(TokenType.MINUS)) {
            return new UnaryMinusExpression(parseFactor());
        }

        if (match(TokenType.NUMBER)) {
            return new NumberExpression(new BigDecimal(previous().text()));
        }

        if (match(TokenType.CELL_REF)) {
            return new CellReferenceExpression(CellAddress.parse(previous().text()));
        }

        if (match(TokenType.LEFT_PAREN)) {
            Expression expression = parseExpression();
            expect(TokenType.RIGHT_PAREN);
            return expression;
        }

        throw new IllegalArgumentException("Unexpected token: " + peek());
    }

    void expect(TokenType expectedType) {
        if (!match(expectedType)) {
            throw new IllegalArgumentException("Expected token " + expectedType + " but found " + peek());
        }
    }

    boolean match(TokenType type) {
        if (check(type)) {
            index++;
            return true;
        }
        return false;
    }

    boolean check(TokenType type) {
        return peek().type() == type;
    }

    Token peek() {
        return tokens.get(index);
    }

    Token previous() {
        return tokens.get(index - 1);
    }
}
