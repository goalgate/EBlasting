package com.eblasting.Bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;


@Entity
public class PersonBean {

    @Id(autoincrement = true)
    Long id;

    @Unique
    String CardID;

    @Unique
    String fp_id;

    String name;

    String person_type;

    @Generated(hash = 1273096979)
    public PersonBean(Long id, String CardID, String fp_id, String name,
            String person_type) {
        this.id = id;
        this.CardID = CardID;
        this.fp_id = fp_id;
        this.name = name;
        this.person_type = person_type;
    }

    @Generated(hash = 836535228)
    public PersonBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardID() {
        return this.CardID;
    }

    public void setCardID(String CardID) {
        this.CardID = CardID;
    }

    public String getFp_id() {
        return this.fp_id;
    }

    public void setFp_id(String fp_id) {
        this.fp_id = fp_id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPerson_type() {
        return this.person_type;
    }

    public void setPerson_type(String person_type) {
        this.person_type = person_type;
    }

}
