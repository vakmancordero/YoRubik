package yorubik.times.selecttournament;

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
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import yorubik.model.Tournament;
import yorubik.times.TimeController;
import yorubik.util.Rubik;

/**
 *
 * @author VakSF
 */
public class SelectTournamentController implements Initializable {
    
    @FXML
    private ComboBox<Tournament> tournamentCB;
    
    private Rubik rubik;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.rubik = new Rubik();
        this.initCB();
    }
    
    private void initCB() {
        
        List<Tournament> tournaments = this.rubik.getList(Tournament.class);
        this.tournamentCB.getItems().addAll(tournaments);
        
        if (!tournaments.isEmpty()) {
            this.tournamentCB.getSelectionModel().selectFirst();
        }
        
    }
    
    @FXML
    private void next(ActionEvent event) throws IOException {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/yorubik/times/TimeFXML.fxml"
        ));

        Stage stage = new Stage();
        stage.setScene(new Scene((Pane) loader.load()));
        
        TimeController controller = 
                loader.<TimeController>getController();
        
        controller.setTournament(this.tournamentCB.getValue());
        
        stage.setTitle("Establecer compañía");

        stage.show();
        
        this.close(event);
        
    }
    
    private void close(ActionEvent event) {
        
        System.out.println("Here");
        
        ((Node) event.getSource()).getScene().getWindow().hide();
    }
    
}
