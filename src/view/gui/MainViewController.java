package view.gui;

import java.awt.Toolkit;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.utils.Constants;
import model.utils.InputHandler;
import model.utils.OperationHandler;
import view.gui.utils.Constraints;

public class MainViewController implements Initializable,Constants {	
	@FXML 
	private AnchorPane anchorPane;
	@FXML 
	private HBox hbox;
	
	@FXML
	private Button btN1;
	@FXML 
	private Button btN2;
	@FXML
	private Button btN3;
	@FXML 
	private Button btN4;
	@FXML
	private Button btN5;
	@FXML
	private Button btN6;
	@FXML
	private Button btN7;
	@FXML 
	private Button btN8;
	@FXML
	private Button btN9;
	@FXML
	private Button btN0;
	
	@FXML
	private Button btSoma;
	@FXML
	private Button btSubt;
	@FXML
	private Button btMult;
	@FXML 
	private Button btDiv;
	@FXML
	private Button btPercentage;
	
	@FXML
	private Button btSqRoot;
	@FXML
	private Button btReciproc;
	@FXML
	private Button btSquare;
	@FXML
	private Button btCube;
	
	@FXML 
	private Button btEqual;
	@FXML 
	private Button btClear;
	@FXML 
	private Button btClearInput;

	@FXML 
	private Button btNegate;
	@FXML
	private Button btBacksp;
	@FXML
	private Button btPoint;
	
	@FXML 
	private Label lb1;
	@FXML 
	private Label lb2;
	@FXML 
	private Label lb3;
	
	private double xOffset = 0;
	private double yOffset = 0;

	@Override 
	public void initialize(URL url, ResourceBundle rb) {
		Constraints.setLabelDouble(lb1);
		Constraints.setLabelMaxLength(lb1,20);
		Constraints.setLabelResizable(lb1);
		Constraints.setLabelNonNull(lb1);
		InputHandler.isOverridible = true;
		OperationHandler.setLabels(lb1, lb2, lb3);
		InputHandler.setLabels(lb1, lb2);
	}
	
	@FXML 
	private void handleButtonAction(ActionEvent event){
		Button eventBT = (Button)event.getSource();
		if(!OperationHandler.lockInput) {
			switch(eventBT.getId()) {
				case "btBacksp":
					InputHandler.handleBackspace();
					break;
				case "btClearInput":
					lb1.setText("0");
					if(OperationHandler.specialOperationInProgress) {
						InputHandler.clearSpecialOperation();
						OperationHandler.specialOperationInProgress = false;
					}
					InputHandler.isOverridible = true;
					break;
				case "btClear":
					lb1.setText("0");
					lb2.setText("");
					OperationHandler.operationInProgress = false;
					OperationHandler.specialOperationInProgress = false;
					InputHandler.isOverridible = true;
					break;
				case "btReciproc":
					OperationHandler.handleOperations("Reciproc",true);
					break;
				case "btNegate":
					InputHandler.handleNegate();
					break;
				case "btMult":
					OperationHandler.handleOperations("*",false);
					break;
				case "btDiv":
					OperationHandler.handleOperations("/",false);
					break;
				case "btPercentage":
				case "btSubt":
				case "btSoma":
					OperationHandler.handleOperations(eventBT.getText(),false);
					break;
				case "btSquare":
				case "btCube":
				case "btSqRoot":
					OperationHandler.handleOperations(eventBT.getText(),true);
					break;
				case "btEqual":
					OperationHandler.handleEquals();
					break;
				default:
					InputHandler.handleNumberAndPoints(eventBT.getText());
					break;
			} 
		} else {
			if(eventBT.getText().matches("[C]([I])?")) {
				lb3.setText("");
				lb2.setText("");
				lb1.setText("0");
				OperationHandler.operationInProgress = false;
				OperationHandler.lockInput = false;
				OperationHandler.specialOperationInProgress = false;
				InputHandler.isOverridible = true;
			} else 
				Toolkit.getDefaultToolkit().beep();
		}
	}
	
	private void handleKeyEvent(KeyEvent event){
		if(!OperationHandler.lockInput) {
			if (SQRT_COMB.match(event)) 
				OperationHandler.handleOperations("√",true);
			else if (PLUS_COMB.match(event)) 
				OperationHandler.handleOperations("+",false);
			else if (MOD_COMB.match(event)) 
				OperationHandler.handleOperations("%",true);
			else if (MULT_COMB.match(event)) 
				OperationHandler.handleOperations("*",false);
			else {
				switch(event.getCode()) {
					case BACK_SPACE:
						InputHandler.handleBackspace();
						break;
					case UNDEFINED:
					case ADD:
					case MINUS:
					case DIVIDE:
					case MULTIPLY:
					case SUBTRACT:
						OperationHandler.handleOperations(event.getText(),false);
						break;
					case EQUALS:
						OperationHandler.handleEquals();
						break;
					default:
						if(!event.getText().equals(""))
							InputHandler.handleNumberAndPoints(event.getText());
				}
			}
		} else Toolkit.getDefaultToolkit().beep();
	}
	
	public void stageSetters(Stage stage,Scene scene) {		
		scene.setOnKeyPressed(event -> handleKeyEvent(event));
		
		hbox.setOnMousePressed(event -> {
			xOffset = event.getSceneX();
			yOffset = event.getSceneY();

		});
		hbox.setOnMouseDragged(event -> {
			stage.setX(event.getScreenX() - xOffset);
			stage.setY(event.getScreenY() - yOffset);
		});
	}
	
	@FXML 
	private void closeApplication() {
		System.exit(0);
	}
	
	@FXML 
	private void minimizeApplication(MouseEvent event) {
		((Stage)((Label)event.getSource()).getScene().getWindow()).setIconified(true);
	}
}
