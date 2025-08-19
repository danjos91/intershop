package io.github.danjos.intershop.util;

import lombok.Data;

@Data
public class Paging {
    private int pageNumber;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;


    public Paging(int pageNumber, int pageSize, boolean hasNext, boolean hasPrevious) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }


    public int pageNumber() { return pageNumber; }
    public boolean hasNext() { return hasNext; }
    public boolean hasPrevious() { return hasPrevious; }
}