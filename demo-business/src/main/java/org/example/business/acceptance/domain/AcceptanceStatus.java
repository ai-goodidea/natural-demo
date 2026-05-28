package org.example.business.acceptance.domain;

import java.util.Map;
import java.util.Set;

/**
 * 验收单状态机：
 *   PENDING → IN_PROGRESS
 *   IN_PROGRESS → PASSED / RETURNED / RECTIFYING
 *   RECTIFYING → RECTIFIED
 *   RECTIFIED → PASSED / RETURNED
 *   PASSED / RETURNED → 终态，不可流转
 */
public enum AcceptanceStatus {

    PENDING("待验收"),
    IN_PROGRESS("验收中"),
    PASSED("验收通过"),
    RETURNED("退货"),
    RECTIFYING("整改中"),
    RECTIFIED("整改完成");

    private final String text;

    AcceptanceStatus(String text) { this.text = text; }

    public String getText() { return text; }

    private static final Map<AcceptanceStatus, Set<AcceptanceStatus>> ALLOWED = Map.of(
            PENDING,     Set.of(IN_PROGRESS),
            IN_PROGRESS, Set.of(PASSED, RETURNED, RECTIFYING),
            RECTIFYING,  Set.of(RECTIFIED),
            RECTIFIED,   Set.of(PASSED, RETURNED),
            PASSED,      Set.of(),
            RETURNED,    Set.of()
    );

    public static boolean canTransit(AcceptanceStatus from, AcceptanceStatus to) {
        return ALLOWED.getOrDefault(from, Set.of()).contains(to);
    }

    public static boolean isFinal(AcceptanceStatus s) {
        return s == PASSED || s == RETURNED;
    }
}
