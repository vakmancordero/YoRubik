package yorubik.times;

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
import yorubik.model.Category;
import yorubik.model.Tournament;
import yorubik.times.round.RoundController;
import yorubik.util.Rubik;

import static yorubik.YoRubikController.rubik;

/**
 *
 * @author VakSF
 */
public class CategoryController implements Initializable {
    
    @FXML
    private ComboBox<Category> categoryCB;
    
    private Tournament tournament;
    
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
        
        List<Category> categories = rubik.getList(Category.class);
        this.categoryCB.getItems().addAll(categories);
        
        if (!categories.isEmpty()) {
            this.categoryCB.getSelectionModel().selectFirst();
        }
        
    }
    
    @FXML
    private void next(ActionEvent event) throws IOException {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/yorubik/times/round/RoundFXML.fxml"
        ));

        Stage stage = new Stage();
        stage.setScene(new Scene((Pane) loader.load()));
        
        RoundController controller =loader.<RoundController>getController();
        controller.setData(this.tournament, this.categoryCB.getValue());
        
        stage.setTitle("Round");
        stage.show();
        
        this.close(event);
        
    }
    
    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }
    
    private void close(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }
    
}
