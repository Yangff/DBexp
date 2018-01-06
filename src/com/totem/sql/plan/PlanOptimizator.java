package com.totem.sql.plan;

/**
 * Run optimization on original plan
 *
 * for example, `where a > 10 and a < 60` would generate Intersect(RangeScan(a, 10, inf) and RangeScan(a, inf, 60))
 * We can change it to RangeScan(a, 10, 60)
 *
 */
public class PlanOptimizator {
}
