# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 2/2 (100.0%)
- **Function parity:** 28/29 matched (target 48) — 96.6%
- **Class/type parity:** 6/6 matched (target 14) — 100.0%
- **Combined symbol parity:** 34/35 matched (target 62) — 97.1%
- **Average inline-code cosine:** 0.69 (function body across 2 matched files)
- **Average documentation cosine:** 0.24 (doc text across 2 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 0 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. printer

- **Target:** `prettyassertions.Printer`
- **Similarity:** 0.66
- **Dependents:** 0
- **Priority Score:** 12803.4
- **Functions:** 25/26 matched (target 33)
- **Missing functions:** `check_printer`
- **Types:** 2/2 matched (target 7)
- **Missing types:** _none_
- **Tests:** 16/17 matched

### 2. lib

- **Target:** `prettyassertions.Lib`
- **Similarity:** 0.73
- **Dependents:** 0
- **Priority Score:** 702.7
- **Functions:** 3/3 matched (target 15)
- **Missing functions:** _none_
- **Types:** 4/4 matched (target 7)
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Next Commands

```bash
# Initialize task queue for systematic porting
cd tools/ast_distance
./ast_distance --init-tasks ../../tmp/pretty_assertions/src rust ../../src/commonMain/kotlin/io/github/kotlinmania/prettyassertions kotlin tasks.json ../../AGENTS.md

# Get next high-priority task
./ast_distance --assign tasks.json <agent-id>
```
