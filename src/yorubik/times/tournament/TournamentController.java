package yorubik.times.tournament;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import yorubik.model.Tournament;
import yorubik.times.CategoryController;
import yorubik.util.Rubik;

import static yorubik.YoRubikController.rubik;

/**
 *
 * @author VakSF
 */
public class TournamentController implements Initializable {
    
    @FXML
    private ComboBox<Tournament> tournamentCB;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            
            if (rubik == null) {
                rubik = new Rubik();
            }
            
        } catch (Exception ex) {
            
            new Alert(
                    Alert.AlertType.ERROR,
                    "No hay internet :("
            ).show();
            
        }
        
        this.initCB();
    }
    
    private void initCB() {
        
        List<Tournament> tournaments = rubik.getList(Tournament.class);
        this.tournamentCB.getItems().addAll(tournaments);
        
        if (!tournaments.isEmpty()) {
            this.tournamentCB.getSelectionModel().selectFirst();
        }
        
    }
    
    @FXML
    private void next(ActionEvent event) throws IOException {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/yorubik/times/CategoryFXML.fxml"
        ));

        Stage stage = new Stage();
        stage.setScene(new Scene((Pane) loader.load()));
        
        CategoryController controller = loader.<CategoryController>getController();
        controller.setTournament(this.tournamentCB.getValue());
        
        stage.setTitle("Establecer categoría");
        stage.show();
        
        this.close(event);
        
    }
    
    private void close(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }
    
}