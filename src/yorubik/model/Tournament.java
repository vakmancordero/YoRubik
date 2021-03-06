package yorubik.model;
// Generated 17-mar-2017 3:57:59 by Hibernate Tools 4.3.1


import java.util.Date;

/**
 * Tournament generated by hbm2java
 */
public class Tournament  implements java.io.Serializable {


     private Integer id;
     private String name;
     private Date date;
     private Date createdAt;
     private Date updatedAt;

    public Tournament() {
    }

	
    public Tournament(String name, Date date) {
        this.name = name;
        this.date = date;
    }
    public Tournament(String name, Date date, Date createdAt, Date updatedAt) {
       this.name = name;
       this.date = date;
       this.createdAt = createdAt;
       this.updatedAt = updatedAt;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public Date getDate() {
        return this.date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    public Date getCreatedAt() {
        return this.createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public Date getUpdatedAt() {
        return this.updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return this.name;
    }

}


