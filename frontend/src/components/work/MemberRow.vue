<template>
  <li class="member-row">
    <div class="mem-av" :style="{ background: member.gradient }" aria-hidden="true">{{ member.initial }}</div>
    <div class="mem-info">
      <div class="mem-name">{{ member.loginId }}</div>
      <div class="mem-role">{{ member.desc }}</div>
    </div>
    <!-- 기본은 역할 칩. 권한 관리처럼 다른 컨트롤이 필요하면 슬롯으로 교체 -->
    <slot>
      <span class="chip" :class="roleChip">{{ member.role }}</span>
    </slot>
  </li>
</template>

<script>
import { ROLE_CHIP } from '@/utils/workMeta'

export default {
  name: 'MemberRow',
  props: {
    member: { type: Object, required: true },
  },
  computed: {
    roleChip() {
      return ROLE_CHIP[this.member.role] || 'badge-muted'
    },
  },
}
</script>
