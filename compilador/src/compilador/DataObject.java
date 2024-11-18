package compilador;

import java.util.ArrayList;
import java.util.List;

public class DataObject {
    private String name;
    private String type;
    private String value;
    private List<DataObject> subdata;

    public DataObject(String name, String type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.subdata = new ArrayList<>();

    }

    public List<DataObject> getSubdata() {
        return subdata;
    }

    public void setSubdata(List<DataObject> subdata) {
        this.subdata = subdata;
    }

    public void addSubdata(DataObject subdata) {
        this.subdata.add(subdata);
    }


    public DataObject(String name, String type) {
        this.name = name;
        this.type = type;
        this.value = null;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

}
