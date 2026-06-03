package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;

public class calculator_controller {

    @FXML private Label  expressionLabel;
    @FXML private Label  resultLabel;
    @FXML private Button percentBtn;
    @FXML private Button backspaceBtn;
    @FXML private Button divideBtn;
    @FXML private Button multiplyBtn;
    @FXML private Button subtractBtn;
    @FXML private Button negateBtn;

    private StringBuilder currentInput    = new StringBuilder("0");
    private double        firstOperand    = 0;
    private String        currentOperator = null;
    private boolean       waitingForSecond = false;
    private boolean       justCalculated   = false;

    @FXML
    public void initialize() {
        percentBtn.setText("%");
        backspaceBtn.setText("\u232B");  // ⌫
        divideBtn.setText("\u00F7");     // ÷
        multiplyBtn.setText("\u00D7");   // ×
        subtractBtn.setText("\u2212");   // −
        negateBtn.setText("+/-");
        resultLabel.setText("0");
        expressionLabel.setText("");
    }

    @FXML
    private void handleNumber(ActionEvent event) {
        String digit = ((Button) event.getSource()).getText();

        if (waitingForSecond || justCalculated) {
            currentInput     = new StringBuilder(digit);
            waitingForSecond = false;
            justCalculated   = false;
        } else {
            if (currentInput.toString().equals("0")) {
                currentInput = new StringBuilder(digit);
            } else {
                currentInput.append(digit);
            }
        }
        resultLabel.setText(currentInput.toString());
    }

    @FXML
    private void handleDecimal(ActionEvent event) {
        if (waitingForSecond || justCalculated) {
            currentInput     = new StringBuilder("0.");
            waitingForSecond = false;
            justCalculated   = false;
        } else if (!currentInput.toString().contains(".")) {
            currentInput.append(".");
        }
        resultLabel.setText(currentInput.toString());
    }

    @FXML
    private void handleOperator(ActionEvent event) {
        String op      = ((Button) event.getSource()).getText();
        double current = parseCurrentInput();

        if (currentOperator != null && !waitingForSecond) {
            double result = calculate(firstOperand, current, currentOperator);
            firstOperand = result;
            expressionLabel.setText(formatNumber(result) + " " + op);
            resultLabel.setText(formatNumber(result));
            currentInput = new StringBuilder(formatNumber(result));
        } else {
            firstOperand = current;
            expressionLabel.setText(formatNumber(current) + " " + op);
        }

        currentOperator  = op;
        waitingForSecond = true;
        justCalculated   = false;
    }

    @FXML
    private void handleEquals(ActionEvent event) {
        if (currentOperator == null) return;

        double second = parseCurrentInput();
        double result = calculate(firstOperand, second, currentOperator);

        expressionLabel.setText(
            formatNumber(firstOperand) + " " + currentOperator
            + " " + formatNumber(second) + " =");
        resultLabel.setText(formatNumber(result));

        currentInput     = new StringBuilder(formatNumber(result));
        currentOperator  = null;
        waitingForSecond = false;
        justCalculated   = true;
    }

    @FXML
    private void handleAC(ActionEvent event) {
        currentInput     = new StringBuilder("0");
        firstOperand     = 0;
        currentOperator  = null;
        waitingForSecond = false;
        justCalculated   = false;
        expressionLabel.setText("");
        resultLabel.setText("0");
    }

    @FXML
    private void handleBackspace(ActionEvent event) {
        if (justCalculated || waitingForSecond) {
            currentInput   = new StringBuilder("0");
            justCalculated = false;
            resultLabel.setText("0");
            return;
        }
        if (currentInput.length() > 1) {
            currentInput.deleteCharAt(currentInput.length() - 1);
        } else {
            currentInput = new StringBuilder("0");
        }
        resultLabel.setText(currentInput.toString());
    }

    private String formatNumber(double val) {
        if (Double.isNaN(val) || Double.isInfinite(val)) return "Error";
        if (val == Math.floor(val) && Math.abs(val) < 1e15) {
            return Long.toString((long) val);
        }
        String s = String.format("%.9f", val);
        s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        return s;
    }

    @FXML
    private void handleNegate(ActionEvent event) {
        double val   = -parseCurrentInput();
        String str   = formatNumber(val);
        currentInput = new StringBuilder(str);
        resultLabel.setText(str);
    }

    private double parseCurrentInput() {
        try {
            return Double.parseDouble(currentInput.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double calculate(double a, double b, String op) {
        if (op.equals("+"))              return a + b;
        if (op.equals("\u2212"))         return a - b;  // −
        if (op.equals("\u00D7"))         return a * b;  // ×
        if (op.equals("\u00F7"))         return (b != 0) ? a / b : Double.NaN;  // ÷
        return b;
    }

    private String formatNumber(double val) {
        if (Double.isNaN(val) || Double.isInfinite(val)) return "Error";
        if (val == Math.floor(val) && Math.abs(val) < 1e15) {
            return Long.toString((long) val);
        }
        String s = String.format("%.9f", val);
        s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        return s;
    }
}
