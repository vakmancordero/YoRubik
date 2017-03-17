package yorubik.util;

import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import com.digitalpersona.onetouch.verification.DPFPVerificationResult;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import yorubik.model.Cuber;
import yorubik.model.Judge;

/**
 *
 * @author VakSF
 */
public class Rubik {
    
    private SessionFactory sessionFactory;

    public Rubik() {
        
        try {
            
            this.sessionFactory = HibernateUtil.getSessionFactory();
            
        } catch (Exception e) {
            
            System.out.println("No hay conexion");
            
        }
    }
    
    private Session getSession() {
        return this.sessionFactory.openSession();
    }
    
    /**
     * Obtiene una lista de empleados
     * 
     * @param <T> The class entity to retrieve data
     * @param entity 
     * @return      lista de empleados
     * @see         Employee
     */
    public <T> List<T> getList(Class entity) {
        
        Session session = this.getSession();
        Transaction transaction = session.beginTransaction();
        
        /* Se crea una lista para almacenar elementos */
        List<T> list = new ArrayList<>();
        
        /* Se obtienen los elementos existentes. */
        try {
            
            list = session.createCriteria(entity).list();
            
            transaction.commit();
             
        } catch (HibernateException ex) {
            
            this.rollback(transaction);
            
        } finally {
            
            session.close();
            
        }
        
        return list;
        
    }
    public boolean save(Object object) {
        
        this.sessionFactory = HibernateUtil.getSessionFactory();
        
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        
        boolean saved = false;
        
        try {
            
            session.save(object);
            
            transaction.commit();
            
            saved = true;
             
        } catch (HibernateException ex) {
            
            this.rollback(transaction);
            
            saved = false;
            
        } finally {
            
            session.close();
            
        }
        
        return saved;
        
    }
    
    public boolean update(Object object) {
        
        this.sessionFactory = HibernateUtil.getSessionFactory();
        
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        
        boolean updated = false;
        
        try {
            
            session.saveOrUpdate(object);
            transaction.commit();
            
            updated = true;
             
        } catch (HibernateException ex) {
            
            this.rollback(transaction);
            
            updated = false;
            
        } finally {
            
            session.close();
            
        }
        
        return updated;
        
    }
    
    public int getLastId(String entity) {
            
        this.sessionFactory = HibernateUtil.getSessionFactory();

        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        
        int id = 0;
        
        try {
            
            id = ((BigInteger) session.createSQLQuery(
                    "SELECT LAST_INSERT_ID() AS last_id FROM " + entity
            ).uniqueResult()).intValue();
            
            transaction.commit();
             
        } catch (HibernateException ex) {
            
            this.rollback(transaction);
            
        } finally {
            
            session.close();
            
        }
        
        return id;
    }
    
    
    /**
     * Guarda una huella
     * 
     * @param object es el empleado que se guardará la huella
     * @see         Employee
     */
    public void saveFingerPrint(Object object) {
        
        Session session = this.sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        
        /*
            Se una huella dactilar
         */
            
        session.saveOrUpdate(object);

        transaction.commit();
        session.flush(); session.close();
        
    }
    
    /**
     * Serializa un template
     * 
     * @param template es un template de una huella dactilar
     * @return      un arreglo de bytes
     * @see         DPFPTemplate
     */
    public byte[] serializeTemplate(DPFPTemplate template) {
        return template.serialize();    
    }
    
    /**
     * Deserializa un template
     * 
     * @param cuber es el cuber al que se le obtendrá el template
     * @return      un template de una huella dactilar
     * @see         DPFPTemplate
     */
    public DPFPTemplate deserializeCuberTemplate(Cuber cuber) {
        
        /*
            Se crea un template vacío.
        */
        DPFPTemplate template = DPFPGlobal.getTemplateFactory().createTemplate();
        
        /*
            Se deserializa el template
        */
        try {
            
            /*
                Se obtienen los bytes del template del empleado.
                Se asigna los deserializado al template anteriormente vacío.
            */
            template.deserialize(cuber.getTemplate());
            
        } catch (IllegalArgumentException ex) {
            
            System.out.println("Empleado sin huella: " + cuber);
            
            /*
                Si existe algún problema con la deserialización, 
                el template será nulo.
            */
            template = null;
            
        }
        
        return template;
        
    }
    
    /**
     * Deserializa un template
     * 
     * @param judge es el cuber al que se le obtendrá el template
     * @return      un template de una huella dactilar
     * @see         DPFPTemplate
     */
    public DPFPTemplate deserializeJudgeTemplate(Judge judge) {
        
        /*
            Se crea un template vacío.
        */
        DPFPTemplate template = DPFPGlobal.getTemplateFactory().createTemplate();
        
        /*
            Se deserializa el template
        */
        try {
            
            /*
                Se obtienen los bytes del template del empleado.
                Se asigna los deserializado al template anteriormente vacío.
            */
            template.deserialize(judge.getDigit());
            
        } catch (IllegalArgumentException ex) {
            
            System.out.println("Empleado sin huella: " + judge);
            
            /*
                Si existe algún problema con la deserialización, 
                el template será nulo.
            */
            template = null;
            
        }
        
        return template;
        
    }
    
    /**
     * Verifica si un sample coincide con un template
     * 
     * @param sample es la muestra obtenida por el Reader
     * @param template es el modelo extraido del empleado
     * @return      si fue verificado o no
     */
    public boolean verify(DPFPSample sample, DPFPTemplate template)  {
        
        boolean verified = false;
        
        try {
            
            /* Se crea un extractor de características */
            DPFPFeatureExtraction featureExtractor = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
            
            /* Se crea un set de características a través de la muestra */
            DPFPFeatureSet featureSet = featureExtractor.createFeatureSet(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);
            
            /* Se crea un verificador */
            DPFPVerification matcher = DPFPGlobal.getVerificationFactory().createVerification();
            
            /* Se establece la seguridad FAR */
            matcher.setFARRequested(DPFPVerification.MEDIUM_SECURITY_FAR);
            
            /* Se verifica entre el ser de características y el template */
            DPFPVerificationResult result = matcher.verify(featureSet, template);
            
            /* Se obtiene el resultado de la verificación */
            verified = result.isVerified();
                        
        } catch (DPFPImageQualityException ex) {
            
            ex.printStackTrace();
            
        }
        
        return verified;
        
    }
    
    private void rollback(Transaction transaction) {
        if (transaction != null) transaction.rollback();
    }
    
}
