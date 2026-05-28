package org.example.business.acceptance.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.business.acceptance.dto.DeviceTypeStatsVO;
import org.example.business.acceptance.dto.SupplierStatsVO;
import org.example.business.acceptance.entity.Acceptance;
import org.example.business.acceptance.entity.AcceptanceItem;
import org.example.business.mapper.AcceptanceItemMapper;
import org.example.business.mapper.AcceptanceMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AcceptanceStatsService {

    private final AcceptanceMapper acceptanceMapper;
    private final AcceptanceItemMapper itemMapper;

    public List<SupplierStatsVO> bySupplier() {
        List<Acceptance> all = acceptanceMapper.selectList(new LambdaQueryWrapper<>());
        Map<String, List<Acceptance>> grouped = all.stream()
                .filter(a -> StrUtil.isNotBlank(a.getSupplierName()))
                .collect(Collectors.groupingBy(Acceptance::getSupplierName));

        List<SupplierStatsVO> ret = new ArrayList<>();
        grouped.forEach((name, list) -> {
            long passed = list.stream().filter(a -> "PASSED".equals(a.getStatus())).count();
            long rectified = list.stream().filter(a -> "RECTIFIED".equals(a.getStatus())
                    || "RECTIFYING".equals(a.getStatus())).count();
            long returned = list.stream().filter(a -> "RETURNED".equals(a.getStatus())).count();
            ret.add(SupplierStatsVO.builder()
                    .supplierName(name)
                    .batchCount(list.size())
                    .passedCount(passed)
                    .rectifiedCount(rectified)
                    .returnedCount(returned)
                    .build());
        });
        ret.sort(Comparator.comparingLong(SupplierStatsVO::getBatchCount).reversed());
        return ret;
    }

    public List<DeviceTypeStatsVO> byDeviceType() {
        List<AcceptanceItem> items = itemMapper.selectList(new LambdaQueryWrapper<>());
        Map<String, List<AcceptanceItem>> grouped = items.stream()
                .filter(it -> StrUtil.isNotBlank(it.getDeviceType()))
                .collect(Collectors.groupingBy(AcceptanceItem::getDeviceType));
        List<DeviceTypeStatsVO> ret = new ArrayList<>();
        grouped.forEach((type, list) -> {
            BigDecimal total = list.stream().map(AcceptanceItem::getQty)
                    .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal qualified = list.stream()
                    .filter(it -> "QUALIFIED".equals(it.getResult()))
                    .map(AcceptanceItem::getQty)
                    .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
            double rate = total.compareTo(BigDecimal.ZERO) == 0 ? 0
                    : Math.round(qualified.doubleValue() * 10000.0 / total.doubleValue()) / 100.0;
            ret.add(DeviceTypeStatsVO.builder()
                    .deviceType(type)
                    .totalQty(total)
                    .qualifiedQty(qualified)
                    .qualifiedRate(rate)
                    .build());
        });
        ret.sort(Comparator.comparing(v -> v.getTotalQty().negate()));
        return ret;
    }
}
