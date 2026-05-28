package org.example.common.entity;

import lombok.Data;

@Data
public class PageQuery {
    private long current = 1;
    private long size = 10;
    private String keyword;
}
