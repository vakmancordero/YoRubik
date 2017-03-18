package yorubik;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author VakSF
 */
public class YoRubik extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("YoRubikFXML.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setOnCloseRequest((WindowEvent event) -> {
            
            System.exit(0);
            
        });
        
        stage.setTitle("YoRubik Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
