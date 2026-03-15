---
name: "\U0001F6E0 리팩터링"
about: Refactor without any change of behavior
title: 'Refactor: <title>'
labels: "\U0001F6E0refactor"
assignees: ''

---

## AS-IS

### 구조 / 흐름

Controller
→ Service
→ Repository
→ Domain

### 문제점

-
-

## TO-BE

### 목표 구조

Controller
→ Application Service
→ Domain Service
→ Repository

### 기대 효과

-
-

## 예상 테스트 영향

- Behavior 변경 없음
- 기존 테스트 유지

## 추가 요건

- 더 나은 개선방안이나 추가 영향이 있으면 명시할 것
