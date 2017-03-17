package yorubik.util;

import org.hibernate.HibernateException;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.SessionFactory;

/**
 * Hibernate Utility class with a convenient method to get Session Factory
 * object.
 *
 * @author VakSF
 */
public class HibernateUtil {

    private static SessionFactory sessionFactory;
    
    static {
        try {
            
            sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
            
        } catch (HibernateException ex) {
            
            System.err.println("Initial SessionFactory creation failed." + ex);
//            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
