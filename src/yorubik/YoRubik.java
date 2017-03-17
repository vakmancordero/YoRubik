package yorubik;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author VakSF
 */
public class YoRubik extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
//        Parent root = FXMLLoader.load(getClass().getResource("/yorubik/times/round/RoundFXML.fxml"));
//        Parent root = FXMLLoader.load(getClass().getResource("/yorubik/judge/JudgeFXML.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("/yorubik/times/selecttournament/SelectTournamentFXML.fxml"));
        
        Scene scene = new Scene(root);
        
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
