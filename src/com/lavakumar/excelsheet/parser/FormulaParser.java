package com.lavakumar.excelsheet.parser;

import com.lavakumar.excelsheet.ExcelSheetDemo;
import com.lavakumar.excelsheet.expression.Expression;

// =========================================================
// Formula Parser
// Grammar:
// expression := term (('+' | '-') term)*
// term       := factor (('*' | '/') factor)*
// factor     := NUMBER | CELL | '(' expression ')' | '-' factor
// =========================================================

public class FormulaParser {
    public Expression parse(String formula) {
        Tokenizer tokenizer = new Tokenizer(formula);
        Parser parser = new Parser(tokenizer.tokenize());
        Expression expression = parser.parseExpression();
        parser.expect(TokenType.EOF);
        return expression;
    }
}
