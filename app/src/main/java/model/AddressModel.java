package model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AddressModel {
    private int code;
    private String name;
    
    @SerializedName(value = "wards", alternate = {"districts"})
    private List<AddressModel> subDivisions;

    public int getCode() { return code; }
    public String getName() { return name; }
    public List<AddressModel> getSubDivisions() { return subDivisions; }

    @Override
    public String toString() {
        return name;
    }
}