<template>
  <aside class="app-sidenav">
    <!-- 내 프로젝트 -->
    <section class="sidebar-section">
      <div class="sidebar-label">내 프로젝트</div>
      <button
        v-for="project in projects"
        :key="project.id"
        type="button"
        class="project-chip"
        :class="{ active: project.id === selectedProjectId }"
        :aria-pressed="project.id === selectedProjectId"
        @click="selectProject(project.id)"
      >
        <span class="project-dot" :style="{ background: project.color }"></span>
        <span class="project-chip-name">{{ project.name }}</span>
        <span class="project-chip-role">{{ project.role }}</span>
      </button>
    </section>

    <!-- 메뉴 -->
    <nav class="sidebar-section" aria-label="메뉴">
      <div class="sidebar-label">메뉴</div>

      <template v-for="item in menu" :key="item.label">
        <!-- 하위 메뉴가 없는 단일 항목 -->
        <router-link v-if="!item.children" :to="item.to" custom v-slot="{ href, navigate }">
          <a :href="href" class="sidebar-item" :class="{ active: isActive(item) }" :aria-current="isActive(item) ? 'page' : null" @click="navigate">
            <svg class="item-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path :d="item.icon" /></svg>
            {{ item.label }}
          </a>
        </router-link>

        <!-- 하위 메뉴가 있는 그룹: 클릭하면 펼침/접힘 -->
        <div v-else class="sidebar-group">
          <button
            type="button"
            class="sidebar-item group-toggle"
            :class="{ 'has-active': isGroupActive(item) }"
            :aria-expanded="isExpanded(item)"
            @click="toggleGroup(item)"
          >
            <svg class="item-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path :d="item.icon" /></svg>
            <span class="item-label">{{ item.label }}</span>
            <span v-if="item.countKey" class="count" :class="{ danger: item.countTone === 'danger' }">{{ counts[item.countKey] }}</span>
            <svg class="chevron" :class="{ open: isExpanded(item) }" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M6 9l6 6 6-6" /></svg>
          </button>

          <ul v-show="isExpanded(item)" class="sidebar-sub">
            <li v-for="child in item.children" :key="child.to">
              <router-link :to="child.to" custom v-slot="{ href, navigate }">
                <a :href="href" class="sidebar-subitem" :class="{ active: isActive(child) }" :aria-current="isActive(child) ? 'page' : null" @click="navigate">
                  {{ child.label }}
                </a>
              </router-link>
            </li>
          </ul>
        </div>
      </template>
    </nav>

    <!-- 프로젝트 관리 -->
    <nav class="sidebar-section" aria-label="프로젝트 관리">
      <div class="sidebar-label">프로젝트 관리</div>

      <!-- <router-link v-for="item in projectMenu" :key="item.to" :to="item.to" custom v-slot="{ href, navigate }">
        <a :href="href" class="sidebar-item" :class="{ active: isActive(item) }" :aria-current="isActive(item) ? 'page' : null" @click="navigate">
          <svg class="item-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path :d="item.icon" /></svg>
          {{ item.label }}
        </a>
      </router-link> -->
      <template v-for="item in projectMenu" :key="item.label">
        <!-- 하위 메뉴가 없는 단일 항목 -->
        <router-link v-if="!item.children" :to="item.to" custom v-slot="{ href, navigate }">
          <a :href="href" class="sidebar-item" :class="{ active: isActive(item) }" :aria-current="isActive(item) ? 'page' : null" @click="navigate">
            <svg class="item-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path :d="item.icon" /></svg>
            {{ item.label }}
          </a>
        </router-link>

        <!-- 하위 메뉴가 있는 그룹: 클릭하면 펼침/접힘 -->
        <div v-else class="sidebar-group">
          <button
            type="button"
            class="sidebar-item group-toggle"
            :class="{ 'has-active': isGroupActive(item) }"
            :aria-expanded="isExpanded(item)"
            @click="toggleGroup(item)"
          >
            <svg class="item-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path :d="item.icon" /></svg>
            <span class="item-label">{{ item.label }}</span>
            <span v-if="item.countKey" class="count" :class="{ danger: item.countTone === 'danger' }">{{ counts[item.countKey] }}</span>
            <svg class="chevron" :class="{ open: isExpanded(item) }" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M6 9l6 6 6-6" /></svg>
          </button>

          <ul v-show="isExpanded(item)" class="sidebar-sub">
            <li v-for="child in item.children" :key="child.to">
              <router-link :to="child.to" custom v-slot="{ href, navigate }">
                <a :href="href" class="sidebar-subitem" :class="{ active: isActive(child) }" :aria-current="isActive(child) ? 'page' : null" @click="navigate">
                  {{ child.label }}
                </a>
              </router-link>
            </li>
          </ul>
        </div>
      </template>
    </nav>
  </aside>
</template>

<script>
import { sidebarMenu, sidebarProjectManage } from '@/config/navigation'
import sampleStore from '@/store/sampleStore'

export default {
  name: 'AppSideNav',
  data() {
    return {
      menu: sidebarMenu,
      projectMenu: sidebarProjectManage,
      // 사용자가 직접 펼침/접힘한 그룹: { [label]: boolean }
      expandedMap: {},
    }
  },
  computed: {
    projects() {
      return sampleStore.state.myProjects
    },
    selectedProjectId() {
      return sampleStore.state.selectedProjectId
    },
    counts() {
      return sampleStore.counts()
    },
  },
  methods: {
    selectProject(id) {
      sampleStore.selectProject(id)
    },
    // route.name 기준으로 활성 여부를 판단 (/documents 와 /documents/new 처럼 경로가 겹쳐도 정확)
    isActive(item) {
      return Array.isArray(item.names) && item.names.includes(this.$route.name)
    },
    isGroupActive(group) {
      return group.children.some((child) => this.isActive(child))
    },
    // 현재 페이지가 속한 그룹은 항상 펼치고, 그 외에는 사용자가 연 상태를 따른다
    isExpanded(group) {
      return this.isGroupActive(group) || !!this.expandedMap[group.label]
    },
    toggleGroup(group) {
      // 현재 페이지가 속한 그룹은 isExpanded 에서 항상 펼쳐지므로 접히지 않는다
      this.expandedMap[group.label] = !this.expandedMap[group.label]
    },
  },
}
</script>

<style scoped>
.sidebar-section {
  padding: var(--space-4) var(--space-3) var(--space-2);
}

.sidebar-section + .sidebar-section {
  border-top: 1px solid var(--color-border);
  padding-top: var(--space-3);
}

.sidebar-label {
  margin-bottom: 6px;
  padding: 0 var(--space-2);
  font-size: 10.5px;
  font-weight: 600;
  letter-spacing: 0.7px;
  color: var(--color-text-faint);
}

/* ---- 내 프로젝트 ---- */
.project-chip {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  width: 100%;
  margin-bottom: 2px;
  padding: 8px 10px;
  border: none;
  border-radius: var(--radius-md);
  background: none;
  text-align: left;
  cursor: pointer;
  transition: background-color 0.12s ease;
}

.project-chip:hover { background: var(--color-surface-muted); }
.project-chip.active { background: var(--color-primary-50); }

.project-dot {
  flex-shrink: 0;
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.project-chip-name {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  font-size: var(--font-size-sm);
  font-weight: 500;
  color: var(--color-text);
}
.project-chip.active .project-chip-name { font-weight: 600; color: var(--color-primary-700); }

.project-chip-role {
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.3px;
  color: var(--color-text-muted);
}

/* ---- 메뉴 항목 ---- */
.sidebar-item {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  margin-bottom: 2px;
  padding: 7px 10px;
  border: none;
  border-radius: var(--radius-md);
  background: none;
  font-size: 13.5px;
  font-weight: 500;
  text-align: left;
  color: var(--color-text-soft);
  cursor: pointer;
  user-select: none;
  transition: background-color 0.12s ease, color 0.12s ease;
}

.item-icon {
  flex-shrink: 0;
  opacity: 0.75;
}

.sidebar-item:hover {
  background: var(--color-surface-muted);
  color: var(--color-text);
}

.sidebar-item.active {
  background: var(--color-primary-50);
  color: var(--color-primary-600);
  font-weight: 600;
}

.sidebar-item.active .item-icon,
.group-toggle.has-active .item-icon {
  opacity: 1;
}

/* 하위 메뉴가 활성인 그룹은 부모 라벨만 강조 (배경은 하위 항목에 준다) */
.group-toggle.has-active {
  color: var(--color-text);
  font-weight: 600;
}

.item-label { flex: 1; min-width: 0; }

.count {
  min-width: 20px;
  padding: 1px 6px;
  border-radius: 10px;
  background: var(--color-surface-muted);
  font-size: 11px;
  font-weight: 600;
  text-align: center;
  color: var(--color-text-soft);
}

.count.danger {
  background: var(--color-error-100);
  color: var(--color-error-700);
}

.chevron {
  flex-shrink: 0;
  color: var(--color-text-faint);
  transition: transform 0.15s ease;
}

.chevron.open { transform: rotate(180deg); }

/* ---- 하위 메뉴 ---- */
.sidebar-sub {
  margin: 0 0 4px;
  padding: 0 0 0 26px;
  list-style: none;
}

.sidebar-subitem {
  display: block;
  margin-bottom: 1px;
  padding: 6px 10px;
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
  font-weight: 500;
  color: var(--color-text-soft);
  transition: background-color 0.12s ease, color 0.12s ease;
}

.sidebar-subitem:hover {
  background: var(--color-surface-muted);
  color: var(--color-text);
}

.sidebar-subitem.active {
  background: var(--color-primary-50);
  color: var(--color-primary-600);
  font-weight: 600;
}
</style>
