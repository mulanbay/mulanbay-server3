package cn.mulanbay.pms.persistent.dto.health;

public class TreatUnionDTO {

    private String hospital;
    private String department;
    private String organ;
    private String disease;
    private String confirmDisease;
    private String drugName;
    private String operationName;

    public TreatUnionDTO(String hospital, String department, String organ, String disease, String confirmDisease, String drugName, String operationName) {
        this.hospital = hospital;
        this.department = department;
        this.organ = organ;
        this.disease = disease;
        this.confirmDisease = confirmDisease;
        this.drugName = drugName;
        this.operationName = operationName;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getConfirmDisease() {
        return confirmDisease;
    }

    public void setConfirmDisease(String confirmDisease) {
        this.confirmDisease = confirmDisease;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }
}
