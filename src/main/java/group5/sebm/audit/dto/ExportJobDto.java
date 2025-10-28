package group5.sebm.audit.dto;

public class ExportJobDto {
    public String jobId;
    public String status;
    public String downloadUrl;

    public ExportJobDto() {
    }

    public ExportJobDto(String jobId, String status, String downloadUrl) {
        this.jobId = jobId;
        this.status = status;
        this.downloadUrl = downloadUrl;
    }
}

