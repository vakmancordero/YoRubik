package yorubik.cuber;

import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import java.net.URL;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;

import java.util.Optional;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import yorubik.model.Cuber;
import yorubik.reader.DPFPReader;

import yorubik.util.Rubik;
import yorubik.util.ValidatorUtil;

import static yorubik.YoRubikController.rubik;

/**
 *
 * @author VakSF
 */
public class CuberController implements Initializable {
    
    private DPFPReader myReader;
    
    @FXML
    private TextField nameTF;
    
    @FXML
    private DatePicker datePicker;
    
    private ValidatorUtil validatorUtil;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.myReader = new DPFPReader();
        
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
        
        this.initValidator();
    }
    
    private void initValidator() {
        this.validatorUtil = new ValidatorUtil(this.nameTF, datePicker);
    }
            
    @FXML
    private void save() {
        
        String name = this.nameTF.getText();
        
        if (this.validatorUtil.validateFields()) {
            
            Date birthday = Date.from(
                    this.datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()
            );
            
            Cuber cuber = new Cuber();
            cuber.setName(name);
            cuber.setBirthday(birthday);
            
            Alert confirmation = new Alert(AlertType.CONFIRMATION);
            
            confirmation.setTitle("Confirmacion de modificación");
            confirmation.setHeaderText("Está seguro?");
            confirmation.setContentText(
                    "Desea establecer la huella del cuber " + cuber.getName() + "?"
            );
            
            Optional<ButtonType> option = confirmation.showAndWait();
            
            if (option.get() == ButtonType.OK) {
                
                Alert alert = new Alert(AlertType.INFORMATION);
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
                    
                    cuber.setTemplate(rubik.serializeTemplate(template));
                    
                    rubik.saveFingerPrint(cuber);
                    
                    new Alert(
                            AlertType.INFORMATION,
                            "La huella ha sido registrada correctamente"
                    ).show();
                    
                    this.validatorUtil.clearFields();
                    
                } else {
                    
                    new Alert(
                            AlertType.ERROR,
                            "No hay lector conectado"
                    ).show();
                    
                }
                
            }
            
        } else {
            
            this.validatorUtil.emptyFields().show();
            
        }

    }
    
}