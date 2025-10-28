package group5.sebm.audit.dto;

import java.util.List;

public class PagedResponse {
    public int total;
    public int page;
    public int size;
    public List<Object> items;

    public PagedResponse() {
    }
}

