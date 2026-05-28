package org.example.common.dto;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Data
public class PageResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<T> records;
    private long total;
    private long size;
    private long current;

    public static <T> PageResult<T> empty() {
        PageResult<T> r = new PageResult<>();
        r.records = Collections.emptyList();
        r.total = 0;
        r.size = 0;
        r.current = 1;
        return r;
    }

    public static <T> PageResult<T> of(List<T> records, long total, long current, long size) {
        PageResult<T> r = new PageResult<>();
        r.records = records == null ? Collections.emptyList() : records;
        r.total = total;
        r.current = current;
        r.size = size;
        return r;
    }

    /** 从 MyBatis-Plus 的 IPage 转换；entity → VO 的映射由 mapper 提供 */
    public static <E, V> PageResult<V> from(IPage<E> page, Function<E, V> mapper) {
        PageResult<V> r = new PageResult<>();
        r.records = page.getRecords().stream().map(mapper).toList();
        r.total = page.getTotal();
        r.current = page.getCurrent();
        r.size = page.getSize();
        return r;
    }
}
