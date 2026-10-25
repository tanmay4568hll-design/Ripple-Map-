package com.ripplemap.algo;

import com.ripplemap.ds.ArrayStack;
import com.ripplemap.ds.CustomStack;

/**
 * Stack-based validator and evaluator for simulation threshold rules and conditions.
 * Implements:
 * 1. Bracket balancing verification for complex filter expressions: O(n) time, O(n) space
 * 2. Postfix evaluation for mathematical impact threshold formulas: O(n) time, O(n) space
 * 3. Shunting-Yard Infix to Postfix conversion: O(n) time, O(n) space
 */
public class ThresholdRuleValidator {

    /**
     * Validates whether parentheses, brackets, and braces in a rule expression are balanced.
     */
    public static boolean isBalanced(String expression) {
        if (expression == null || expression.isEmpty()) return true;
        CustomStack<Character> stack = new ArrayStack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '(' || c == '{' || c == '[') {
                stack.push(c);
            } else if (c == ')' || c == '}' || c == ']') {
                if (stack.isEmpty()) return false;
                char open = stack.pop();
                if (!matches(open, c)) return false;
            }
        }
        return stack.isEmpty();
    }

    private static boolean matches(char open, char close) {
        return (open == '(' && close == ')') ||
               (open == '{' && close == '}') ||
               (open == '[' && close == ']');
    }

    /**
     * Evaluates a space-delimited postfix arithmetic expression.
     * Supports operators +, -, *, /, ^.
     */
    public static double evaluatePostfix(String postfixExpr) {
        if (postfixExpr == null || postfixExpr.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty postfix expression");
        }
        CustomStack<Double> stack = new ArrayStack<>();
        String[] tokens = postfixExpr.trim().split("\\s+");

        for (String token : tokens) {
            if (isOperator(token)) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Invalid postfix expression: not enough operands for " + token);
                }
                double b = stack.pop();
                double a = stack.pop();
                double res = applyOperator(token.charAt(0), a, b);
                stack.push(res);
            } else {
                try {
                    stack.push(Double.parseDouble(token));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid number token: " + token);
                }
            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid postfix expression: multiple remaining values on stack");
        }
        return stack.pop();
    }

    /**
     * Converts standard infix expression with tokens separated by spaces to postfix notation
     * using Dijkstra's Shunting-Yard algorithm.
     */
    public static String infixToPostfix(String infixExpr) {
        if (infixExpr == null || infixExpr.trim().isEmpty()) return "";
        StringBuilder output = new StringBuilder();
        CustomStack<String> stack = new ArrayStack<>();
        String[] tokens = infixExpr.trim().split("\\s+");

        for (String token : tokens) {
            if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.append(stack.pop()).append(" ");
                }
                if (!stack.isEmpty() && stack.peek().equals("(")) {
                    stack.pop(); // pop '('
                }
            } else if (isOperator(token)) {
                while (!stack.isEmpty() && !stack.peek().equals("(") &&
                       precedence(stack.peek()) >= precedence(token)) {
                    output.append(stack.pop()).append(" ");
                }
                stack.push(token);
            } else {
                // Operand
                output.append(token).append(" ");
            }
        }

        while (!stack.isEmpty()) {
            output.append(stack.pop()).append(" ");
        }

        return output.toString().trim();
    }

    private static boolean isOperator(String s) {
        return s.length() == 1 && "+-*/^".contains(s);
    }

    private static int precedence(String op) {
        switch (op) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            case "^":
                return 3;
            default:
                return 0;
        }
    }

    private static double applyOperator(char op, double a, double b) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/':
                if (Math.abs(b) < 1e-9) throw new ArithmeticException("Division by zero");
                return a / b;
            case '^': return Math.pow(a, b);
            default: throw new IllegalArgumentException("Unknown operator: " + op);
        }
    }
}
