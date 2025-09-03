package vn.huuhuy.controller.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class PageResponseAbstract {
    private int pageNumber;
    private int PageSize;
    private int totalPages;
    private Long totalElements;
}
