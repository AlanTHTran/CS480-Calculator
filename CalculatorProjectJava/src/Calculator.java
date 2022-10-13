import java.util.*;

public class Calculator{
    // Create a custom eval function, which is a version of a recursive descent parser
    //  It is able to take a user submitted math string and evaluate it
    //  The majority of the eval function was taken from this link:
    //  https://stackoverflow.com/questions/3422673/how-to-evaluate-a-math-expression-given-in-string-form/26227947#26227947
    //  and was modified to contain and work with more functions
    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;


            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') {
                    nextChar();
                }
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }
                return x;
            }

            // To parse the expression based on the operators given and by precedence

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            //        term = pow | term `*` pow | term `/` pow
            //        pow = factor | pow ^ factor
            // factor = `+` pow  | `-` pow | `(` expression `)`
            //        | number | functionName factor

            // To parse addition and subtraction
            double parseExpression() {
                double x = parseTerm();
                while(true) {
                    if (eat('+')) { //addition
                        x += parseTerm();
                    } else if (eat('-')) { //subtraction
                        x -= parseTerm();
                    } else {
                        return x;
                    }
                }
            }

            // To parse multiplication and division
            double parseTerm() {
                double x = parsePow();
                while(true) {
                    if (eat('*')) { //multiplication
                        x *= parsePow();
                    } else if (eat('/')) { //division
                        x /= parsePow();
                    } else {
                        return x;
                    }
                }
            }

            // To parse exponents
            double parsePow() {
                double x = parseFactor();
                while(true) {
                    if (eat('^')) {
                        x = Math.pow(x, parseFactor());
                    } else {
                        return x;
                    }
                }
            }


            // The general parse function, which also sets up unary and binary + and -, while also setting up
            // the parentheses precedence
            double parseFactor() {
                if (eat('+')) { //unary plus
                    return parsePow(); //unary plus
                }
                if (eat('-')) { //unary minus
                    return -parsePow(); // unary minus
                }
                double x = 0;
                int startPos = this.pos;
                if (eat('(')) { //parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { //numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') {
                        nextChar();
                    }
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z' || ch == '!') { //functions
                    while (ch >= 'a' && ch <= 'z' || ch == '!') {
                        nextChar();
                    }
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    // All the equals func can be to execute the associated trigonometric or logarithmic function
                    if (func.equals("sqrt")) {
                        x = Math.sqrt(x);
                    } else if (func.equals("log")) {
                        x = Math.log10(x);
                    } else if (func.equals("ln")) {
                        x = Math.log(x);
                    } else if (func.equals("sin")) {
                        x = Math.sin(Math.toRadians(x));
                    } else if (func.equals("cos")) {
                        x = Math.cos(Math.toRadians(x));
                    } else if (func.equals("tan")) {
                        x = Math.tan(Math.toRadians(x));
                    } else if (func.equals("cot")) {
                        x = 1.0 / Math.tan(Math.toRadians(x));
                    } else if (func.equals("sinh")) {
                        x = Math.sinh(x);
                    } else if (func.equals("cosh")) {
                        x = Math.cosh(x);
                    } else if (func.equals("tanh")) {
                        x = Math.tanh(x);
                    } else if (func.equals("!")) {
                        int fact = 1;
                        double number = x;
                        for (int i = 1; i <= number; i++) {
                            fact *= i;
                        }
                        x = fact;
                    } else {
                        throw new RuntimeException("Unknown function: " + func);
                    }
                } else if (str.equalsIgnoreCase("exit")) {
                    System.exit(0);
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }
                return x;
            }
        }.parse();
    }

    public static void main(String args[])
    {
        Scanner scanner = new Scanner(System.in);
        String userInput;
        System.out.println("Hello");
        do {
            System.out.println("Please input the expression you would like solved. Or Type 'exit' to close the program.");
            userInput = scanner.nextLine();
            try {
                System.out.println("Answer is: " + eval(userInput));
            }
            catch (RuntimeException e) {
                System.out.println("There seems to be a problem with the expression you put in.");
            }
        } while (!userInput.equalsIgnoreCase("exit"));
    }
}