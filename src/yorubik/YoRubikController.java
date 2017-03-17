package yorubik;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import yorubik.util.Rubik;

/**
 *
 * @author VakSF
 */
public class YoRubikController implements Initializable {
    
    public static Rubik rubik;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        if (YoRubikController.rubik == null) {
            YoRubikController.rubik = new Rubik();
        }
        
    }
    
    @FXML
    private void openJudge() throws IOException {
        
        this.openFXML("/yorubik/judge/JudgeFXML.fxml", "Registrar juez");
        
    }
    
    @FXML
    private void openCuber() throws IOException {
        
        this.openFXML("/yorubik/cuber/CuberFXML.fxml", "Registrar cuber");
        
    }
    
    @FXML
    private void openTournament() throws IOException {
        
        this.openFXML("/yorubik/times/tournament/TournamentFXML.fxml", "Seleccionar torneo");
        
    }
    
    private void openFXML(String fxml, String title) throws IOException {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml)); 
       
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene((Pane) loader.load()));
        
        stage.setTitle(title);
        
        stage.showAndWait();
    }
    
}