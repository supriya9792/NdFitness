package com.ndfitnessplus.Model;

public class Spinner_List {
    String id,name,finicialyear;

    public Spinner_List() {
    }

    public Spinner_List(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFinicialyear() {
        return finicialyear;
    }

    public void setFinicialyear(String finicialyear) {
        this.finicialyear = finicialyear;
    }
}
