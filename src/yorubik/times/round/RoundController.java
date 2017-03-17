package yorubik.times.round;

import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import yorubik.model.Category;
import yorubik.model.Cuber;
import yorubik.model.Judge;
import yorubik.model.Round;
import yorubik.model.Time;
import yorubik.model.Tournament;
import yorubik.reader.Reader;
import yorubik.util.Rubik;
import yorubik.util.ValidatorUtil;

/**
 *
 * @author VakSF
 */
public class RoundController implements Initializable {
    
    @FXML
    private TextField time1TF, time2TF, time3TF, time4TF, time5TF;
    
    @FXML
    private Label judgeLabel, cuberLabel;
    
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
    
    private Rubik rubik;
    
    private String findBy = "cuber";
    private Alert alert;
    
    private ValidatorUtil validatorUtil;
    
    private Tournament tornament;
    private Category category;
    private Cuber cuber;
    private Judge judge;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.rubik = new Rubik();
        
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
        
    }
    
    @FXML
    private void setCuber() {
        
        findBy = "cuber";
        
        this.alert.show();
        
        readerEvent.setIsRunning(true);
        readerThread = new Thread(readerEvent);

        readerThread.start();
    }
    
    @FXML
    private void setJudge() {
        
        findBy = "judge";
        
        this.alert.show();
        
        readerEvent.setIsRunning(true);
        readerThread = new Thread(readerEvent);

        readerThread.start();
        
    }
    
    @FXML
    private void save() {
        
        if (this.cuber != null) {
            
            if (this.judge != null) {
                
                if (this.validatorUtil.validateFields()) {
                    
                    Round round = new Round(
                            this.tornament.getId(), this.judge.getId(), 
                            this.cuber.getId(), this.category.getId()
                    );
                    
                    this.rubik.save(round);
                    
                    int roundId = this.rubik.getLastId("rounds");
                    
                    List<Time> timeList = new ArrayList<>();
                    
                    double dnf = this.category.getDnf();
                    
                    timeList.add(getTime(dnf1, plus1, time1TF, timePlusTF1, roundId, 1, dnf));
                    timeList.add(getTime(dnf2, plus2, time2TF, timePlusTF2, roundId, 2, dnf));
                    timeList.add(getTime(dnf3, plus3, time3TF, timePlusTF3, roundId, 3, dnf));
                    timeList.add(getTime(dnf4, plus4, time4TF, timePlusTF4, roundId, 4, dnf));
                    timeList.add(getTime(dnf5, plus5, time5TF, timePlusTF5, roundId, 5, dnf));
                    
                    double average = this.generarAVG(timeList);
                    double pb = this.getPb(timeList);
                    
                    round.setAvg(average);
                    round.setPb(pb);
                    
                    this.rubik.update(round);
                    
                    System.out.println("average = " + average);
                    
                } else {
                    
                    System.out.println("Faltan campos");
                    
                }
                
            } else {
                
                System.out.println("Falta juez");
                
            }
            
        } else {
            
            System.out.println("Falta cuber");
            
        }
        
    }
    
    private Time getTime(RadioButton dnf, RadioButton plus, TextField timeValue, TextField timePlus, int roundId, int iteration, double dnfValue) {
        
        Time _time = null;
        
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
        
        this.rubik.save(_time);
        
        return _time;
    }
    
    public double generarAVG(List<Time> times){
      
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
                                    
                                    new Alert(
                                            AlertType.INFORMATION, 
                                            "Cuber encontrado: " + cuber.toString()
                                    ).show();
                                    
                                    this.cuberLabel.setText(cuber.getName());
                                    
                                    this.cuber = cuber;
                                    
                                    readerEvent.setIsRunning(false);
                                    readerThread.interrupt();
                                    
                                    this.alert.close();
                                    
                                    return;
                                    
                                } else {
                                    
                                    new Alert(
                                            AlertType.INFORMATION, 
                                            "Cuber no encontrado :/"
                                    ).show();
                                    
                                    readerEvent.setIsRunning(false);
                                    readerThread.interrupt();
                                    
                                    this.alert.close();
                                    
                                    found = true;
                                    
                                }
                                
                            }
                            
                        }
                        
                        if (!found) {
                            
                            new Alert(
                                    AlertType.INFORMATION,
                                    "Juez no encontrado :/"
                            ).show();
                            
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
                                        
                                        new Alert(
                                                AlertType.INFORMATION, 
                                                "Juez encontrado: " + judge.toString()
                                        ).show();
                                        
                                        this.judgeLabel.setText(judge.getName());
                                        
                                        this.judge = judge;
                                        
                                        readerEvent.setIsRunning(false);
                                        readerThread.interrupt();
                                        
                                        this.alert.close();
                                    
                                        found = true;
                                        
                                    }
                                    
                                }
                                
                            }
                            
                            if (!found) {
                                
                                new Alert(
                                        AlertType.INFORMATION,
                                        "Juez no encontrado :/"
                                ).show();

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
            
            new Alert(
                    Alert.AlertType.ERROR,
                    "Error " + ex.toString()
            ).show();
            
        }
        
    }
    
}
