package yorubik.times.round;

import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;

import yorubik.model.Tournament;
import yorubik.model.Round;
import yorubik.model.Category;
import yorubik.model.Cuber;
import yorubik.model.Judge;
import yorubik.model.Time;

import yorubik.reader.Reader;
import yorubik.util.Rubik;
import yorubik.util.ValidatorUtil;

import static yorubik.YoRubikController.rubik;

/**
 *
 * @author VakSF
 */
public class RoundController implements Initializable {
    
    @FXML
    private TextField time1TF, time2TF, time3TF, time4TF, time5TF;
    
    @FXML
    private Label judgeLabel, cuberLabel, dnfLabel;
    
    @FXML
    private Label categoryLabel, tournamentLabel;
    
    @FXML
    private RadioButton dnf1, dnf2, dnf3, dnf4, dnf5;
    
    @FXML
    private RadioButton plus1, plus2, plus3, plus4, plus5;
    
    @FXML
    private TextField timePlusTF1, timePlusTF2, timePlusTF3, timePlusTF4, timePlusTF5;
    
    public static Reader readerEvent;
    public static Thread readerThread;
    
    private ValidatorUtil validatorUtil;
    
    private Tournament tornament;
    private Category category;
    private Cuber cuber;
    private Judge judge;
    
    private String findBy = "cuber";
    private Alert alert;
    
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
        
        this.initAlert();
        this.initValidator();
        this.initObserver();
        
        readerThread = new Thread(readerEvent);
    }
    
    private void initAlert() {
        this.alert = new Alert(AlertType.INFORMATION);
        this.alert.setTitle("Información");
        this.alert.setHeaderText("Busqueda de huella");
        this.alert.setContentText("Introduce tu huella");
    }
    
    private void initValidator() {
        this.validatorUtil = new ValidatorUtil(
                this.time1TF,
                this.time2TF,
                this.time3TF,
                this.time4TF,
                this.time5TF
        );
    }
    
    public void setData(Tournament tornament, Category category) {
        this.tornament = tornament;
        this.category = category;
        
        this.tournamentLabel.setText(this.tornament.getName());
        this.categoryLabel.setText(this.category.getName());
        this.dnfLabel.setText(Double.toString(this.category.getDnf()));
    }
    
    @FXML
    private void setCuber() {
        
        readerEvent.setIsRunning(true);
        readerThread = new Thread(readerEvent);
        readerThread.start();
        
        this.findBy = "cuber";
        Optional<ButtonType> option = this.alert.showAndWait();
        
        if (option.isPresent()) {
            
            if (option.get() == ButtonType.OK) {
            
                readerEvent.setIsRunning(false);
                readerThread.interrupt();

            }
            
        }
        
    }
    
    @FXML
    private void setJudge() {
        
        readerEvent.setIsRunning(true);
        readerThread = new Thread(readerEvent);
        readerThread.start();
        
        this.findBy = "judge";
        Optional<ButtonType> option = this.alert.showAndWait();
        
        if (option.isPresent()) {
            
            if (option.get() == ButtonType.OK) {
            
                readerEvent.setIsRunning(false);
                readerThread.interrupt();

            }
            
        }
    }
    
    @FXML
    private void save(ActionEvent event) {
        
        if (this.cuber != null) {
            
            if (this.judge != null) {
                
                if (this.validatorUtil.validateFields()) {
                    
                    if (this.validatePlus()) {
                        
                        Round round = new Round(
                                this.tornament.getId(), this.judge.getId(),
                                this.cuber.getId(), this.category.getId()
                        );
                        
                        rubik.save(round);
                        
                        int roundId = rubik.getLastId("rounds");
                        
                        List<Time> timeList = new ArrayList<>();
                        
                        double dnf = this.category.getDnf();
                        
                        timeList.add(getTime(dnf1, plus1, time1TF, timePlusTF1, roundId, 1, dnf));
                        timeList.add(getTime(dnf2, plus2, time2TF, timePlusTF2, roundId, 2, dnf));
                        timeList.add(getTime(dnf3, plus3, time3TF, timePlusTF3, roundId, 3, dnf));
                        timeList.add(getTime(dnf4, plus4, time4TF, timePlusTF4, roundId, 4, dnf));
                        timeList.add(getTime(dnf5, plus5, time5TF, timePlusTF5, roundId, 5, dnf));
                        
                        double average = this.getAverage(timeList);
                        double pb = this.getPb(timeList);
                        
                        round.setAvg(average);
                        round.setPb(pb);
                        
                        rubik.update(round);
                        
                        this.alert("info", "Promedio: " + average);
                        
                        ((Node) event.getSource()).getScene().getWindow().hide();
                        
                    } else {
                        
                        this.alert("error", "Por favor, complete el plus faltante");
                        
                    }
                    
                } else {
                    
                    this.alert("error", "Por favor, complete los campos faltantes");
                    
                }
                
            } else {
                
                this.alert("error", "Por favor, establezca un juez para la ronda");
                
            }
            
        } else {
            
            this.alert("error", "Por favor, establezca un cuber para la ronda");
            
        }
        
    }
    
    private boolean validatePlus() {
        
        TextField[] textFields = new TextField[] {
            this.timePlusTF1,
            this.timePlusTF2,
            this.timePlusTF3,
            this.timePlusTF4,
            this.timePlusTF5
        };
        
        RadioButton[] radioButtons = new RadioButton[] {
            this.plus1,
            this.plus2,
            this.plus3,
            this.plus4,
            this.plus5
        };
        
        for (int i = 0; i < radioButtons.length; i++) {
            
            RadioButton radioButton = radioButtons[i];
            
            if (radioButton.isSelected()) {
                
                TextField textField = textFields[i];
                
                if (textField.getText().isEmpty()) {
                    
                    return false;
                    
                }
                
            }
            
            
        }
        
        
        return true;
    }
    
    private Time getTime(RadioButton dnf, RadioButton plus, TextField timeValue,
            TextField timePlus, int roundId, int iteration, double dnfValue) {
        
        Time _time;
        
        if (dnf.isSelected()) {
                        
            _time = new Time(dnfValue, "no reason", dnfValue, 0.0, iteration, roundId);
            
        } else {
            
            double time1 = Double.parseDouble(timeValue.getText());
            
            if (plus.isSelected()) {
                
                double _timePlus = Double.parseDouble(timePlus.getText());
                
                _time = new Time(time1 + _timePlus, "no reason", 0.0, _timePlus, iteration, roundId);
                
            } else {
                
                _time = new Time(time1, "no reason", 0.0, 0.0, iteration, roundId);
                
            }

        }
        
        System.out.println(_time.toString());
        
        rubik.save(_time);
        
        return _time;
    }
    
    public double getAverage(List<Time> times){
      
        double best = 10000;
        double worst = -100000;
        double number;
        
        for (int i = 0; i < times.size(); i++) {
            
            number = times.get(i).getTime();
            
            if (number < best) {
                
                best = number;
                
            } else {
                
                if (number > worst) {
                    
                    worst = number;
                    
                }
                
            }
                
        }
        
        double suma = 0;
        
        for (int i = 0; i < times.size(); i++) {
            
            suma += times.get(i).getTime();
            
        }
        
        suma = suma - best - worst;
        
        double avg = (suma / (times.size() - 2));
        
        return avg;
        
    }
    
    private double getPb(List<Time> times) {
        
        double best = 10000;
        double worst = -100000;
        double number;
        
        for (int i = 0; i < times.size(); i++) {
            
            number = times.get(i).getTime();
            
            if (number < best) {
                
                best = number;
                
            } else {
                
                if (number > worst) {
                    
                    worst = number;
                    
                }
                
            }
                
        }
        
        return best;
    }
    
    private void initObserver() {
        
        try {
            
            Observer observer = (Observable observable, Object sampleObject) -> {

                DPFPSample sample = (DPFPSample) sampleObject;

                Platform.runLater(() -> {
                    
                    if (findBy.equalsIgnoreCase("cuber")) {
                        
                        boolean found = false;
                        
                        List<Cuber> cubers = rubik.getList(Cuber.class);
                        
                        for (Cuber cuber : cubers) {
                            
                            DPFPTemplate template = rubik.deserializeCuberTemplate(cuber);
                            
                            if (template != null) {
                                
                                if (rubik.verify(sample, template)) {
                                    
                                    this.alert("info", "Cuber encontrado: " + cuber.toString());
                                    
                                    this.cuberLabel.setText(cuber.getName());
                                    
                                    this.cuber = cuber;
                                    
                                    readerEvent.setIsRunning(false);
                                    readerThread.interrupt();
                                    
                                    this.alert.close();
                                    
                                    found = true;
                                    
                                    break;
                                    
                                }
                                
                            }
                            
                        }
                        
                        if (!found) {
                            
                            this.alert("error", "Cuber no encontrado :/");
                            
                            readerEvent.setIsRunning(false);
                            readerThread.interrupt();
                            
                            this.alert.close();
                            
                        }
                            
                    } else {
                        
                        if (findBy.equalsIgnoreCase("judge")) {
                            
                            boolean found = false;
                            
                            List<Judge> judges = rubik.getList(Judge.class);
                            
                            for (Judge judge : judges) {
                                
                                DPFPTemplate template = rubik.deserializeJudgeTemplate(judge);
                                
                                if (template != null) {
                                    
                                    if (rubik.verify(sample, template)) {
                                        
                                        this.alert("info", "Juez encontrado: " + judge.toString());
                                        
                                        this.judgeLabel.setText(judge.getName());
                                        
                                        this.judge = judge;
                                        
                                        readerEvent.setIsRunning(false);
                                        readerThread.interrupt();
                                        
                                        this.alert.close();
                                    
                                        found = true;
                                        
                                        break;
                                        
                                    }
                                    
                                }
                                
                            }
                            
                            if (!found) {
                                
                                this.alert("error", "Juez no encontrado :/");
                                
                                readerEvent.setIsRunning(false);
                                readerThread.interrupt();

                                this.alert.close();
                                
                            }
                            
                        }
                        
                    }

                });
            };

            readerEvent = new Reader();
            readerEvent.addObserver(observer);
            
        } catch (Exception ex) {
            
            ex.printStackTrace();
            
            this.alert("error", ex.toString());
            
        }
        
    }
    
    public void alert(String type, String message) {
        
        if (type.equalsIgnoreCase("error")) {
            
            new Alert(AlertType.ERROR, message).show();
            
        } else {
            
            if (type.equalsIgnoreCase("info")) {
                
                new Alert(AlertType.INFORMATION, message).show();
                
            }
            
        }
        
    }
    
}