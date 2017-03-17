package yorubik.util;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 *
 * @author Arturh
 */
public class ValidatorUtil {
    
    private final Node[] nodes;
    
    public ValidatorUtil(Node... nodes) {
        this.nodes = nodes;
    }

    public Node[] getNodes() {
        return nodes;
    }
    
    public boolean validateFields() {
        
        for (Node node : this.nodes) {
            
            String className = node.getClass().getSimpleName();
            
            if (className.equals("TextField")) {
                
                TextField textField = (TextField) node;
                
                if (textField.getText().isEmpty()) {
                    
                    return false;
                    
                }
                
            } else {
                
                if (className.equals("PasswordField")) {
                    
                    PasswordField passwordField = (PasswordField) node;
                    
                    if (passwordField.getText().isEmpty()) {
                        
                        return false;
                        
                    }
                    
                } else {
                    
                    if (className.equals("DatePicker")) {
                        
                        DatePicker datePicker = (DatePicker) node;
                        
                        if (datePicker.getValue() == null) {
                            
                            return false;
                            
                        }
                        
                    }
                    
                }
                
            }
            
        }
        
        return true;
    }
    
    public void clearFields() {
        
        for (Node node : nodes) {
            
            String className = node.getClass().getSimpleName();
            
            if (className.equals("TextField")) {
                
                TextField textField = (TextField) node;
                
                textField.clear();
                
            } else {
                
                if (className.equals("PasswordField")) {
                    
                    PasswordField passwordField = (PasswordField) node;
                    
                    passwordField.clear();
                    
                } else {
                    
                    if (className.equals("DatePicker")) {
                        
                        DatePicker datePicker = (DatePicker) node;
                        
                        datePicker.getEditor().clear();
                        datePicker.setValue(null);
                        
                    }
                    
                }
                
            }
            
        }
        
    }
    
    public Alert emptyFields() {
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Ha olvidado llenar algún campo");
        alert.setContentText("Por favor introduzca los campos faltantes...");
        
        return alert;
        
    }
    
}