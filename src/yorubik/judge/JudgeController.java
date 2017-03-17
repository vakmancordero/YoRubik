/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yorubik.judge;

import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import java.net.URL;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import yorubik.model.Cuber;
import yorubik.model.Judge;
import yorubik.reader.DPFPReader;
import yorubik.util.Rubik;
import yorubik.util.ValidatorUtil;

/**
 * FXML Controller class
 *
 * @author VakSF
 */
public class JudgeController implements Initializable {
    
    private DPFPReader myReader;
    
    @FXML
    private TextField nameTF;
    
    @FXML
    private DatePicker datePicker;
    
    private Rubik rubik;
    
    private ValidatorUtil validatorUtil;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.myReader = new DPFPReader();
        
        try {
            
            this.rubik = new Rubik();
            
        } catch (Exception e) {
            
            new Alert(
                    Alert.AlertType.ERROR,
                    "No hay internet :("
            ).show();
            
        }
        
        this.initValidator();
    }
    
    private void initValidator() {
        this.validatorUtil = new ValidatorUtil(this.nameTF, this.datePicker);
    }
            
    @FXML
    private void save() {
        
        String name = this.nameTF.getText();
        
        if (this.validatorUtil.validateFields()) {
            
            Date birthday = Date.from(
                    this.datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()
            );
            
            Judge judge = new Judge();
            judge.setName(name);
            judge.setBornDate(birthday);
            
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            
            confirmation.setTitle("Confirmacion de modificación");
            confirmation.setHeaderText("Está seguro?");
            confirmation.setContentText(
                    "Desea establecer la huella del juez " + judge.getName() + "?"
            );
            
            Optional<ButtonType> option = confirmation.showAndWait();
            
            if (option.get() == ButtonType.OK) {
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Captura");
                alert.setHeaderText("Captura de datos");
                
                DPFPFeatureExtraction featureExtractor = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
                DPFPEnrollment enrollment = DPFPGlobal.getEnrollmentFactory().createEnrollment();
                
                this.myReader.findReader();
                
                if (!this.myReader.getActiveReader().equals("empty")) {
                    
                    try {
                        
                        while (enrollment.getFeaturesNeeded() > 0) {
                            
                            alert.setContentText("Ingresar dedo... " + enrollment.getFeaturesNeeded());
                            alert.show();
                            
                            DPFPSample sample = this.myReader.getSample();
                            
                            if (sample == null) {
                                continue;
                            }
                            
                            DPFPFeatureSet featureSet;
                            
                            try {
                                
                                featureSet = featureExtractor.createFeatureSet(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);
                                
                            } catch (DPFPImageQualityException ex) {
                                
                                System.out.println("Error, mala calidad en la huella capturada");
                                
                                continue;
                                
                            }
                            
                            alert.close();
                            
                            enrollment.addFeatures(featureSet);
                        }
                        
                    } catch (DPFPImageQualityException | InterruptedException ex) {
                        
                    }
                    
                    DPFPTemplate template = enrollment.getTemplate();
                    
                    judge.setDigit(this.rubik.serializeTemplate(template));
                    
                    try {
                        
                        this.rubik.saveFingerPrint(judge);
                        
                        new Alert(
                                Alert.AlertType.INFORMATION,
                                "La huella ha sido registrada correctamente"
                        ).show();
                        
                    } catch (Exception ex) {
                        
                        new Alert(
                                Alert.AlertType.ERROR,
                                "No hay internet :("
                        ).show();
                        
                    }
                    
                    this.validatorUtil.clearFields();
                    
                } else {
                    
                    new Alert(
                            Alert.AlertType.ERROR,
                            "No hay lector conectado"
                    ).show();
                    
                }
                
            }
            
        } else {
            
            this.validatorUtil.emptyFields().show();
            
        }

    }
    
}
