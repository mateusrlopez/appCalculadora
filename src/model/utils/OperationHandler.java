package model.utils;

import java.awt.Toolkit;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import javafx.scene.control.Label;

public class OperationHandler implements Constants {
	private static ScriptEngineManager manager = new ScriptEngineManager();
	
	private static Label lb1;
	private static Label lb2;
	private static Label lb3;
	
	public static boolean operationInProgress;
	public static boolean lockInput;
	public static boolean specialOperationInProgress;
	private static String result;
	private static String temporaryResult;
	
	public static void handleOperations(String op,boolean special){
		String recentOperation = lb2.getText();
		String currentOperand = lb1.getText();
		char lastOperand;

		if (currentOperand.matches("\\d*[\\.]")) {
			currentOperand = Backsp.apply(currentOperand);
			lb1.setText(currentOperand);
		}
		try {
			if(special) {
				if (op.equals("Reciproc"))
					temporaryResult = processOperation(String.format("1/%s",(specialOperationInProgress)?temporaryResult:currentOperand));
				else if (op.equals("√"))
					temporaryResult = processOperation(String.format("Math.sqrt(%s)",(specialOperationInProgress)?temporaryResult:currentOperand));
				else if (op.equals("x²"))
					temporaryResult = processOperation(String.format("Math.pow(%s,2)",(specialOperationInProgress)?temporaryResult:currentOperand));
				else 
					temporaryResult = processOperation(String.format("Math.pow(%s,3)",(specialOperationInProgress)?temporaryResult:currentOperand));
				handleSpecialOperation(SpecialOP.get(op));
			} else {
				if (operationInProgress && InputHandler.isOverridible && !specialOperationInProgress) 
					lb2.setText(recentOperation.substring(0, recentOperation.length() - 1) + op);
				else if (operationInProgress && !InputHandler.isOverridible) {
					lastOperand = recentOperation.charAt(recentOperation.length() - 1);
					lb2.setText(recentOperation + String.format(" %s %s", currentOperand, op));
					result = OperationHandler.processOperation(result + lastOperand + currentOperand);
					lb1.setText(String.format("%s",result));
					InputHandler.isOverridible = true;
				} else if (specialOperationInProgress && !operationInProgress) {
					lb2.setText(recentOperation + " " + op);
					result = temporaryResult;
					specialOperationInProgress = false;
					operationInProgress = true;
				} else if (specialOperationInProgress && operationInProgress) {
					lastOperand = recentOperation.split(" ")[recentOperation.split(" ").length-2].charAt(0);
					lb2.setText(recentOperation + String.format(" %s",op));
					result = OperationHandler.processOperation(result + lastOperand + temporaryResult);
					lb1.setText(String.format("%s",result));
					InputHandler.isOverridible = true;
					specialOperationInProgress = false;
				} else {
					lb2.setText(recentOperation + String.format(" %s %s", currentOperand, op));
					result = currentOperand;
					operationInProgress = true;
					InputHandler.isOverridible = true;
				}
			}
		} catch (OperationException e) {
			Toolkit.getDefaultToolkit().beep();
			lb1.setText("");
			lb3.setText(e.getMessage());
			lockInput = true;
		} catch(ScriptException e) {
			e.printStackTrace();
		}
	}
	
	public static void handleEquals(){
		String recentOperation = lb2.getText();
		if (lb2.getText().equals("")) 
			InputHandler.isOverridible = true;
		else {
			try {
				if (operationInProgress && !specialOperationInProgress)
					result = processOperation(result + recentOperation.charAt(recentOperation.length() - 1) + lb1.getText()).toString();
				else if (specialOperationInProgress && !operationInProgress)
					result = temporaryResult;
				else {
					char lastOperand = recentOperation.split(" ")[recentOperation.split(" ").length-2].charAt(0);
					result = processOperation(result + lastOperand + temporaryResult).toString();
				}
				lb2.setText("");
				lb1.setText(String.format("%s",result));
				InputHandler.isOverridible = true;
				operationInProgress = false;
				specialOperationInProgress = false;
			} catch (OperationException e) {
				Toolkit.getDefaultToolkit().beep();
				lb1.setText("");
				lb3.setText(e.getMessage());
				lockInput = true;
			} catch(ScriptException e) {
				e.printStackTrace();
			}
		}
	}

	private static String processOperation(String operation) throws ScriptException {
		ScriptEngine engine = manager.getEngineByName("javascript");
		Object result = engine.eval(operation);
		if (!result.toString().matches(Regex1))
			throw new OperationException("Operação inválida");
		return handleResultString(result.toString());
	}
	
	private static String handleResultString(String result) {
		if(result.length() > 20) {
			if(result.matches("(\\-)?\\d*[\\.]\\d*[E](\\-)?\\d*?")) {
				int i = result.indexOf("E");
				String exponentialPart = result.substring(i);
				result = result.substring(0,20-exponentialPart.length()) + exponentialPart;
			}
			else result = result.substring(0,20);
		}
		if(result.matches("\\d*[\\.][0]")) result = result.substring(0,result.length()-2);
		return result;
	}
	
	private static void handleSpecialOperation(String operation) {
		if(!specialOperationInProgress) {
			lb2.setText(lb2.getText() + String.format(" %s(%s)",operation,lb1.getText()));
			InputHandler.isOverridible = true;
			specialOperationInProgress = true;
		} else handleMultipleSpecialOperations(operation);
		lb1.setText(temporaryResult);
	}
	
	private static void handleMultipleSpecialOperations(String s) {
		String recentOperation = lb2.getText();
		String txt = recentOperation.split(" ")[recentOperation.split(" ").length - 1];
		StringBuilder sb = new StringBuilder(recentOperation);
		int index = sb.lastIndexOf(" ");
		sb.delete(index,sb.length());
		sb.append(String.format(" %s(%s)",s,txt));
		lb2.setText(sb.toString());
	}
	
	public static void setLabels(Label lb1, Label lb2, Label lb3) {
		OperationHandler.lb1 = lb1;
		OperationHandler.lb2 = lb2;
		OperationHandler.lb3 = lb3;
	}
}
