<template>
  <section class="card calendar-card">
    <div class="card-head">
      <span class="card-title">캘린더</span>
      <div class="cal-nav">
        <button type="button" class="btn btn-ghost btn-sm" aria-label="이전 달" @click="moveMonth(-1)">‹</button>
        <strong class="cal-label" aria-live="polite">{{ monthLabel }}</strong>
        <button type="button" class="btn btn-ghost btn-sm" aria-label="다음 달" @click="moveMonth(1)">›</button>
        <button type="button" class="btn btn-outline btn-sm" @click="goToday">오늘</button>
      </div>
    </div>

    <!-- 범례 -->
    <ul class="cal-legend">
      <li v-for="item in legend" :key="item.tone"><span class="cal-dot" :class="item.tone"></span>{{ item.label }}</li>
    </ul>

    <!-- 7열 고정 그리드: 요일 머리글 1행 + 날짜 6행(42칸). 모든 칸이 같은 폭/높이라 열·행이 항상 맞는다 -->
    <div class="cal-grid" role="grid" :aria-label="monthLabel">
      <div class="cal-row" role="row">
        <div
          v-for="(name, i) in weekdays"
          :key="name"
          class="cal-dow"
          :class="{ sun: i === 0, sat: i === 6 }"
          role="columnheader"
        >{{ name }}</div>
      </div>

      <div v-for="(week, wi) in weeks" :key="wi" class="cal-row" role="row">
        <div
          v-for="day in week"
          :key="day.key"
          class="cal-cell"
          :class="{ out: !day.inMonth, today: day.isToday, selected: day.key === selectedKey, sun: day.dow === 0, sat: day.dow === 6 }"
          role="gridcell"
          :aria-selected="day.key === selectedKey"
        >
          <button type="button" class="cal-date" :aria-label="`${day.label} 일정 ${day.events.length}건`" @click="select(day.key)">
            {{ day.date }}
          </button>

          <!-- 데스크톱: 일정 제목 칩(최대 MAX_VISIBLE 개) + 나머지는 '+N건' -->
          <ul v-if="day.events.length" class="cal-events">
            <li v-for="ev in day.events.slice(0, MAX_VISIBLE)" :key="ev.id">
              <router-link :to="ev.to" class="cal-ev" :class="ev.tone" :title="`${ev.kind} · ${ev.title}`">{{ ev.title }}</router-link>
            </li>
          </ul>
          <button v-if="day.events.length > MAX_VISIBLE" type="button" class="cal-more" @click="select(day.key)">
            +{{ day.events.length - MAX_VISIBLE }}건
          </button>

          <!-- 모바일: 칩 대신 점으로 표시 -->
          <span v-if="day.events.length" class="cal-dots" aria-hidden="true">
            <i v-for="ev in day.events.slice(0, 4)" :key="ev.id" class="cal-dot" :class="ev.tone"></i>
          </span>
        </div>
      </div>
    </div>

    <!-- 선택한 날짜의 전체 일정 -->
    <div class="cal-detail">
      <div class="cal-detail-title">{{ selectedLabel }}</div>
      <ul v-if="selectedEvents.length" class="cal-detail-list">
        <li v-for="ev in selectedEvents" :key="ev.id">
          <span class="cal-dot" :class="ev.tone"></span>
          <span class="cal-detail-kind">{{ ev.kind }}</span>
          <router-link :to="ev.to" class="cal-detail-link">{{ ev.title }}</router-link>
        </li>
      </ul>
      <p v-else class="cal-detail-empty">선택한 날짜에 일정이 없습니다.</p>
    </div>
  </section>
</template>

<script>
import dayjs from 'dayjs'
import 'dayjs/locale/ko'

// 요일 표기(화)를 한국어로 — 전역 locale 은 바꾸지 않고 이 컴포넌트의 포맷에서만 사용한다
const ko = (value) => dayjs(value).locale('ko')

const MAX_VISIBLE = 2 // 칸 높이를 고정하므로 칩은 2개까지만, 나머지는 '+N건'
const WEEKDAYS = ['일', '월', '화', '수', '목', '금', '토']
const LEGEND = [
  { tone: 'tone-primary', label: '진행중 업무' },
  { tone: 'tone-warn', label: '지연 업무' },
  { tone: 'tone-success', label: '완료 업무' },
  { tone: 'tone-muted', label: '대기 업무' },
  { tone: 'tone-danger', label: '이슈' },
  { tone: 'tone-accent', label: '프로젝트 시작/종료' },
]

export default {
  name: 'CalendarCard',
  props: {
    // [{ id, date: 'YYYY-MM-DD', title, kind, tone, to }]
    events: { type: Array, default: () => [] },
  },
  data() {
    return {
      MAX_VISIBLE,
      weekdays: WEEKDAYS,
      legend: LEGEND,
      cursor: dayjs().startOf('month'),
      selectedKey: dayjs().format('YYYY-MM-DD'),
    }
  },
  computed: {
    monthLabel() {
      return this.cursor.format('YYYY년 M월')
    },
    // 날짜(YYYY-MM-DD) → 일정 목록
    eventsByDate() {
      const map = {}
      this.events.forEach((ev) => {
        if (!ev.date) return
        const key = dayjs(ev.date).format('YYYY-MM-DD')
        ;(map[key] = map[key] || []).push(ev)
      })
      return map
    },
    // 항상 6주(42칸)를 만든다: 달마다 행 수가 달라 카드 높이가 흔들리는 것을 막고, 이전/다음 달 날짜는 흐리게 표시
    weeks() {
      const first = this.cursor.startOf('month')
      const start = first.subtract(first.day(), 'day') // 그 주의 일요일
      const todayKey = dayjs().format('YYYY-MM-DD')
      const weeks = []
      for (let w = 0; w < 6; w++) {
        const row = []
        for (let d = 0; d < 7; d++) {
          const date = start.add(w * 7 + d, 'day')
          const key = date.format('YYYY-MM-DD')
          row.push({
            key,
            date: date.date(),
            dow: d,
            inMonth: date.month() === this.cursor.month(),
            isToday: key === todayKey,
            label: date.locale('ko').format('M월 D일 (dd)'),
            events: this.eventsByDate[key] || [],
          })
        }
        weeks.push(row)
      }
      return weeks
    },
    selectedEvents() {
      return this.eventsByDate[this.selectedKey] || []
    },
    selectedLabel() {
      return `${ko(this.selectedKey).format('M월 D일 (dd)')} 일정 ${this.selectedEvents.length}건`
    },
  },
  methods: {
    moveMonth(delta) {
      this.cursor = this.cursor.add(delta, 'month')
    },
    goToday() {
      this.cursor = dayjs().startOf('month')
      this.selectedKey = dayjs().format('YYYY-MM-DD')
    },
    select(key) {
      this.selectedKey = key
      // 이전/다음 달 날짜를 누르면 그 달로 이동
      const d = dayjs(key)
      if (d.month() !== this.cursor.month()) this.cursor = d.startOf('month')
    },
  },
}
</script>

<style scoped>
.cal-nav {
  display: flex;
  align-items: center;
  gap: var(--space-1);
}

.cal-label {
  min-width: 110px;
  text-align: center;
  font-size: var(--font-size-md);
  font-variant-numeric: tabular-nums;
}

.cal-legend {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-1) var(--space-4);
  margin: 0;
  padding: 10px var(--space-4);
  list-style: none;
  font-size: 11.5px;
  color: var(--color-text-muted);
}

.cal-legend li {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.cal-dot {
  display: inline-block;
  flex-shrink: 0;
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

/* 색상: 칩 배경(옅은 색)과 점(진한 색)을 같은 톤 이름으로 맞춘다 */
.cal-dot.tone-primary { background: var(--color-primary-600); }
.cal-dot.tone-warn { background: var(--color-warning-500); }
.cal-dot.tone-success { background: var(--color-success-500); }
.cal-dot.tone-muted { background: var(--color-neutral-400); }
.cal-dot.tone-danger { background: var(--color-error-500); }
.cal-dot.tone-accent { background: var(--color-accent-500); }

/* ---- 그리드 ---- */
.cal-grid {
  display: grid;
  /* 요일 머리글 1행(34px) + 날짜 6행(고정 높이). 행마다 높이가 같아 줄이 맞는다 */
  grid-template-rows: 34px repeat(6, 104px);
  margin: 0 var(--space-4) var(--space-4);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  overflow: hidden;
}

/* role="row" 래퍼는 접근성용이므로 레이아웃에서는 투명하게 두고, 7열은 래퍼 안에서 만든다 */
.cal-row {
  display: grid;
  grid-template-columns: repeat(7, minmax(0, 1fr));
}

.cal-row + .cal-row .cal-cell {
  border-top: 1px solid var(--color-border);
}

.cal-dow {
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-surface-muted);
  font-size: var(--font-size-xs);
  font-weight: 700;
  color: var(--color-text-soft);
}
.cal-dow.sun { color: var(--color-error-600); }
.cal-dow.sat { color: var(--color-info-600); }

.cal-cell {
  position: relative;
  min-width: 0;
  overflow: hidden;
  padding: 4px 6px;
  background: var(--color-surface);
}

/* 세로 구분선: 첫 칸을 제외한 모든 칸의 왼쪽 */
.cal-cell + .cal-cell {
  border-left: 1px solid var(--color-border);
}

.cal-cell.out { background: var(--color-neutral-50); }
.cal-cell.out .cal-date { color: var(--color-text-faint); }
.cal-cell.selected { box-shadow: inset 0 0 0 2px var(--color-primary-400); }

.cal-date {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 24px;
  height: 24px;
  padding: 0 4px;
  border: none;
  border-radius: 12px;
  background: none;
  font-size: var(--font-size-xs);
  font-weight: 600;
  font-variant-numeric: tabular-nums;
  color: var(--color-text);
  cursor: pointer;
}
.cal-cell.sun .cal-date { color: var(--color-error-600); }
.cal-cell.sat .cal-date { color: var(--color-info-600); }
.cal-cell.out.sun .cal-date,
.cal-cell.out.sat .cal-date { color: var(--color-text-faint); }
.cal-date:hover { background: var(--color-surface-muted); }
.cal-cell.today .cal-date {
  background: var(--color-primary-600);
  color: #fff;
}

.cal-events {
  margin: 2px 0 0;
  padding: 0;
  list-style: none;
}

.cal-events li + li { margin-top: 2px; }

.cal-ev {
  display: block;
  overflow: hidden;
  padding: 1px 6px;
  border-radius: var(--radius-sm);
  white-space: nowrap;
  text-overflow: ellipsis;
  font-size: 11px;
  font-weight: 600;
  line-height: 1.6;
}
.cal-ev:hover { filter: brightness(0.96); }
.cal-ev.tone-primary { background: var(--color-primary-50); color: var(--color-primary-700); }
.cal-ev.tone-warn { background: var(--color-warning-50); color: var(--color-warning-700); }
.cal-ev.tone-success { background: var(--color-success-50); color: var(--color-success-700); }
.cal-ev.tone-muted { background: var(--color-surface-muted); color: var(--color-text-soft); }
.cal-ev.tone-danger { background: var(--color-error-50); color: var(--color-error-700); }
.cal-ev.tone-accent { background: var(--color-accent-50); color: var(--color-accent-700); }

.cal-more {
  display: block;
  margin-top: 1px;
  padding: 0 6px;
  border: none;
  background: none;
  font-size: 11px;
  font-weight: 600;
  color: var(--color-text-muted);
  cursor: pointer;
}
.cal-more:hover { color: var(--color-primary-700); }

.cal-dots { display: none; }

/* ---- 선택한 날짜 ---- */
.cal-detail {
  padding: var(--space-3) var(--space-4) var(--space-4);
  border-top: 1px solid var(--color-border);
}

.cal-detail-title {
  margin-bottom: var(--space-2);
  font-size: var(--font-size-sm);
  font-weight: 700;
}

.cal-detail-list {
  margin: 0;
  padding: 0;
  list-style: none;
}

.cal-detail-list li {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: 5px 0;
  font-size: var(--font-size-sm);
}

.cal-detail-kind {
  flex-shrink: 0;
  min-width: 72px;
  font-size: 11.5px;
  color: var(--color-text-muted);
}

.cal-detail-link {
  min-width: 0;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  color: var(--color-text);
}
.cal-detail-link:hover { color: var(--color-primary-700); text-decoration: underline; }

.cal-detail-empty {
  margin: 0;
  font-size: var(--font-size-sm);
  color: var(--color-text-muted);
}

/* ---- 모바일: 행 높이를 줄이고 칩 대신 점만 표시 ---- */
@media (max-width: 700px) {
  .cal-grid {
    grid-template-rows: 30px repeat(6, 56px);
    margin: 0 var(--space-2) var(--space-3);
  }
  .cal-cell { padding: 2px; text-align: center; }
  .cal-events, .cal-more { display: none; }
  .cal-dots {
    display: flex;
    justify-content: center;
    gap: 2px;
    margin-top: 2px;
  }
  .cal-dots .cal-dot { width: 6px; height: 6px; }
  .cal-label { min-width: 90px; font-size: var(--font-size-sm); }
}
</style>
